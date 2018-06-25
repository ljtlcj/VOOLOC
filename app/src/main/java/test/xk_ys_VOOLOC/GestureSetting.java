package test.xk_ys_VOOLOC;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GestureSetting extends AppCompatActivity {
    @BindView(R.id.back)
    public TextView back;
    @BindView(R.id.gesture)
    public Switch aSwitch;
    @BindView(R.id.set_gesture)
    public LinearLayout line;
    private SharedPreferences userInfo;
    private SharedPreferences.Editor editor;
    private boolean gestureState=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_setting);
        ButterKnife.bind(this);
        userInfo=getSharedPreferences("userInfo",MODE_PRIVATE);
        gestureState=userInfo.getBoolean("gesture",false);
        aSwitch.setChecked(gestureState);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        aSwitch.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    Intent i=new Intent(GestureSetting.this, GesturePW1.class);
                        i.putExtra("state",1);
                    startActivity(i);
            }
        });
        line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(GestureSetting.this, GesturePW1.class);
                i.putExtra("state",2);
                startActivity(i);
            }
        });

        line.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    v.setBackgroundColor(Color.LTGRAY);
                }else if(event.getAction() == MotionEvent.ACTION_MOVE){
                    v.setBackgroundColor(Color.LTGRAY);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    v.setBackgroundColor(Color.WHITE);
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(userInfo.getBoolean("gesture",false)){
            aSwitch.setChecked(true);
        }
        else{
            aSwitch.setChecked(false);
        }
    }

}
