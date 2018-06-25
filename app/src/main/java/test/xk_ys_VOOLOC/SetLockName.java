package test.xk_ys_VOOLOC;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import test.xk_ys_VOOLOC.AboutFile.BaseApplication;
import test.xk_ys_VOOLOC.AboutFile.isEmpty;
import test.xk_ys_VOOLOC.Net.CorrectLockName;
import test.xk_ys_VOOLOC.Net.NetCallback;

public class SetLockName extends AppCompatActivity {
    private String account;
    private String password;
    private String lockId;
    private TextView back;
    private EditText lockName;
    private TextView finish;
    private String lockKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_lock_name);
        getUserInfo();
    }
    public void getUserInfo(){
        SharedPreferences info=getSharedPreferences("UserInformation",MODE_PRIVATE);
        account=info.getString("account",null);
        password=info.getString("password",null);
        lockId=getIntent().getStringExtra("lockId");
        lockKey=getIntent().getStringExtra("lockKey");
        if(BaseApplication.netVersion==0){
            if(account==null||password==null||lockKey==null){
                Toast.makeText(this, getString(R.string.read_error), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        else{
            if(account==null||password==null||lockId==null){
                Toast.makeText(this, getString(R.string.read_error), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        initialize();
    }
    public void initialize(){
        back=(TextView)findViewById(R.id.back);
        finish=(TextView)findViewById(R.id.finish);
        lockName=(EditText) findViewById(R.id.lock_name);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEmpty.StringIsEmpty(lockName.getText().toString())){
                    Toast.makeText(SetLockName.this, getString(R.string.no_write_lock_name), Toast.LENGTH_SHORT).show();
                }
                else {
                    CorrectLockName correctLockName;
                    if(BaseApplication.netVersion==0){
                        correctLockName=new CorrectLockName(SetLockName.this,account,lockKey,lockName.getText().toString(),password);
                    }
                    else {
                        correctLockName = new CorrectLockName(SetLockName.this, account, lockId, lockName.getText().toString(),null);
                    }
                    NetCallback netCallback=new NetCallback() {
                        @Override
                        public void execute(String result) {
                            Toast.makeText(SetLockName.this, getString(R.string.correct_successfully), Toast.LENGTH_SHORT).show();
                            Intent i=new Intent(SetLockName.this,add_lock.class);
                            startActivity(i);
                            finish();
                        }

                        @Override
                        public void error(String result) {
                            Toast.makeText(SetLockName.this, getString(R.string.correct_fault), Toast.LENGTH_SHORT).show();
                        }
                    };
                    correctLockName.setCallback(netCallback);
                    new Thread(correctLockName).start();
                }
            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
