package test.xk_ys_VOOLOC;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import test.xk_ys_VOOLOC.AboutFile.LockInfo;
import test.xk_ys_VOOLOC.Net.AddSendPasswordLog;
import test.xk_ys_VOOLOC.Net.GainLockList1;
import test.xk_ys_VOOLOC.Net.GainLockPW;
import test.xk_ys_VOOLOC.Net.NetCallback;

public class Sent_PW1 extends AppCompatActivity {
    @BindView(R.id.times)
    public TextView times;
    @BindView(R.id.once)
    public TextView once;
    @BindView(R.id.send)
    public TextView send;
    @BindView(R.id.back)
    public TextView back;
    @BindView(R.id.tip)
    public TextView tip;
    private LockInfo lockInfo;
    private int order;
    private String name;
    private String account;
    private String id;
    private String sAccount;
    private String sPassword;
    private int type = 1;
    private static final String TAG = "Sent_PW1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent__pw);
        ButterKnife.bind(this);
        SharedPreferences info = getSharedPreferences("UserInformation", MODE_PRIVATE);
        sPassword = info.getString("password", null);
        sAccount = info.getString("account", null);
        Log.d(TAG, "onCreate: " + sPassword + sAccount);
        if (sPassword == null || sAccount == null)
            finish();
        id = info.getString("id", null);
        Intent i = getIntent();

        lockInfo = i.getParcelableExtra("lockInfo");
        account = i.getStringExtra("account");
        name = i.getStringExtra("name");
        order = i.getIntExtra("order", 0);
        initBegin();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (account == null || account.equals("")) {
            finish();
        }

        times.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = 1;
                times.setTextColor(Color.RED);
                once.setTextColor(getResources().getColor(R.color.Text_color));
                send.setText(getResources().getString(R.string.next));
                send.setVisibility(View.VISIBLE);
                if (order == 2) {
                    tip.setText("高级权限指的是拥有发送密码/开锁/授权/设置密码这几个权限");
                }
            }
        });
        if (order == 1) {
            once.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    type = 2;
                    send.setText(getResources().getString(R.string.sending_password));
                    once.setTextColor(Color.RED);
                    times.setTextColor(getResources().getColor(R.color.Text_color));
                    send.setVisibility(View.VISIBLE);
                }
            });
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (type == 1) {
                        Intent i = new Intent(Sent_PW1.this, Setting_time1.class);
                        i.putExtra("lockInfo", lockInfo);
                        i.putExtra("account", account);
                        i.putExtra("name", name);
                        i.putExtra("order", order);
                        startActivity(i);
                    } else {
                        //    String password = Compress.GeneratePassword(new Date().getTime(), 0, lockInfo.getLockKey(), 1);
                        //从服务器上获取密码
                        GainLockPW gainLockPW = new GainLockPW(Sent_PW1.this, lockInfo.getLockKey(), "1", String.valueOf(new Date().getTime() / 1000), "0");
                        gainLockPW.setCallback(new NetCallback() {
                            @Override
                            public void execute(String result) {
                                String password = result;
                                //添加发送密码记录
                                AddSendPasswordLog addSendPasswordLog = new AddSendPasswordLog(Sent_PW1.this, id, account, lockInfo.getLockId_2(), name, "2", password);
                                new Thread(addSendPasswordLog).start();
                                //生成消息
                                String str = "欢迎使用物勒智能门锁，您的开锁密码为：" + password.substring(0, 4) + "-" + password.substring(4, 8) + "-" + password.substring(8, 11) + password.substring(11) + "门锁名称为:" + lockInfo.getLockName() + ",2小时后失效。输入密码后按 # 号键即可开门";
                                if (account.equals("WeChat")) {
                                    Intent intent = new Intent(Intent.ACTION_SEND);
                                    intent.setType("text/plain");//  intent.setPackage("com.tencent.mm");
                                    intent.putExtra(Intent.EXTRA_TEXT, str);
                                    startActivity(Intent.createChooser(intent, "请选择"));
                                } else {
                                    Intent sendIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + account));
                                    sendIntent.putExtra("sms_body", str);
                                    startActivity(sendIntent);
                                }

                            }

                            @Override
                            public void error(String result) {
                                Toast.makeText(Sent_PW1.this, "从服务器获取密码失败，请重新尝试！", Toast.LENGTH_SHORT).show();
                            }
                        });
                        new Thread(gainLockPW).start();
                    }
                }
            });
        } else if (order == 2) {
            tip.setText("高级权限指的是拥有发送密码/开锁/授权/设置密码这几个权限");

            times.setText(getString(R.string.advanced));
            once.setText(getString(R.string.popular));
            once.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    type = 2;
                    send.setText(getResources().getString(R.string.next));
                    once.setTextColor(Color.RED);
                    times.setTextColor(getResources().getColor(R.color.Text_color));
                    send.setVisibility(View.VISIBLE);
                    tip.setText("普通权限指的是只拥有开锁这个权限");
                }
            });
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Sent_PW1.this, Setting_time1.class);
                    i.putExtra("lockInfo", lockInfo);
                    i.putExtra("account", account);
                    i.putExtra("name", name);
                    i.putExtra("order", order);
                    i.putExtra("type", type);
                    startActivity(i);
                }
            });
        } else {
            finish();
        }
    }

    private void initBegin() {
        type = 1;
        times.setTextColor(Color.RED);
        once.setTextColor(getResources().getColor(R.color.Text_color));
        send.setText(getResources().getString(R.string.next));
        send.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        GainLockList1 gainLockList = new GainLockList1(this, sAccount, sPassword);
        gainLockList.setCallback(new NetCallback() {
            @Override
            public void execute(String result) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    boolean b = false;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Log.e("lockKey", jsonObject.getString("key"));
                        if (jsonObject.getString("key").equals(lockInfo.getLockKey())) {
                            b = true;
                            Log.e("result", "----");
                            break;
                        }
                    }
                    if (!b) {
                        Toast.makeText(Sent_PW1.this, "门锁不存在", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(Sent_PW1.this, add_lock.class);
                        startActivity(i);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void error(String result) {
                Toast.makeText(Sent_PW1.this, "门锁不存在", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Sent_PW1.this, add_lock.class);
                startActivity(i);
                finish();
            }
        });
        new Thread(gainLockList).start();
    }

    protected void onRestart() {
        super.onRestart();
        Intent i = new Intent(Sent_PW1.this, Select_Lock.class);
        startActivity(i);
        finish();
    }
}
