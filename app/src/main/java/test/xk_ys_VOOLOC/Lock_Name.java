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
import test.xk_ys_VOOLOC.AboutFile.LockInfo;
import test.xk_ys_VOOLOC.AboutFile.isEmpty;
import test.xk_ys_VOOLOC.Net.CorrectLockName;
import test.xk_ys_VOOLOC.Net.NetCallback;

public class Lock_Name extends AppCompatActivity {
    private TextView back;
    private EditText lockName;
    private TextView next;

    private String account;
    private String password;
    private String lockKey;
    private String lockBluetooth;
    private String lockId;
    private String lockId2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock__name);
        getUserInfo();
    }
    public void getUserInfo(){
        SharedPreferences info=getSharedPreferences("UserInformation",MODE_PRIVATE);
        account=info.getString("account",null);
        password=info.getString("password",null);
        lockKey=getIntent().getStringExtra("lockKey");
        lockBluetooth=getIntent().getStringExtra("bluetooth");
        lockId=getIntent().getStringExtra("lockId");
        lockId2=getIntent().getStringExtra("lockId2");
        if(BaseApplication.netVersion==0){
            if(account==null||password==null||lockKey==null){
                Toast.makeText(this, getString(R.string.read_error), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        else
        {
            if(account==null||password==null||lockKey==null||lockId==null){
                Toast.makeText(this, getString(R.string.read_error), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        initialize();
    }
    public void initialize(){
        back=(TextView)findViewById(R.id.back);
        next=(TextView)findViewById(R.id.next);
        lockName=(EditText) findViewById(R.id.lock_name2);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEmpty.StringIsEmpty(lockName.getText().toString())){
                    Toast.makeText(Lock_Name.this, getString(R.string.no_write_lock_name), Toast.LENGTH_SHORT).show();
                }
                else {
                    CorrectLockName correctLockName;
                    if(BaseApplication.netVersion==0){
                        correctLockName=new CorrectLockName(Lock_Name.this,account,lockKey,lockName.getText().toString(),password);
                    }
                    else{
                        correctLockName = new CorrectLockName(Lock_Name.this,account,lockId,lockName.getText().toString(),null);
                    }
                    NetCallback netCallback=new NetCallback() {
                        @Override
                        public void execute(String result) {
                            LockInfo lockInfo=new LockInfo();
                            lockInfo.setLockKey(lockKey);
                            lockInfo.setLockId(lockId);
                            lockInfo.setBluetoothAddress(lockBluetooth);
                            lockInfo.setLockId_2(lockId2);
                            lockInfo.setLockName(lockName.getText().toString());
                            Intent i=new Intent(Lock_Name.this,Lock_of_address.class);
                            i.putExtra("lockInfo",lockInfo);
                            startActivity(i);
                            finish();
                        }

                        @Override
                        public void error(String result) {

                        }
                    };
                    correctLockName.setCallback(netCallback);
                    new Thread(correctLockName).start();
                }
            }
        });
    }
}
