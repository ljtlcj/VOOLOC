package test.xk_ys_VOOLOC.AboutFile;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

/**
 * Created by kedongyu on 2017/4/20.
 */



public class phoneInfo {
    private String name;
    private String number;
    private String pinyin;
    private String firstLetter;
    public phoneInfo(String name, String number) {
        this.name = name;
        this.number = number;
        this.pinyin =PinYin(name).toUpperCase();
        String first=pinyin.substring(0,1);
        if(first.matches("[A-Z]"))
        {
            firstLetter=first;
        }
        else{
            firstLetter="#";
        }
    }
    public String getFirstLetter(){
        return firstLetter;
    }
    public String getName() {
        return name;
    }
    public String getNumber() {
        return number;
    }
    public String getPinyin()
    {
        return pinyin;
    }
    public String PinYin(String chines) {
        StringBuffer sb = new StringBuffer();
        sb.setLength(0);
        char[] nameChar = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < nameChar.length; i++) {
            if (nameChar[i] > 128) {
                try {
                    sb.append(PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat)[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                sb.append(nameChar[i]);
            }
        }
        return sb.toString();
    }
}