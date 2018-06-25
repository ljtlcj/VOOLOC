package test.xk_ys_VOOLOC.AboutFile;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kedongyu on 2017/5/20.
 * 日期String转long
 * long转String
 */

public class StringToDate {
    static public long TransferDate(String time) //throws ParseException
    {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = format.parse(time);
            return date.getTime();
        }
        catch (ParseException pe){
            return 0;
        }
    }

    public static String times(String time) {
        SimpleDateFormat sdr;
        Log.e("-----------",BaseApplication.language);
        if(BaseApplication.language.equals("zh")){
            sdr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        else{
            sdr = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        }
        @SuppressWarnings("unused")
        long lcc = Long.valueOf(time);
     //   int i = Integer.parseInt(time);
        String times = sdr.format(new Date(lcc));
        return times;
    }
    public static long StringToLong(String time){
        return TransferDate(times(time));
    }
    public static String times(String time,String form) {
        SimpleDateFormat sdr = new SimpleDateFormat(form);
        @SuppressWarnings("unused")
        long lcc = Long.valueOf(time);
        int i = Integer.parseInt(time);
        String times = sdr.format(new Date(i * 1000L));
        return times;
    }
}
