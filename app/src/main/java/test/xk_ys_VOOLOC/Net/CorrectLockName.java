package test.xk_ys_VOOLOC.Net;

import android.content.Context;
import android.os.Handler;
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
 * Created by 柯东煜 on 2017/9/29.
 */

public class CorrectLockName extends BaseNet {
    private final String GG="http://123.207.57.91:8080/WooLock/Home/Lock/changeLockName";
    private final String JY="http://120.77.211.48/WooLock.php/Home/lock/changeLockName";
    private Handler handler=new Handler();
    private Map<String,String> map=new HashMap<>();
    private Context context;
    public CorrectLockName(Context context,String account,String lockId,String lockName,String password){
        this.context=context;
        if(BaseApplication.netVersion==0){
            map.put("phone",account);
            map.put("password",password);
            map.put("lockKey",lockId);
            map.put("name",lockName);
        }
        else {
            map.put("account",account);
            map.put("lockId",lockId);
            map.put("lockName",lockName);
        }


    }


    public void run(){
        //final String url ="http://123.207.57.91:8080/WooLock/Home/Lock/changeLockName";
        final String url;
        if(BaseApplication.netVersion==0){
            url=JY;
        }
        else {
            url = GG;
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
                            //                    Toast.makeText(Login.this, ConnectResult, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            @Override
            public void onResponse(final Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String result = response.body().string();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
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
        if(result.contains("{\"code\":1,\"message\":\"操作成功\"}"))
        {
            callback.execute(result);
        }
        else
        {
            callback.error(result);
        }
    }
    private void resultJY(String result){
        if(result.contains("{\"code\":1}"))
        {
            callback.execute(result);
        }
        else
        {
            callback.error(result);
        }
    }

}