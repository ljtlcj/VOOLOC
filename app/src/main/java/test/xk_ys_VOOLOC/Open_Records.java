package test.xk_ys_VOOLOC;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import test.xk_ys_VOOLOC.AboutFile.BaseApplication;
import test.xk_ys_VOOLOC.AboutFile.LockInfo;
import test.xk_ys_VOOLOC.AboutFile.OpenRecord;
import test.xk_ys_VOOLOC.AboutFile.StringToDate;
import test.xk_ys_VOOLOC.BluetoothGattCallback.BluetoothReceiver;
import test.xk_ys_VOOLOC.BluetoothGattCallback.GattCallback;
import test.xk_ys_VOOLOC.MyAdapter.MyOpenRecordAdapter;
import test.xk_ys_VOOLOC.Net.AddOpenRecord;
import test.xk_ys_VOOLOC.Net.GainOpenRecord;
import test.xk_ys_VOOLOC.Net.NetCallback;

public class Open_Records extends AppCompatActivity {
    @BindView(R.id.list)
    public ListView listView;
    @BindView(R.id.title)
    public LinearLayout title;
    @BindView(R.id.title_content)
    public TextView titleContent;
    private String lockId;
    private String account;
    private LockInfo lockInfo;
    @BindView(R.id.back)
    public TextView back;


    private    int count=0;


    private GainOpenRecord gainOpenRecord;
    private static final String TAG = "Open_Records";
    private BluetoothDevice device;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open__records);
        ButterKnife.bind(this);
        account=getIntent().getStringExtra("account");
        lockInfo=getIntent().getParcelableExtra("lockInfo");
        lockId=lockInfo.getLockId();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if(BaseApplication.netVersion==0){
            gainOpenRecord=new GainOpenRecord(this,account,lockInfo.getLockId_2());

        }
        else {
            gainOpenRecord = new GainOpenRecord(this, account, lockId);
        }

        gainOpenRecord.setCallback(netCallback);

        new Thread(gainOpenRecord).start();



        getBluetooth();
        i=new Intent(Open_Records.this,BluetoothReceiver.class);
        i.setAction("woolock.bluetooth.result");
    }
    NetCallback netCallback=new NetCallback() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void execute(String result) {
            Log.e("result",result);
            Vector<OpenRecord> v = new Vector<>();
            if(BaseApplication.netVersion==0){
                JSONArray jsonArray= null;
                try {
                    jsonArray = new JSONArray(result);
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                        OpenRecord openRecord=new OpenRecord(jsonObject.getString("time")+"000");
                        openRecord.type=jsonObject.getString("type");
                        openRecord.name=jsonObject.getString("name");
                        openRecord.detail=jsonObject.getString("detial");
                        v.add(openRecord);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                Pattern p = Pattern.compile("[0-9]{13}");
                Matcher m = p.matcher(result);
                while (m.find()) {
                    OpenRecord o=new OpenRecord(m.group(0));
                    o.type="1";
                    o.detail="<null>";
                    o.name="<null>";
                    v.add(o);
                }
            }
            v.add(new OpenRecord("今天"));
            v.add(new OpenRecord("最近三天"));
            v.add(new OpenRecord("最近一周"));
            v.add(new OpenRecord("其他"));
            Collections.sort(v,comparator);
            final MyOpenRecordAdapter arrayAdapter=new MyOpenRecordAdapter(Open_Records.this,v);
            listView.setAdapter(arrayAdapter);
            listView.setDivider(null);
/*            listView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                    int i=listView.getFirstVisiblePosition();
                    OpenRecord s=(OpenRecord)arrayAdapter.getItem(i);
                    Log.e("ooooooooooo","ooooooooooooo");

                }
            });*/
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    MyOpenRecordAdapter.Holder holder=(MyOpenRecordAdapter.Holder)view.getTag();
                    String s=holder.textView3.getText().toString();
                    if(s.equals("今天")){
                        arrayAdapter.today=!arrayAdapter.today;
                    }
                    else if(s.equals("最近三天")){
                        arrayAdapter.threeDay=!arrayAdapter.threeDay;
                    }
                    else if(s.equals("最近一周")){
                        arrayAdapter.weekend=!arrayAdapter.weekend;
                    }
                    else if(s.equals("其他")){
                        arrayAdapter.other=!arrayAdapter.other;
                    }
                    arrayAdapter.notifyDataSetChanged();
                }
            });
        }
        @Override
        public void error(String result) {
            Log.e("======","=====");
        }
    };
//******************2017.11.25*****************
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void getBluetooth()
    {
        //检查蓝牙地址
        if (BluetoothAdapter.getDefaultAdapter().checkBluetoothAddress(lockInfo.getBluetoothAddress()))
        {
            device=BluetoothAdapter.getDefaultAdapter().getRemoteDevice(lockInfo.getBluetoothAddress());
            if(device==null)
            {
                Toast.makeText(this, getString(R.string.lock_info_error), Toast.LENGTH_SHORT).show();
                finish();
            }
            else{
                getGatt();
            }
        }
        else{
            Toast.makeText(this, getString(R.string.lock_info_error), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private GattCallback callback;
    private BluetoothGatt bluetoothGatt;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void getGatt()
    {
        callback = new GattCallback(this);
        callback.setCallback(gcb);
        bluetoothGatt = device.connectGatt(this,false,callback);
        if(bluetoothGatt==null)
        {
            Toast.makeText(this, "bluetoothGatt is null", Toast.LENGTH_SHORT).show();
        }
        else{
            bluetoothGatt.connect();
        }
    }
    private Intent i;
    GattCallback.Callback gcb=new GattCallback.Callback() {
        @Override
        public void readCallback(String result) {
            if(result.contains("{")&&result.contains("}"))
            {
               Log.e("count===========",String.valueOf(count++));
                for(int i=0;i<result.length();i++) {
                    Log.e("---------", i + "--->" + String.valueOf(Integer.valueOf(result.charAt(i))));
                }
                Log.e("result",result);
                int c=result.indexOf('A');
                String str=result.substring(1,c);
                int month=result.charAt(15)-48;
                int day=result.charAt(16)-48;
                int hour=result.charAt(17)-48;
                int min=result.charAt(18)-48;
                String a=getCurrentYear()+"-"+month+"-"+day+" "+hour+":"+min+":"+"00";
                long date=StringToDate.TransferDate(a);
                Log.e("a",a);
                Log.e("date",String.valueOf(date));
                AddOpenRecord addOpenRecord=new AddOpenRecord(Open_Records.this,account,lockInfo.getLockId(),String.valueOf(date),"2","<null>",str);
                new Thread(addOpenRecord).start();
            }
            if(result.contains("AOK")){
                if(bluetoothGatt!=null){
                    i.putExtra("result",0x100);
                    i.putExtra("message",result);
                    sendBroadcast(i);
                    bluetoothGatt.disconnect();
                    bluetoothGatt.close();

                    gainOpenRecord.setCallback(netCallback);

                    new Thread(gainOpenRecord).start();
                }
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void connectCallback() {
            callback.setMessage("(!A*)");
            bluetoothGatt.discoverServices();
        }
        @Override
        public void unConnectCallback() {
        }
    };
    public static String getCurrentYear(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        Date date = new Date();
        return sdf.format(date);
    }
    //------------------------------------


    //long to 2017-12-22
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
     Comparator comparator=new Comparator<OpenRecord>() {
        @Override
        public int compare(OpenRecord O1, OpenRecord O2) {
            String o1=O1.time;
            String o2=O2.time;
//            long a=0;
//            long b=0;
//            a=Long.valueOf(o1);
//            b=Long.valueOf(o2);
            if(o1.equals("今天"))
            {
                return -1;
            }
            else if(o2.equals("今天"))
            {
                return 1;
            }
            long a=0;
            long b=0;
            if(o1.equals("最近三天")){
                a=Time(new Date().getTime());
            }
            else if(o1.equals("最近一周")){
                a=Time(new Date().getTime()-259200000);
            }
            else if(o1.equals("其他")){
                a=Time(new Date().getTime()-604800000);
            }
            else {
                a = Long.valueOf(o1);
            }
            if(o2.equals("最近三天"))
            {
                b=Time(new Date().getTime());
            }
            else if(o2.equals("最近一周")){
                b=Time(new Date().getTime()-259200000);
            }
            else if(o2.equals("其他")){
                b=Time(new Date().getTime()-604800000);
            }
            else{
                b=Long.valueOf(o2);
            }
             return a>= b?-1:1;
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        finish();
        if(bluetoothGatt!=null){
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
            bluetoothGatt=null;
        }
    }
}
