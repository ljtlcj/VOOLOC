package test.xk_ys_VOOLOC;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import test.xk_ys_VOOLOC.AboutFile.BaseApplication;
import test.xk_ys_VOOLOC.BluetoothGattCallback.GattCallback;
import test.xk_ys_VOOLOC.MyView.MyProgressDialog;
import test.xk_ys_VOOLOC.MyView.SetSecret;
import test.xk_ys_VOOLOC.Net.DeleteLock;
import test.xk_ys_VOOLOC.Net.NetCallback;
import test.xk_ys_VOOLOC.Net.SettingResetSecret;


public class Reset extends AppCompatActivity {
    @BindView(R.id.finish)
    public TextView finish;
    @BindView(R.id.password)
    public SetSecret secret;


    private String account;
    private String password;
    private String lockKey;
    private String lockBluetooth;
    private String lockId;
    private String lockId2;
    private BluetoothDevice device;
    private BluetoothGatt bluetoothGatt;
    private GattCallback callback;
    private SettingResetSecret setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);
        ButterKnife.bind(this);
        getUserInfo();
    }
    public void getUserInfo(){
        SharedPreferences info=getSharedPreferences("UserInformation",MODE_PRIVATE);
        account=info.getString("account",null);
        password=info.getString("password",null);
        lockKey=getIntent().getStringExtra("lockKey");
        lockBluetooth=getIntent().getStringExtra("bluetooth");
        lockId=getIntent().getStringExtra("lockId");
        lockId2=getIntent().getStringExtra("lockId2");
//        Log.e("lockid2",lockId2);
        if(BaseApplication.netVersion==0){
            if(account==null||password==null||lockKey==null){
                Toast.makeText(this, getString(R.string.read_error), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        else
        {
            if(account==null||password==null||lockKey==null||lockId==null){
                Toast.makeText(this, getString(R.string.read_error), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        initialize();
    }
    public void initialize(){
        finish.setClickable(false);
        ;
        secret.setCallback(new SetSecret.myCallback(){
            public void execute(){
                finish.setClickable(true);
            }
        });
        callback = new GattCallback(this);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s=secret.getText();
                if(s!=null&&s.length()>=8){
                    secret.noInput();

                    MyProgressDialog.show(Reset.this,"Updating...",false,null);
                    getBluetooth();
                }
                else{
                    Toast.makeText(Reset.this, "未设置恢复出厂密码", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void getBluetooth()
    {
        //检查蓝牙地址
        if (BluetoothAdapter.getDefaultAdapter().checkBluetoothAddress(lockBluetooth))
        {
            device=BluetoothAdapter.getDefaultAdapter().getRemoteDevice(lockBluetooth);
            if(device==null)
            {
                Toast.makeText(this, getString(R.string.lock_info_error), Toast.LENGTH_SHORT).show();
                finish();
            }
            else{
                getGatt();
            }
        }
        else{
            Toast.makeText(this, getString(R.string.lock_info_error), Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void getGatt()
    {
        callback.setCallback(gcb);
        bluetoothGatt = device.connectGatt(this,false,callback);
        if(bluetoothGatt==null)
        {
            Toast.makeText(this, "bluetoothGatt is null", Toast.LENGTH_SHORT).show();
        }
        else {
            bluetoothGatt.connect();
        }
    }
    GattCallback.Callback gcb=new GattCallback.Callback() {
        @Override
        public void readCallback(String result) {
            if(result.contains("RESETOK")){
                if(lockId2==null)
                {
                    SharedPreferences sharedPreferences=getSharedPreferences(account,MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("secret",secret.getText());
                    editor.commit();
                    finish();
                }
                else {
                    setting = new SettingResetSecret(Reset.this, lockId2, secret.getText());
                    setting.setCallback(new NetCallback() {
                        @Override
                        public void execute(String result) {
                            if (bluetoothGatt != null) {
                                bluetoothGatt.disconnect();
                                bluetoothGatt.close();
                                bluetoothGatt = null;
                            }
                            Intent i = new Intent(Reset.this, Lock_Name.class);
                            i.putExtra("lockKey", lockKey);
                            i.putExtra("bluetooth", lockBluetooth);
                            if (BaseApplication.netVersion == 0) {
                                i.putExtra("lockId", lockKey);
                                i.putExtra("lockId2", lockId2);
                                //    i.putExtra("lockId2",lockId);
                            } else {
                                try {
                                    JSONObject json = new JSONObject(result);
                                    JSONObject data = new JSONObject(json.getString("data"));
                                    i.putExtra("lockId", data.getString("lockId"));
                                    i.putExtra("lockId2", data.getString("lockId"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            startActivity(i);
                            finish();
                        }

                        @Override
                        public void error(String result) {
                            secret.reInput();
                        }
                    });
                    new Thread(setting).start();
                }
            }
        }
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void connectCallback() {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            callback.setMessage("(!R"+secret.getText()+"*)");
            bluetoothGatt.discoverServices();
            MyProgressDialog.remove();
        }

        @Override
        public void unConnectCallback() {
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if(bluetoothGatt!=null) {
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
        }
    }
}
