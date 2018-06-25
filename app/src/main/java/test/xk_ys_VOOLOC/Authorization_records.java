package test.xk_ys_VOOLOC;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;
import test.xk_ys_VOOLOC.AboutFile.BaseApplication;
import test.xk_ys_VOOLOC.MyAdapter.RecordAdapter;
import test.xk_ys_VOOLOC.Net.DeleteAuthority;
import test.xk_ys_VOOLOC.Net.GiverRecord;
import test.xk_ys_VOOLOC.Net.NetCallback;

public class Authorization_records extends AppCompatActivity {
    @BindView(R.id.giver_record)
    public ListView record;
    @BindView(R.id.back)
    public TextView back;

    private String account;
    private String password;
    private String lockId;
    private GiverRecord giverRecord;
    private RecordAdapter recordAdapter;
    Vector<String> start=new Vector<>();
    Vector<String> end=new Vector<>();
    Vector<String> power=new Vector<>();
    Vector<String> customer=new Vector<>();
    Vector<String> username=new Vector<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization_records);
        ButterKnife.bind(this);

        account=getIntent().getStringExtra("account");
        password=getIntent().getStringExtra("password");
        lockId=getIntent().getStringExtra("lockId");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
         giverRecord=new GiverRecord(this,account,password,lockId);
        giverRecord.setCallback(netCallback);
        record.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder=new AlertDialog.Builder(Authorization_records.this);
                builder.setTitle("系统提示");
                builder.setMessage("是否删除此授权？\n删除后对方不可使用此门锁！");
                builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeleteAuthority deleteAuthority=new DeleteAuthority(Authorization_records.this,account,password,lockId,customer.get(position));
                        deleteAuthority.setCallback(new NetCallback() {
                            @Override
                            public void execute(String result) {
                                start.remove(position);
                                end.remove(position);
                                customer.remove(position);
                                power.remove(position);
                                username.remove(position);
                                recordAdapter.notifyDataSetChanged();
                                Toast.makeText(Authorization_records.this, "删除成功", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void error(String result) {
                                try {
                                    JSONObject jsonObject=new JSONObject(result);
                                    Toast.makeText(Authorization_records.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                    Toast.makeText(Authorization_records.this, "删除失败", Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                               }
                        });
                        new Thread(deleteAuthority).start();
                    }
                });
                builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog=builder.create();
                alertDialog.show();
            }
        });
    }
    public NetCallback netCallback=new NetCallback() {
        @Override
        public void execute(String result) {
            if(BaseApplication.netVersion==0){
                resultJY(result);
            }
            else{
                resultGG(result);
            }
        }

        @Override
        public void error(String result) {
            finish();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(giverRecord).start();
    }
    private void resultGG(String result){
        try {
            /**
             * {"code":1,"message":"操作成功","data":{"giveInfo":[{"customerPhone":"17875511333","customerPower":2,"startTime":1510116556000,"endTime":1518065340000,"customerName":"123456"},{"customerPhone":"17875511222","customerPower":2,"startTime":1510116875000,"endTime":1515387240000,"customerName":"柯1"}]}}
             */
            JSONObject jsons=new JSONObject(result);
            JSONObject data=new JSONObject(jsons.getString("data"));
            JSONArray giveInfos=new JSONArray(data.getString("giveInfo"));
            for(int i=0;i<giveInfos.length();i++){
                JSONObject json=giveInfos.getJSONObject(i);
                start.add(json.getString("startTime"));
                end.add(json.getString("endTime"));
                power.add(json.getString("customerPower"));
                if(json.toString().contains("Phone")) {
                    customer.add(json.getString("customerPhone"));
                }
                else if(json.toString().contains("customerEmail")){
                    customer.add(json.getString("customerEmail"));
                }
                else{
                    continue;
                }
                username.add(json.getString("customerName"));
            }
            recordAdapter=new RecordAdapter(Authorization_records.this,start,end,power,customer,username);
            record.setAdapter(recordAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void resultJY(String result){
        Log.e("result",result);
        try {
            JSONArray jsons=new JSONArray(result);
            start.clear();
            end.clear();
            power.clear();
            customer.clear();
            username.clear();
            for(int i=0;i<jsons.length();i++){
                JSONObject json=jsons.getJSONObject(i);
                start.add(json.getString("starttime"));
                end.add(json.getString("endtime"));
                power.add(json.getString("power"));
                if(json.getString("customer").equals("")){
                    customer.add(json.getString("customerMail"));
                }
                else {
                    customer.add(json.getString("customer"));
                }
                username.add(json.getString("username"));
            }
            recordAdapter=new RecordAdapter(Authorization_records.this,start,end,power,customer,username);
            record.setAdapter(recordAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
