package test.xk_ys_VOOLOC;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import test.xk_ys_VOOLOC.AboutFile.StringToDate;
import test.xk_ys_VOOLOC.AboutFile.isEmpty;
import test.xk_ys_VOOLOC.BluetoothGattCallback.GattCallback;
import test.xk_ys_VOOLOC.MyView.MyProgressDialog;
import test.xk_ys_VOOLOC.Net.GainLockPW;
import test.xk_ys_VOOLOC.Net.NetCallback;

public class SettingPasswordTime extends AppCompatActivity {
    private String lockKey;
    private String password;

    private TextView back;
    private TextView start;
    private TextView end;
    private TextView finish;
    private TextView self;
    private TextView forever;


    private String startTime;
    private String endTime;
    private String bluetooth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_password_time);
        getInfo();
        initialize();
    }
    public void getInfo(){
        Intent i=getIntent();
        lockKey=i.getStringExtra("lockKey");
        password=i.getStringExtra("password");
        startTime=i.getStringExtra("startTime");
        endTime=i.getStringExtra("endTime");
        bluetooth=i.getStringExtra("bluetooth");
        if(isEmpty.StringIsEmpty(bluetooth)||isEmpty.StringIsEmpty(lockKey)||isEmpty.StringIsEmpty(password)||isEmpty.StringIsEmpty(startTime)||isEmpty.StringIsEmpty(endTime)){
            Toast.makeText(this, getString(R.string.lock_info_error), Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    public void initialize(){
        back=(TextView)findViewById(R.id.back);
        start=(TextView)findViewById(R.id.start_time);
        end=(TextView)findViewById(R.id.end_time);
        finish=(TextView)findViewById(R.id.finish);

        forever=(TextView)findViewById(R.id.forever);
        self=(TextView)findViewById(R.id.self);

        self.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                self.setTextColor(Color.RED);
                forever.setTextColor(getResources().getColor(R.color.colorhint));
                end.setText(getString(R.string.deadline));
            }
        });
        forever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                self.setTextColor(getResources().getColor(R.color.colorhint));
                forever.setTextColor(Color.RED);
                finish.setVisibility(View.VISIBLE);
                if(endTime.equals("0")){
               //     long e=new Date().getTime()+(long)(1000)*(long)(60)*60*24*365*10;
                    end.setText(StringToDate.times("4102333200000"));
                }
                else{
                    end.setText(StringToDate.times(endTime+"000"));
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
       // finish.setVisibility(View.GONE);
        start.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showDate(start);
                return false;
            }
        });
        end.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showDate(end);
                return false;
            }
        });
        finish.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onClick(View v) {
                if(!end.getText().toString().equals(getString(R.string.deadline))) {
                    getBluetooth();
                    MyProgressDialog.show(SettingPasswordTime.this, "setting...", true, null);
                    handler.sendEmptyMessageDelayed(0x123,10000);
                }
                else{
                    Toast.makeText(SettingPasswordTime.this, getString(R.string.unset_time), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void showDate(final TextView textView)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.date_time_dialog, null);
        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
        final TimePicker timePicker = (android.widget.TimePicker) view.findViewById(R.id.time_picker);
        builder.setView(view);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);
        timePicker.setIs24HourView(true);
        builder.setPositiveButton(getString(R.string.certain), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish.setVisibility(View.VISIBLE);
                StringBuffer sb = new StringBuffer();
                sb.append(String.format("%04d-%02d-%02d",
                        datePicker.getYear(),
                        datePicker.getMonth() + 1,
                        datePicker.getDayOfMonth()));
                String hour=timePicker.getCurrentHour().toString();
                if(hour.length()==1)
                    hour="0"+hour;
                String min=timePicker.getCurrentMinute().toString();
                if(min.length()==1)
                    min="0"+min;
                String time=sb+" "+hour+":"+min+":00";
                long setTime= StringToDate.TransferDate(time);
                long powerStartTime=StringToDate.StringToLong(startTime);
                if(endTime.equals("0"))
                {
                    if(setTime>=powerStartTime){

                        textView.setTextColor(Color.parseColor("#000000"));
                        textView.setText(time);
                    }
                    else{
                        Toast.makeText(SettingPasswordTime.this, getString(R.string.set_time_error), Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    long powerEndTime = StringToDate.StringToLong(endTime+"000");
                    if(setTime>=powerStartTime&&setTime<=powerEndTime){
                        textView.setTextColor(Color.parseColor("#000000"));
                        textView.setText(time);
                    }
                    else{
                        Toast.makeText(SettingPasswordTime.this, getString(R.string.set_time_error), Toast.LENGTH_SHORT).show();
                    }
                }
                if(!end.getText().toString().equals(getString(R.string.deadline))){
                    long time1;
                    if(start.getText().toString().equals(getString(R.string.from_now_start))) {
                        time1=new Date().getTime();
                    }
                    else{
                        time1=StringToDate.TransferDate(start.getText().toString());
                    }
                    long time2=StringToDate.TransferDate(end.getText().toString());
                    if(time1>time2){
                        Toast.makeText(SettingPasswordTime.this, getString(R.string.endTime_error), Toast.LENGTH_SHORT).show();
                        end.setText(getString(R.string.deadline));
                    }
                    time1=time1+24l*60l*60l*1000l*365l*2l;
                    Log.e("time1",String.valueOf(time1));
                    Log.e("time2",String.valueOf(time2));
                    if(time1<time2){
                        Toast.makeText(SettingPasswordTime.this, "有效时间不能超过两年", Toast.LENGTH_SHORT).show();
                        end.setText(getString(R.string.deadline));
                    }
                }
            }
        });
        if(textView.getId()==start.getId()){
            builder.setNegativeButton(getString(R.string.from_now_start), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    textView.setText(getString(R.string.from_now_start));
                }
            });
        }
        final Dialog dialog = builder.create();
        dialog.show();
    }

    private BluetoothDevice device;
    private GattCallback callback;
    private BluetoothGatt bluetoothGatt;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    protected void onStart(){
        super.onStart();
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    protected void onPause(){
        super.onPause();
        if(bluetoothGatt!=null){
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
            bluetoothGatt=null;
        }
        finish();
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void getBluetooth()
    {
        long rank=new Date().getTime();
     //   final String Password= Compress.GeneratePassword(rank,StringToDate.TransferDate(end.getText().toString()),lockKey,2);
     //   Log.e("password",Password);
        Log.e("==",String.valueOf(StringToDate.TransferDate(end.getText().toString())));
        //检查蓝牙地址
        if (BluetoothAdapter.getDefaultAdapter().checkBluetoothAddress(bluetooth))
        {
            device=BluetoothAdapter.getDefaultAdapter().getRemoteDevice(bluetooth);
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
        }
    }

    GattCallback.Callback gcb=new GattCallback.Callback() {
        @Override
        public void readCallback(String result) {
            if(result.contains("Changed")){
                if(bluetoothGatt!=null)
                {
                    bluetoothGatt.disconnect();
                    bluetoothGatt.close();
                }
                MyProgressDialog.remove();
                handler.removeMessages(0x123);
                Toast.makeText(SettingPasswordTime.this, getString(R.string.setting_succssful), Toast.LENGTH_SHORT).show();
                Intent i=new Intent(SettingPasswordTime.this,Lock_of_Setting.class);
                startActivity(i);
                finish();
            }
            else{
                Toast.makeText(SettingPasswordTime.this, getString(R.string.have_a_error), Toast.LENGTH_SHORT).show();
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void connectCallback() {
            long rank=new Date().getTime();
        //    final String Password= Compress.GeneratePassword(rank,StringToDate.TransferDate(end.getText().toString()),lockKey,2);
            GainLockPW gainLockPW=new GainLockPW(SettingPasswordTime.this,lockKey,"0", String.valueOf(StringToDate.TransferDate(end.getText().toString())/1000),String.valueOf(rank/1000));
            gainLockPW.setCallback(new NetCallback() {
                @Override
                public void execute(final String result) {
                    Log.e("password1",result);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                Thread.sleep(800);
                            }
                            catch (InterruptedException e){
                            }
                            callback.setMessage("(!" + lockKey + ".W)");
                            bluetoothGatt.discoverServices();
                            try{
                                Thread.sleep(800);
                            }
                            catch (InterruptedException ie){
                            }
                            callback.setMessage("("+result+")") ;
                            bluetoothGatt.discoverServices();
                            try{
                                Thread.sleep(800);
                            }
                            catch (InterruptedException e){
                            }
                            callback.setMessage("(L."+password+"Z*)");
                            bluetoothGatt.discoverServices();
                        }
                    }).start();

                }
                @Override
                public void error(String result) {

                }
            });
            new Thread(gainLockPW).start();


        }

        @Override
        public void unConnectCallback() {

        }
    };


    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message message){
            if(message.what==0x123){
                MyProgressDialog.remove();
            }
        }
    };

}
