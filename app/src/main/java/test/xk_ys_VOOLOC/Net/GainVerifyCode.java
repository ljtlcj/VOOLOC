package test.xk_ys_VOOLOC.Net;


import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import test.xk_ys_VOOLOC.AboutFile.BaseApplication;
import test.xk_ys_VOOLOC.AboutFile.HttpUtils;
import test.xk_ys_VOOLOC.R;

/**
 * Created by 柯东煜 on 2017/9/27.
 */

public class GainVerifyCode extends BaseNet{
    private final String GG="http://123.207.57.91:8080/WooLock/Home/VerifyCode/getVerifyCode";
    private final String JY="http://120.77.211.48/WooLock.php/Home/user/GetVerifyCode";
    private String account;
    private Context context;
    Handler handler=new Handler();
    public GainVerifyCode(Context context,String account){
        this.account=account;
        this.context=context;
    }
    @Override
    public void run()
    {
        //final String str ="http://123.207.57.91:8080/WooLock/Home/VerifyCode/getVerifyCode?account="+account;
        final String url;
        if(BaseApplication.netVersion==0){
            url=JY+"?account="+account;
        }
        else{
            url=GG+"?account="+account;
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
                        }
                    }
                });
            }
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String result = response.body().string();
                    Log.e("e-mail",result);
                    Log.e("code_result",result);
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
        if(result.contains("{\"code\":1,\"message\":\"操作成功\"}"))
        {
            Toast.makeText(context, context.getResources().getString(R.string.send_successful), Toast.LENGTH_SHORT).show();
            callback.execute(result);
        }
        else{
            callback.error(result);
        }
    }
    private void resultJY(String result){
        if(true)
        {
            Toast.makeText(context, context.getResources().getString(R.string.send_successful), Toast.LENGTH_SHORT).show();
            callback.execute(result);
        }
        else{
            callback.error(result);
        }
    }
}
