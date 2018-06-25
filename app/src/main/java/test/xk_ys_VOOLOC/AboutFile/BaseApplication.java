package test.xk_ys_VOOLOC.AboutFile;

import android.app.Application;

/**
 * Created by 柯东煜 on 2017/10/21.
 */

public class BaseApplication extends Application {
    static public String language="zh";
    static public int languageCode=0;//0
    static public int netVersion=0;//1为广工，0为嘉应
    static public int time=0;
    static public String getLanguage() {
        return language;
    }
    static public void setLanguage(String language1) {
        language = language1;
    }
}
