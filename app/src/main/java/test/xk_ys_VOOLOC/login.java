package test.xk_ys_VOOLOC;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import test.xk_ys_VOOLOC.AboutFile.BaseApplication;
import test.xk_ys_VOOLOC.AboutFile.Variate;
import test.xk_ys_VOOLOC.AboutFile.isEmpty;
import test.xk_ys_VOOLOC.MyView.MyProgressDialog;
import test.xk_ys_VOOLOC.Net.NetCallback;
import test.xk_ys_VOOLOC.Net.VerifyAccount;

public class login extends AppCompatActivity {
    @BindView(R.id.login_phone)
    public EditText phone;
    @BindView(R.id.login_next)
    public Button next;
    @BindView(R.id.nation)
    public TextView nation;
    @BindView(R.id.textView3)
    public TextView textView3;
    static public Activity activity;

    public String p="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        activity=this;
        int version=getSharedPreferences("userInfo",MODE_PRIVATE).getInt("netVersion",0);
        BaseApplication.netVersion=version;
        Log.e("netVersion",String.valueOf(version));
        Log.e("BnetVersion",String.valueOf(BaseApplication.netVersion));
        boolean back=getIntent().getBooleanExtra("back",false);
        if(getSharedPreferences("userInfo",MODE_PRIVATE).getBoolean("gesture",false)&&!back){
            Intent i=new Intent(this,GesturePW1.class);
            startActivity(i);
        }
        int lan=getSharedPreferences("userInfo",MODE_PRIVATE).getInt("language",0);
        String language;
        if(lan==0){
            Locale locale = Locale.getDefault();
            language = locale.getLanguage();
        }
        else if(lan==1){
            language="zh";
        }
        else{
            language="cn";
        }
        if(language.equals("zh"))
        {
            nation.setText("CN > +86");
            phone.setInputType(InputType.TYPE_CLASS_PHONE);
            phone.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(phone.length()==11){
                        p=phone.getText().toString();
                    }
                    if(phone.length()>11){
                        phone.setText(p);
                    }
                }
            });
            //控件监听
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String account=phone.getText().toString();
                    if(!isEmpty.StringIsEmpty(account)) {
                        MyProgressDialog.show(login.this, "loading...", true, null);
                        final VerifyAccount verifyAccount = new VerifyAccount(login.this,account);
                        verifyAccount.setCallback(new NetCallback() {
                            @Override
                            public void execute(String result) {
                                Intent i=new Intent(login.this,input_password.class);
                                i.putExtra("account",account);
                                i.putExtra("state",1);//1为登录，2为注册
                                startActivity(i);

                                MyProgressDialog.remove();
                                finish();
                            }

                            @Override
                            public void error(String result) {
                                Intent i=new Intent(login.this,verify_registration.class);
                                i.putExtra("account",account);
                                i.putExtra("state",1);//1为注册，2为修改密码
                                startActivity(i);
                                MyProgressDialog.remove();
                                finish();
                            }
                        });
                        new Thread(verifyAccount).start();
                    }
                    else{
                        Toast.makeText(login.this, getResources().getString(R.string.no_write_phone), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else {
            nation.setText("E-Mail");
            phone.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String account=phone.getText().toString();
                    if(!isEmpty.StringIsEmpty(account)&&account.contains("@")) {
                        MyProgressDialog.show(login.this, "loading...", true, null);
                        final VerifyAccount verifyAccount = new VerifyAccount(login.this,account);
                        verifyAccount.setCallback(new NetCallback() {
                            @Override
                            public void execute(String result) {
                                Intent i=new Intent(login.this,input_password.class);
                                i.putExtra("account",account);
                                i.putExtra("state",1);//1为登录，2为注册
                                startActivity(i);
                                MyProgressDialog.remove();
                                finish();
                            }

                            @Override
                            public void error(String result) {
                                Intent i=new Intent(login.this,verify_registration.class);
                                i.putExtra("account",account);
                                i.putExtra("state",1);
                                startActivity(i);
                                MyProgressDialog.remove();
                                finish();
                            }
                        });
                        new Thread(verifyAccount).start();
                    }
                    else if(account!=null&&!account.contains("@"))
                    {
                        Toast.makeText(login.this,"uncontain char '@'",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(login.this, getResources().getString(R.string.no_write_phone), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }




        SharedPreferences sharedPreferences=getSharedPreferences("UserInformation",MODE_PRIVATE);
        phone.setText(sharedPreferences.getString("account",""));

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Variate.setState){
            Variate.setState=false;
            finish();}
    }
}
