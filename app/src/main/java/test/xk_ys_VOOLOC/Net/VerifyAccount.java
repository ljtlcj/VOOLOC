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
import test.xk_ys_VOOLOC.MyView.MyProgressDialog;
import test.xk_ys_VOOLOC.R;

/**
 * Created by Administrator on 2017/9/25.
 */

public class VerifyAccount  extends BaseNet{
    private final String JY="http://120.77.211.48/WooLock.php/Home/User/isexist";
    private final String GG="http://123.207.57.91:8080/WooLock/Home/User/isExist";
    Handler handler=new Handler();
    private String account;
    private Context context;
    private Map<String,String> post=new HashMap<>();
    public VerifyAccount(Context context,String account ){
        this.account=account;
        this.context=context;
        post.put("account",account);
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
        HttpUtils.doPost(url, post, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                    final String ConnectResult=e.toString();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (ConnectResult.contains("ConnectException")) {
                                MyProgressDialog.remove();
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
                            Log.e("login_infor",result);
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
        if(result.contains("{\"code\":1,\"message\":\"操作成功\"}")){
            callback.execute(result);
        }
        else{
            callback.error(result);
        }
    }
    private void resultJY(String result){
        if(result.contains("{\"code\":1}")){
            callback.execute(result);
        }
        else if(result.contains("{\"code\":-1}")){
            callback.error(result);
        }
        else{
            Toast.makeText(context, "无法连接服务器，请检查网络是否正常", Toast.LENGTH_SHORT).show();
        }
        MyProgressDialog.remove();
    }
}
