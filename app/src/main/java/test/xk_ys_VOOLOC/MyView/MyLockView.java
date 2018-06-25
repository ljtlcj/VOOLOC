package test.xk_ys_VOOLOC.MyView;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import test.xk_ys_VOOLOC.R;


/**
 * Created by 柯东煜 on 2017/9/28.
 */

public class MyLockView extends LinearLayout {
    private LinearLayout linearLayout;
    private TextView lockName;
    private TextView lockAddress;
    private String name;
    private String address;
    private Context context;
    public MyLockView(Context context, AttributeSet as){
        super(context,as);
        this.context=context;
        TypedArray ta = context.obtainStyledAttributes(as,R.styleable.MyView);
        name=ta.getString(R.styleable.MyView_lock_name);
        address=ta.getString(R.styleable.MyView_lock_address);
        LayoutInflater.from(context).inflate(R.layout.my_lock_view,this);
        lockName=(TextView)findViewById(R.id.lock_name);
        lockAddress=(TextView)findViewById(R.id.lock_address);
        linearLayout=(LinearLayout)findViewById(R.id.linear);
        lockName.setText(name);
        lockAddress.setText(address);
    }
    public void setText(String name,String address){
        this.lockName.setText(name);
        this.lockAddress.setText(address);
    }
    public String getLockName(){
        return lockName.getText().toString();
    }
    public String getLockAddress(){
        return lockAddress.getText().toString();
    }

    @Override
    public void setOnClickListener(OnClickListener onClickListener){
        linearLayout.setOnClickListener(onClickListener);
    }

}
