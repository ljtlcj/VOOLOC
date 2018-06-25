package test.xk_ys_VOOLOC;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import test.xk_ys_VOOLOC.AboutFile.LockInfo;
import test.xk_ys_VOOLOC.BluetoothGattCallback.GattCallback;
import test.xk_ys_VOOLOC.MyView.MyProgressDialog;
import test.xk_ys_VOOLOC.Net.DeleteLock;
import test.xk_ys_VOOLOC.Net.NetCallback;


public class DeletePage extends AppCompatActivity {
    @BindView(R.id.back)
    public TextView back;
    @BindView(R.id.delete)
    public Button delete;
    private Intent bluetoothIntent;

    private LockInfo lockInfo;
    private String account;
    private String password;
    public String sendMessage;
    private BluetoothDevice device;
    private BluetoothGatt bluetoothGatt;
    private GattCallback callback;
    private SharedPreferences userInfo;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_page);
        ButterKnife.bind(this);
        initialize();
    }
    public void initialize(){
        callback = new GattCallback(this);

        Intent i=getIntent();
        lockInfo=i.getParcelableExtra("lockInfo");
        account=i.getStringExtra("account");
        password=i.getStringExtra("password");
        userInfo=getSharedPreferences(account,MODE_PRIVATE);
        editor=userInfo.edit();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                certainDelete();
            }
        });

    }
    public void certainDelete(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.lock_tip));
        builder.setMessage(getString(R.string.certain_delete_lock));
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setPositiveButton(getString(R.string.certain), new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(lockInfo.getPower().equals("1")){
                    if(lockInfo.getBluetoothAddress().equals("16:07:05:00:00:00")) {
                        Toast.makeText(DeletePage.this, "首次连接此门锁，正在尝试连接", Toast.LENGTH_SHORT).show();
                        SearchBluetoothService.bluetoothName=lockInfo.getBluetoothName();
                        bluetoothIntent=new Intent(DeletePage.this,SearchBluetoothService.class);
                        startService(bluetoothIntent);
                    }
                    else {
                        sendMessage="(!" + lockInfo.getLockKey() + ".D*)";
                        getBluetooth();
                        MyProgressDialog.show(DeletePage.this,"Deleting...",false,null);
                        handler.sendEmptyMessageDelayed(0x124,10000);
                    }

                }
                else{
                    MyProgressDialog.show(DeletePage.this,"Deleting...",false,null);
                    handler.sendEmptyMessageDelayed(0x124,10000);
                    DeleteLock deleteLock=new DeleteLock(DeletePage.this,account,password,lockInfo.getLockId());
                    deleteLock.setCallback(netCallback);
                    new Thread(deleteLock).start();
                }
            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
    }
    public void getBluetooth()
    {
        //检查蓝牙地址
        if (BluetoothAdapter.getDefaultAdapter().checkBluetoothAddress(lockInfo.getBluetoothAddress()))
        {
            device=BluetoothAdapter.getDefaultAdapter().getRemoteDevice(lockInfo.getBluetoothAddress());
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
            MyProgressDialog.remove();
            if(result.contains("OK")){

                DeleteLock deleteLock=new DeleteLock(DeletePage.this,account,password,lockInfo.getLockId());

                editor.putString("account",account);
                editor.putString("password",password);
                editor.putString("lockId",lockInfo.getLockId());
                editor.commit();

                deleteLock.setCallback(netCallback);
                new Thread(deleteLock).start();
                handler.removeMessages(0x124);
                bluetoothGatt.disconnect();
                bluetoothGatt.close();

            }
            handler.removeMessages(0x125);
        }
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void connectCallback() {
            handler.sendEmptyMessageDelayed(0x126,500);
        }

        @Override
        public void unConnectCallback() {
        }
    };
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message message){
            if(message.what==0x123){
                if(bluetoothGatt!=null){
                    bluetoothGatt.disconnect();
                    bluetoothGatt.close();
                }
            }
            else if(message.what==0x124){
                MyProgressDialog.remove();
                Toast.makeText(DeletePage.this, getString(R.string.delete_fault), Toast.LENGTH_SHORT).show();
            }
            else if(message.what==0x125){
                MyProgressDialog.remove();
                Toast.makeText(DeletePage.this, "更新失败，请重新更新", Toast.LENGTH_SHORT).show();
            }
            else if(message.what==0x126){
                callback.setMessage(sendMessage);
                bluetoothGatt.discoverServices();
                Log.e("------",sendMessage);
            }
        }
    };
    private NetCallback netCallback=new NetCallback() {
        @Override
        public void execute(String result) {
            MyProgressDialog.remove();
            editor.putBoolean("delete",false);
            editor.commit();
            Intent i=new Intent(DeletePage.this,add_lock.class);
            handler.removeMessages(0x124);
            startActivity(i);
            finish();
        }

        @Override
        public void error(String result) {
            MyProgressDialog.remove();
            editor.putBoolean("delete",true);
            editor.commit();

            Intent i=new Intent(DeletePage.this,add_lock.class);
            handler.removeMessages(0x124);
            startActivity(i);
            finish();
        }
    };

}
