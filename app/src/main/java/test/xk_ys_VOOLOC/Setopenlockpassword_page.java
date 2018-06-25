package test.xk_ys_VOOLOC;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;


public class Setopenlockpassword_page extends AppCompatActivity {
    @BindView(R.id.back)
    public TextView back;
    @BindView(R.id.next)
    public Button next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setopenlockpassword_page);
        ButterKnife.bind(this);
       final Intent i=getIntent();
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i.setClass(Setopenlockpassword_page.this,setLockPW.class);
               startActivity(i);
                finish();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
