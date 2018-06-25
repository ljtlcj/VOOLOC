package test.xk_ys_VOOLOC;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import test.xk_ys_VOOLOC.AboutFile.LockInfo;
import test.xk_ys_VOOLOC.AboutFile.isEmpty;
import test.xk_ys_VOOLOC.Net.CorrectLockAddress;
import test.xk_ys_VOOLOC.Net.NetCallback;

public class Lock_of_address extends AppCompatActivity {
    private EditText lockAddress;
    private TextView back;
    private TextView next;

    private String account;
    private String password;
    private LockInfo lockInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_of_address);
        getUserInfo();
    }
    public void getUserInfo(){
        SharedPreferences info=getSharedPreferences("UserInformation",MODE_PRIVATE);
        account=info.getString("account",null);
        password=info.getString("password",null);
        lockInfo=getIntent().getParcelableExtra("lockInfo");
        if(lockInfo==null){
            Toast.makeText(this, getString(R.string.read_error), Toast.LENGTH_SHORT).show();
            finish();
        }
        initialize();
    }
    public void initialize(){
        back=(TextView)findViewById(R.id.back);
        next=(TextView)findViewById(R.id.finish);
        lockAddress=(EditText)findViewById(R.id.lock_address);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        /*next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEmpty.StringIsEmpty(lockAddress.getText().toString())||true)
                {
                    Intent i=new Intent(Lock_of_address.this,Select_Lock.class);
                    lockInfo.setPower("1");
                    lockInfo.setStartTime(Long.toString(new Date().getTime()/1000));
                    lockInfo.setEndTime("0");
                    i.putExtra("lockInfo",lockInfo);
                    startActivity(i);
                    finish();
                }
            }
        });*/
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isEmpty.StringIsEmpty(lockAddress.getText().toString())) {
                    CorrectLockAddress correctLockAddress = new CorrectLockAddress(Lock_of_address.this, account, lockInfo.getLockKey(), lockAddress.getText().toString());
                    NetCallback callback=new NetCallback() {
                        @Override
                        public void execute(String result) {
                            Intent i=new Intent(Lock_of_address.this,Select_Lock.class);
                            lockInfo.setPower("1");
                            lockInfo.setStartTime(Long.toString(new Date().getTime()/1000));
                            lockInfo.setEndTime("0");
                            i.putExtra("lockInfo",lockInfo);
                            startActivity(i);
                            finish();
                        }

                        @Override
                        public void error(String result) {

                        }
                    };
                    correctLockAddress.setCallback(callback);
                    new Thread(correctLockAddress).start();
                }
                else{
                    Toast.makeText(Lock_of_address.this, "No Write the Lock of Address!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
