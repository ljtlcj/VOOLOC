package test.xk_ys_VOOLOC.Net;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import test.xk_ys_VOOLOC.AboutFile.BaseApplication;
import test.xk_ys_VOOLOC.AboutFile.HttpUtils;
import test.xk_ys_VOOLOC.R;

/**
 * Created by 柯东煜 on 2017/9/29.
 */

public class GainLockKey extends BaseNet {
    private final String GG="http://123.207.57.91:8080/WooLock/Home/Lock/activeLock";
    private final String JY="http://120.77.211.48/WooLock.php/Home/Lock/activeLock";
    private Context context;
    private Handler handler=new Handler();
    private String BTAdress;
    private String lockKey;
    public GainLockKey(Context context,final String address)
    {
        this.context=context;
        this.BTAdress=address;
    }
    public String getLockKey(){
        return lockKey;
    }
    public void run() {
        //final String str ="http://123.207.57.91:8080/WooLock/Home/Lock/activeLock?btAddress="+BTAdress;
        final String url;
        if(BaseApplication.netVersion==0){
            url=JY+"?BTAdress="+BTAdress;
        }
        else {
            url = GG + "?btAddress=" + BTAdress;
        }
        HttpUtils.doGet(url, new Callback() {
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
            public void onResponse(Call call, final Response response) throws IOException {
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
        if(result.contains("{\"code\":1,\"message\":\"操作成功\",\"data\":"))
        {
            /**
             * {"code":1,"message":"操作成功","data":{"lockKey":"00000001"}}
             */
            Pattern p=Pattern.compile("[0-9]{8}");
            Matcher m=p.matcher(result);
            m.find();
            lockKey=m.group();
            callback.execute(lockKey);
        }
        else
        {
            if(result.contains("{\"code\":208,\"message\":\"门锁已被激活\"}")){
                Toast.makeText(context, "门锁已被激活", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(context, "门锁已存在", Toast.LENGTH_SHORT).show();
            }
            callback.error(result);
        }
    }
    private void resultJY(String result){
     //
        if(result.contains("\",\"code\":1}"))
        {
            Pattern p=Pattern.compile("[0-9]{8}");
            Matcher m=p.matcher(result);
            m.find();
            lockKey=m.group();
            callback.execute(lockKey);
        }
        else
        {
            callback.error(result);
        }
    }
}
