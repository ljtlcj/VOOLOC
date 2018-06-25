package test.xk_ys_VOOLOC;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class legal_provisions extends AppCompatActivity {

    private Button agress;

    private Button disagress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legal_provisions);
        agress=(Button)findViewById(R.id.agress);
        disagress=(Button)findViewById(R.id.disagress);
        SharedPreferences s=getSharedPreferences("UserInformation",MODE_PRIVATE);
        int i=s.getInt("times",-1);
        i++;
        final SharedPreferences.Editor editor=s.edit();
        editor.putInt("times",i);
        editor.commit();
        if(i!=0){
            Intent intent=new Intent(this,login.class);
            startActivity(intent);
            finish();
        }
        agress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(legal_provisions.this,login.class);
                startActivity(intent);
                finish();
            }
        });
        disagress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt("times",-1);
                editor.commit();
                finish();
            }
        });
    }
}
