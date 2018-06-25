package test.xk_ys_VOOLOC.Net;

import android.content.Context;
import android.content.SharedPreferences;
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
 * Created by 柯东煜 on 2017/9/28.
 */

public class RegisterAccount extends BaseNet {
    final private String GG="http://123.207.57.91:8080/WooLock/Home/User/signUp";
    final private String JY="http://120.77.211.48/WooLock.php/Home/User/signUp";
    private String account;
    private String password;
    private String username;
    private Context context;
    private Map<String,String> map=new HashMap<>();
    Handler handler=new Handler();
    public RegisterAccount(Context context,String username, String account, String password){
        map.put("account",account);
        map.put("name",username);
        map.put("password",password);
        this.context=context;
        this.username=username;
        this.account=account;
        this.password=password;
    }
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
                            //                    Toast.makeText(Login.this, ConnectResult, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
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
        if(result.contains("{\"code\":1,\"message\":\"操作成功\"}"))
        {
            SharedPreferences userInformation=context.getSharedPreferences("UserInformation",context.MODE_PRIVATE);
            SharedPreferences.Editor editor=userInformation.edit();
            editor.putString("account",account);
            editor.putString("password",password);
            editor.putString("username", username);
            editor.commit();
            callback.execute(result);
        }
        else {
            callback.error(result);
        }
    }
    private void resultJY(String result){
        if(result.contains("{\"code\":1}"))
        {
            SharedPreferences userInformation=context.getSharedPreferences("UserInformation",context.MODE_PRIVATE);
            SharedPreferences.Editor editor=userInformation.edit();
            editor.putString("account",account);
            editor.putString("password",password);
            editor.putString("username", username);
            editor.commit();
            callback.execute(result);
        }
        else {
            callback.error(result);
        }
    }
}
