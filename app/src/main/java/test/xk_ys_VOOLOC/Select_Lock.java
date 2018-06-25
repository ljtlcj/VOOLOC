package test.xk_ys_VOOLOC;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import test.xk_ys_VOOLOC.AboutFile.BaseApplication;
import test.xk_ys_VOOLOC.AboutFile.LockInfo;
import test.xk_ys_VOOLOC.AboutFile.StringToDate;
import test.xk_ys_VOOLOC.BluetoothGattCallback.BluetoothReceiver;
import test.xk_ys_VOOLOC.BluetoothGattCallback.GattCallback;
import test.xk_ys_VOOLOC.MyView.MyProgressDialog;
import test.xk_ys_VOOLOC.Net.AddOpenRecord;
import test.xk_ys_VOOLOC.Net.GainLockList1;
import test.xk_ys_VOOLOC.Net.GainLockPower;
import test.xk_ys_VOOLOC.Net.NetCallback;
import test.xk_ys_VOOLOC.Net.UpdateLockPower;

public class Select_Lock extends Activity {
    @BindView(R.id.back)
    public TextView back;
    @BindView(R.id.lock_setting)
    public TextView lockSetting;
    @BindView(R.id.power)
    public TextView power;
    @BindView(R.id.power_photo)
    public TextView powerPhoto;
    @BindView(R.id.start_time)
    public TextView startTime;
    @BindView(R.id.end_time)
    public TextView endTime;
    @BindView(R.id.lock_name)
    public TextView lockName;
    @BindView(R.id.open_lock)
    public LinearLayout openLock;
    @BindView(R.id.send_password)
    public LinearLayout sendLock;
    @BindView(R.id.giver)
    public LinearLayout userGiver;

    public LockInfo lockInfo;

    private BluetoothDevice device;
    private GattCallback callback;
    private BluetoothGatt bluetoothGatt;

    private String account;
    private String password;
    static public Activity activity;

    private Intent bluetoothIntent;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {
                openLock.setBackgroundResource(R.color.Text_color);
                openLock.setClickable(false);
            } else if (msg.what == 0x124) {
                openLock.setBackgroundResource(R.color.line);
                openLock.setClickable(true);
                if (bluetoothGatt != null) {
                    bluetoothGatt.disconnect();
                    bluetoothGatt.close();
                    bluetoothGatt = null;
                }
            } else if (msg.what == 0x125) {
                if (Select_Lock.this != null)
                    MyProgressDialog.remove();
                ;
            }
            super.handleMessage(msg);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select__lock);
        ButterKnife.bind(this);
        activity = this;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
        }
        account = getSharedPreferences("UserInformation", MODE_PRIVATE).getString("account", null);
        password = getSharedPreferences("UserInformation", MODE_PRIVATE).getString("password", null);
        if (account == null || password == null)
            finish();
        getLockInformation();
        initialize();
        long now = new Date().getTime() / 1000;
        long startTime = Long.valueOf(lockInfo.getStartTime());
        long endTime = Long.valueOf(lockInfo.getEndTime());
        Log.e("now", String.valueOf(now));
        Log.e("startTime", String.valueOf(startTime));
        Log.e("endTime", String.valueOf(endTime));
        if (now >= startTime && now <= endTime) {
        } else if (now >= startTime && endTime == 0) {
        } else {
            finish();
        }
    }

    public void getLockInformation() {
        Intent i = getIntent();
        lockInfo = i.getParcelableExtra("lockInfo");
        if (lockInfo == null) {
            Toast.makeText(this, getString(R.string.lock_info_error), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void initialize() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        lockSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Select_Lock.this, Lock_of_Setting.class);
                i.putExtra("lockInfo", lockInfo);
                startActivity(i);
            }
        });
        lockName.setText(lockInfo.getLockName());
        //设置开始时间和结束时间
        if (lockInfo.getStartTime().equals("0")) {
            startTime.setText(getString(R.string.forever));
        } else {
            if (BaseApplication.netVersion == 0) {
                startTime.setText(StringToDate.times(String.valueOf(Long.valueOf(lockInfo.getStartTime()) * 1000)));
            } else {
                startTime.setText(StringToDate.times(lockInfo.getStartTime()));
            }
        }
        if (lockInfo.getEndTime().equals("0")) {
            endTime.setText(getString(R.string.forever));
        } else {
            if (BaseApplication.netVersion == 0) {
                endTime.setText(StringToDate.times(String.valueOf(Long.valueOf(lockInfo.getEndTime()) * 1000)));
            } else {
                endTime.setText(StringToDate.times(lockInfo.getEndTime()));
            }
        }
        if (lockInfo.getPower().equals("3")) {
            sendLock.setVisibility(View.INVISIBLE);
            userGiver.setVisibility(View.INVISIBLE);
        }
        //发送密码
        sendLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Select_Lock.this, Authorization_Phone.class);
                i.putExtra("lockInfo", lockInfo);
                i.putExtra("order", 1);
                startActivity(i);
            }
        });
        sendLock.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setBackgroundResource(R.drawable.rectangle2_1);
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    v.setBackgroundResource(R.drawable.rectangle2_1);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.setBackgroundResource(R.drawable.rectangle2);
                }
                return false;
            }
        });


        userGiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Select_Lock.this, Authorization_Phone.class);
                i.putExtra("lockInfo", lockInfo);
                if (lockInfo.getPower().equals("1")) {
                    i.putExtra("order", 2);
                } else {
                    i.putExtra("order", 3);
                }
                startActivity(i);
            }
        });

        userGiver.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setBackgroundResource(R.drawable.rectangle3_1);
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    v.setBackgroundResource(R.drawable.rectangle3_1);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.setBackgroundResource(R.drawable.rectangle3);
                }
                return false;
            }
        });

//        openLock.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if(event.getAction() == MotionEvent.ACTION_DOWN){
////                    v.setBackgroundResource(R.drawable.rectangle1_1);
//                }else if (event.getAction() == MotionEvent.ACTION_MOVE){
////                    v.setBackgroundResource(R.drawable.rectangle1_1);
//                }else if(event.getAction() == MotionEvent.ACTION_UP){
//                    v.setBackgroundResource(R.drawable.rectangle1);
//                }
//                return false;
//            }
//        });


        if (lockInfo.getBluetoothAddress().equals("16:07:05:00:00:00")) {

            openLock.setBackgroundResource(R.color.danger);
            openLock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(Select_Lock.this, "首次连接此门锁，正在尝试连接", Toast.LENGTH_SHORT).show();
                    SearchBluetoothService.bluetoothName = lockInfo.getBluetoothName();
                    bluetoothIntent = new Intent(Select_Lock.this, SearchBluetoothService.class);
                    startService(bluetoothIntent);
                }
            });

        } else {
            connBluetooth conn = new connBluetooth("(!" + lockInfo.getLockKey() + ".O*)");
            openLock.setOnClickListener(conn);
        }


        //    showNotification();
    }

    @Override
    protected void onResume() {
        super.onResume();

        GainLockPower gain = new GainLockPower(this, lockInfo.getLockKey());
        gain.setCallback(new NetCallback() {
            @Override
            public void execute(String result) {
                power.setText(result);
                int s = Integer.valueOf(result);
                if (s >= 90) {
                    powerPhoto.setBackgroundResource(R.drawable.battery);
                } else if (s >= 75) {
                    powerPhoto.setBackgroundResource(R.drawable.battery1);
                } else if (s >= 60) {
                    powerPhoto.setBackgroundResource(R.drawable.battery2);
                } else if (s >= 40) {
                    powerPhoto.setBackgroundResource(R.drawable.battery3);
                } else if (s >= 20) {
                    powerPhoto.setBackgroundResource(R.drawable.battery4);
                } else {
                    //powerPhoto.setBackgroundResource(R.drawable.battery5);
                    powerPhoto.setBackgroundResource(R.drawable.flashing);
                    AnimationDrawable animationDrawable1 = (AnimationDrawable) powerPhoto.getBackground();
                    animationDrawable1.start();
                }

                Log.e("result", "log.eeeee");
            }

            @Override
            public void error(String result) {
                Log.e("result", result);
            }
        });
        new Thread(gain).start();
        GainLockList1 gainLockList = new GainLockList1(this, account, password);
        gainLockList.setCallback(new NetCallback() {
            @Override
            public void execute(String result) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    boolean b = false;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Log.e("lockKey", jsonObject.getString("key"));
                        if (jsonObject.getString("key").equals(lockInfo.getLockKey())) {
                            b = true;
                            Log.e("result", "----");
                            break;
                        }
                    }
                    if (!b) {
                        Toast.makeText(Select_Lock.this, "门锁不存在", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void error(String result) {

            }
        });
        new Thread(gainLockList).start();

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onStart() {
        super.onStart();
        //   getBluetooth();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    protected void onPause() {
        super.onPause();
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
            bluetoothGatt = null;
        }
        if (bluetoothIntent != null) {
            stopService(bluetoothIntent);
            bluetoothIntent = null;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void getBluetooth() {
        state = false;
        //检查蓝牙地址
        if (BluetoothAdapter.getDefaultAdapter().checkBluetoothAddress(lockInfo.getBluetoothAddress())) {
            device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(lockInfo.getBluetoothAddress());

            if (device == null) {
                Toast.makeText(this, getString(R.string.lock_info_error), Toast.LENGTH_SHORT).show();
                finish();
            } else {
                getGatt();
            }
        } else {
            Toast.makeText(this, getString(R.string.lock_info_error), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void getGatt() {
        callback = new GattCallback(this);
        callback.setCallback(gcb);
        bluetoothGatt = device.connectGatt(this, false, callback);
        if (bluetoothGatt == null) {
            Toast.makeText(this, "bluetoothGatt is null", Toast.LENGTH_SHORT).show();
        } else {
            bluetoothGatt.connect();
            //   connBluetooth conn=new connBluetooth("(!"+lockInfo.getLockKey()+".O*)");
            //   openLock.setOnClickListener(conn);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    class connBluetooth implements View.OnClickListener {
        private String control;

        public connBluetooth(String control) {
            this.control = control;
        }

        @Override
        public void onClick(View v) {
            if (BaseApplication.time == 0) {
                getBluetooth();
                handler.sendEmptyMessageDelayed(0x125, 4000);
                MyProgressDialog.show(Select_Lock.this, "Opening...", false, null);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(Select_Lock.this);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.setTitle("系统提示")
                        .setMessage("门锁已打开！");
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }

        public void send(String message) {
            getBluetooth();
        }
    }


    private boolean state = false;
    GattCallback.Callback gcb = new GattCallback.Callback() {
        @Override
        public void readCallback(String result) {
            state = true;
            Log.e("read", result);
            final Intent i = new Intent(Select_Lock.this, BluetoothReceiver.class);
            i.setAction("woolock.bluetooth.result");
            if (result.contains("1111")) {

            }
            if (result.contains("POWER")) {

                AddOpenRecord addOpenRecord;

                if (BaseApplication.netVersion == 0) {
                    addOpenRecord = new AddOpenRecord(Select_Lock.this, account, lockInfo.getLockId(), String.valueOf(new Date().getTime()), "1", account, "<null>");
                } else {
                    addOpenRecord = new AddOpenRecord(Select_Lock.this, account, lockInfo.getLockId(), String.valueOf(new Date().getTime()), null, null, null);
                }

                addOpenRecord.setCallback(new NetCallback() {
                    @Override
                    public void execute(String result) {
                        i.putExtra("result", 0x123);
                        sendBroadcast(i);
                    }

                    @Override
                    public void error(String result) {

                    }
                });
                new Thread(addOpenRecord).start();


                // String p=result.substring(5,result.length());
                String num = "POWER[0-9]{1,3}";
                Pattern pattern = Pattern.compile(num);
                Matcher matcher = pattern.matcher(result);
                if (matcher.find()) {
                    String re = matcher.group(0).substring(5, matcher.group(0).length()) + "%";
                    power.setText(re);
                    int s = Integer.valueOf(matcher.group(0).substring(5, matcher.group(0).length()));
                    if (s >= 90) {
                        powerPhoto.setBackgroundResource(R.drawable.battery);
                    } else if (s >= 75) {

                        powerPhoto.setBackgroundResource(R.drawable.battery1);
                    } else if (s >= 60) {
                        powerPhoto.setBackgroundResource(R.drawable.battery2);
                    } else if (s >= 40) {
                        powerPhoto.setBackgroundResource(R.drawable.battery3);
                    } else if (s >= 20) {
                        powerPhoto.setBackgroundResource(R.drawable.battery4);
                    } else {
                        powerPhoto.setBackgroundResource(R.drawable.battery5);
                    }
                    UpdateLockPower update = new UpdateLockPower(Select_Lock.this, lockInfo.getLockKey(), re);
                    update.setCallback(new NetCallback() {
                        @Override
                        public void execute(String result) {
                            i.putExtra("result", 0x123);
                            sendBroadcast(i);
                        }

                        @Override
                        public void error(String result) {

                        }
                    });
                    new Thread(update).start();
                }
            }
            if (result.contains("3333")) {
                i.putExtra("result", 0x124);
                sendBroadcast(i);
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void connectCallback() {
            MyProgressDialog.remove();
            BaseApplication.time = 3;
            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!state && bluetoothGatt != null) {
                        state = false;
                        bluetoothGatt.disconnect();
                        bluetoothGatt.close();
                    }
                }
            }).start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    handler.sendEmptyMessageDelayed(0x123, 0);
                    while (BaseApplication.time != 0) {
                        BaseApplication.time--;
                        Log.e("time", String.valueOf(BaseApplication.time));
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    handler.sendEmptyMessageDelayed(0x124, 0);
                }
            }).start();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    callback.setMessage("(!" + lockInfo.getLockKey() + ".O*)");
                    bluetoothGatt.discoverServices();
                }
            }).start();
            showNotification(1);
        }

        @Override
        public void unConnectCallback() {
            showNotification(2);
        }
    };

    public void showNotification(int i) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(this);
        if (i == 1) {
            builder.setContentText(lockInfo.getLockName() + "智能门锁已连上!");
            builder.setTicker("Woolock智能门锁已连接上门锁" + lockInfo.getLockName());
        } else if (i == 2) {
            builder.setContentText(lockInfo.getLockName() + "智能门锁已断开!");
            builder.setTicker("触摸可重新连接门锁");
        } else {
            builder.setContentText("门锁未连接");
            builder.setTicker("触摸进行连接");
        }
        builder.setContentTitle("WooLock智能门锁");
        builder.setLargeIcon(((BitmapDrawable) getResources().getDrawable(R.drawable.icon)).getBitmap());

        builder.setSmallIcon(R.drawable.icon);
        builder.setWhen(System.currentTimeMillis());
        //    builder.setDefaults(Notification.DEFAULT_ALL);
        Intent intent = new Intent(this, Select_Lock.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        builder.setPriority(Notification.PRIORITY_DEFAULT);
        //    builder.setLights(Color.RED,500,500);
        Notification notification = builder.build();
        notificationManager.notify(1, notification);
    }

    public void update() {
        openLock.setBackgroundResource(R.drawable.rectangle1);
        connBluetooth conn = new connBluetooth("(!" + lockInfo.getLockKey() + ".O*)");
        openLock.setOnClickListener(conn);
        if (bluetoothIntent != null) {
            stopService(bluetoothIntent);
            bluetoothIntent = null;
        }
    }
}
