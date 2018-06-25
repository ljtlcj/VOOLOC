package test.xk_ys_VOOLOC.MyAdapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Vector;

import test.xk_ys_VOOLOC.AboutFile.BaseApplication;
import test.xk_ys_VOOLOC.AboutFile.StringToDate;
import test.xk_ys_VOOLOC.R;

/**
 * Created by 柯东煜 on 2017/11/2.
 */

public class RecordAdapter extends BaseAdapter {
    private Context context;
    private Vector<String> startTime;
    private Vector<String> endTime;
    private Vector<String> power;
    private Vector<String> customer;
    private Vector<String> username;

    public RecordAdapter(Context context,Vector<String> v1,Vector<String> v2,Vector<String> v3,Vector<String> v4,Vector<String> v5){
        this.context=context;
        startTime=v1;
        endTime=v2;
        power=v3;
        customer=v4;
        username=v5;
    }
    @Override
    public int getCount() {
        return startTime.size();
    }

    @Override
    public Object getItem(int position) {
        return startTime.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            holder=new ViewHolder();
            convertView= LayoutInflater.from(context).inflate(R.layout.list_record,null);
            holder.customer=(TextView)convertView.findViewById(R.id.device_name);
            holder.time=(TextView)convertView.findViewById(R.id.device_addr);
            holder.power=(TextView)convertView.findViewById(R.id.power);
            convertView.setTag(holder);
        }
        else{
            holder=(ViewHolder)convertView.getTag();
        }
        if(username.get(position).equals("")) {
            holder.customer.setText(customer.get(position));
        }
        else{
            holder.customer.setText(customer.get(position) + "(" + username.get(position) + ")");
        }
        if(BaseApplication.netVersion==0) {
            Log.e("----------",String.valueOf(Long.valueOf(endTime.get(position))*1000));
            holder.time.setText("有效时间至：" + StringToDate.times(String.valueOf(Long.valueOf(endTime.get(position))*1000)));
        }
        else{
            holder.time.setText("有效时间至：" + StringToDate.times(endTime.get(position)));
        }
        if(power.get(position).contains("2")) {
            holder.power.setText("高级权限");
        }
        else{
            holder.power.setText("普通权限");
        }
        return convertView;
    }

    static class ViewHolder {
        TextView customer;
        TextView time;
        TextView power;
    }
}
