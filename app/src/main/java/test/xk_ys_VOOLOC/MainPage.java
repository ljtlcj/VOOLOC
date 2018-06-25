package test.xk_ys_VOOLOC;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import test.xk_ys_VOOLOC.AboutFile.BaseApplication;

public class MainPage extends Activity {
    public Context context;
    private Handler handler=new Handler();
    private TimerTask tast;
    private LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this.context;
        SharedPreferences sharedPreferences=getSharedPreferences("userInfo",MODE_PRIVATE);
        int lan=sharedPreferences.getInt("language",0);
        BaseApplication.languageCode=lan;
        Log.e("language",String.valueOf(lan));
        if(lan==0){
            initAppLanguage(context);
        }
        else if(lan==1){
            showLanguage("zh");
        }
        else{
            showLanguage("cn");
        }
        setContentView(R.layout.activity_main_page);
        linearLayout=(LinearLayout)findViewById(R.id.image);
        tast=new TimerTask()
        {
            @Override
            public void run(){
                Intent i=new Intent(MainPage.this,legal_provisions.class);
                startActivity(i);
                finish();
            }
        };

        final Timer timer=new Timer();
        final TextView tv=(TextView)findViewById(R.id.textView);
        TimerTask tast1=new TimerTask(){
            @Override
            public void run(){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        tv.setText(getResources().getString(R.string.jump_after_one_second));
                    }
                });
            }
        };

        timer.schedule(tast,2000);
        timer.schedule(tast1,1000);

        Locale locale = Locale.getDefault();
        String language = locale.getLanguage();
        BaseApplication.setLanguage(language);

    }
    public void onDestroy(){
        super.onDestroy();
        tast.cancel();
        handler=null;
    }
    public static void initAppLanguage(Context context) {
        if (context == null) {
            return;
        }
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        // 应用用户选择语言
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.getLocales().get(0);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            context.createConfigurationContext(config);
        }

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


        Log.e("-----------",BaseApplication.language);
        resources.updateConfiguration(config, dm);
    }



}
