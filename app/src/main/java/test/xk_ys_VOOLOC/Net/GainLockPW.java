package test.xk_ys_VOOLOC.Net;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import test.xk_ys_VOOLOC.AboutFile.BaseApplication;
import test.xk_ys_VOOLOC.AboutFile.HttpUtils;
import test.xk_ys_VOOLOC.R;

/**
 * Created by 柯东煜 on 2017/12/30.
 */

public class GainLockPW extends BaseNet {
    final private String JY="http://120.77.211.48/WooLock.php/Home/Lock/GetLockPassword";
  //  final private String GG="http://123.207.57.91:8080/WooLock/Home/User/login";
    private Context context;
    private Handler handler=new Handler();
    private String account;
    private String password;
    private Map<String,String> post=new HashMap<>();
    public GainLockPW(Context context, String key,String type,String startTime,String endTime){
        this.context=context;
        post.put("key",key);
        post.put("type",type);
        Log.e("url",JY+"?key="+key+"&type="+type+"&start="+times(String.valueOf(Long.valueOf(startTime)*1000l))+"&end="+times(String.valueOf(Long.valueOf(endTime)*1000l)));
        Log.e("start",times(String.valueOf(Long.valueOf(startTime)*1000l)));
        Log.e("end",times(String.valueOf(Long.valueOf(endTime)*1000l)));

        post.put("startTime",times(String.valueOf(Long.valueOf(startTime)*1000l)));
        post.put("endTime",times(String.valueOf(Long.valueOf(endTime)*1000l)));
    }
    public static String times(String time) {
        SimpleDateFormat sdr;
        sdr = new SimpleDateFormat("yyMMddHHmm");
        long lcc = Long.valueOf(time);
        String times = sdr.format(new Date(lcc));
        return times;
    }
    @Override
    public void run(){
        final String url=JY;
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
                        }
                    }
                });
            }
            @Override
            public void onResponse(final Call call, Response response) throws IOException {
                if(response.isSuccessful())
                {
                    final String result=response.body().string();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("result",result);
                            resultJY(result);

                        }
                    });
                }
            }
        });
    }
    public void resultGG(String result){
        if(result.contains("{\"code\":1,")) {
            /**
             * {"code":1,"message":"操作成功","data":{"name":"柯东煜"}}
             */
            try {
                JSONObject myJsonObject = new JSONObject(result);
                SharedPreferences userInformation=context.getSharedPreferences("UserInformation",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = userInformation.edit();
                JSONObject data=new JSONObject(myJsonObject.getString("data"));
                editor.putString("account", account);
                editor.putString("password", password);
                editor.putString("username", data.getString("name"));
                editor.commit();
                callback.execute(result);
            }
            catch (Exception e) {
                Toast.makeText(context, context.getResources().getString(R.string.not_gain_user_information), Toast.LENGTH_SHORT).show();
            }
        }
        else{
            callback.error(result);
        }
    }
    public void resultJY(String result){
        Log.e("secret","获取密码成功："+result);
        if(result.contains("{\"code\":1,\"password\"")) {
            try {
                JSONObject jsonObject=new JSONObject(result);
                callback.execute(jsonObject.getString("password"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            callback.error(result);
        }
    }
}
