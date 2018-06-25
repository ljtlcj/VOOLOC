package test.xk_ys_VOOLOC.Net;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import test.xk_ys_VOOLOC.AboutFile.BaseApplication;
import test.xk_ys_VOOLOC.AboutFile.HttpUtils;
import test.xk_ys_VOOLOC.R;

/**
 * Created by 柯东煜 on 2017/10/21.
 */

public class GainLockAddress extends BaseNet {
    private final String GG="http://120.77.211.48/WooLock.php/Home/Lock/GetLockAddress";
    private final String JY="http://120.77.211.48/WooLock.php/Home/Lock/GetLockAddress";
    private Context context;
    private Handler handler=new Handler();
    private String lockKey;
    public GainLockAddress(Context context,final String lockKey)
    {
        this.context=context;
        this.lockKey=lockKey;
    }
    public void run() {
        final String str ="http://120.77.211.48/WooLock.php/Home/Lock/GetLockAddress?lockKey="+lockKey;
        final String url;
        if(BaseApplication.netVersion==0){
            url=JY;
        }
        else{
            url=GG;
        }
        HttpUtils.doGet(str, new Callback() {
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
            public void onResponse(final Call call, final Response response) throws IOException {
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
        if(result.contains("{\"code\":1,\"address\":\""))
        {
            try {
                JSONObject json=new JSONObject(result);
                if(1==json.getInt("code")){
                    callback.execute(json.getString("address"));
                }
                else{
                    callback.error(json.getString("address"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else
        {
            callback.error(result);
        }
    }
}
