package test.xk_ys_VOOLOC.BluetoothGattCallback;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import test.xk_ys_VOOLOC.R;

/**
 * Created by 柯东煜 on 2017/9/30.
 */

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class GattCallback extends BluetoothGattCallback {
    private Handler handler=new Handler();
    private Context context;

    final UUID SERVER=UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    final UUID NOTIFY=UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
    final UUID WRITE=UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb");
    //final UUID SERVER=UUID.fromString("0783b03e-8535-b5a0-7140-a304d2495cb7");
    //final UUID NOTIFY=UUID.fromString("0783b03e-8535-b5a0-7140-a304d2495cb8");
    //final UUID WRITE=UUID.fromString("0783b03e-8535-b5a0-7140-a304d2495cba");

    //final UUID SERVER=UUID.fromString("00001000-0000-1000-8000-00805f9b34fb");
   // final UUID NOTIFY=UUID.fromString("00001002-0000-1000-8000-00805f9b34fb");
   //  final UUID WRITE=UUID.fromString("00001001-0000-1000-8000-00805f9b34fb");
    private String message="!S*";
    private Callback callback=new Callback() {
        @Override
        public void readCallback(String result) {

        }
        @Override
        public void unConnectCallback(){

        }
        @Override
        public void connectCallback() {

        }
    };
    public void setCallback(Callback callback) {
        this.callback = callback;
    }
    public void setMessage(String message)
    {
        this.message=message;
    }
    public GattCallback(Context context) {
        this.context = context;
    }
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                Log.e("bluetoothGatt","connection");
                    callback.connectCallback();
                }
            });
        } else
        {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context,context.getString(R.string.unconnected_lock), Toast.LENGTH_SHORT).show();
                    Log.e("bluetoothGatt","disconnection");
                    callback.unConnectCallback();
                }
            });
        }
    }

    public void onCharacteristicRead(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, int status) {
        Log.e("bluetoothGatt","read");
    }
    public void onCharacteristicWrite(BluetoothGatt gatt,BluetoothGattCharacteristic characteristic, int status) {
        Log.e("bluetoothGatt","write");
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        gatt.readCharacteristic(characteristic);
        String str=null;
        try {
            str=new String(characteristic.getValue(),"GB2312");
        } catch (UnsupportedEncodingException e) {
            Log.e("UnsupportedEncoding","-------------------------");
            e.printStackTrace();
        }
        final String result=str;
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.e("BluetoothGatt_gainData",result);
                callback.readCallback(result);
            }
        });

    }
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
                                 int status) {
    }
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
                                  int status) {
    }
    public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
    }
    public void onServicesDiscovered(final BluetoothGatt gatt, int status){
        BluetoothGattService Service=gatt.getService(SERVER);
        if(Service!=null)
        {
            final BluetoothGattCharacteristic WriteCharacteristic=Service.getCharacteristic(WRITE);
            final BluetoothGattCharacteristic ReadCharacteristic=Service.getCharacteristic(NOTIFY);
            if(WriteCharacteristic!=null&&ReadCharacteristic!=null)
            {
             /*   new Thread(new Runnable() {
                    @Override
                    public void run() {*/
                        gatt.setCharacteristicNotification(ReadCharacteristic,true);
                        boolean isEnableNotification = gatt.setCharacteristicNotification(ReadCharacteristic,true);
                     /*   if(isEnableNotification) {
                            List<BluetoothGattDescriptor> descriptorList = ReadCharacteristic.getDescriptors();
                            if(descriptorList != null && descriptorList.size() > 0) {
                                for(BluetoothGattDescriptor descriptor : descriptorList) {
                                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                    gatt.writeDescriptor(descriptor);
                                }
                            }
                        }
                    }
                }).start();*/
                WriteCharacteristic.setValue(message);
                gatt.writeCharacteristic(WriteCharacteristic);
            }
        }
    }

    public interface Callback{
        void readCallback(String result);
        void connectCallback();
        void unConnectCallback();
    }
}