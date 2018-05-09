package com.pt.filetransfer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pt.filetransfer.AppContext;
import com.pt.filetransfer.Constant;
import com.pt.filetransfer.R;
import com.pt.filetransfer.adapter.FileSendAdapter;
import com.pt.filetransfer.core.FileSender;
import com.pt.filetransfer.dao.HistoryDao;
import com.pt.filetransfer.entity.FileInfo;
import com.pt.filetransfer.entity.HistoryInfo;
import com.pt.filetransfer.utils.ConvertUtils;
import com.pt.filetransfer.utils.NavigatorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SendFileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SendFileActivity";
    private static final int MSG_SEND_SUCCESS = 0;
    private static final int MSG_UPDATE_PROGRESS = 1;

    private LinearLayout ll_search;
    private TextView tv_left;
    private TextView tv_title;
    private TextView tv_little_title;
    private TextView tv_send_size;
    //当前发送个数
    private TextView tv_sended_size;
    //发送速度
    private TextView tv_send_speed;
    private Button btn_send_history;
    private ImageView iv_search;
    //文件的发送列表视图
    private ListView lv_list_send;
    //接收者的ip地址
    private String receiverIp;
    //当前成功的个数
    private int successNum = 0;
    List<FileInfo> SendFileLists = new ArrayList<FileInfo>();
    //平均速度
    private long avgSpeed = 0;
    //文件发送List的Adapter
    private FileSendAdapter mAdapter;
    //历史记录数据dao类
    private HistoryDao historyDao;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_UPDATE_PROGRESS:
                    tv_send_speed.setText(ConvertUtils.convertFileSize(avgSpeed) + "/s");
                    mAdapter.notifyDataSetChanged();
                    break;
                case MSG_SEND_SUCCESS:
                    //设置已发送成功个数
                    tv_sended_size.setText(successNum + "/" + SendFileLists.size());
                    mAdapter.notifyDataSetChanged();
                    break;
            }
            super.handleMessage(msg);
        }
    };
    private String ssid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_file);
        //初始化开启数据库操作
        historyDao = new HistoryDao(this);
        historyDao.open();
        //得到上一个Acitivity传输过来的接收者ip，ssid
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        receiverIp = bundle.getString("receiverIp");
        ssid = bundle.getString("ssid");
        initView();
        init();
    }

    @Override
    protected void onDestroy() {
        //关闭数据库
        if(historyDao != null)
            historyDao.close();
        super.onDestroy();
    }

    private void init() {
        //文件发送列表
        Map<String ,FileInfo > fileSRMaps = AppContext.getAppContext().getFileInfoMap();
        for (Map.Entry<String, FileInfo> entry : fileSRMaps.entrySet()) {
            final FileInfo fileInfo = entry.getValue();
            SendFileLists.add(fileInfo);
            mAdapter = new FileSendAdapter(SendFileActivity.this);
        }
        mAdapter.setmFileSendList(SendFileLists);
        lv_list_send.setAdapter(mAdapter);

        Log.i(TAG, "FileSendAdapter: " + fileSRMaps.toString());
        for (final FileInfo fileInfo :SendFileLists) {
            //构造文件发送这Runnable
            FileSender fileSender = new FileSender(fileInfo,receiverIp,Constant.DEFAULT_TCP_PORT);
            fileSender.setmOnSendListener(new FileSender.OnSendListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onProgress(long progress,long speed) {
                    Log.i(TAG, "onProgress: " + progress);
                    avgSpeed = speed;
                    fileInfo.setProcceed(progress);
                    mHandler.obtainMessage(MSG_UPDATE_PROGRESS).sendToTarget();
                }

                @Override
                public void onSuccess() {
                    Log.i(TAG, "onSuccess: ");
                    historyDao.addHistoryInfo(fileInfo,"",ssid,fileInfo.getInstallTime(), HistoryInfo.ACTION_SNED);
                    successNum++;
                    fileInfo.setSuccess(true);
                    mHandler.obtainMessage(MSG_SEND_SUCCESS).sendToTarget();
                }

                @Override
                public void onFailure(Throwable t, FileInfo fileInfo) {

                }
            });
            AppContext.MAIN_EXECUTOR.execute(fileSender);
        }
    }

    private void initView() {
        btn_send_history = (Button) findViewById(R.id.btn_send_history);
        btn_send_history.setOnClickListener(this);
        ll_search = (LinearLayout) findViewById(R.id.ll_search);
        tv_left = (TextView) findViewById(R.id.tv_left);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_send_size = (TextView) findViewById(R.id.tv_send_size);
        tv_sended_size = (TextView) findViewById(R.id.tv_sended_size);
        tv_little_title = (TextView) findViewById(R.id.tv_little_title);
        tv_send_speed = (TextView) findViewById(R.id.tv_send_speed);
        iv_search = (ImageView) findViewById(R.id.iv_search);
        lv_list_send = (ListView) findViewById(R.id.lv_list_send);
        tv_left.setText(getResources().getString(R.string.str_back));
        ll_search.setBackgroundColor(ContextCompat.getColor(this, R.color.color_storage));
        iv_search.setVisibility(View.GONE);
        tv_title.setText(ssid);
        tv_little_title.setVisibility(View.VISIBLE);
        tv_little_title.setText(getResources().getString(R.string.str_send_title));
        String total = ConvertUtils.convertFileSize(AppContext.getAppContext().getFileTotalSize());
        tv_send_size.setText(total.substring(0,total.length() - 2));
        tv_sended_size.setText(successNum + "/" + AppContext.getAppContext().getFileMapSize());
        tv_left.setOnClickListener(this);
    }

    public void continueSend(View v){
        if(successNum == SendFileLists.size()){
            NavigatorUtils.toChooseFileUI(SendFileActivity.this);
        }else{
            Toast.makeText(SendFileActivity.this,"当前文件还没发送完成，请完成之后再操作",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_left:
                this.finish();
                break;
            case R.id.btn_send_history:
                NavigatorUtils.toHistoryFileUI(this);
                break;
        }
    }
}
