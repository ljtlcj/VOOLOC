package test.xk_ys_VOOLOC;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import test.xk_ys_VOOLOC.AboutFile.BaseApplication;
import test.xk_ys_VOOLOC.AboutFile.LockInfo;
import test.xk_ys_VOOLOC.BluetoothGattCallback.BluetoothReceiver;
import test.xk_ys_VOOLOC.BluetoothGattCallback.GattCallback;
import test.xk_ys_VOOLOC.MyView.MyProgressDialog;
import test.xk_ys_VOOLOC.Net.AddOpenRecord;
import test.xk_ys_VOOLOC.Net.NetCallback;
import test.xk_ys_VOOLOC.Net.UpdateLockPower;


public class Delete_all_password extends AppCompatActivity {
    @BindView(R.id.back)
    public TextView back;
    @BindView(R.id.delete_all_password)
    public Button delete;

    private LockInfo lockInfo;
    private BluetoothDevice device;
    private BluetoothGatt bluetoothGatt;
    private GattCallback callback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_all_password);
        ButterKnife.bind(this);
        lockInfo=getIntent().getParcelableExtra("lockInfo");
        callback=new GattCallback(this);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBluetooth();
                MyProgressDialog.show(Delete_all_password.this,"Deleting...",false,null);

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
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
        callback = new GattCallback(this);
        callback.setCallback(gcb);
        bluetoothGatt = device.connectGatt(this,false,callback);
        if(bluetoothGatt==null)
        {
            Toast.makeText(this, "bluetoothGatt is null", Toast.LENGTH_SHORT).show();
        }
        else{
            bluetoothGatt.connect();
            //   connBluetooth conn=new connBluetooth("(!"+lockInfo.getLockKey()+".O*)");
            //   openLock.setOnClickListener(conn);
        }
    }
    GattCallback.Callback gcb=new GattCallback.Callback() {
        @Override
        public void readCallback(String result) {
            if(result.contains("CLOK")){
                Toast.makeText(Delete_all_password.this, "已清除所有密码", Toast.LENGTH_SHORT).show();
                if(bluetoothGatt!=null){
                    bluetoothGatt.disconnect();
                    bluetoothGatt.close();
                    bluetoothGatt=null;
                }
                finish();
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void connectCallback() {
            MyProgressDialog.remove();
            try{
                Thread.sleep(500);
            }
            catch (InterruptedException ie){
            }
            callback.setMessage("(!CL*)");
            bluetoothGatt.discoverServices();
        }
        @Override
        public void unConnectCallback() {
        }
    };
}
