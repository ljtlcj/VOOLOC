package test.xk_ys_VOOLOC;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import test.xk_ys_VOOLOC.AboutFile.BaseApplication;
import test.xk_ys_VOOLOC.MyView.RefreshableView;
import test.xk_ys_VOOLOC.Net.AddLock;
import test.xk_ys_VOOLOC.Net.DeleteLock;
import test.xk_ys_VOOLOC.Net.GainLockList;
import test.xk_ys_VOOLOC.Net.NetCallback;
import test.xk_ys_VOOLOC.Net.SettingResetSecret;

public class add_lock extends AppCompatActivity {
    static public Activity activity;
    @BindView(R.id.setting)
    public TextView setting;
    @BindView(R.id.add_lock)
    public RelativeLayout addLock;
    @BindView(R.id.lock_list)
    public ListView lockList;
    @BindView(R.id.refreshable_view)
    public RefreshableView refreshableView;
    private String account;
    private String password;
    private GainLockList gainLockList;
    private BluetoothAdapter bluetooth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLan();
        setContentView(R.layout.activity_add_lock);
        activity=this;
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        if(!openBluetooth()) {
            Toast.makeText(this, "Bluetooth was not open！", Toast.LENGTH_SHORT).show();
            finish();
        }
        getInfo();
        initialize();

    }

    public void judge(){
        final SharedPreferences sharedPreferences=getSharedPreferences(account,MODE_PRIVATE);
        boolean upload=sharedPreferences.getBoolean("upload",false);
        boolean delete=sharedPreferences.getBoolean("delete",false);
        if(upload){
            Log.e("upload","--------------------");
            String p1=sharedPreferences.getString("lockKey","null");
            String p2=sharedPreferences.getString("name","null");
            String p3=sharedPreferences.getString("address","null");
            final String secret=sharedPreferences.getString("secret","null");
            AddLock addLock=new AddLock(this,account,password,p1,p2,p3);
            addLock.setCallback(new NetCallback() {
                @Override
                public void execute(String result) {
                    new Thread(gainLockList).start();
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putBoolean("upload",false);
                    editor.commit();
                    try {
                        JSONObject jsonObject=new JSONObject(result);
                        SettingResetSecret settingResetSecret = new SettingResetSecret(add_lock.this, jsonObject.getString("lockid"), secret);
                        new Thread(settingResetSecret).start();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void error(String result) {

                }
            });
            new Thread(addLock).start();
        }
        if(delete){
            Log.e("delete","--------------------");
            String p1=sharedPreferences.getString("account","null");
            String p2=sharedPreferences.getString("password","null");
            String p3=sharedPreferences.getString("lockId","null");
            DeleteLock deleteLock=new DeleteLock(add_lock.this,p1,p2,p3);
            deleteLock.setCallback(new NetCallback() {
                @Override
                public void execute(String result) {
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putBoolean("delete",false);
                    editor.commit();
                    new Thread(gainLockList).start();

                }

                @Override
                public void error(String result) {

                }
            });
            new Thread(deleteLock).start();

        }
    }


    private boolean openBluetooth()
    {
        //获取手机蓝牙适配器
        bluetooth = BluetoothAdapter.getDefaultAdapter();
        //检查是否有蓝牙驱动
        if(bluetooth==null) {
            Toast.makeText(getApplicationContext(), "No bluetooth Adapter!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!bluetooth.isEnabled())
        {
            //询问打开蓝牙
            Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(mIntent, 1);
            // mBluetoothAdapter.disable();//关闭蓝牙
            //bluetooth.enable();  //无声打开蓝牙
            return true;
        }
        return bluetooth.isEnabled();
    }
    public void getInfo(){
        SharedPreferences info=getSharedPreferences("UserInformation",MODE_PRIVATE);
        account=info.getString("account",null);
        password=info.getString("password",null);
        if(account==null||password==null){
            Toast.makeText(this, getString(R.string.read_error), Toast.LENGTH_SHORT).show();
            Intent i=new Intent(this,login.class);
            startActivity(i);
            finish();
        }
    }

    public void initialize(){
        ButterKnife.bind(this);
        //系统设置
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(add_lock.this,Setting.class);
                i.putExtra("account",account);
                i.putExtra("password",password);
                startActivity(i);
            }
        });
        //添加门锁
        addLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(add_lock.this,search_door_lock2.class);
                startActivity(i);
            }
        });

        addLock.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    v.setBackgroundColor(Color.parseColor("#602196F3"));
                }else if (event.getAction() == MotionEvent.ACTION_MOVE){
                    v.setBackgroundColor(Color.parseColor("#602196F3"));
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    v.setBackgroundColor(Color.parseColor("#2196F3"));
                }
                return false;
            }
        });

        gainLockList=new GainLockList(add_lock.this,account,password,lockList);
        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    new Thread(gainLockList).start();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                refreshableView.finishRefreshing();
            }
        }, 0);
        new Thread(gainLockList).start();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {

        if (keyCode == KeyEvent.KEYCODE_BACK )
        {
            exit();
        }
        return false;

    }
    public void exit(){
        // 创建退出对话框
        AlertDialog isExit = new AlertDialog.Builder(this).create();
        // 设置对话框标题
        isExit.setTitle("系统提示");
        // 设置对话框消息
        isExit.setMessage("确定要退出吗");
        // 添加选择按钮并注册监听
        isExit.setButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        isExit.setButton2("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        // 显示对话框
        isExit.show();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getInfo();
        new Thread(gainLockList).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        judge();
    }
    public void setLan(){
        int lan= BaseApplication.languageCode;
        Log.e("language",String.valueOf(lan));
        if(lan==0){
            initAppLanguage(this);
        }
        else if(lan==1){
            showLanguage("zh");
        }
        else{
            showLanguage("cn");
        }
    }
    public  void initAppLanguage(Context context) {
        Locale locale = Locale.getDefault();
        String language = locale.getLanguage();
        showLanguage(language);

    }
    public void showLanguage(String language) {
        //设置应用语言类型
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        if (language.equals("zh")) {
            config.locale = Locale.SIMPLIFIED_CHINESE;
            BaseApplication.language="zh";
        } else {
            config.locale = Locale.ENGLISH;
            BaseApplication.language="en";
        }
        resources.updateConfiguration(config, dm);
    }
}
