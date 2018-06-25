package test.xk_ys_VOOLOC.MyAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import test.xk_ys_VOOLOC.AboutFile.BaseApplication;
import test.xk_ys_VOOLOC.AboutFile.OpenRecord;
import test.xk_ys_VOOLOC.AboutFile.StringToDate;
import test.xk_ys_VOOLOC.R;

/**
 * Created by 柯东煜 on 2017/11/8.
 */

public class MyOpenRecordAdapter extends BaseAdapter {
    private Vector<OpenRecord> vector;


    public boolean today=false;
    public boolean weekend=false;
    public boolean threeDay=false;
    public boolean other=false;
    //今天
    public long t1=Time(new Date().getTime());
    //最近三天
    public long t2=Time(new Date().getTime()-259200000);
    public long t3=Time(new Date().getTime()-604800000);




    private Context context;
    public MyOpenRecordAdapter(Context context,Vector<OpenRecord> v){
        this.context=context;
        vector=v;
    }
    @Override
    public int getCount() {
        return vector.size();
    }

    @Override
    public Object getItem(int position) {
        return vector.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if(convertView==null){
            holder=new Holder();
            convertView= LayoutInflater.from(context).inflate(R.layout.list_record1,null);
            holder.textView1=(TextView)convertView.findViewById(R.id.device_name);
            holder.textView2=(TextView)convertView.findViewById(R.id.device_addr);
            holder.title=(LinearLayout)convertView.findViewById(R.id.title);
            holder.textView3=(TextView)convertView.findViewById(R.id.title_content);
            holder.content=(LinearLayout)convertView.findViewById(R.id.content);
            holder.type=(TextView)convertView.findViewById(R.id.power);
            convertView.setTag(holder);
        }
        else{
            holder=(Holder)convertView.getTag();
        }
        if(vector.get(position).time.equals("今天")||vector.get(position).time.equals("最近三天")||vector.get(position).time.equals("最近一周")||vector.get(position).time.equals("其他")){
            holder.textView3.setText(vector.get(position).time);
            holder.title.setVisibility(View.VISIBLE);
            holder.content.setVisibility(View.GONE);
        }
        else{
            holder.title.setVisibility(View.GONE);

            boolean b;
            if(Long.valueOf(vector.get(position).time)>=t1){
                b=today;
            }
            else if(Long.valueOf(vector.get(position).time)<t1&&Long.valueOf(vector.get(position).time)>=t2){
                b=threeDay;
            }
            else if(Long.valueOf(vector.get(position).time)<t2&&Long.valueOf(vector.get(position).time)>=t3){
                b=weekend;
            }
            else if(Long.valueOf(vector.get(position).time)<t3){
                b=other;
            }
            else{
                b=false;
            }
            if(b){
                holder.content.setVisibility(View.GONE);
            }
            else {
                holder.content.setVisibility(View.VISIBLE);
            }
            if(BaseApplication.netVersion==0){
                holder.textView1.setText(vector.get(position).name+" 打开了门锁");
            }
            else {
                holder.textView1.setText("门开了...");
                holder.type.setText("蓝牙");
            }
            if(vector.get(position).type.equals("1")){
                holder.type.setText("蓝牙");
            }
            else{
                holder.type.setText("密码");
            }
            holder.textView2.setText(StringToDate.times(vector.get(position).time));
        }
        return convertView;
    }
    public class Holder{
        public TextView textView1;
        public TextView textView2;
        public TextView type;
        public TextView textView3;
        public LinearLayout title;
        public LinearLayout content;
    }





    public String times(long time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd");
        String times = sdr.format(new Date(time));
        return times;
    }
    public long TransferDate(String time) //throws ParseException
    {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = format.parse(time);
            return date.getTime();
        }
        catch (ParseException pe){
            return 0;
        }
    }
    public long Time(long time){
        String result=times(time)+" 00:00:00";
        return TransferDate(result);
    }
}
