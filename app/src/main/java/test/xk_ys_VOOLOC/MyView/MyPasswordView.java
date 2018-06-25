package test.xk_ys_VOOLOC.MyView;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.Timer;
import java.util.TimerTask;

import test.xk_ys_VOOLOC.R;


/**
 * Created by 柯东煜 on 2017/9/25.
 */

public class MyPasswordView extends LinearLayout implements View.OnClickListener,TextWatcher,View.OnKeyListener{
    EditText e1;
    EditText e2;
    EditText e3;
    EditText e4;
    Context c;
    myCallback callback=new myCallback() {
        @Override
        public void execute() {

        }
    };
    public MyPasswordView(Context c, AttributeSet as){
        super(c,as);
        this.c=c;
        LayoutInflater.from(c).inflate(R.layout.my_password_view,this,true);
        e1=(EditText)findViewById(R.id.E1);
        e2=(EditText)findViewById(R.id.E2);
        e3=(EditText)findViewById(R.id.E3);
        e4=(EditText)findViewById(R.id.E4);
        e1.setOnClickListener(this);
        e2.setOnClickListener(this);
        e3.setOnClickListener(this);
        e4.setOnClickListener(this);
        e1.addTextChangedListener(this);
        e2.addTextChangedListener(this);
        e3.addTextChangedListener(this);
        e4.addTextChangedListener(this);
        e1.setOnKeyListener(this);
        e2.setOnKeyListener(this);
        e3.setOnKeyListener(this);
        e4.setOnKeyListener(this);
    }
    public String getText(){
        return e1.getText().toString()+e2.getText().toString()+e3.getText().toString()+e4.getText().toString();
    }
    @Override
    public void onClick(View v){
        EditText et=(EditText)v;
        et.setText("");
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }
    public void setCallback(myCallback callback){
        this.callback=callback;
    }
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(s.toString().length()==1){
            if(e1.isFocused()){
                e1.clearFocus();
                if(e2.getText().toString().length()==1)
                    e2.setText("");
                e2.requestFocus();
            }
            else if(e2.isFocused()){
                e2.clearFocus();
                if(e3.getText().toString().length()==1)
                    e3.setText("");
                e3.requestFocus();
            }
            else if(e3.isFocused()){
                e3.clearFocus();
                if(e4.getText().toString().length()==1)
                    e4.setText("");
                e4.requestFocus();
            }
            else if(e4.isFocused()){
//                e4.clearFocus();
  //              e1.setSelection(e1.getText().length());//将光标移至文字末尾
                noInput();
                callback.execute();
            }

        }
        else if(s.toString().length()==2){
            if(e1.isFocused()){
                e1.setText(s.subSequence(1,2));
            }
        }
    }
    @Override
    public void afterTextChanged(Editable s) {
    }
    public interface myCallback{
        void execute();
    }
    public void reInput(){
        e1.setEnabled(true);
        e2.setEnabled(true);
        e3.setEnabled(true);
        e4.setEnabled(true);
        e1.setText("");
        e2.setText("");
        e3.setText("");
        e4.setText("");
        e4.clearFocus();
        e1.setFocusable(true);
        e1.setFocusableInTouchMode(true);
        Timer timer=new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                InputMethodManager inputManager =
                        (InputMethodManager) e1.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(e1, 0);
            }
        }, 200);
    }
    public void noInput(){
        e1.setEnabled(false);
        e2.setEnabled(false);
        e3.setEnabled(false);
        e4.setEnabled(false);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            switch (v.getId()){
                case R.id.E1:
                    e1.setText("");
                    break;

                case R.id.E2:
                    e1.setText("");
                    e2.clearFocus();
                    e1.requestFocus();
                    break;

                case R.id.E3:
                    e3.clearFocus();
                    e2.requestFocus();
                    e2.setText("");
                    break;

                case R.id.E4:
                    e4.clearFocus();
                    e3.requestFocus();
                    e3.setText("");
                    break;
            }

        }
        return false;
    }
}
