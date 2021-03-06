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
 * Created by 柯东煜 on 2017/10/21.
 */

public class CorrectLockAddress extends BaseNet {
    private final String GG="http://120.77.211.48/WooLock.php/Home/Lock/SetLockAddress";
    private final String JY="http://120.77.211.48/WooLock.php/Home/Lock/SetLockAddress";
    private Handler handler=new Handler();
    private Map<String,String> map=new HashMap<>();
    private Context context;
    public CorrectLockAddress(Context context,String account,String lockKey,String address){
        this.context=context;
        map.put("account",account);
        map.put("lockKey",lockKey);
        map.put("address",address);
    }
    public void run(){
       // final String url ="http://120.77.211.48/WooLock.php/Home/Lock/SetLockAddress";
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
