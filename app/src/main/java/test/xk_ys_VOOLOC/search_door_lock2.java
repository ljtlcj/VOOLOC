package test.xk_ys_VOOLOC;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;
import test.xk_ys_VOOLOC.AboutFile.BaseApplication;
import test.xk_ys_VOOLOC.AboutFile.GetDate;
import test.xk_ys_VOOLOC.AboutFile.isEmpty;
import test.xk_ys_VOOLOC.BluetoothGattCallback.DeviceAdapter1;
import test.xk_ys_VOOLOC.BluetoothGattCallback.GattAppService;
import test.xk_ys_VOOLOC.BluetoothGattCallback.GattCallback;
import test.xk_ys_VOOLOC.BluetoothGattCallback.LockList;
import test.xk_ys_VOOLOC.MyView.MyProgressDialog;
import test.xk_ys_VOOLOC.Net.AddLock;
import test.xk_ys_VOOLOC.Net.GainLockKey;
import test.xk_ys_VOOLOC.Net.NetCallback;
import test.xk_ys_VOOLOC.Net.UpdateLockPower;

public class search_door_lock2 extends AppCompatActivity implements BluetoothAdapter.LeScanCallback{
    @BindView(R.id.myList)
    public ListView listView;
    @BindView(R.id.back) public TextView back;
    DeviceAdapter1 mDeviceAdapter;
    BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning = false;
    private Vector<LockList> lockLists=new Vector<>();
    BluetoothGatt bluetoothGatt;
    GattCallback gattCallback;

    private String account;
    private String name;
    private String address;
    private boolean lockstate=false;
    private String lockKey=null;
    private boolean oneTime=false;
    private boolean twoTime=false;
    private String password;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message message){
            if(message.what==0x123){
                MyProgressDialog.remove();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_door_lock2);
        ButterKnife.bind(this);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getUserInfo();
        mDeviceAdapter = new DeviceAdapter1(this,lockLists);
        listView.setAdapter(mDeviceAdapter);
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null) return;
        mBluetoothAdapter = bluetoothManager.getAdapter();
        gattCallback=new GattCallback(search_door_lock2.this);
        gattCallback.setCallback(gcb);
        gattCallback.setMessage("(!S*)");
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,final int position, long id) {
                final LockList lockList=lockLists.get(position);
                bluetoothGatt=lockList.device.connectGatt(search_door_lock2.this,false, gattCallback);
                address=lockList.device.getAddress();
                name=lockList.device.getName();
                bluetoothGatt.connect();
                MyProgressDialog.show(search_door_lock2.this,"connecting...",false,null);
                handler.sendEmptyMessageDelayed(0x123,10000);
                Log.e("device",lockList.device.getAddress());
            }
        });
    }

    public void getUserInfo(){
        SharedPreferences info=getSharedPreferences("UserInformation",MODE_PRIVATE);
        account=info.getString("account",null);
        password=info.getString("password",null);
        if(isEmpty.StringIsEmpty(account)||isEmpty.StringIsEmpty(password)){
            finish();
        }
    }
    @Override
    public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(device.getName()!=null) {
                    boolean changed=true;
                    for (int i = 0; i < lockLists.size(); i++) {
                        if (lockLists.get(i).device.getName().equals(device.getName())) {
                            lockLists.get(i).state = rssi;
                            changed = false;
                            break;
                        }
                    }
                    if (changed) {
                        LockList lockList = new LockList(device, rssi);
                        lockLists.add(lockList);
                    }
                    mDeviceAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GattAppService.GATT_DEVICE_FOUND);
        registerReceiver(GattDeviceReceiver, intentFilter);
        scan(true);
    }

    @Override
    public void onPause() {
        unregisterReceiver(GattDeviceReceiver);
        scan(false);
        lockLists.clear();
        mDeviceAdapter.notifyDataSetChanged();
        if(bluetoothGatt!=null)
        {
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
        }
        super.onPause();
    }
    @Override
    public void onStop(){

        if(bluetoothGatt!=null){
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
            bluetoothGatt=null;
        }
        super.onStop();
    }
    private void scan(boolean enable) {
        if (mBluetoothAdapter == null) return;

        if (enable)
            mBluetoothAdapter.startLeScan(this);
        else
            mBluetoothAdapter.stopLeScan(this);

        mScanning = enable;
        invalidateOptionsMenu();
    }
    private final BroadcastReceiver GattDeviceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final BluetoothDevice device = intent.getParcelableExtra(GattAppService.EXTRA_DEVICE);
            final int rssi = intent.getIntExtra(GattAppService.EXTRA_RSSI, 0);
            final int source = intent.getIntExtra(GattAppService.EXTRA_SOURCE, 0);

            if (GattAppService.GATT_DEVICE_FOUND.equals(intent.getAction())) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(device.getName()!=null) {
                            boolean changed=true;
                            for (int i = 0; i < lockLists.size(); i++) {
                                if (lockLists.get(i).device.getName().equals(device.getName())) {
                                    lockLists.get(i).state = rssi;
                                    changed = false;
                                    break;
                                }
                            }
                            if (changed) {
                                LockList lockList = new LockList(device, rssi);
                                lockLists.add(lockList);
                            }
                            mDeviceAdapter.notifyDataSetChanged();
                        }
                    }
                } );
            }
        }
    };
    GattCallback.Callback gcb=new GattCallback.Callback() {
        @Override
        public void readCallback(String result) {

            if(result.contains("Noneuser")&&!lockstate) {
                lockstate = true;
                final GainLockKey gainLockKey=new GainLockKey(search_door_lock2.this,address);
                NetCallback netCallback=new NetCallback() {
                    @Override
                    public void execute(String result) {
                        if(result!=null) {
                            lockKey=result;
                            gattCallback.setMessage("(!Key" + result + "*)");
                            bluetoothGatt.discoverServices();
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
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        String message = GetDate.getDate();
                        gattCallback.setMessage("(" + message+ ")");
                        bluetoothGatt.discoverServices();
                    }
                });
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(lockKey!=null) {
                    AddLock addLock=new AddLock(search_door_lock2.this,account,password,lockKey,name,address);

                    SharedPreferences sharedPreferences=getSharedPreferences(account,MODE_PRIVATE);
                    final SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("lockKey",lockKey);
                    editor.putString("name",name);
                    editor.putString("address",address);
                    editor.commit();

                    NetCallback netCallback=new NetCallback() {
                        @Override
                        public void execute(String result) {
                            operation(result);
                        }
                        @Override
                        public void error(String result) {
                            editor.putBoolean("upload",true);
                            editor.commit();
                            operation(null);
                        }
                    };
                    addLock.setCallback(netCallback);
                    new Thread(addLock).start();
                }
                else
                    Toast.makeText(search_door_lock2.this, getString(R.string.info_is_empty), Toast.LENGTH_SHORT).show();
                lockstate=false;
            }
            else if(result.contains("Locked")&&!lockstate){
                Toast.makeText(search_door_lock2.this, getString(R.string.lock_is_haven), Toast.LENGTH_SHORT).show();
                lockstate=false;
                bluetoothGatt.close();
                MyProgressDialog.remove();
            }
            else if(!oneTime){
                gattCallback.setMessage("(!Key"+lockKey+"*)");
                bluetoothGatt.discoverServices();
                oneTime=true;
            }
            else if(!twoTime){
                gattCallback.setMessage("(!Key"+lockKey+"*)");
                bluetoothGatt.discoverServices();
                twoTime=true;
            }
            else{
                lockstate=false;
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void connectCallback() {
            MyProgressDialog.remove();
            MyProgressDialog.show(search_door_lock2.this,"adding...",false,null);
            handler.sendEmptyMessageDelayed(0x123,10000);
            gattCallback.setMessage("(!S*)");
            bluetoothGatt.discoverServices();
        }
        @Override
        public void unConnectCallback() {
        }
    };
    public void operation(String result){
        UpdateLockPower update=new UpdateLockPower(search_door_lock2.this,lockKey,"100");
        new Thread(update).start();
        MyProgressDialog.remove();
        //    Intent i=new Intent(search_door_lock2.this,Lock_Name.class);
        Intent i=new Intent(search_door_lock2.this,Reset.class);
        i.putExtra("lockKey",lockKey);
        i.putExtra("bluetooth",address);
        /**
         * {"code" : 1, "message" : "操作成功", "data" : {" lockId " : "4549798715854848 "} }
         */
        if(BaseApplication.netVersion==0){
            i.putExtra("lockId",lockKey);
            if(result!=null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    i.putExtra("lockId2", jsonObject.getString("lockid"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            //    i.putExtra("lockId2",lockId);
        }
        else {
            try {
                JSONObject json = new JSONObject(result);
                JSONObject data = new JSONObject(json.getString("data"));
                i.putExtra("lockId", data.getString("lockId"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        startActivity(i);
        finish();
    }
}
