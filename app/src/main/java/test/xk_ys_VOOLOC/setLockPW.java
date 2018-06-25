package test.xk_ys_VOOLOC;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import test.xk_ys_VOOLOC.AboutFile.LockInfo;
import test.xk_ys_VOOLOC.MyView.SetLockPasswordView;

public class setLockPW extends AppCompatActivity {
    private TextView back;
    private TextView next;
    private SetLockPasswordView setLockPasswordView;

    private LockInfo lockInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_lock_pw);
        getLockInfo();
        initialize();
    }
    public void getLockInfo(){
        Intent i=getIntent();
        lockInfo=i.getParcelableExtra("lockInfo");
        if(lockInfo==null){
            Toast.makeText(this, getString(R.string.lock_info_error), Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    public void initialize(){
        back=(TextView)findViewById(R.id.back);
        next=(TextView)findViewById(R.id.next);
        setLockPasswordView=(SetLockPasswordView)findViewById(R.id.my_password);
        SetLockPasswordView.myCallback mycallback=new SetLockPasswordView.myCallback() {
            @Override
            public void execute() {
                setLockPasswordView.reInput();
            }
        };
        setLockPasswordView.setCallback(mycallback);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(setLockPasswordView.isNotEmpty()){
                    Intent i=new Intent(setLockPW.this,SettingPasswordTime.class);
                    i.putExtra("lockKey",lockInfo.getLockKey());
                    i.putExtra("password",setLockPasswordView.getText());
                    i.putExtra("startTime",lockInfo.getStartTime());
                    i.putExtra("endTime",lockInfo.getEndTime());
                    i.putExtra("bluetooth",lockInfo.getBluetoothAddress());
                    startActivity(i);
                }
                else{
                    Toast.makeText(setLockPW.this, getString(R.string.password_not_write), Toast.LENGTH_SHORT).show();
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
