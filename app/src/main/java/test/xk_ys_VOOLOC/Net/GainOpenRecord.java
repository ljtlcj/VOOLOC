package test.xk_ys_VOOLOC.Net;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

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
 * Created by 柯东煜 on 2017/11/8.
 */

public class GainOpenRecord extends BaseNet {
    private final String GG="http://123.207.57.91:8080/WooLock/Home/Log/getUnlockDate";
    private final String JY="http://120.77.211.48/WooLock.php/Home/Lock./GetOpenLog";
    private Context context;
    private Handler handler=new Handler();
    private Map<String,String> post=new HashMap<>();
    public GainOpenRecord(Context context, String account,String lockId){
        this.context=context;
        post.put("account",account);
        post.put("lockId",lockId);
    }
    @Override
    public void run(){
       // final String url="http://123.207.57.91:8080/WooLock/Home/Log/getUnlockDate";
        final String url;
        if(BaseApplication.netVersion==0)
        {
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
                        }
                    }
                });
            }
            @Override
            public void onResponse(final Call call, Response response) throws IOException {
                if(response.isSuccessful())
                {
                    final String result=response.body().string();
                    Log.e("Log.openRecord",result);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                         if(BaseApplication.netVersion==0){
                             resultJY(result);
                         }
                         else{
                             resutlGG(result);
                         }
                        }
                    });
                }
            }
        });
    }
    private void resutlGG(String result){
        if(result.contains("{\"code\":1,\"message\":\"操作成功\"")) {
            /**
             * {"code":1,"message":"操作成功","data":{"unlockLogs":[0,1510131691000]}}
             */
            callback.execute(result);
        }
        else{
            callback.error(result);
        }
    }
    private void resultJY(String result){
        callback.execute(result);
    }
}
