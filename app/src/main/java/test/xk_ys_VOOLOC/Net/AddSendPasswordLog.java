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
import test.xk_ys_VOOLOC.AboutFile.HttpUtils;
import test.xk_ys_VOOLOC.R;

/**
 * Created by 柯东煜 on 2017/12/14.
 */

public class AddSendPasswordLog extends BaseNet {
    private  String JY="http://120.77.211.48/WooLock.php/Home/Lock/SendPasswordLog2";
 // private final String GG="http://123.207.57.91:8080/WooLock/Home/User/isExist";
    Handler handler=new Handler();
    private String account;
    private Context context;
    private Map<String,String> post=new HashMap<>();
    public AddSendPasswordLog(Context context, String from,String to,String lockid,String name,String type ,String pw){
        this.context=context;
        JY=JY+"?from="+from+"&to="+to+"&lockid="+lockid+"&name="+name+"&type="+type+"&pw="+pw;
    }
    @Override
    public void run(){
        final String url;
        Log.e("JY",JY);
        url=JY;
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
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String result = response.body().string();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("result---",result);
                                resultJY(result);
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
        Log.e("secret","上传发送密码记录");
        callback.execute(result);
    }
}
