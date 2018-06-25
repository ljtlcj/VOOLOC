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
 * Created by 柯东煜 on 2017/11/5.
 */

public class DeleteAuthority extends BaseNet {
    private final String GG="http://123.207.57.91:8080/WooLock/Home/Lock/cancelGive";
    private final String JY="http://120.77.211.48/WooLock.php/Home/lock/cancelGive";
    private Context context;
    private Handler handler=new Handler();
    private String account;
    private String password;
    private Map<String,String> post=new HashMap<>();
    public DeleteAuthority(Context context, String account, String password, String lockId, String customerAccount){
        this.context=context;
        this.account=account;
        this.password=password;
        if(BaseApplication.netVersion==0){

            post.put("phone",account);
            post.put("lockKey",lockId);
            post.put("customer",customerAccount);
        }
        else{

            post.put("account",account);
            post.put("lockId",lockId);
            post.put("customerAccount",customerAccount);
        }
        post.put("password",password);





    }
    @Override
    public void run(){
       // final String url="http://123.207.57.91:8080/WooLock/Home/Lock/cancelGive";
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
                    Log.e("deleteAuthority",result);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("result",result);
                            if(BaseApplication.netVersion==0){
                                resultJY(result);
                            }
                            else{
                                resultGG(result);
                            }
                        }
                    });
                }
                else{
                    Log.e("------",response.toString());
                }
            }
        });
    }
    private void resultGG(String result){
        if(result.contains("{\"code\":1,\"message\":\"操作成功\"}")) {
            callback.execute(result);
        }
        else{
            callback.error(result);
        }
    }
    private void resultJY(String result){
        if(result.contains("{\"code\":1}")) {
            callback.execute(result);
        }
        else{
            callback.error(result);
        }
    }
}
