package test.xk_ys_VOOLOC;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import test.xk_ys_VOOLOC.AboutFile.isEmpty;
import test.xk_ys_VOOLOC.Net.AddManager;
import test.xk_ys_VOOLOC.Net.NetCallback;

public class Sent_PW extends AppCompatActivity implements View.OnClickListener {
    private String lockKey;
    private long startTime;
    private long endTime;
    private String phone;
    private String lockId;
    private int state=0;
    private boolean sendState=false;

    private TextView once;
    private TextView times;
    private TextView back;
    private TextView send;
    private int order;
    private String account;
    private String name;
    private String password;
    private static final String TAG = "Sent_PW";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent__pw);
        getInfo();
        initialize();
    }
    public void getInfo(){
        Intent i=getIntent();
        lockKey=i.getStringExtra("lockKey");
        startTime=i.getLongExtra("startTime",0);
        endTime=i.getLongExtra("endTime",0);
        phone=i.getStringExtra("phone");
        lockId=i.getStringExtra("lockId");
        name=i.getStringExtra("name");
        order=i.getIntExtra("order",0);
        if(order==0||isEmpty.StringIsEmpty(lockKey)||isEmpty.StringIsEmpty(phone)||endTime==0){
            Toast.makeText(this, getString(R.string.lock_info_error), Toast.LENGTH_SHORT).show();
            finish();
        }
        Log.e("start",Long.toString(startTime));
        Log.e("end",Long.toString(endTime));
        Log.e("lockKey",lockKey);
        SharedPreferences info=getSharedPreferences("UserInformation",MODE_PRIVATE);
        account=info.getString("account",null);
        password=info.getString("password",null);
        Log.e(TAG, "getInfo: "+password+account );
        if(account==null||password==null){
            Toast.makeText(this, getString(R.string.read_error), Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    public void initialize(){
        back=(TextView)findViewById(R.id.back);
        once=(TextView)findViewById(R.id.once);
        times=(TextView)findViewById(R.id.times);
        send=(TextView)findViewById(R.id.send);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if(order==1) {
            times.setOnClickListener(this);
            once.setOnClickListener(this);
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (state == 0) {
                        startTime = endTime;
                    } else if (state == 1) {
                        endTime = 0;
                    }
                    sendState = true;
            //        String password = Compress.GeneratePassword(startTime, endTime, lockKey, state);
                    Intent sendIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phone));
                    if (state == 1) {
                        sendIntent.putExtra("sms_body", "尊敬的用户，你的开锁密码为：" + password + ",2小时后失效。");
                    } else {
                        sendIntent.putExtra("sms_body", "尊敬的用户，你的开锁密码为：" + password + ",失效时间为：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(endTime)));
                    }
                    startActivity(sendIntent);

                }
            });
        }
        else if(order==2){
            times.setOnClickListener(this);
            once.setOnClickListener(this);
            once.setText(getString(R.string.popular));
            times.setText(getString(R.string.advanced));
            send.setText(getString(R.string.giver));
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(state==0||state==1){
                    //    AddManager addManager=new AddManager(Sent_PW.this,account,password,phone,lockKey,String.valueOf(endTime),String.valueOf(startTime),"3",name);
                        AddManager addManager=new AddManager(Sent_PW.this,account,password,lockId,phone,String.valueOf(startTime),String.valueOf(endTime),"3",name);
                        NetCallback callback=new NetCallback() {
                            @Override
                            public void execute(String result) {
                                Intent i = new Intent(Sent_PW.this, Select_Lock.class);
                                startActivity(i);
                            }

                            @Override
                            public void error(String result) {

                            }
                        };
                        addManager.setCallback(callback);
                        new Thread(addManager).start();
                    }
                    else{
                        AddManager addManager=new AddManager(Sent_PW.this,account,password,lockId,phone,String.valueOf(startTime),String.valueOf(endTime),"2",name);
                        NetCallback callback=new NetCallback() {
                            @Override
                            public void execute(String result) {
                                Intent i = new Intent(Sent_PW.this, Select_Lock.class);
                                startActivity(i);
                            }

                            @Override
                            public void error(String result) {

                            }
                        };
                        addManager.setCallback(callback);
                        new Thread(addManager).start();
                    }
                }
            });

        }
        else if(order==3){
            once.setOnClickListener(this);
            times.setVisibility(View.INVISIBLE);
            send.setText(getString(R.string.giver));
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddManager addManager=new AddManager(Sent_PW.this,account,password,lockId,phone,String.valueOf(startTime),String.valueOf(endTime),"3",name);
                        NetCallback callback=new NetCallback() {
                            @Override
                            public void execute(String result) {
                                Intent i = new Intent(Sent_PW.this, Select_Lock.class);
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
            Toast.makeText(this, getString(R.string.lock_info_error), Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    @Override
    public void onClick(View v){
        times.setTextColor(getResources().getColor(R.color.Text_color));
        once.setTextColor(getResources().getColor(R.color.Text_color));
        TextView textView=(TextView)v;
        textView.setTextColor(Color.RED);
        if(v.getId()==R.id.once){
            state=1;
        }
        else {
            state=2;
        }
        Log.e("state",Integer.toString(state));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(sendState) {
            Intent i = new Intent(Sent_PW.this, Select_Lock.class);
            startActivity(i);
        }
    }
}
