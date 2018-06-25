package test.xk_ys_VOOLOC;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import test.xk_ys_VOOLOC.AboutFile.LockInfo;
import test.xk_ys_VOOLOC.AboutFile.StringToDate;
import test.xk_ys_VOOLOC.Net.AddManager;
import test.xk_ys_VOOLOC.Net.AddSendPasswordLog;
import test.xk_ys_VOOLOC.Net.GainLockList1;
import test.xk_ys_VOOLOC.Net.GainLockPW;
import test.xk_ys_VOOLOC.Net.NetCallback;

public class Setting_time1 extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.self) public TextView self;
    @BindView(R.id.one_day) public TextView one_day;
    @BindView(R.id.one_week) public TextView one_week;
    @BindView(R.id.one_month) public TextView one_month;
    @BindView(R.id.one_year) public TextView one_year;
    @BindView(R.id.start_time) public TextView start;
    @BindView(R.id.end_time) public TextView end;
    @BindView(R.id.next) public TextView send;
    @BindView(R.id.back) public TextView back;
    private TextView mytextView;
    private LockInfo lockInfo;
    private int order;
    private String account;
    private String name;
    private int type=2;
    private String user;
    private String id;
    private String password;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_time);
        ButterKnife.bind(this);

        SharedPreferences info=getSharedPreferences("UserInformation",MODE_PRIVATE);
        user=info.getString("account",null);
        password=info.getString("password",null);
        id=info.getString("id",null);
        if(user==null||password==null||id==null){
            Toast.makeText(this, getString(R.string.read_error), Toast.LENGTH_SHORT).show();
            finish();
        }
        mytextView = self;
        self.setOnClickListener(this);
        one_day.setOnClickListener(this);
        one_week.setOnClickListener(this);
        one_month.setOnClickListener(this);
        one_year.setOnClickListener(this);
        Intent i=getIntent();
        order=i.getIntExtra("order",0);
        lockInfo=i.getParcelableExtra("lockInfo");
        account=i.getStringExtra("account");
        name=i.getStringExtra("name");
        type=i.getIntExtra("type",2);
        if(lockInfo==null||account==null||account.equals(""))
        {
            finish();
        }
        start.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showDate(start);
                return false;
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        end.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showDate(end);
                return false;
            }
        });
        if(order==1){
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!end.getText().toString().equals(getString(R.string.deadline))) {
                        if(start.getText().toString().equals(getString(R.string.from_now_start))){
                        //    String password = Compress.GeneratePassword(StringToDate.TransferDate(end.getText().toString()), 0, lockInfo.getLockKey(), 0);
                            GainLockPW gainLockPW=new GainLockPW(Setting_time1.this,lockInfo.getLockKey(),"0",String.valueOf(StringToDate.TransferDate(end.getText().toString())/1000),"0");
                            gainLockPW.setCallback(new NetCallback() {
                                @Override
                                public void execute(String result) {
                                    Log.e("Password2",result);
                                    String password=result;
                                    AddSendPasswordLog addSendPasswordLog=new AddSendPasswordLog(Setting_time1.this,id,account,lockInfo.getLockId_2(),name,"1",password);
                                    new Thread(addSendPasswordLog).start();
                                    String str="欢迎使用物勒智能门锁，您的开锁密码为：" + password.substring(0, 4) + "-" + password.substring(4, 8) + "-" + password.substring(8) + "门锁名称为:" + lockInfo.getLockName() + ",有效时间至" + end.getText() + "。输入密码后按 # 号键即可开门";
                                    if(account.equals("WeChat")){
                                        Intent intent=new Intent(Intent.ACTION_SEND);
                                        intent.setType("text/plain");
                                        intent.setPackage("com.tencent.mm");//intent.setPackage("com.sina.weibo");
                                        intent.putExtra(Intent.EXTRA_TEXT, str);
                                        startActivity(Intent.createChooser(intent, "请选择"));
                                    }
                                    else {
                                        Intent sendIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + account));
                                        sendIntent.putExtra("sms_body",str );
                                        startActivity(sendIntent);
                                    }

                                }
                                @Override
                                public void error(String result) {
                                    Toast.makeText(Setting_time1.this, "从服务器获取密码失败，请重新尝试！", Toast.LENGTH_SHORT).show();

                                }
                            });
                            new Thread(gainLockPW).start();

                        }
                        else {
                        //    String password = Compress.GeneratePassword(StringToDate.TransferDate(start.getText().toString()), StringToDate.TransferDate(end.getText().toString()), lockInfo.getLockKey(), 2);
                        //    Log.e("password",password);
                            GainLockPW gainLockPW=new GainLockPW(Setting_time1.this,lockInfo.getLockKey(),"2",String.valueOf(StringToDate.TransferDate(start.getText().toString())/1000), String.valueOf(StringToDate.TransferDate(end.getText().toString())/1000));

                            gainLockPW.setCallback(new NetCallback() {
                                @Override
                                public void execute(String result) {
                                    Log.e("password",result);
                                    String password=result;
                                    AddSendPasswordLog addSendPasswordLog=new AddSendPasswordLog(Setting_time1.this,id,account,lockInfo.getLockId_2(),name,"3",password);
                                    new Thread(addSendPasswordLog).start();
                                    String str= "欢迎使用物勒智能门锁，您的开锁密码为：" + password.substring(0, 4) + "-" + password.substring(4, 8) + "-" + password.substring(8, 11) + password.substring(11) + "门锁名称为:" + lockInfo.getLockName() + ",有效时间为：" + start.getText() + "~" + end.getText() + "。输入密码后按 # 号键即可开门";
                                    if(account.equals("WeChat")){
                                        Intent intent=new Intent(Intent.ACTION_SEND);
                                        intent.setType("text/plain");
                                        intent.setPackage("com.tencent.mm");//intent.setPackage("com.sina.weibo");
                                        intent.putExtra(Intent.EXTRA_TEXT, str);
                                        startActivity(Intent.createChooser(intent, "请选择"));
                                    }
                                    else {
                                        Intent sendIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + account));
                                        sendIntent.putExtra("sms_body",str);
                                        startActivity(sendIntent);
                                    }
                                }
                                @Override
                                public void error(String result) {
                                    Toast.makeText(Setting_time1.this, "从服务器获取密码失败，请重新尝试！", Toast.LENGTH_SHORT).show();

                                }
                            });
                            new Thread(gainLockPW).start();

                        }
                    }
                    else{
                        Toast.makeText(Setting_time1.this, getString(R.string.unset_time), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else if(order==2){
            if(type==1){
                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        long startTime;
                        if(start.getText().toString().equals(getString(R.string.from_now_on))){
                            startTime=new Date().getTime();
                        }
                        else {
                            startTime=StringToDate.TransferDate(start.getText().toString());
                        }
                        long endTime=StringToDate.TransferDate(end.getText().toString());
                        AddManager addManager=new AddManager(Setting_time1.this,user,password,lockInfo.getLockId(),account.replace(" ",""),String.valueOf(startTime),String.valueOf(endTime),"2",name);
                        NetCallback callback=new NetCallback() {
                            @Override
                            public void execute(String result) {
                                Intent i = new Intent(Setting_time1.this, Select_Lock.class);
                                startActivity(i);
                            }

                            @Override
                            public void error(String result) {

                            }
                        };
                        addManager.setCallback(callback);
                        new Thread(addManager).start();
                    }
                });
            }
            else if(type==2){
                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        long startTime;
                        if(start.getText().toString().equals(getString(R.string.from_now_on))){
                            startTime=new Date().getTime();
                        }
                        else {
                            startTime=StringToDate.TransferDate(start.getText().toString());
                        }
                        long endTime=StringToDate.TransferDate(end.getText().toString());
                        AddManager addManager=new AddManager(Setting_time1.this,user,password,lockInfo.getLockId(),account.replace(" ",""),String.valueOf(startTime),String.valueOf(endTime),"3",name);
                        NetCallback callback=new NetCallback() {
                            @Override
                            public void execute(String result) {
                                Intent i = new Intent(Setting_time1.this, Select_Lock.class);
                                startActivity(i);
                            }

                            @Override
                            public void error(String result) {

                            }
                        };
                        addManager.setCallback(callback);
                        new Thread(addManager).start();
                    }
                });
            }
            else{
                finish();
            }
        }
        else{
            finish();
        }

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
                long powerStartTime=StringToDate.StringToLong(lockInfo.getEndTime());
                if(lockInfo.getEndTime().equals("0"))
                {
                    if(setTime>=powerStartTime){
                        textView.setTextColor(Color.parseColor("#000000"));
                        textView.setText(time);
                    }
                    else{
                        Toast.makeText(Setting_time1.this, getString(R.string.set_time_error), Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    long powerEndTime =Long.valueOf(lockInfo.getEndTime()+"000");
                    if(setTime>=powerStartTime&&setTime<=powerEndTime){
                        textView.setTextColor(Color.parseColor("#000000"));
                        textView.setText(time);
                    }
                    else{
                        Toast.makeText(Setting_time1.this, getString(R.string.set_time_error), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(Setting_time1.this, getString(R.string.endTime_error), Toast.LENGTH_SHORT).show();
                        end.setText(getString(R.string.deadline));
                    }
                    time1=time1+24l*60l*60l*1000l*365l*2l;
                    Log.e("time1",String.valueOf(time1));
                    Log.e("time2",String.valueOf(time2));
                    if(time1<time2){
                        Toast.makeText(Setting_time1.this, "有效时间不能超过两年", Toast.LENGTH_SHORT).show();
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
    @Override
    public void onClick(View v){
        long day=24*60*60*1000;
        long powerEnd=Long.valueOf(lockInfo.getEndTime()+"000");
        switch (v.getId()){
            case R.id.self:
                mytextView.setTextColor(Color.parseColor("#333333"));
                self.setTextColor(Color.RED);
                start.setText("从现在开始");
                end.setText("截止时间");
                mytextView = self;
                break;
            case R.id.one_day:
                mytextView.setTextColor(Color.parseColor("#333333"));
                one_day.setTextColor(Color.RED);
                Log.e("-----",String.valueOf(powerEnd));
                Log.e("-----",String.valueOf(StringToDate.TransferDate(times(new Date().getTime()+day))));
                if(powerEnd==0||powerEnd>=StringToDate.TransferDate(times(new Date().getTime()+day))){
                    start.setText(times(new Date().getTime()));
                    end.setText(times(new Date().getTime()+day));
                }
                else{
                    Toast.makeText(this, "该时间超过权限范围", Toast.LENGTH_SHORT).show();
                }
                mytextView = one_day;
                break;
            case R.id.one_week:
                mytextView.setTextColor(Color.parseColor("#333333"));
                one_week.setTextColor(Color.RED);
                if(powerEnd==0||powerEnd>=StringToDate.TransferDate(times(new Date().getTime()+7*day))){
                    start.setText(times(new Date().getTime()));
                    end.setText(times(new Date().getTime()+7*day));
                }
                else{
                    Toast.makeText(this, "该时间超过权限范围", Toast.LENGTH_SHORT).show();
                }
                mytextView = one_week;
                break;
            case R.id.one_month:
                mytextView.setTextColor(Color.parseColor("#333333"));
                one_month.setTextColor(Color.RED);
                if(powerEnd==0||powerEnd>=StringToDate.TransferDate(times(new Date().getTime()+30*day))){
                    start.setText(times(new Date().getTime()));
                    end.setText(times(new Date().getTime()+30*day));
                }
                else{
                    Toast.makeText(this, "该时间超过权限范围", Toast.LENGTH_SHORT).show();
                }
                mytextView = one_month;
                break;
            case R.id.one_year:
                mytextView.setTextColor(Color.parseColor("#333333"));
                one_year.setTextColor(Color.RED);
                if(powerEnd==0||powerEnd>=StringToDate.TransferDate(times(new Date().getTime()+365*day))){
                    start.setText(times(new Date().getTime()));
                    end.setText(times(new Date().getTime()+365*day)); }
                else{
                    Toast.makeText(this, "该时间超过权限范围", Toast.LENGTH_SHORT).show();
                }
                mytextView = one_year;
        }
    }
    public static String times(long time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        @SuppressWarnings("unused")
        //   int i = Integer.parseInt(time);
                String times = sdr.format(time);
        return times;
    }

    @Override
    protected void onResume() {
        super.onResume();
        GainLockList1 gainLockList=new GainLockList1(this,user,password);
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
                        Toast.makeText(Setting_time1.this, "门锁不存在", Toast.LENGTH_SHORT).show();
                        Intent i=new Intent(Setting_time1.this, add_lock.class);
                        startActivity(i);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void error(String result) {
                Toast.makeText(Setting_time1.this, "门锁不存在", Toast.LENGTH_SHORT).show();
                Intent i=new Intent(Setting_time1.this, add_lock.class);
                startActivity(i);
                finish();
            }
        });
        new Thread(gainLockList).start();
    }
    protected void onRestart(){
        super.onRestart();
        Intent i=new Intent(Setting_time1.this, Select_Lock.class);
        startActivity(i);
        finish();
    }
}
