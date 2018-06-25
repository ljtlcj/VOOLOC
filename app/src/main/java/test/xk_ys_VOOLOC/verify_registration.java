package test.xk_ys_VOOLOC;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import test.xk_ys_VOOLOC.MyView.MyPasswordView;
import test.xk_ys_VOOLOC.MyView.MyProgressDialog;
import test.xk_ys_VOOLOC.Net.GainVerifyCode;
import test.xk_ys_VOOLOC.Net.NetCallback;
import test.xk_ys_VOOLOC.Net.VerifyCode;

public class verify_registration extends AppCompatActivity {
    private MyPasswordView verifyCode;
    private TextView back;
    private TextView resend;
    private TextView sendState;

    private int state;//状态信息 1为注册 2为修改密码 3为修改密码
    private String account;
    private String password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_registration);
        getState();
        initialize();
    }
    public void getState(){
        Intent i=getIntent();
        account=i.getStringExtra("account");
        password=i.getStringExtra("password");
        state=i.getIntExtra("state",0);
    }
    public void initialize(){
        verifyCode=(MyPasswordView)findViewById(R.id.verify_code);
        resend=(TextView)findViewById(R.id.resend);
        sendState=(TextView)findViewById(R.id.send_state);
        back=(TextView)findViewById(R.id.correct_back);
        //当填完验证码就回调
        sendState.setText(getResources().getString(R.string.click_send_code));
        if(state==1){       //注册
            verifyCode.setCallback(myCallback);
            StateOne();
        }
        else if(state==2){      //修改密码
            StateTwo();
            verifyCode.setCallback(myCallback);
        }
        else if(state==3){
            StateThree();
            verifyCode.setCallback(myCallback3);
        }
        else{
            finish();
        }
        resend.setText(getResources().getString(R.string.click_send));
        //获取验证码
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MyProgressDialog.show(verify_registration.this, "sending...", true, null);
                GainVerifyCode gainVerifyCode=new GainVerifyCode(verify_registration.this,account);
                gainVerifyCode.setCallback(callback);
                new Thread(gainVerifyCode).start();
            }
        });
    }
    //注册
    public void StateOne(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(verify_registration.this,login.class);
                startActivity(i);
                finish();
            }
        });
    }
    //修改密码
    public void StateTwo(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(verify_registration.this,input_password.class);
                i.putExtra("account",account);
                i.putExtra("state",1);
                startActivity(i);
                finish();
            }
        });
    }
    public void StateThree(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK &&state!=3)
        {
            Intent i;
            if(state==2) {
                i = new Intent(verify_registration.this, input_password.class);
                i.putExtra("account",account);
                i.putExtra("state",1);
            }
            else{
                i=new Intent(verify_registration.this,login.class);
            }
            startActivity(i);
            finish();
        }
        if(keyCode==KeyEvent.KEYCODE_DEL){

        }
        else{
            finish();
        }
        return false;
    }

    MyPasswordView.myCallback myCallback=new MyPasswordView.myCallback() {
        @Override
        public void execute() {

            MyProgressDialog.show(verify_registration.this, "loading...", true, null);
            VerifyCode verifyCode=new VerifyCode(verify_registration.this,account,verify_registration.this.verifyCode.getText());
            verifyCode.setCallback(callback1);
            new Thread(verifyCode).start();
        }
    };
    MyPasswordView.myCallback myCallback3=new MyPasswordView.myCallback() {
        @Override
        public void execute() {
            Intent i=new Intent(verify_registration.this,input_password.class);
            i.putExtra("account",account);
            i.putExtra("password",password);
            i.putExtra("code",verifyCode.getText());
            i.putExtra("state",4);
            startActivity(i);
            finish();
        }
    };
    //获取验证码
    NetCallback callback=new NetCallback() {
        @Override
        public void execute(String result) {
            sendState.setText(getResources().getString(R.string.send_code)+" "+account);
            resend.setEnabled(false);
            second=60;
            handler.postDelayed(r,1000);
            verifyCode.reInput();
            MyProgressDialog.remove();
        }

        @Override
        public void error(String result) {
            MyProgressDialog.remove();
            Toast.makeText(verify_registration.this, getResources().getString(R.string.send_failure), Toast.LENGTH_SHORT).show();
        }
    };
    //验证验证码
    NetCallback callback1=new NetCallback() {
        @Override
        public void execute(String result) {
            Intent i=new Intent(verify_registration.this,input_password.class);
            i.putExtra("account",account);
            i.putExtra("password",password);
            if(state==1) i.putExtra("state",2);
            else if(state ==2||state==3) i.putExtra("state",3);
            startActivity(i);
            finish();
        }

        @Override
        public void error(String result) {
            verifyCode.reInput();
            Toast.makeText(verify_registration.this, getResources().getString(R.string.rewrite_code), Toast.LENGTH_SHORT).show();
            MyProgressDialog.remove();
        }
    };
    Handler handler=new Handler();
    private int second=60;
    Runnable r=new Runnable() {
        @Override
        public void run() {
            second--;
            resend.setText(second+getResources().getString(R.string.compare_resend));
            if(second==0){
                resend.setText(getResources().getString(R.string.click_send));
                resend.setEnabled(true);
            }
            else{
                handler.postDelayed(this,1000);
            }
        }
    };
}
