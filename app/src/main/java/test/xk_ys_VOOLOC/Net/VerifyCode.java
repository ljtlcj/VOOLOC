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
 * Created by 柯东煜 on 2017/9/28.
 */
//手机验证码验证
public class VerifyCode extends BaseNet {
    private final String GG="http://123.207.57.91:8080/WooLock/Home/VerifyCode/isCorrect";
    private final String JY="http://120.77.211.48/WooLock.php/Home/user/VerifyCode";
    private String account;
    private String code;
    private Handler handler=new Handler();
    private Context context;
    public VerifyCode(Context context,String account, String code)
    {
        this.account=account;
        this.code=code;
        this.context=context;
    }
    @Override
    public void run()
    {
        final String url;
        if(BaseApplication.netVersion==0){
            url=JY+"?account="+account+"&code="+code;
        }
        else{
            url=GG+"?account="+account+"&code="+code;
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
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String result = response.body().string();
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
            callback.execute(result);
        }
        else {
            callback.error(result);
        }
    }
    private void resultJY(String result){
        if(result.contains("{\"code\":1}"))
        {
            callback.execute(result);
        }
        else {
            callback.error(result);
        }
    }
}
