package test.xk_ys_VOOLOC;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import test.xk_ys_VOOLOC.AboutFile.isEmpty;
import test.xk_ys_VOOLOC.Net.CorrectLockAddress;
import test.xk_ys_VOOLOC.Net.NetCallback;

public class SetLockAddress extends AppCompatActivity {
    private String phone;
    private String password;
    private String lockKey;

    private TextView back;
    private EditText lockAddress;
    private TextView finish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_lock_address);
        getUserInfo();
    }
    public void getUserInfo(){
        SharedPreferences info=getSharedPreferences("UserInformation",MODE_PRIVATE);
        phone=info.getString("account",null);
        password=info.getString("password",null);
        lockKey=getIntent().getStringExtra("lockKey");
        if(phone==null||password==null||lockKey==null){
            Toast.makeText(this, getString(R.string.read_error), Toast.LENGTH_SHORT).show();
            finish();
        }
        initialize();
    }
    public void initialize(){
        back=(TextView)findViewById(R.id.back);
        finish=(TextView)findViewById(R.id.finish);
        lockAddress=(EditText) findViewById(R.id.lock_address);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEmpty.StringIsEmpty(lockAddress.getText().toString())){
                    Toast.makeText(SetLockAddress.this, getString(R.string.no_write_lock_name), Toast.LENGTH_SHORT).show();
                }
                else {
                    CorrectLockAddress correctLockAddress = new CorrectLockAddress(SetLockAddress.this,phone,lockKey,lockAddress.getText().toString());
                    NetCallback netCallback=new NetCallback() {
                        @Override
                        public void execute(String result) {
                            Toast.makeText(SetLockAddress.this, getString(R.string.correct_successfully), Toast.LENGTH_SHORT).show();
                            Intent i=new Intent(SetLockAddress.this,add_lock.class);
                            startActivity(i);
                            finish();
                        }

                        @Override
                        public void error(String result) {
                            Toast.makeText(SetLockAddress.this, getString(R.string.correct_fault), Toast.LENGTH_SHORT).show();
                        }
                    };
                    correctLockAddress.setCallback(netCallback);
                    new Thread(correctLockAddress).start();
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
