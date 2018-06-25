package test.xk_ys_VOOLOC;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import test.xk_ys_VOOLOC.BluetoothGattCallback.GattAppService;

/**
 * Created by 柯东煜 on 2017/12/6.
 */

public class SearchBluetoothService extends Service implements BluetoothAdapter.LeScanCallback {

    static public String bluetoothName;
    static public String bluetoothAddress;
    private BluetoothAdapter mBluetoothAdapter;
    public SearchBluetoothService(){
        super();
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("=====",bluetoothName);
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null) return;
        mBluetoothAdapter = bluetoothManager.getAdapter();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GattAppService.GATT_DEVICE_FOUND);
        registerReceiver(GattDeviceReceiver, intentFilter);
        scan(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("----","onDestroy()");
        unregisterReceiver(GattDeviceReceiver);
        scan(false);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
        Log.e("SearchBluetooth1","bluetoothName:"+device.getName()+"    bluetoothAddress:"+device.getAddress());
        if(Lock_of_Setting.activity!=null&&device!=null&&device.getName()!=null&&device.getName().equals(bluetoothName)){
            Log.e("device1","-----");
            ((Lock_of_Setting)Lock_of_Setting.activity).lockInfo.setBluetoothAddress(device.getAddress());
            ((Lock_of_Setting)Lock_of_Setting.activity).update();
        }
        else if(Select_Lock.activity!=null&&device!=null&&device.getName()!=null&&device.getName().equals(bluetoothName)){
            Log.e("device2","---------");
            ((Select_Lock)Select_Lock.activity).lockInfo.setBluetoothAddress(device.getAddress());
            ((Select_Lock)Select_Lock.activity).update();
        }


    }

    private void scan(boolean enable) {
        if (mBluetoothAdapter == null) return;

        if (enable)
            mBluetoothAdapter.startLeScan(this);
        else
            mBluetoothAdapter.stopLeScan(this);

    }
    private final BroadcastReceiver GattDeviceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final BluetoothDevice device = intent.getParcelableExtra(GattAppService.EXTRA_DEVICE);
            final int rssi = intent.getIntExtra(GattAppService.EXTRA_RSSI, 0);
            final int source = intent.getIntExtra(GattAppService.EXTRA_SOURCE, 0);

            if (GattAppService.GATT_DEVICE_FOUND.equals(intent.getAction())) {
                Log.e("SearchBluetooth2","bluetoothName:"+device.getName()+"    bluetoothAddress:"+device.getAddress());
            }
        }
    };
}
