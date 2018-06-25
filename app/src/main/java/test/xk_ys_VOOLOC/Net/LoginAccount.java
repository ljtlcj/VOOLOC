package test.xk_ys_VOOLOC.Net;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

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
 * Created by 柯东煜 on 2017/9/25.
 */

public class LoginAccount  extends BaseNet {
    final private String JY="http://120.77.211.48/WooLock.php/Home/User/logIn";
    final private String GG="http://123.207.57.91:8080/WooLock/Home/User/login";
    private Context context;
    private Handler handler=new Handler();
    private String account;
    private String password;
    private Map<String,String> post=new HashMap<>();
    public LoginAccount(Context context, String account,String password){
        this.context=context;
        this.account=account;
        this.password=password;
        post.put("account",account);
        post.put("password",password);
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

                            Log.e("login",result);
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
    public void resultGG(String result){
        if(result.contains("{\"code\":1,")) {
            /**
             * {"code":1,"message":"操作成功","data":{"name":"柯东煜"}}
             */
            try {
                JSONObject myJsonObject = new JSONObject(result);
                SharedPreferences userInformation=context.getSharedPreferences("UserInformation",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = userInformation.edit();
                JSONObject data=new JSONObject(myJsonObject.getString("data"));
                editor.putString("account", account);
                editor.putString("password", password);
                editor.putString("username", data.getString("name"));
                editor.commit();
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
    public void resultJY(String result){
        if(result.contains("{\"code\":1,\"id\":")) {
            try {
                JSONObject myJsonObject = new JSONObject(result);
                SharedPreferences userInformation=context.getSharedPreferences("UserInformation",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = userInformation.edit();
                editor.putString("account", account);
                editor.putString("phone",myJsonObject.getString("phone"));
                editor.putString("mail",myJsonObject.getString("mail"));
                editor.putString("password", password);
                editor.putString("username", myJsonObject.getString("name"));
                editor.putString("id",myJsonObject.getString("id"));
                editor.commit();
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
