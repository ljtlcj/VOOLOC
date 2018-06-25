package test.xk_ys_VOOLOC.MyAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Vector;

import test.xk_ys_VOOLOC.AboutFile.LockInfo;
import test.xk_ys_VOOLOC.Net.GainLockAddress;
import test.xk_ys_VOOLOC.R;
import test.xk_ys_VOOLOC.Select_Lock;

/**
 * Created by 柯东煜 on 2017/9/28.
 */

public class MyLockAdapter extends BaseAdapter {
    private Vector<String> lockName;
    private Vector<LockInfo> lockInfo;
    private Context context;
    public MyLockAdapter(Context context,Vector<String> lockName,Vector<LockInfo> lockInfo){
        this.context=context;
        this.lockInfo=lockInfo;
        this.lockName=lockName;
    }
    @Override
    public int getCount() {
        return lockName.size();
    }

    @Override
    public Object getItem(int i) {
        return lockName.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    ViewHolder mHolder;

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        mHolder = new ViewHolder();
        if (view == null) {

            view = LayoutInflater.from(context).inflate(R.layout.my_lock_view, null);
            mHolder.lockAddress = (TextView) view.findViewById(R.id.lock_address);
            mHolder.lockName = (TextView) view.findViewById(R.id.lock_name);
            mHolder.linearLayout=(LinearLayout)view.findViewById(R.id.linear) ;
            view.setTag(mHolder);

        } else {
            mHolder = (ViewHolder) view.getTag();
        }
        //如果没有这些内容，将会显示布局文件中的内容
        mHolder.lockName.setText(lockName.get(position));
        GainLockAddress gainLockAddress=new GainLockAddress(context,lockInfo.get(position).getLockKey());
       /* NetCallback netCallback=new NetCallback() {
            @Override
            public void execute(String result) {
               lockAddress.get(position).setText(result);
            }

            @Override
            public void error(String result) {
                lockAddress.get(position).setText("");
            }
        };
        gainLockAddress.setCallback(netCallback);
        new Thread(gainLockAddress).start();*/
        //头像的点击事件并传值
        mHolder.lockAddress.setText(lockInfo.get(position).getAddress());
        mHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //开始传值
                Intent i=new Intent(context, Select_Lock.class);
                i.putExtra("lockInfo",lockInfo.get(position));
                //利用上下文开启跳转
                context.startActivity(i);
            }
        });
        return view;
    }
    class ViewHolder {
        TextView lockAddress;
        TextView lockName;
        LinearLayout linearLayout;

    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        lockInfo.clear();
        lockName.clear();
        lockName=null;
        lockInfo=null;
    }

}
