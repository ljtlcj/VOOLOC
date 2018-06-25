package test.xk_ys_VOOLOC.AboutFile;

import java.util.Comparator;

public class ComparatorUser implements Comparator {

    public int compare(Object arg0, Object arg1) {
        phoneInfo user0 = (phoneInfo) arg0;
        phoneInfo user1 = (phoneInfo) arg1;

        int flag = user0.getFirstLetter().compareTo(user1.getFirstLetter());
        return flag;
    }
}