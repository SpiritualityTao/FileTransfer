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

import com.pt.filetransfer.AppContext;
import com.pt.filetransfer.Constant;
import com.pt.filetransfer.R;
import com.pt.filetransfer.adapter.FileReceiveAdapter;
import com.pt.filetransfer.core.FileReceiver;
import com.pt.filetransfer.dao.HistoryDao;
import com.pt.filetransfer.entity.FileInfo;
import com.pt.filetransfer.entity.HistoryInfo;
import com.pt.filetransfer.utils.ConvertUtils;
import com.pt.filetransfer.utils.JsonUtils;
import com.pt.filetransfer.utils.NavigatorUtils;
import com.pt.filetransfer.utils.WifiUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class ReceiveFileActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ReceiveFileActivity";

    private static final int MSG_FILE_RECEIVER_INIT_SUCCESS = 0;
    private static final int MSG_UPDATE_PROGRESS = 1;
    private static final int MSG_RECEIVE_SUCCESS = 2;
    private LinearLayout ll_search;
    private TextView tv_left;
    private TextView tv_title;
    private TextView tv_little_title;
    private ImageView iv_search;
    private TextView tv_receive_size;
    private Button btn_receive_history;
    private TextView tv_received_size;
    private TextView tv_receive_speed;

    //文件的发送列表视图
    private ListView lv_list_receive;
    private List<FileInfo> fileLists;
    //接收者Adapter
    private FileReceiveAdapter mAdapter;
    //socket 操作
    private Runnable mSocketRunnable;
    //wifi热点ip
    private String wifiApip;

    private HistoryDao historyDao;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_FILE_RECEIVER_INIT_SUCCESS:
                    try {
                       sendMsgToSender();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case MSG_UPDATE_PROGRESS:
                    tv_receive_speed.setText(ConvertUtils.convertFileSize(avgSpeed) + "/s");
                    mAdapter.notifyDataSetChanged();
                    break;
                case MSG_RECEIVE_SUCCESS:
                    tv_received_size.setText(successNum + "/" + fileLists.size());
                    mAdapter.notifyDataSetChanged();
                    break;
            }
            super.handleMessage(msg);
        }
    };
    private String ip;
    private long avgSpeed;
    private int successNum = 0;
    //ssid
    private String ssid;

    private void sendMsgToSender() {
        new Thread(){
            @Override
            public void run() {
                try {
                    sendInitSuccessMsgToSender();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    DatagramSocket mDatagramSocket;
    private void sendInitSuccessMsgToSender() throws Exception {
        Log.i(TAG, "sendFileReceiverInitSuccessMsgToFileSender------>>>start");
        if(mDatagramSocket==null){
            mDatagramSocket = new DatagramSocket(null);
            mDatagramSocket.setReuseAddress(true);
            mDatagramSocket.bind(new InetSocketAddress(Constant.DEFAULT_UDP_PORT));
        }
        wifiApip = WifiUtils.getInstance(this).getIpAddressFromHotspot();
        InetAddress ip = InetAddress.getByName(wifiApip);
        byte[] sendData = null;
        //1.发送 文件接收方 初始化
        sendData = JsonUtils.getInitSuccess().getBytes("UTF-8");
        DatagramPacket sendPacket =
                new DatagramPacket(sendData, sendData.length,ip,Constant.DEFAULT_UDP_PORT);
        mDatagramSocket.send(sendPacket);
        Log.i(TAG, "Send Msg To FileSender######>>>" + sendData.toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_file);

        historyDao = new HistoryDao(this);
        historyDao.open();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String fileListJson = bundle.getString("fileList");
        try {
            fileLists = JsonUtils.parseFileList(fileListJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initView();
        init();
    }

    @Override
    protected void onDestroy() {
        if(historyDao != null)
            historyDao.close();
        super.onDestroy();
    }

    private void init() {
        mAdapter = new FileReceiveAdapter(ReceiveFileActivity.this,fileLists);
        lv_list_receive.setAdapter(mAdapter);
        ip = WifiUtils.getInstance(ReceiveFileActivity.this).getIpAddressFromHotspot();
        mSocketRunnable = new SocketRunnable(ip,Constant.DEFAULT_TCP_PORT);
        new Thread(mSocketRunnable).start();

    }


    /**
     * 初始化View
     */
    private void initView() {
        btn_receive_history = (Button) findViewById(R.id.btn_receive_history);
        btn_receive_history.setOnClickListener(this);
        ll_search = (LinearLayout) findViewById(R.id.ll_search);

        tv_receive_size = (TextView) findViewById(R.id.tv_receive_size);
        tv_received_size = (TextView) findViewById(R.id.tv_received_size);
        tv_receive_speed = (TextView) findViewById(R.id.tv_receive_speed);
        tv_left = (TextView) findViewById(R.id.tv_left);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_little_title = (TextView) findViewById(R.id.tv_little_title);
        iv_search = (ImageView) findViewById(R.id.iv_search);
        lv_list_receive = (ListView) findViewById(R.id.lv_list_receive);
        tv_left.setText(getResources().getString(R.string.str_back));
        ll_search.setBackgroundColor(ContextCompat.getColor(this, R.color.color_storage));
        iv_search.setVisibility(View.GONE);
        ssid = WifiUtils.getInstance(this).getCurWifiSSID().replace("\"","");
        tv_title.setText(ssid);
        tv_little_title.setVisibility(View.VISIBLE);
        tv_little_title.setText(getResources().getString(R.string.str_send_title));

        long total_size = 0;
        for (int i = 0; i < fileLists.size(); i++) {
            total_size += fileLists.get(i).getSize();
        }
        String total = ConvertUtils.convertFileSize(total_size);
        tv_receive_size.setText(total.substring(0,total.length() - 2));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_receive_history:
                NavigatorUtils.toHistoryFileUI(this);
                break;
        }
    }

    /**
     * 创建TCP通信Runnable
     * 1.发送接收方初始化成功    UDP
     * 2.接收文件                TCP
     */
    class SocketRunnable implements  Runnable{

        ServerSocket serverSocket;
        private String ip;
        private int port;
        public SocketRunnable(String ipAddressFromHotspot, int port) {
            this.ip = ipAddressFromHotspot;
            this.port = port;
        }

        @Override
        public void run() {
            try {

                serverSocket = new ServerSocket(port);
                mHandler.obtainMessage(MSG_FILE_RECEIVER_INIT_SUCCESS).sendToTarget();
                //当前线程没有中断
                while (!Thread.currentThread().isInterrupted()){
                    Socket socket = serverSocket.accept();
                    //文件接收者
                    FileReceiver fileReceiver = new FileReceiver(socket);
                    fileReceiver.setmOnReceiveListener(new FileReceiver.OnReceiveListener() {
                        @Override
                        public void onStart() {

                        }



                        @Override
                        public void onProgress(long progress, long speed,String filename) {
                            avgSpeed = speed;
                            for (int i = 0; i < fileLists.size(); i++) {
                                Log.i(TAG, "onProgress: filename =" + filename);
                                Log.i(TAG, "onProgress: fileLists =" + fileLists.get(i).getName());
                                if(fileLists.get(i).getName().equals(filename)){
                                    fileLists.get(i).setProcceed(progress);
                                }
                            }
                            mHandler.obtainMessage(MSG_UPDATE_PROGRESS).sendToTarget();
                            Log.i(TAG, "onProgress: ");
                        }

                        @Override
                        public void onSuccess(FileInfo fileInfo,String filePath,long InstallTime) {
                            for (int i = 0; i < fileLists.size(); i++) {
                                if(fileLists.get(i).getName().equals(fileInfo.getName())){
                                    fileLists.get(i).setPath(filePath);
                                    fileLists.get(i).setSuccess(true);
                                    historyDao.addHistoryInfo(fileInfo,filePath,ssid,InstallTime, HistoryInfo.ACTION_RECEIVE);
                                    successNum++;
                                    mHandler.obtainMessage(MSG_RECEIVE_SUCCESS).sendToTarget();
                                }
                            }

                            Log.i(TAG, "onSuccess: ");
                        }

                        @Override
                        public void onFailure(Throwable t, FileInfo fileInfo) {
                            Log.i(TAG, "onFailure: ");
                        }
                    });
                    AppContext.getAppContext().MAIN_EXECUTOR.execute(fileReceiver);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
