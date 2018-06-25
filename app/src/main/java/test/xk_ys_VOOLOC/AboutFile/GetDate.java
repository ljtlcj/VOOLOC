package test.xk_ys_VOOLOC.AboutFile;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kedongyu on 2017/5/17.
 */

public class GetDate {
    static public String getDate(){
        Date testD = new Date();
        DateFormat df = new SimpleDateFormat("yyMMddHHmmss");
        String currDate = "!T"+df.format(testD);
        DateFormat dF = new SimpleDateFormat("E");
        switch (dF.format(testD)) {
            case "周一":
            case "Mon":
                currDate += "1*";
                break;
            case "周二":
            case "Tue":
                currDate += "2*";
                break;
            case "周三":
            case "Wed":
                currDate += "3*";
                break;
            case "周四":
            case "Thu":
                currDate += "4*";
                break;
            case "周五":
            case "Fri":
                currDate += "5*";
                break;
            case "周六":
            case "Sat":
                currDate += "6*";
                break;
            case "周日":
            case "Sun":
                currDate += "7*";
                break;
        }
        return currDate;
    }
}
