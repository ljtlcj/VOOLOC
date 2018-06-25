package test.xk_ys_VOOLOC.AboutFile;

import java.util.regex.Pattern;

/**
 * Created by kedongyu on 2017/5/10.
 */

public class TranferBetweenStringAndStringCarry {
    static public String[] StringToStringCarry(String s)
    {
        Pattern pattern=Pattern.compile("[#]");
        String[] sc=pattern.split(s);
        return sc;
    }
    static public String StringCarryToString(String sc[])
    {
        String s="";
        for(String st:sc)
        {
            s+=st;
            s+="#";
        }
        return s;
    }

}
