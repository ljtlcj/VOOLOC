package test.xk_ys_VOOLOC;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.junkchen.blelib.BleService;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.Vector;

import test.xk_ys_VOOLOC.AboutFile.isEmpty;
import test.xk_ys_VOOLOC.MyView.MySearchLockView;
import test.xk_ys_VOOLOC.Net.AddLock;
import test.xk_ys_VOOLOC.Net.GainLockKey;
import test.xk_ys_VOOLOC.Net.NetCallback;

public class search_door_lock extends AppCompatActivity {
    private TextView back;
    private TextView refresh;
    private LinearLayout linearLayout;
    private Handler handler=new Handler();
    private String account;
    private String password;
    private boolean lockstate=false;
    private String lockKey;
    private BroadcastReceiver receiver;
    private BluetoothAdapter bluetooth;
    private Vector<BluetoothDevice> devices=new Vector<>();
    private GattCallback callback=new GattCallback();
    private BluetoothGatt bg;


    private BleService mBleService;
    private boolean mIsBind;

    private Handler handler1=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==0x123){
                if(bluetooth!=null)
                    bluetooth.cancelDiscovery();
            }
            else if(msg.what==0x124){
                Find_bluetooth();
                Log.e("BluetoothGatt_gainData","---------");
            }
        }
    };
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_door_lock);
        getUserInfo();
        if(!openBluetooth()) {
            Toast.makeText(this, "Bluetooth was not open！", Toast.LENGTH_SHORT).show();
            finish();
        }
        initialize();
    }
    public void getUserInfo(){
        SharedPreferences info=getSharedPreferences("UserInformation",MODE_PRIVATE);
        account=info.getString("account",null);
        if(isEmpty.StringIsEmpty(account)){
            finish();
        }
    }
    private boolean openBluetooth()
    {
        //获取手机蓝牙适配器
        bluetooth = BluetoothAdapter.getDefaultAdapter();
        //检查是否有蓝牙驱动
        if(bluetooth==null) {
            Toast.makeText(getApplicationContext(), "No bluetooth Adapter!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!bluetooth.isEnabled())
        {
            //询问打开蓝牙
            //   Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //   startActivityForResult(mIntent, 1);
            // mBluetoothAdapter.disable();//关闭蓝牙
            bluetooth.enable();  //无声打开蓝牙
            return true;
        }
        return bluetooth.isEnabled();
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void initialize(){
        back=(TextView)findViewById(R.id.back);
        refresh=(TextView)findViewById(R.id.refresh);
        linearLayout=(LinearLayout)findViewById(R.id.lock_list);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onClick(View v) {
                Find_bluetooth();
            }
        });
        Find_bluetooth();
        Timer timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler1.sendEmptyMessage(0x124);
            }
        },2000);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler1.sendEmptyMessage(0x124);
            }
        },3000);
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void Find_bluetooth()
    {
     //   doBindService();
        final UUID SERVER=UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
        final UUID SERVER1=UUID.fromString("00001800-0000-1000-8000-00805f9b34fb");
        final UUID SERVER2=UUID.fromString("00001801-0000-1000-8000-00805f9b34fb");
        final UUID SERVER3=UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");
        UUID[] uuids=new UUID[]{SERVER,SERVER1,SERVER2,SERVER3};
        bluetooth.startLeScan(uuids,leScanCallback);
        bluetooth.startDiscovery();
        handler1.sendEmptyMessageDelayed(0x123,10000);
        linearLayout.removeAllViews();
        for(BluetoothDevice a:devices)
        {
            addButtonUI(a);
        }
        //      Toast.makeText(this, "-----------", Toast.LENGTH_SHORT).show();
        // 设置广播信息过滤
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device!=null&&!devices.contains(device))   //判断是否已经搜索到
                    {
                        addButtonUI(device);
                        devices.add(device);
                    }
                }
            }
        };
        // 注册广播接收器，接收并处理搜索结果
        registerReceiver(receiver, intentFilter);
        bluetooth.startDiscovery();
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void addButtonUI(BluetoothDevice dev)
    {
        String lockname=null;
        try{
            //lockname=dev.getName().substring(0,dev.getName().indexOf(" "));
            lockname=dev.getName();
            Log.e("-------",dev.getName().toString());
            //lockname=dev.getName();
        }
        catch (Exception e)
        {
        }
        if(lockname!=null){
            MySearchLockView mySearchLockView=new MySearchLockView(search_door_lock.this,null);
            mySearchLockView.setText(lockname+"-"+dev.getAddress());
            Listenner listenner = new Listenner(dev);
            mySearchLockView.setOnClickListener(listenner);
            linearLayout.addView(mySearchLockView);
        }
    }

    class Listenner implements View.OnClickListener {
        private BluetoothDevice device;
        private BluetoothGatt bluetoothGatt;
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        public Listenner(BluetoothDevice dev) {
            device = dev;
        }
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(), getString(R.string.connecting_lock), Toast.LENGTH_SHORT).show();
            bluetoothGatt = device.connectGatt(search_door_lock.this, false, callback);
            callback.setAddress(device.getAddress());
            callback.SetName(device.getName());
            bluetoothGatt.connect();
 //           mBleService.connect(device.getAddress());
            bg=bluetoothGatt;
            bluetoothGatt.discoverServices();
            final   AlertDialog.Builder normalDialog =  new   AlertDialog.Builder(search_door_lock.this);
            normalDialog.setTitle(  getString(R.string.connection_certain)  );
            normalDialog.setPositiveButton(  getString(R.string.certain)  ,
                    new   DialogInterface.OnClickListener() {
                        @Override
                        public   void   onClick(DialogInterface dialog,   int   which) {
                            bluetoothGatt.connect();
                            bg=bluetoothGatt;
                            bluetoothGatt.discoverServices();
                        }
                    });
            normalDialog.setNegativeButton( getString(R.string.close)  ,
                    new   DialogInterface.OnClickListener() {
                        @Override
                        public   void   onClick(DialogInterface dialog,   int   which) {
                        }
                    });
            normalDialog.show();// 显示
        }
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        public void finalize(){
            if(bluetooth!=null) {
                bluetoothGatt.disconnect();
                bluetoothGatt.close();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    class GattCallback extends BluetoothGattCallback {

        final UUID SERVER=UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
        final UUID NOTIFY=UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
        final UUID WRITE=UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb");
        //final UUID SERVER=UUID.fromString("0783b03e-8535-b5a0-7140-a304d2495cb7");
        //final UUID NOTIFY=UUID.fromString("0783b03e-8535-b5a0-7140-a304d2495cb8");
        //final UUID WRITE=UUID.fromString("0783b03e-8535-b5a0-7140-a304d2495cba");

        //final UUID SERVER=UUID.fromString("00001000-0000-1000-8000-00805f9b34fb");
        //final UUID NOTIFY=UUID.fromString("00001002-0000-1000-8000-00805f9b34fb");
        //final UUID WRITE=UUID.fromString("00001001-0000-1000-8000-00805f9b34fb");
        private String message="(!S*)";
        private String address=null;
        private String name=null;
        private boolean oneTime=false;
        private boolean twoTime=false;
        public void setAddress(String address){
            this.address=address;
        }
        public void setMessage(String message)
        {
            this.message=message;
        }
        public void SetName(String name){
            this.name=name;
        }
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                //    gatt.discoverServices(); //执行到这里其实蓝牙已经连接成功了
            } else
            {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(search_door_lock.this, "无法连接蓝牙！", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        public void onCharacteristicRead(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, int status) {
            Log.e("BluetoothGatt_gainData","+++++++++");

        }
        public void onCharacteristicWrite(BluetoothGatt gatt,BluetoothGattCharacteristic characteristic, int status) {
        }
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            gatt.readCharacteristic(characteristic);
            //  gatt.disconnect();
            String str=null;
            try {
                str=new String(characteristic.getValue(),"GB2312");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            final String result=str;
            Log.e("BluetoothGatt_gainData",result);
            handler.post(new Runnable() {
                @Override
                public void run() {
                     if(result.contains("Noneuser")&&!lockstate) {
                        lockstate = true;
                        final GainLockKey gainLockKey=new GainLockKey(search_door_lock.this,address);
                        NetCallback netCallback=new NetCallback() {
                            @Override
                            public void execute(String result) {
                                if(result!=null) {
                                    lockKey=result;
                                    callback.setMessage("(!Key" + result + "*)");
                                    bg.discoverServices();
                                }
                            }
                            @Override
                            public void error(String result) {
                            }
                        };
                        gainLockKey.setCallback(netCallback);
                        new Thread(gainLockKey).start();
                    }
                    else if(lockKey!=null&&result.contains(lockKey)&&lockstate){
                        if(lockKey!=null) {
                            AddLock addLock=new AddLock(search_door_lock.this,account,password,lockKey,name,address);
                            NetCallback netCallback=new NetCallback() {
                                @Override
                                public void execute(String result) {
                                    Intent i=new Intent(search_door_lock.this,Lock_Name.class);
                                    i.putExtra("lockKey",lockKey);
                                    i.putExtra("bluetooth",address);
                                    startActivity(i);
                                    finish();
                                }
                                @Override
                                public void error(String result) {
                                }
                            };
                            addLock.setCallback(netCallback);
                            new Thread(addLock).start();
                            callback.setMessage("(!KeyOK*)");
                            bg.discoverServices();
                        }
                        else
                            Toast.makeText(search_door_lock.this, getString(R.string.info_is_empty), Toast.LENGTH_SHORT).show();
                        lockstate=false;
                    }
                    else if(result.contains("Locked")&&!lockstate){
                        Toast.makeText(search_door_lock.this, getString(R.string.lock_is_haven), Toast.LENGTH_SHORT).show();
                        lockstate=false;
                        bg.close();
                    }
                    else if(!oneTime){
                        callback.setMessage("(!Key"+lockKey+"*)");
                        bg.discoverServices();
                        oneTime=true;
                    }
                    else if(!twoTime){
                        callback.setMessage("(!Key"+lockKey+"*)");
                        bg.discoverServices();
                        twoTime=true;
                    }
                    else{
                        lockstate=false;
                    }
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
                BluetoothGattCharacteristic WriteCharacteristic=Service.getCharacteristic(WRITE);
                final BluetoothGattCharacteristic ReadCharacteristic=Service.getCharacteristic(NOTIFY);
                if(WriteCharacteristic!=null&&ReadCharacteristic!=null)
                {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            gatt.setCharacteristicNotification(ReadCharacteristic,true);
                            boolean isEnableNotification = gatt.setCharacteristicNotification(ReadCharacteristic,true);
                            if(isEnableNotification) {
                                List<BluetoothGattDescriptor> descriptorList = ReadCharacteristic.getDescriptors();
                                if(descriptorList != null && descriptorList.size() > 0) {
                                    for(BluetoothGattDescriptor descriptor : descriptorList) {
                                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                        gatt.writeDescriptor(descriptor);
                                    }
                                }
                            }
                        }
                    }).start();
                  //  mBleService.setCharacteristicNotification(ReadCharacteristic,true);//设置通知
                    WriteCharacteristic.setValue(message);
                    gatt.writeCharacteristic(WriteCharacteristic);
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(bg!=null){
            bg.disconnect();
            bg.close();
        }
        if(bluetooth!=null){
            bluetooth.cancelDiscovery();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    protected void onPause(){
        super.onPause();
        if(bg!=null){
            bg.disconnect();
            bg.close();
        }
        doUnBindService();
        if(receiver!=null)
            unregisterReceiver(receiver);
    }
    private boolean startSearthBltDevice(Context context){
        return true;
    }
    BluetoothAdapter.LeScanCallback leScanCallback=new BluetoothAdapter.LeScanCallback() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (device!=null&&!devices.contains(device))   //判断是否已经搜索到
            {
         //       addButtonUI(device);
                devices.add(device);
            }
        }
    };







    //绑定服务
    public void doBindService() {
        Intent serviceIntent = new Intent(search_door_lock.this, BleService.class);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    //解绑服务
    public void doUnBindService() {
        if (mIsBind) {
            unbindService(serviceConnection);
            mBleService = null;
            mIsBind = false;
        }
    }
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIsBind = true;
            mBleService = ((BleService.LocalBinder) service).getService();
            //Ble初始化操作
            if (mBleService.initialize()) {
                //打开蓝牙
                //Ble扫描回调
                mBleService.setOnLeScanListener(new BleService.OnLeScanListener(){
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
                    @Override
                    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                        //每当扫描到一个Ble设备时就会返回，（扫描结果重复的库中已处理）
                        if(device!=null&&!devices.contains(device)) {
                            devices.add(device);
                        //    addButtonUI(device);
                        }
                    }
                });
                mBleService.scanLeDevice(true);
                Toast.makeText(getApplicationContext(), "蓝牙已打开", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getApplicationContext(), "不支持蓝牙", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBleService = null;
            mIsBind = false;
        }
    };








}
