package test.xk_ys_VOOLOC.BluetoothGattCallback;

import android.bluetooth.BluetoothDevice;

/**
 * Created by 柯东煜 on 2017/10/31.
 */

public class LockList{
    public BluetoothDevice device;
    public int state;
    public LockList(BluetoothDevice device,int s){
        this.device=device;
        state=s;
    }
}
