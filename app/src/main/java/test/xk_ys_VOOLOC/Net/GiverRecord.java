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
 * Created by 柯东煜 on 2017/11/2.
 */

public class GiverRecord extends BaseNet {
    private final String GG="http://123.207.57.91:8080/WooLock/Home/Lock/readGive";
    private final String JY="http://120.77.211.48/WooLock.php /Home/lock/ReadGive";
    private Map<String,String> map=new HashMap<>();
    private Context context;
    private Handler handler=new Handler();
    public GiverRecord(Context context,String account,String password,String lockId){
        this.context=context;
        if(BaseApplication.netVersion==0){
            map.put("phone",account);
            map.put("lockKey",lockId);
        }
        else{
            map.put("account",account);
            map.put("lockId",lockId);
        }
        map.put("password",password);
    }
    public void run(){
       // final String url="http://123.207.57.91:8080/WooLock/Home/Lock/readGive";
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
                        }
                    }
                });
            }
            @Override
            public void onResponse(final Call call, Response response) throws IOException {
                if(response.isSuccessful())
                {
                    final String result=response.body().string();
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
            }
        });
    }
    private void resultGG(String result){
        if(result.contains("{\"code\":1,\"message\":\"操作成功\"")) {
            try {
                callback.execute(result);
            }
            catch (Exception e) {
                Toast.makeText(context, context.getResources().getString(R.string.not_gain_user_information), Toast.LENGTH_SHORT).show();
            }
        }
        else{
            callback.error(result);
        }
    }
    private void resultJY(String result){
        if(result.contains("[")) {
            try {
                callback.execute(result);
            }
            catch (Exception e) {
                Toast.makeText(context, context.getResources().getString(R.string.not_gain_user_information), Toast.LENGTH_SHORT).show();
            }
        }
        else{
            callback.error(result);
        }
    }
}
