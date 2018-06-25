package test.xk_ys_VOOLOC;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import test.xk_ys_VOOLOC.AboutFile.ComparatorUser;
import test.xk_ys_VOOLOC.AboutFile.LetterIndexView;
import test.xk_ys_VOOLOC.AboutFile.LockInfo;
import test.xk_ys_VOOLOC.AboutFile.MyAdapter;
import test.xk_ys_VOOLOC.AboutFile.getPhoneNumberFormMobile;
import test.xk_ys_VOOLOC.AboutFile.isEmpty;
import test.xk_ys_VOOLOC.AboutFile.phoneInfo;



public class Authorization_Phone extends Activity implements TextWatcher {
    private LockInfo lockInfo;
    @BindView(R.id.back)
    public TextView back;
    @BindView(R.id.phone)
    public EditText phone;
    @BindView(R.id.get_phone)
    public TextView getPhone;
    @BindView(R.id.next)
    public TextView next;
    @BindView(R.id.name_line)
    public LinearLayout linearLayout;
    @BindView(R.id.name)
    public EditText editText;
    @BindView(R.id.WeChat)
    public TextView weChat;
    private int order=0;
    private String username;
    private String usernumber;

    List<phoneInfo> list = new ArrayList<phoneInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization__phone);
        ButterKnife.bind(this);
        try {
            list = getPhoneNumberFormMobile.getPhoneNumberFormMobile(Authorization_Phone.this);
        }
        catch (SecurityException se){
            Toast.makeText(this, "无法读取通讯录权限", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "请退出重新进入并确认获取权限", Toast.LENGTH_SHORT).show();
            finish();
        }
        ComparatorUser comparator=new ComparatorUser();
        Collections.sort(list, comparator);
        getUserInfo();
        initialize();
    }




    public void getUserInfo(){
        Intent i=getIntent();
        lockInfo=i.getParcelableExtra("lockInfo");
        order=i.getIntExtra("order",0);
        if(order==0||lockInfo==null||isEmpty.StringIsEmpty(lockInfo.getLockKey())||isEmpty.StringIsEmpty(lockInfo.getEndTime())||isEmpty.StringIsEmpty(lockInfo.getStartTime())){
            Toast.makeText(this, getString(R.string.lock_info_error), Toast.LENGTH_SHORT).show();
            finish();
        }
        //order 1 为 发送密码 2为1级用户授权 3为2级用户授权
        if(order==1){
            weChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Authorization_Phone.this, Sent_PW1.class);
                    intent.putExtra("account", "WeChat");
                    intent.putExtra("order", order);
                    intent.putExtra("name", "WeChat");
                    intent.putExtra("lockInfo", lockInfo);
                    startActivity(intent);
                }
            });
        }
        else{
            weChat.setVisibility(View.GONE);
        }
    }

    public void initialize(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        phone.addTextChangedListener(this);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isEmpty.StringIsEmpty(phone.getText().toString())) {
                    if(order==3){
                        Intent i = new Intent(Authorization_Phone.this, Setting_time1.class);
                        String temp = phone.getText().toString().replaceAll("-","");
                        temp = temp.replaceAll(" ","");
                        i.putExtra("account", temp);
                        i.putExtra("order", 2);
                        i.putExtra("name", editText.getText().toString());
                        i.putExtra("lockInfo", lockInfo);
                        i.putExtra("type",2);
                        startActivity(i);
                    }
                    else {
                        Intent i = new Intent(Authorization_Phone.this, Sent_PW1.class);
                        String temp = phone.getText().toString().replaceAll("-","");
                        temp = temp.replaceAll(" ","");
                        Log.e("onClick:temp",temp);
                        i.putExtra("account", temp);
                        i.putExtra("order", order);
                        i.putExtra("name", editText.getText().toString());
                        i.putExtra("lockInfo", lockInfo);
                        startActivity(i);
                    }
                }
                else{
                    Toast.makeText(Authorization_Phone.this, getString(R.string.no_write_phone), Toast.LENGTH_SHORT).show();
                }
            }
        });
        getPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //读取通讯录
                startActivityForResult(new Intent(
                        Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), 0);
//                if(list.size()>0) {
//                    showContract("请选择联系人");
//                }
//                else{
//                    Toast.makeText(Authorization_Phone.this, "通讯录无联系人", Toast.LENGTH_SHORT).show();
//                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            ContentResolver reContentResolverol = getContentResolver();
            Uri contactData = data.getData();
            @SuppressWarnings("deprecation")
            Cursor cursor = managedQuery(contactData, null, null, null, null);
            cursor.moveToFirst();
            username = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phone1 = reContentResolverol.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                    null,
                    null);
            while (phone1.moveToNext()) {
                usernumber = phone1.getString(phone1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                phone.setText(usernumber);
                editText.setText(username);
            }
//            cursor.close();
        }

    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        linearLayout.setVisibility(View.VISIBLE);
    }
    public void showContract(final String title){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
    //    builder.setTitle(title);

        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        View view = View.inflate(getApplicationContext(), R.layout.contact, null);
        builder.setView(view);
        final ListView listView = (ListView) view.findViewById(R.id.lv);
        final MyAdapter adapter = new MyAdapter(this, list);
        listView.setAdapter(adapter);
        TextView textView = (TextView) view.findViewById(R.id.show_letter_in_center);
        final LetterIndexView letterIndexView = (LetterIndexView) view.findViewById(R.id.letter_index_view);
        letterIndexView.setTextViewDialog(textView);
        letterIndexView.setUpdateListView(new LetterIndexView.UpdateListView() {
            @Override
            public void updateListView(String currentChar) {
                int positionForSection = adapter.getPositionForSection(currentChar.charAt(0));
                listView.setSelection(positionForSection);
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int sectionForPosition = adapter.getSectionForPosition(firstVisibleItem);
                letterIndexView.updateLetterIndexView(sectionForPosition);
            }
        });
        final Dialog dialog=builder.create();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                phone.setText(list.get(position).getNumber());
                editText.setText(list.get(position).getName());
                dialog.cancel();
            }
        });
        dialog.show();
    }

}
