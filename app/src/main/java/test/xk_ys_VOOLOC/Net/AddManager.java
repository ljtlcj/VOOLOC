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
 * Created by 柯东煜 on 2017/10/6.
 */

public class AddManager extends BaseNet {
    private final String GG="http://123.207.57.91:8080/WooLock/Home/Lock/giveLock";
    private final String JY="http://120.77.211.48/WooLock.php/Home/lock/GiveLock";
    private Map<String,String> post=new HashMap<>();
    private Context context;
    private Handler handler=new Handler();
    private static final String TAG = "AddManager";

    /**
     *
     * @param context
     * @param account
     * @param password
     * @param lockId
     * @param customerAccount
     * @param startTime
     * @param endTime
     * @param power
     * @param customerName
     */
    public AddManager(Context context,String account,String password,String lockId,String customerAccount,String startTime,String endTime,String power,String customerName){
        this.context=context;
        if(BaseApplication.netVersion==0){
            post.put("phone",account);
            post.put("password",password);
            post.put("lockKey",lockId);
            post.put("customer",customerAccount);
            post.put("customerPower",power);
            post.put("customerName",customerName);
            post.put("endTime",String.valueOf(Long.valueOf(endTime)/1000));
            post.put("startTime",String.valueOf(Long.valueOf(startTime)/1000));
        }
        else{
            post.put("account",account);
            post.put("password",password);
            post.put("lockId",lockId);
            post.put("customerAccount",customerAccount);
            post.put("power",power);
            post.put("customerName",customerName);
            post.put("endTime",endTime);
            post.put("startTime",startTime);
        }

    }
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
                //关闭防止内存泄漏
                if (response.body() != null) {
                    response.body().close();
                }
            }
        });
    }
    private void resultGG(String result){
        if(result.contains("{\"code\":1,\"message\""))
        {
            Toast.makeText(context, context.getString(R.string.giver_succ), Toast.LENGTH_SHORT).show();
            callback.execute(result);
        }
        else if(result.contains("{\"code\":103,\"message\""))
        {
            Toast.makeText(context,context.getString(R.string.user_or_password_error), Toast.LENGTH_SHORT).show();
        }
        else if(result.contains("{\"code\":206,\"message\""))
        {
            Toast.makeText(context,context.getString(R.string.had_this_lock), Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(context, context.getString(R.string.regiver), Toast.LENGTH_SHORT).show();
            callback.error(result);
        }
    }
    private void resultJY(String result){
        Log.e(TAG, "resultJY: "+result );

        if(result.contains("\"code\":1"))
        {
            Toast.makeText(context, context.getString(R.string.giver_succ), Toast.LENGTH_SHORT).show();
            callback.execute(result);
        }
        else if(result.contains("\"code\":8"))
        {
            Toast.makeText(context,context.getString(R.string.no_this_customer), Toast.LENGTH_SHORT).show();
        }
        else if(result.contains("\"code\":9"))
        {
            Toast.makeText(context,context.getString(R.string.had_this_lock), Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(context, context.getString(R.string.regiver), Toast.LENGTH_SHORT).show();
            callback.error(result);
        }
    }
}
