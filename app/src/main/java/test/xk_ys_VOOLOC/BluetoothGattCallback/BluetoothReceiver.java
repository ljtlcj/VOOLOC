package test.xk_ys_VOOLOC.BluetoothGattCallback;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import test.xk_ys_VOOLOC.R;

/**
 * Created by 柯东煜 on 2017/11/22.
 */

public class BluetoothReceiver extends BroadcastReceiver {
    private int result;
    private String message;
    public void onReceive(Context context ,Intent intent){
        String str="";
        result=intent.getIntExtra("result",0);
        message=intent.getStringExtra("message");
        switch (result){
            case 0x123:
                str="开锁成功";
                break;
            case 0x124:
                str="校准时间成功";
                break;
            case 0x125:
                str="更新电量";
                break;
            case 0x126:
                str=context.getResources().getString(R.string.unconnected_lock);
                break;
            case 0x127:
                str="当前系统时间与实际时间相差太大，无法校准时间";
                break;
            case 0x100:
                Toast.makeText(context, "读取开锁记录成功", Toast.LENGTH_SHORT).show();
                break;
            default:
                str="";

        }
        if(result>=0x123) {
            Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
        }
    }
}
