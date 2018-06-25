package test.xk_ys_VOOLOC.MyView;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import test.xk_ys_VOOLOC.R;

/**
 * Created by 柯东煜 on 2017/9/29.
 */

public class MySearchLockView extends LinearLayout {
    private String name;
    private String address;
    private Context context;
    private LinearLayout linearLayout;
    private TextView lockName;
    public MySearchLockView(Context context, AttributeSet as){
        super(context,as);
        this.context=context;
        TypedArray ta=context.obtainStyledAttributes(as,R.styleable.MyView);
        name=ta.getString(R.styleable.MyView_lock_name);
        LayoutInflater.from(context).inflate(R.layout.my_search_lock_view,this);
        linearLayout=(LinearLayout)findViewById(R.id.linear);
        lockName=(TextView)findViewById(R.id.lock_name);
        lockName.setText(name);
    }
    public void setText(String name){
        lockName.setText(name);
    }
    public String getText(){
        return lockName.getText().toString();
    }
    public void setOnClickListener(OnClickListener l){
        linearLayout.setOnClickListener(l);
    }
}
