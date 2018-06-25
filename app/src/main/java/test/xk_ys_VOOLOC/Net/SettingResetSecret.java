package test.xk_ys_VOOLOC.Net;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import test.xk_ys_VOOLOC.AboutFile.BaseApplication;
import test.xk_ys_VOOLOC.AboutFile.HttpUtils;
import test.xk_ys_VOOLOC.R;

/**
 * Created by 柯东煜 on 2018/1/19.
 */

public class SettingResetSecret extends BaseNet {
    private String JY="http://120.77.211.48/WooLock.php/Home/Lock/SetRestore";
    private String GG="http://120.77.211.48/WooLock.php/Home/Lock/SetRestore";
    private Context context;
    private Handler handler=new Handler();
    private Map<String,String> map;
    public SettingResetSecret(Context context,String lockid,String restore){
        this.context=context;
        map=new HashMap<>();
        map.put("lockId",lockid);
        map.put("restore",restore);
    }
    @Override
    public void run(){
        final String url;
        if(BaseApplication.netVersion==0){
            url=JY;
        }
        else{
            url=GG;
        }
        HttpUtils.doPost(url, map, new Callback() {
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
                            if(BaseApplication.netVersion==0){
                                resultJY(result);
                            }
                            else{
                                resultGG(result);
                            }
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
        if(result.contains("{\"code\":1}")) {
            Toast.makeText(context, "上传恢复出厂密码成功", Toast.LENGTH_SHORT).show();
            try {
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
}
