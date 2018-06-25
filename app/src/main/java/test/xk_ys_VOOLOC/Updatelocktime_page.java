package test.xk_ys_VOOLOC;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import test.xk_ys_VOOLOC.AboutFile.GetDate;
import test.xk_ys_VOOLOC.AboutFile.LockInfo;
import test.xk_ys_VOOLOC.BluetoothGattCallback.BluetoothReceiver;
import test.xk_ys_VOOLOC.BluetoothGattCallback.GattCallback;
import test.xk_ys_VOOLOC.MyView.MyProgressDialog;
import test.xk_ys_VOOLOC.Net.DeleteLock;
import test.xk_ys_VOOLOC.Net.NetCallback;


public class Updatelocktime_page extends AppCompatActivity {
    @BindView(R.id.back)
    public TextView back;
    @BindView(R.id.update)
    public Button update;

    private String account;
    private String password;
    private LockInfo lockInfo;
    private BluetoothDevice device;
    private BluetoothGatt bluetoothGatt;
    private GattCallback callback;
    public String sendMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatelocktime_page);
        ButterKnife.bind(this);
        lockInfo=getIntent().getParcelableExtra("lockInfo");
        account=getIntent().getStringExtra("account");
        password=getIntent().getStringExtra("password");
        callback=new GattCallback(this);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        updateTheTime();
                    }
                }).start();
                MyProgressDialog.show(Updatelocktime_page.this,"Updating...",false,null);
                handler.sendEmptyMessageDelayed(0x125,40000);
            }
        });
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
            if(result.contains("33333")){
                Toast.makeText(Updatelocktime_page.this, "校准成功", Toast.LENGTH_SHORT).show();
                bluetoothGatt.disconnect();
                bluetoothGatt.close();
                bluetoothGatt=null;
                finish();
            }
            handler.removeMessages(0x125);
        }
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void connectCallback() {
            handler.sendEmptyMessageDelayed(0x126,800);
        }

        @Override
        public void unConnectCallback() {
        }
    };
    private NetCallback netCallback=new NetCallback() {
        @Override
        public void execute(String result) {
            MyProgressDialog.remove();
            Intent i=new Intent(Updatelocktime_page.this,add_lock.class);
            handler.removeMessages(0x124);
            startActivity(i);
            finish();
        }

        @Override
        public void error(String result) {
            MyProgressDialog.remove();
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
                Toast.makeText(Updatelocktime_page.this, getString(R.string.delete_fault), Toast.LENGTH_SHORT).show();
            }
            else if(message.what==0x125){
                MyProgressDialog.remove();
                Toast.makeText(Updatelocktime_page.this, "更新失败，请重新更新", Toast.LENGTH_SHORT).show();
            }
            else if(message.what==0x126){
                callback.setMessage(sendMessage);
                bluetoothGatt.discoverServices();
                Log.e("------",sendMessage);
            }
        }
    };
    public void updateTheTime(){
        Intent i=new Intent(this, BluetoothReceiver.class);
        i.setAction("woolock.bluetooth.result");
        long result=getWebsiteDatetime(webUrl2)-new Date().getTime();
        Log.e("time1",String.valueOf(getWebsiteDatetime(webUrl4)));
        Log.e("time2",String.valueOf(new Date().getTime()));
        if(lockInfo.getPower().equals("1")){
            String message = GetDate.getDate();
            sendMessage="(" + message+ ")";
            getBluetooth();
        }
        else{
            if(result>-12*60*60*1000&&result<12*60*60*1000){
                String message = GetDate.getDate();
                sendMessage="(" + message+ ")";
                getBluetooth();
            }
            else {
                i.putExtra("result", 0x127);
                sendBroadcast(i);
            }
        }
    }

    String webUrl1 = "http://www.bjtime.cn";//bjTime
    String webUrl2 = "http://202.108.22.5";//百度
    String webUrl3 = "http://www.taobao.com";//淘宝
    String webUrl4 = "http://www.ntsc.ac.cn";//中国科学院国家授时中心
    String webUrl5 = "http://www.360.cn";//360
    String webUrl6 = "http://www.beijing-time.org";//beijing-time
    private static long getWebsiteDatetime(String webUrl){
        try {
            URL url = new URL(webUrl);// 取得资源对象
            URLConnection uc = url.openConnection();// 生成连接对象
            uc.connect();// 发出连接
            long ld = uc.getDate();// 读取网站日期时间
            Date date = new Date(ld);// 转换为标准时间对象
            return date.getTime();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
