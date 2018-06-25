package test.xk_ys_VOOLOC.AboutFile;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kedongyu on 2017/4/20.
 */
public class getPhoneNumberFormMobile {
    static private List<phoneInfo> list;

    static  public List<phoneInfo> getPhoneNumberFormMobile(Context context) {
        // TODO Auto-generated constructor stub
        list = new ArrayList<phoneInfo>();
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);
        //moveToNext方法返回的是一个boolean类型的数据
        while (cursor.moveToNext()) {
            //读取通讯录的姓名
            String name = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            //读取通讯录的号码
            String number = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phoneInfo phoneInfo = new phoneInfo(name, number);
            list.add(phoneInfo);
        }
        return list;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        list.clear();
        list=null;
    }
}