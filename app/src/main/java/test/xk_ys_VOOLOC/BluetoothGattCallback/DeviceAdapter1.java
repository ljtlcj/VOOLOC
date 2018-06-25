package test.xk_ys_VOOLOC.BluetoothGattCallback;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Vector;

import test.xk_ys_VOOLOC.R;

/**
 * Created by 柯东煜 on 2017/10/31.
 */

public class DeviceAdapter1 extends BaseAdapter {
    private Vector<LockList> lockLists;
    private Context context;
    public DeviceAdapter1(Context context,Vector<LockList> l){
        this.context=context;
        lockLists=l;
    }
    @Override
    public int getCount() {
        return lockLists.size();
    }

    @Override
    public Object getItem(int position) {
        return lockLists.get(position).device;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_device, null);
            holder = new ViewHolder();
            holder.device_name = (TextView) convertView.findViewById(R.id.device_name);
            holder.device_addr = (TextView) convertView.findViewById(R.id.device_addr);
            holder.device_rssi = (ProgressBar) convertView.findViewById(R.id.device_rssi);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        LockList lockList=lockLists.get(position);
        holder.device_rssi.setProgress(normaliseRssi(lockList.state));

        if (lockList.device.getName() != null && lockList.device.getName().length() > 0) {
            holder.device_name.setText(lockList.device.getName());
            holder.device_addr.setText(lockList.device.getAddress());
        } else {
            holder.device_name.setText(lockList.device.getAddress());
            holder.device_addr.setText(context.getResources().getString(R.string.unknown_device));
        }

        return convertView;
    }

    static class ViewHolder {
        TextView device_name;
        TextView device_addr;
        ProgressBar device_rssi;
    }

    private int normaliseRssi(int rssi) {
        // Expected input range is -127 -> 20
        // Output range is 0 -> 100
        final int RSSI_RANGE = 147;
        final int RSSI_MAX = 20;

        return (RSSI_RANGE + (rssi - RSSI_MAX)) * 100 / RSSI_RANGE;
    }

}
