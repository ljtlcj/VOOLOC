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
 * Created by 柯东煜 on 2017/9/29.
 */

public class AddLock extends BaseNet {
    private final String GG="http://123.207.57.91:8080/WooLock/Home/Lock/addLock";
    private final String JY="http://120.77.211.48/WooLock.php/Home/lock/addlock";
    private Handler handler=new Handler();
    private Context context;
    private String account;
    private String name;
    private String BTAdress;
    private String lockKey;
    private String password;
    public AddLock(Context context,final String account, final String password ,final String lockKey,final String name, final String address) {
        this.context=context;
        this.account=account;
        this.password=password;
        this.BTAdress=address;
        this.name = name;
        this.lockKey=lockKey;
    }
    public void run() {
      //  final String str ="http://123.207.57.91:8080/WooLock/Home/Lock/addLock?account="+ account +"&password="+password+ "&lockKey=" + lockKey+"&btName="+name+"&btAddress="+BTAdress;
        final String url;
        if(BaseApplication.netVersion==0){
            url=JY+"?phone="+ account + "&lockKey=" + lockKey+"&name="+name+"&BTAdress="+BTAdress;
        }
        else{
            url=GG+"?account="+ account +"&password="+password+ "&lockKey=" + lockKey+"&btName="+name+"&btAddress="+BTAdress;
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
                        callback.error(ConnectResult);
                    }
                });
            }
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String result = response.body().string();
                    Log.e("-----------",result);
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
            Toast.makeText(context, context.getString(R.string.add_successful), Toast.LENGTH_SHORT).show();
            callback.execute(result);
        }
        else
        {
            callback.error(result);
        }
    }
    private void resultJY(String result){
        Log.e("result",result);
        if(result.contains("{\"code\":1"))
        {
            Toast.makeText(context, context.getString(R.string.add_successful), Toast.LENGTH_SHORT).show();
            callback.execute(result);
        }
        else
        {
            callback.error(result);
        }
    }
}
