package test.xk_ys_VOOLOC;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.concurrent.locks.Lock;

import butterknife.BindView;
import butterknife.ButterKnife;
import test.xk_ys_VOOLOC.AboutFile.GetDate;
import test.xk_ys_VOOLOC.AboutFile.LockInfo;
import test.xk_ys_VOOLOC.BluetoothGattCallback.BluetoothReceiver;
import test.xk_ys_VOOLOC.BluetoothGattCallback.GattCallback;
import test.xk_ys_VOOLOC.MyView.MyFastMenuBar;
import test.xk_ys_VOOLOC.MyView.MyProgressDialog;
import test.xk_ys_VOOLOC.Net.DeleteLock;
import test.xk_ys_VOOLOC.Net.GainLockList1;
import test.xk_ys_VOOLOC.Net.NetCallback;

public class Lock_of_Setting extends AppCompatActivity implements View.OnClickListener, MyFastMenuBar.onMenuBarClickListener{

    @BindView(R.id.back)
    public TextView back;
//    @BindView(R.id.correct_lock_name)
//    public LinearLayout correct;
    @BindView(R.id.correct_lock_name)
    public MyFastMenuBar correct;
//    @BindView(R.id.set_open_password)
//    public LinearLayout setLockPW;
    @BindView(R.id.set_open_password)
    public MyFastMenuBar setLockPW;
//    @BindView(R.id.open_record)
//    public LinearLayout openLockRecord;
    @BindView(R.id.open_record)
    public MyFastMenuBar openLockRecord;
//    @BindView(R.id.giver_record)
//    public LinearLayout giverRecord;
    @BindView(R.id.giver_record)
    public MyFastMenuBar giverRecord;
//    @BindView(R.id.delete_lock)
//    public LinearLayout deleteLock;
    @BindView(R.id.delete_lock)
    public MyFastMenuBar deleteLock;
//    @BindView(R.id.correct_lock_address)
//    public LinearLayout correctLockAddress;
    @BindView(R.id.correct_lock_address)
    public MyFastMenuBar correctLockAddress;
//    @BindView(R.id.updated_lock_time)
//    public LinearLayout updated_lock_time;
    @BindView(R.id.updated_lock_time)
    public MyFastMenuBar updated_lock_time;

    @BindView(R.id.delete_all_password)
    public MyFastMenuBar delete_all_password;


    private String account;
    private String password;
    public LockInfo lockInfo;


    public String sendMessage;

    private BluetoothDevice device;
    private GattCallback callback;
    private BluetoothGatt bluetoothGatt;
    static public Activity activity=null;
    private Intent bluetoothIntent;

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
                    Toast.makeText(Lock_of_Setting.this, getString(R.string.delete_fault), Toast.LENGTH_SHORT).show();
            }
            else if(message.what==0x125){
                MyProgressDialog.remove();
                Toast.makeText(Lock_of_Setting.this, "更新失败，请重新更新", Toast.LENGTH_SHORT).show();
            }
            else if(message.what==0x126){
                callback.setMessage(sendMessage);
                bluetoothGatt.discoverServices();
                Log.e("------",sendMessage);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_of__setting);
        ButterKnife.bind(this);
        activity=this;
        getLockInformation();
        initialize();
    }
    public void getLockInformation(){
        Intent i=getIntent();
        lockInfo=i.getParcelableExtra("lockInfo");
        if(lockInfo==null){
            Toast.makeText(this, getString(R.string.lock_info_error), Toast.LENGTH_SHORT).show();
            finish();
        }
        SharedPreferences info=getSharedPreferences("UserInformation",MODE_PRIVATE);
        account=info.getString("account",null);
        password=info.getString("password",null);
        if(account==null||password==null){
            Toast.makeText(this, getString(R.string.not_gain_user_information), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void initialize(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        correct.setOnMenuBarClickListener(this);
        if(lockInfo.getPower().equals("3")){
            setLockPW.setVisibility(View.GONE);
            giverRecord.setVisibility(View.GONE);
            openLockRecord.setVisibility(View.GONE);
        }
        else{
            setLockPW.setOnMenuBarClickListener(this);
            giverRecord.setOnMenuBarClickListener(this);
            openLockRecord.setOnMenuBarClickListener(this);
        }
        correctLockAddress.setOnMenuBarClickListener(this);
        callback = new GattCallback(this);

        delete_all_password.setOnMenuBarClickListener(this);
        deleteLock.setOnMenuBarClickListener(this);
        if(lockInfo.getBluetoothAddress().equals("16:07:05:00:00:00")){
            updated_lock_time.setBackgroundResource(R.color.danger);
            updated_lock_time.setOnMenuBarClickListener(new MyFastMenuBar.onMenuBarClickListener() {
                @Override
                public void onMenuBarClick(MyFastMenuBar view) {
                    Toast.makeText(Lock_of_Setting.this, "首次连接此门锁，正在尝试连接", Toast.LENGTH_SHORT).show();
                    SearchBluetoothService.bluetoothName=lockInfo.getBluetoothName();
                    bluetoothIntent=new Intent(Lock_of_Setting.this,SearchBluetoothService.class);
                    startService(bluetoothIntent);
                }
            });
        }
        else{
            updated_lock_time.setOnMenuBarClickListener(this);
        }
    }
    @Override
    public void onMenuBarClick(MyFastMenuBar v) {
        final Intent i=new Intent();
        switch (v.getId()) {
            case R.id.correct_lock_address:
                i.setClass(Lock_of_Setting.this,SetLockAddress.class);
                i.putExtra("lockKey",lockInfo.getLockKey());
                startActivity(i);
                break;
            case R.id.correct_lock_name:
                i.setClass(Lock_of_Setting.this,SetLockName.class);
                i.putExtra("lockId",lockInfo.getLockId());
                i.putExtra("lockKey",lockInfo.getLockKey());
                startActivity(i);
                break;
            case R.id.set_open_password:
                if(lockInfo.getPower().equals("1")||lockInfo.getPower().equals("2")) {
                    i.setClass(this, Setopenlockpassword_page.class);
                    i.putExtra("lockInfo", lockInfo);
                    startActivity(i);
                    break;
                }
                else{
                    Toast.makeText(this, getString(R.string.power_is_enough), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.open_record:
                i.setClass(this,Open_Records.class);
                i.putExtra("account",account);
                i.putExtra("lockInfo",lockInfo);
                startActivity(i);
                break;
            case R.id.giver_record:
                i.setClass(this,Authorization_records.class);
                i.putExtra("account",account);
                i.putExtra("password",password);
                i.putExtra("lockId",lockInfo.getLockId());
                startActivity(i);
                break;
            case R.id.delete_all_password:
                i.setClass(this,Delete_all_password.class);
                i.putExtra("lockInfo",lockInfo);
                startActivity(i);
                break;
            case R.id.delete_lock:
              //  certainDelete();
                //i.setClass(this,DeletePage.class);
                //startActivity(i);
                i.setClass(this,DeletePage.class);
                i.putExtra("lockInfo",lockInfo);
                i.putExtra("account",account);
                i.putExtra("password",password);
                startActivity(i);
                break;
            case R.id.updated_lock_time:
            /*    new Thread(new Runnable() {
                    @Override
                    public void run() {
                        updateTheTime();
                    }
                }).start();
                MyProgressDialog.show(Lock_of_Setting.this,"Updating...",false,null);
                handler.sendEmptyMessageDelayed(0x125,15000);*/
                i.setClass(this,Updatelocktime_page.class);
                i.putExtra("lockInfo",lockInfo);
                i.putExtra("account",account);
                i.putExtra("password",password);
                startActivity(i);
            default:
        }
    }
    @Override
    public void onClick(View v){
        Intent i=new Intent();
        switch (v.getId()){
            case R.id.delete_lock:
                i.setClass(this,DeletePage.class);
                i.putExtra("lockInfo",lockInfo);
                i.putExtra("account",account);
                i.putExtra("password",password);
                startActivity(i);

             //   certainDelete();
                break;
            default:
        }
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
                        Toast.makeText(Lock_of_Setting.this, "首次连接此门锁，正在尝试连接", Toast.LENGTH_SHORT).show();
                        SearchBluetoothService.bluetoothName=lockInfo.getBluetoothName();
                        bluetoothIntent=new Intent(Lock_of_Setting.this,SearchBluetoothService.class);
                        startService(bluetoothIntent);
                    }
                    else {
                        sendMessage="(!" + lockInfo.getLockKey() + ".D*)";
                        getBluetooth();
                        MyProgressDialog.show(Lock_of_Setting.this,"Deleting...",false,null);
                        handler.sendEmptyMessageDelayed(0x124,10000);
                    }

                }
                else{
                    MyProgressDialog.show(Lock_of_Setting.this,"Deleting...",false,null);
                    handler.sendEmptyMessageDelayed(0x124,10000);
                    DeleteLock deleteLock=new DeleteLock(Lock_of_Setting.this,account,password,lockInfo.getLockId());
                    deleteLock.setCallback(netCallback);
                    new Thread(deleteLock).start();
                }
            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
    }
    private NetCallback netCallback=new NetCallback() {
        @Override
        public void execute(String result) {
            MyProgressDialog.remove();
            Intent i=new Intent(Lock_of_Setting.this,add_lock.class);
            handler.removeMessages(0x124);
            startActivity(i);
            finish();
        }

        @Override
        public void error(String result) {
            MyProgressDialog.remove();
        }
    };

    protected void onResume(){
        super.onResume();

        GainLockList1 gainLockList=new GainLockList1(this,account,password);
        gainLockList.setCallback(new NetCallback() {
            @Override
            public void execute(String result) {
                try {
                    JSONArray jsonArray=new JSONArray(result);
                    boolean b=false;
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                        Log.e("lockKey",jsonObject.getString("key"));
                        if(jsonObject.getString("key").equals(lockInfo.getLockKey())) {
                            b = true;
                            Log.e("result","----");
                            break;
                        }
                    }
                    if(!b){
                        Toast.makeText(Lock_of_Setting.this, "门锁不存在", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void error(String result) {

            }
        });
        new Thread(gainLockList).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    protected void onPause(){
        super.onPause();
        if(bluetoothGatt!=null){
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
            bluetoothGatt=null;
        }
        if(bluetoothIntent!=null) {
            stopService(bluetoothIntent);
            bluetoothIntent = null;
        }
    }
    protected void onDestroy(){
        super.onDestroy();
        activity=null;
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

                DeleteLock deleteLock=new DeleteLock(Lock_of_Setting.this,account,password,lockInfo.getLockId());
                deleteLock.setCallback(netCallback);
                new Thread(deleteLock).start();
                handler.removeMessages(0x124);
                bluetoothGatt.disconnect();
                bluetoothGatt.close();

            }
            if(result.contains("33333")){
                Toast.makeText(Lock_of_Setting.this, "校准成功", Toast.LENGTH_SHORT).show();
                bluetoothGatt.disconnect();
                bluetoothGatt.close();
                bluetoothGatt=null;
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

    String webUrl1 = "http://www.bjtime.cn";//bjTime
    String webUrl2 = "http://www.baidu.com";//百度
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
    public void updateTheTime(){
        Intent i=new Intent(this, BluetoothReceiver.class);
        i.setAction("woolock.bluetooth.result");
        long result=getWebsiteDatetime(webUrl2)-new Date().getTime();
        Log.e("time1",String.valueOf(getWebsiteDatetime(webUrl4)));
        Log.e("time2",String.valueOf(new Date().getTime()));
        if(result>-12*60*60*1000&&result<12*60*60*1000){
            String message = GetDate.getDate();
            sendMessage="(" + message+ ")";
            getBluetooth();
        }
        else{
            i.putExtra("result",0x127);
            sendBroadcast(i);
        }
    }
    public void update(){
        updated_lock_time.setBackgroundColor(Color.WHITE);
        updated_lock_time.setOnClickListener(this);
        if(bluetoothIntent!=null) {
            stopService(bluetoothIntent);
            bluetoothIntent = null;
        }
    }
}
