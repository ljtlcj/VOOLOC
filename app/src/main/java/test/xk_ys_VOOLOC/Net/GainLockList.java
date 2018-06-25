package test.xk_ys_VOOLOC.Net;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import test.xk_ys_VOOLOC.AboutFile.BaseApplication;
import test.xk_ys_VOOLOC.AboutFile.HttpUtils;
import test.xk_ys_VOOLOC.AboutFile.LockInfo;
import test.xk_ys_VOOLOC.AboutFile.isEmpty;
import test.xk_ys_VOOLOC.MyAdapter.MyLockAdapter;
import test.xk_ys_VOOLOC.R;

/**
 * Created by 柯东煜 on 2017/9/28.
 */

public class GainLockList extends BaseNet {
    private final String GG="http://123.207.57.91:8080/WooLock/Home/Lock/readAllLock";
    private final String JY="http://120.77.211.48/WooLock.php /Home/lock/ReadAllLock";
    private Handler handler=new Handler();
    private Context context;
    private Map<String,String> post=new HashMap<>();
    private Vector<String> lockName;

    private Vector<LockInfo> lockInfo;
    private ListView listView;
    public GainLockList(Context context,String account,String password,ListView listView){
        if(BaseApplication.netVersion==0){
            post.put("phone",account);
        }
        else{
            post.put("account",account);
        }
        post.put("password",password);
        this.listView=listView;
        this.context=context;
    }
    public void setListView(ListView listView) {
        this.listView=listView;
    }
    @Override
    public void run(){
      //  String url="http://123.207.57.91:8080/WooLock/Home/Lock/readAllLock";
        final String url;
        if(BaseApplication.netVersion==0){
            url=JY;
        }
        else{
            url=GG;
        }
        HttpUtils.doPost(url, post, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                final String ConnectResult=e.toString();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (ConnectResult.contains("ConnectException")) {
                            Toast.makeText(context, context.getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, context.getResources().getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                            //                    Toast.makeText(Login.this, ConnectResult, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()) {
                    final String result = response.body().string();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(BaseApplication.netVersion==0){
                                resultJY(result);
                            }
                            else{
                                resultGG(result);
                            }
                        }
                    });
                }
                //关闭防止内存泄漏
                if (response.body() != null) {
                    response.body().close();
                }
            }
        });
    }
    private void resultGG(String result){
        if (result.contains("{\"code\":1,\"message\":\"操作成功")) {
            try {
                JSONObject myJsonArray = new JSONObject(result);
/**
 * {"code":1,"message":"操作成功","data":{"lockInfo":[{"lockId":8490465494175744,"bluetooth":"aa:bb:cc:dd:ee:ff","startTime":1510109510000,"lockKey":"00000000","power":1,"lockName":"WL001"}]}}
 */
                lockName=new Vector<String>();
                lockInfo=new Vector<LockInfo>();
                if(result.contains("data")) {
                    JSONObject lockInfos = new JSONObject(myJsonArray.getString("data"));
                    JSONArray locks = new JSONArray(lockInfos.getString("lockInfo"));
                    for (int i = 0; i < locks.length(); i++) {
                        JSONObject myjObject = locks.getJSONObject(i);
                        LockInfo lock_info = new LockInfo();
                        lock_info.setLockId(myjObject.getString("lockId"));
                        if (!BluetoothAdapter.getDefaultAdapter().checkBluetoothAddress(myjObject.getString("btAddress")) || myjObject.getString("lockKey").length() != 8) {
                            continue;
                        }
                        lock_info.setLockKey(myjObject.getString("lockKey"));
                        lock_info.setBluetoothAddress(myjObject.getString("btAddress"));
                        //    lock_info.setAddress(myjObject.getString("address"));
                        lock_info.setStartTime(myjObject.getString("startTime"));
                        lock_info.setPower(myjObject.getString("power"));
                        if (!lock_info.getPower().equals("1")) {
                            lock_info.setEndTime(myjObject.getString("endTime"));
                        } else {
                            lock_info.setEndTime("0");
                        }
                        if (!myjObject.isNull("lockName")&&!isEmpty.StringIsEmpty(myjObject.getString("lockName"))) {
                            lock_info.setLockName(myjObject.getString("lockName"));
                        } else {
                            lock_info.setLockName(myjObject.getString("btName"));
                        }
                        lock_info.setBluetoothName(myjObject.getString("btName"));
                        lockName.add(lock_info.getLockName());
                        lockInfo.add(lock_info);
                    }
                }
                MyLockAdapter myAdapter = new MyLockAdapter(context, lockName, lockInfo);
                listView.setAdapter(myAdapter);
                myAdapter.notifyDataSetChanged();
                callback.execute(result);
            } catch (JSONException je) {

            }
        }
        else {
            callback.error(result);
        }
    }
    public void resultJY(String result){
        if (!result.contains("error")) {
            try {
                JSONArray myJsonArray = new JSONArray(result);
                lockName=new Vector<String>();
                lockInfo=new Vector<LockInfo>();
                for (int i = 0; i < myJsonArray.length(); i++) {
                    JSONObject myjObject = myJsonArray.getJSONObject(i);
                    LockInfo lock_info = new LockInfo();
                    if(myjObject.getString("key").length()!=8){
                        continue;
                    }
                    lock_info.setLockKey( myjObject.getString("key"));
                    lock_info.setLockId( myjObject.getString("key"));
                    lock_info.setLockId_2(myjObject.getString("id"));
                    if(myjObject.getString("bluetooth").length()!=17){
                        continue;
                    }
                    lock_info.setBluetoothAddress(myjObject.getString("bluetooth"));
                    lock_info.setAddress(myjObject.getString("address"));
                    lock_info.setPower(myjObject.getString("power"));
                    if(!isEmpty.StringIsEmpty(myjObject.getString("lockname"))) {
                        lock_info.setLockName(myjObject.getString("lockname"));
                    }
                    else{
                        lock_info.setLockName(myjObject.getString("name"));
                    }
                    lock_info.setStartTime(myjObject.getString("starttime"));
                    lock_info.setEndTime(myjObject.getString("endtime"));
                    lock_info.setBluetoothName(myjObject.getString("name"));
                    lockName.add(lock_info.getLockName());
                    lockInfo.add(lock_info);
                    Log.e("locklist","lockKey:"+lock_info.getLockKey()+"   lockName:"+lock_info.getLockName()+"   lockAddress:"+lock_info.getBluetoothAddress()+"    bluetoothName:"+lock_info.getBluetoothName());
                }
                MyLockAdapter myAdapter = new MyLockAdapter(context, lockName, lockInfo);
                listView.setAdapter(myAdapter);
                callback.execute(result);
            } catch (JSONException je) {

            }
        }
        else {
            callback.error(result);
        }
    }
}
