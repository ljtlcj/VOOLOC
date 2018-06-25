package test.xk_ys_VOOLOC.Net;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import test.xk_ys_VOOLOC.AboutFile.BaseApplication;
import test.xk_ys_VOOLOC.AboutFile.HttpUtils;
import test.xk_ys_VOOLOC.R;

/**
 * Created by 柯东煜 on 2017/11/26.
 */

public class GainLockPower extends BaseNet {
    private final String GG="http://120.77.211.48/WooLock.php/Home/Lock./GetLockBattery";
    private final String JY="http://120.77.211.48/WooLock.php/Home/Lock./GetLockBattery";
    private Context context;
    private Handler handler=new Handler();
    private String lockKey;
    public GainLockPower(Context context , String lockKey){
        this.context=context;
        this.lockKey=lockKey;
    }
    @Override
    public void run(){
        final String url;
        if(BaseApplication.netVersion==0){
            url=JY+"?lockKey="+lockKey;
        }
        else{
            url=GG+"?lockKey="+lockKey;
        }

        HttpUtils.doPost(url, new HashMap<String, String>(), new Callback() {
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
    private void resultGG(String result){

    }
    private void resultJY(String result){
        if(result.contains("{\"code\":1,\"battery\":")){
            try {
                JSONObject json=new JSONObject(result);
                callback.execute(json.getString("battery"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            callback.error(result);
        }
    }
}
