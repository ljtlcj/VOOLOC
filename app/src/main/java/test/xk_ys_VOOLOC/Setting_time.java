package test.xk_ys_VOOLOC;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import test.xk_ys_VOOLOC.AboutFile.StringToDate;
import test.xk_ys_VOOLOC.AboutFile.isEmpty;

public class Setting_time extends AppCompatActivity implements View.OnClickListener{
    @BindView(R.id.back)
    public  TextView back;
    @BindView(R.id.start_time)
    public TextView start;
    @BindView(R.id.end_time)
    public TextView end;
    @BindView(R.id.next)
    public TextView next;

    private String startTime;
    private String endTime;
    private String lockKey;
    private String phone;
    private String name;
    private String lockId;
    private int order=0;

    @BindView(R.id.one_day)
    public TextView one_day;
    @BindView(R.id.one_month)
    public TextView one_month;
    @BindView(R.id.one_week)
    public TextView one_week;
    @BindView(R.id.one_year)
    public TextView one_year;
    @BindView(R.id.self)
    public TextView self;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_time);
        ButterKnife.bind(this);
        getInfo();
        initialize();
    }
    public void getInfo(){
        Intent i=getIntent();
        startTime=i.getStringExtra("startTime");
        endTime=i.getStringExtra("endTime");
        lockKey=i.getStringExtra("lockKey");
        phone=i.getStringExtra("phone");
        name=i.getStringExtra("name");
        lockId=i.getStringExtra("lockId");
        order=i.getIntExtra("order",0);
        if(order==0||isEmpty.StringIsEmpty(startTime)||isEmpty.StringIsEmpty(endTime)||isEmpty.StringIsEmpty(lockKey)||isEmpty.StringIsEmpty(phone)){
            Toast.makeText(this, getString(R.string.lock_info_error), Toast.LENGTH_SHORT).show();
            finish();
        }
    }



    public void initialize(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        start.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showDate(start);
                return false;
            }
        });
        end.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showDate(end);
                return false;
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!end.getText().toString().equals(getString(R.string.deadline))) {
                    Intent i = new Intent(Setting_time.this, Sent_PW.class);
                    i.putExtra("phone", phone);
                    if(start.getText().toString().equals(getString(R.string.from_now_start))){
                        i.putExtra("startTime",new Date().getTime());
                    }
                    else {
                        i.putExtra("startTime", StringToDate.TransferDate(start.getText().toString()));
                    }
                    i.putExtra("endTime", StringToDate.TransferDate(end.getText().toString()));
                    i.putExtra("lockKey", lockKey);
                    i.putExtra("name",name);
                    i.putExtra("order",order);
                    i.putExtra("lockId",lockId);
                    startActivity(i);
                }
                else{
                    Toast.makeText(Setting_time.this, getString(R.string.unset_time), Toast.LENGTH_SHORT).show();
                }
            }
        });
        one_day.setOnClickListener(this);
        one_month.setOnClickListener(this);
        one_year.setOnClickListener(this);
        one_week.setOnClickListener(this);
        self.setOnClickListener(this);

    }

    public void showDate(final TextView textView)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.date_time_dialog, null);
        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
        final TimePicker timePicker = (android.widget.TimePicker) view.findViewById(R.id.time_picker);
        builder.setView(view);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);
        timePicker.setIs24HourView(true);
        builder.setPositiveButton(getString(R.string.certain), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringBuffer sb = new StringBuffer();
                sb.append(String.format("%04d-%02d-%02d",
                        datePicker.getYear(),
                        datePicker.getMonth() + 1,
                        datePicker.getDayOfMonth()));
                String hour=timePicker.getCurrentHour().toString();
                if(hour.length()==1)
                    hour="0"+hour;
                String min=timePicker.getCurrentMinute().toString();
                if(min.length()==1)
                    min="0"+min;
                String time=sb+" "+hour+":"+min+":00";
                long setTime= StringToDate.TransferDate(time);
                long powerStartTime=StringToDate.StringToLong(startTime);
                if(endTime.equals("0"))
                {
                    if(setTime>=powerStartTime){
                        textView.setTextColor(Color.parseColor("#000000"));
                        textView.setText(time);
                    }
                    else{
                        Toast.makeText(Setting_time.this, getString(R.string.set_time_error), Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    long powerEndTime = StringToDate.StringToLong(endTime);
                    if(setTime>=powerStartTime&&setTime<=powerEndTime){
                        textView.setTextColor(Color.parseColor("#000000"));
                        textView.setText(time);
                    }
                    else{
                        Toast.makeText(Setting_time.this, getString(R.string.set_time_error), Toast.LENGTH_SHORT).show();
                    }
                }
                if(!end.getText().toString().equals(getString(R.string.deadline))){
                    long time1;
                    if(start.getText().toString().equals(getString(R.string.from_now_start))) {
                        time1=new Date().getTime();
                    }
                    else{
                        time1=StringToDate.TransferDate(start.getText().toString());
                    }
                    long time2=StringToDate.TransferDate(end.getText().toString());
                    if(time1>time2){
                        Toast.makeText(Setting_time.this, getString(R.string.endTime_error), Toast.LENGTH_SHORT).show();
                        end.setText(getString(R.string.deadline));
                    }
                }
            }
        });
        if(textView.getId()==start.getId()){
            builder.setNegativeButton(getString(R.string.from_now_start), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    textView.setText(getString(R.string.from_now_start));
                }
            });
        }
        final Dialog dialog = builder.create();
        dialog.show();
    }
    @Override
    public void onClick(View v){
        long day=24*60*60*1000;
        switch (v.getId()){
            case R.id.self:
                start.setText("从现在开始");
                end.setText("截止时间");
                break;
            case R.id.one_day:
                start.setText(times(new Date().getTime()));
                end.setText(times(new Date().getTime()+day));
                break;
            case R.id.one_week:
                start.setText(times(new Date().getTime()));
                end.setText(times(new Date().getTime()+7*day));
                break;
            case R.id.one_month:
                start.setText(times(new Date().getTime()));
                end.setText(times(new Date().getTime()+30*day));
                break;
            case R.id.one_year:
                start.setText(times(new Date().getTime()));
                end.setText(times(new Date().getTime()+365*day));
        }
    }

    public static String times(long time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        @SuppressWarnings("unused")
        //   int i = Integer.parseInt(time);
        String times = sdr.format(time);
        return times;
    }

}
