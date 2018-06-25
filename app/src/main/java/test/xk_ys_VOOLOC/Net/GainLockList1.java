package test.xk_ys_VOOLOC.Net;

import android.content.Context;

/**
 * Created by 柯东煜 on 2017/12/30.
 */

public class GainLockList1 extends GainLockList {
    public GainLockList1(Context context,String account,String password){
        super(context,account,password,null);
    }
    @Override
    public void resultJY(String result){
        if (!result.contains("error")) {
            callback.execute(result);
        }
        else{
            callback.error(result);
        }
    }
}
