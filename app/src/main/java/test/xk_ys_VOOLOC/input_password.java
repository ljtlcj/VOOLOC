package test.xk_ys_VOOLOC;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import test.xk_ys_VOOLOC.AboutFile.isEmpty;
import test.xk_ys_VOOLOC.MyView.MyProgressDialog;
import test.xk_ys_VOOLOC.Net.ChangePassword;
import test.xk_ys_VOOLOC.Net.ForgetPassword;
import test.xk_ys_VOOLOC.Net.LoginAccount;
import test.xk_ys_VOOLOC.Net.NetCallback;
import test.xk_ys_VOOLOC.Net.RegisterAccount;

public class input_password extends AppCompatActivity {
    @BindView(R.id.input_password)
    public EditText password;
    @BindView(R.id.input_account)
    public EditText account;
    @BindView(R.id.input_back)
    public TextView back;
    @BindView(R.id.input_forget_password)
    public TextView forgetPW;
    @BindView(R.id.login)
    public Button login;
    @BindView(R.id.nation)
    public TextView nation;

    private String sAccount;
    private String sPassword;
    //state 为状态符，1正常登录，2注册，3修改密码,4为忘记密码
    private int state;
    private String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_input_password);
        ButterKnife.bind(this);
        getInformation();
        initialize();
    }
    //获取信息
    public void getInformation(){
        Intent information=getIntent();
        sAccount=information.getStringExtra("account");
        state=information.getIntExtra("state",0);
        if(state==4){
            code=information.getStringExtra("code");
        }
    }
    //控件初始化
    public void initialize(){

        SharedPreferences sharedPreferences=getSharedPreferences("UserInformation",MODE_PRIVATE);
        sPassword=sharedPreferences.getString("password","");
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
        }
        else{
            nation.setText("E-Mail");
        }
        password.setText(sharedPreferences.getString("password",""));
        account.setEnabled(false);
        forgetPW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i;
                i = new Intent(input_password.this, verify_registration.class);
                i.putExtra("state",3);//state=2为验证修改密码
                i.putExtra("password",sPassword);
                i.putExtra("account",sAccount);
                startActivity(i);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(input_password.this,login.class);
                i.putExtra("back",true);
                startActivity(i);
                finish();
            }
        });
        account.setText(sAccount);
        if(state==1){//登录
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isEmpty.StringIsEmpty(password.getText().toString()))
                    {
                        Toast.makeText(input_password.this, getResources().getString(R.string.password_is_empty), Toast.LENGTH_SHORT).show();
                    }
                    else {
                        MyProgressDialog.show(input_password.this, "loading...", true, null);
                        LoginAccount loginAccount = new LoginAccount(input_password.this, sAccount, password.getText().toString());
                        loginAccount.setCallback(callback);
                        new Thread(loginAccount).start();
                    }
                }
            });
        }
        else if(state==2){//注册
            login.setText(getString(R.string.register));
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isEmpty.StringIsEmpty(password.getText().toString()))
                    {
                        Toast.makeText(input_password.this, getResources().getString(R.string.password_is_empty), Toast.LENGTH_SHORT).show();
                    }
                    else {

                        MyProgressDialog.show(input_password.this, "loading...", true, null);
                        RegisterAccount registerAccount=new RegisterAccount(input_password.this,"<null>",sAccount,password.getText().toString());
                        registerAccount.setCallback(callback);
                        new Thread(registerAccount).start();
                    }
                }
            });
        }
        else if(state==3){//修改密码
            login.setText(getString(R.string.correct_password));
            forgetPW.setVisibility(View.INVISIBLE);
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChangePassword changePassword=new ChangePassword(input_password.this,sAccount,sPassword,password.getText().toString(),"2");
                    changePassword.setCallback(new NetCallback() {
                        @Override
                        public void execute(String result) {
                            SharedPreferences sharedPreferences=getSharedPreferences("UserInformation",MODE_PRIVATE);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString("account",sAccount);
                            editor.putString("password",password.getText().toString());
                            editor.commit();
                            Intent i=new Intent(input_password.this,add_lock.class);
                            startActivity(i);
                            finish();
                        }
                        @Override
                        public void error(String result) {
                            Toast.makeText(input_password.this, "原密码错误", Toast.LENGTH_SHORT).show();
                        }
                    });
                    new Thread(changePassword).start();
                }
            });
        }
        else if(state==4){
            login.setText(getString(R.string.correct_password));
            forgetPW.setVisibility(View.INVISIBLE);
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ForgetPassword forgetPassword=new ForgetPassword(input_password.this,sAccount,code,password.getText().toString());
                    forgetPassword.setCallback(new NetCallback() {
                        @Override
                        public void execute(String result) {
                            SharedPreferences sharedPreferences=getSharedPreferences("UserInformation",MODE_PRIVATE);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString("account",sAccount);
                            editor.putString("password",password.getText().toString());
                            editor.commit();
                            Intent i=new Intent(input_password.this,add_lock.class);
                            startActivity(i);
                            finish();
                        }
                        @Override
                        public void error(String result) {
                            Log.e("forget_password",result);
                            Toast.makeText(input_password.this, "验证码错误", Toast.LENGTH_SHORT).show();
                        }
                    });
                    new Thread(forgetPassword).start();
                }
            });
        }
        else{
            Toast.makeText(this, getResources().getString(R.string.re_login), Toast.LENGTH_SHORT).show();
            Intent i=new Intent(this,login.class);
            startActivity(i);
            finish();
        }
    }
    //登录回调
    final NetCallback callback=new NetCallback() {
        @Override
        public void execute(String result) {
            Intent i=new Intent(input_password.this,add_lock.class);
            MyProgressDialog.remove();
            startActivity(i);
            finish();
        }

        @Override
        public void error(String result) {
            Log.e("Login_result",result);
            if(result.contains("4")) {
                Toast.makeText(input_password.this, getResources().getString(R.string.user_or_password_error), Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(input_password.this, "not know error", Toast.LENGTH_SHORT).show();
            }
            MyProgressDialog.remove();
        }
    };
}
