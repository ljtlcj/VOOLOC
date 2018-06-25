package test.xk_ys_VOOLOC;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import test.xk_ys_VOOLOC.AboutFile.BaseApplication;
import test.xk_ys_VOOLOC.MyView.MyFastMenuBar;

public class Setting extends AppCompatActivity implements View.OnClickListener, MyFastMenuBar.onMenuBarClickListener {
    @BindView(R.id.correct_back)
    public TextView back;
//    @BindView(R.id.correct_password)
//    public LinearLayout correct_password;
    @BindView(R.id.correct_password)
    public MyFastMenuBar correct_password;
//    @BindView(R.id.gesture)
//    public LinearLayout gesture;
    @BindView(R.id.gesture)
    public MyFastMenuBar gesture;
//    @BindView(R.id.version)
//    public LinearLayout version;
    @BindView(R.id.version)
    public MyFastMenuBar version;
//    @BindView(R.id.about)
//    public LinearLayout about;
    @BindView(R.id.about)
    public MyFastMenuBar about;
//    @BindView(R.id.question)
//    public LinearLayout question;
    @BindView(R.id.question)
    public MyFastMenuBar question;
//    @BindView(R.id.logout)
//    public LinearLayout logout;
    @BindView(R.id.logout)
    public MyFastMenuBar logout;
//    @BindView(R.id.net_version)
//    public LinearLayout net;
    @BindView(R.id.net_version)
    public MyFastMenuBar net;
//    @BindView(R.id.language)
//    public LinearLayout language;
    @BindView(R.id.language)
    public MyFastMenuBar language;
    private String account=null;
    private String password;
    private SharedPreferences sharedPreferences;
    int ver=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        account=getIntent().getStringExtra("account");
        password=getIntent().getStringExtra("password");
        sharedPreferences=getSharedPreferences("userInfo",MODE_PRIVATE);
        ver=sharedPreferences.getInt("netVersion",0);
        result=sharedPreferences.getInt("language",2);
        back.setOnClickListener(this);
//        logout.setOnClickListener(this);
        logout.setOnMenuBarClickListener(this);
//        version.setOnClickListener(this);
        version.setOnMenuBarClickListener(this);
//        correct_password.setOnClickListener(this);
        correct_password.setOnMenuBarClickListener(this);
//        gesture.setOnClickListener(this);
        gesture.setOnMenuBarClickListener(this);

//        question.setOnClickListener(this);
        question.setOnMenuBarClickListener(this);
//        about.setOnClickListener(this);
        about.setOnMenuBarClickListener(this);
//        net.setOnClickListener(this);
        net.setOnMenuBarClickListener(this);
//        language.setOnClickListener(this);
        language.setOnMenuBarClickListener(this);
        net.setVisibility(View.GONE);
    }
    int result=-1;
    public void setLanguage() {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.three_edit_text, null);
        builder.setView(view);
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.language);
        switch (result) {
            case 0:
                radioGroup.check(R.id.wait_system);
                break;
            case 1:
                radioGroup.check(R.id.china);
                break;
            case 2:
                radioGroup.check(R.id.English);
                break;
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                    switch (checkedId) {
                        case R.id.wait_system:
                            result = 0;
                            break;
                        case R.id.china:
                            result = 1;
                            break;
                        case R.id.English:
                            result = 2;
                            break;
                    }
                }
            });
            builder.setPositiveButton(R.string.certain, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    editor.putInt("language", result);
                    editor.commit();
                    BaseApplication.languageCode=result;
                    add_lock.activity.finish();
                    Intent i=new Intent(Setting.this,add_lock.class);
                    startActivity(i);
                    finish();
                }
            });
            AlertDialog dialog=builder.create();
            dialog.show();
    }


    public void onMenuBarClick(MyFastMenuBar v) {
        final Intent i=new Intent();
        switch (v.getId()) {
            case R.id.language:
                setLanguage();
                break;
            case R.id.logout:
                i.setClass(this,login.class);
                i.putExtra("back",true);
                startActivity(i);
                if(add_lock.activity!=null)
                    add_lock.activity.finish();
                finish();
                break;
            case R.id.question:
            case R.id.about:
            case R.id.version:
                break;
            case R.id.correct_password:
                i.setClass(this,verify_registration.class);
                i.putExtra("state",3);
                i.putExtra("account",account);
                i.putExtra("password",password);
                startActivity(i);
                break;
            case R.id.gesture:
                i.setClass(this,GestureSetting.class);
                startActivity(i);
                break;
            case R.id.net_version:
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle("系统提示");
                if(ver%2==0){
                    builder.setMessage("是否设置为广工服务器版本?");
                }
                else{
                    builder.setMessage("是否设置为嘉应服务器版本");
                }
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putInt("netVersion",(ver+1)%2);
                        editor.commit();
                        i.setClass(Setting.this,login.class);
                        startActivity(i);
                        if(add_lock.activity!=null)
                            add_lock.activity.finish();
                        finish();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog alertDialog=builder.create();
                alertDialog.show();
                break;
            default:
                /* ???? */
        }
    }


    public void onClick(View v){
        final Intent i=new Intent();
        switch (v.getId()){
//            case R.id.language:
//                setLanguage();
//                break;
//            case R.id.logout:
//                i.setClass(this,login.class);
//                i.putExtra("back",true);
//                startActivity(i);
//                if(add_lock.activity!=null)
//                    add_lock.activity.finish();
//                finish();
//                break;
//            case R.id.question:
//            case R.id.about:
//            case R.id.version:
//                break;
//            case R.id.correct_password:
//                if(BaseApplication.getLanguage().equals("zh")){
//                    i.setClass(this,verify_registration.class);
//                }
//                else{
//                    i.setClass(this,verify_registration.class);
//                }
//                i.putExtra("state",3);
//                i.putExtra("account",account);
//                i.putExtra("password",password);
//                startActivity(i);
//                break;
//            case R.id.gesture:
//                i.setClass(this,GestureSetting.class);
//                startActivity(i);
//                break;
            case R.id.correct_back:
                finish();
                break;
//            case R.id.net_version:
//                AlertDialog.Builder builder=new AlertDialog.Builder(this);
//                builder.setTitle("系统提示");
//                if(ver%2==0){
//                    builder.setMessage("是否设置为广工服务器版本?");
//                }
//                else{
//                    builder.setMessage("是否设置为嘉应服务器版本");
//                }
//                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        SharedPreferences.Editor editor=sharedPreferences.edit();
//                        editor.putInt("netVersion",(ver+1)%2);
//                        editor.commit();
//                        i.setClass(Setting.this,login.class);
//                        startActivity(i);
//                        if(add_lock.activity!=null)
//                            add_lock.activity.finish();
//                        finish();
//                    }
//                });
//                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                    }
//                });
//                AlertDialog alertDialog=builder.create();
//                alertDialog.show();
//                break;
            default:
                /* ???? */

        }

    }
}
