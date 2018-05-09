package com.pt.filetransfer.ui;

import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pt.filetransfer.AppContext;
import com.pt.filetransfer.Constant;
import com.pt.filetransfer.R;
import com.pt.filetransfer.receiver.WifiApBroadCastReceiver;
import com.pt.filetransfer.utils.JsonUtils;
import com.pt.filetransfer.utils.NavigatorUtils;
import com.pt.filetransfer.utils.QRCodeUtils;
import com.pt.filetransfer.utils.TextUtils;
import com.pt.filetransfer.utils.WifiApUtils;
import com.pt.filetransfer.utils.WifiUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import de.hdodenhof.circleimageview.CircleImageView;

public class SenderWaitingActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SenderWaitingActivity";
    private static final int MSG_CONNECTION = 0;
    private static final int MSG_SEND_FILE = 1;
    //控件
    private TextView tv_left;
    private TextView tv_title;
    private TextView tv_little_title;
    private TextView tv_send_warn;
    private ProgressBar pb_wait_send;
    private TextView tv_refresh;
    private ImageView iv_search;
    //搜索附近按钮
    private LinearLayout btn_search_receiver;
    //二位码发送按钮
    private LinearLayout btn_qrcode_send;
    //生成二维码的图片
    private ImageView iv_QRCode;

    private CircleImageView iv_receiver;
    private TextView tv_receiver_ssid;
    //广播接收者
    WifiApBroadCastReceiver mWifiApReceiver;
    boolean isInitWifiAp = false;

    //
     private Runnable mUdpReceiveRunnable;
    private Runnable mUdpSendRunnable;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_CONNECTION:
                    tv_receiver_ssid.setText(ssid);
                    iv_receiver.setVisibility(View.VISIBLE);
                    iv_receiver.setOnClickListener(SenderWaitingActivity.this);
                    tv_receiver_ssid.setVisibility(View.VISIBLE);
                    tv_send_warn.setText(getResources().getString(R.string.str_send_file_des));
                    break;
                case MSG_SEND_FILE:
                    String receiverIp = (String) msg.obj;
                    NavigatorUtils.toSendFileUI(SenderWaitingActivity.this,receiverIp,ssid);
                    break;
            }
        }
    };
    private String ssid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender_waiting);
        initView();
        initWifiAp();
        receiveMessageFromReceiver();
    }

    /**
     * 按下返回
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(mWifiApReceiver != null){
            unregisterReceiver(mWifiApReceiver);
            mWifiApReceiver = null;
        }

        //关闭热点
        WifiApUtils.closeWifiAp(this);
        this.finish();
    }

    @Override
    protected void onDestroy() {
        if(mWifiApReceiver != null){
            unregisterReceiver(mWifiApReceiver);
            mWifiApReceiver = null;
        }
        mDatagramSocket.close();

        super.onDestroy();
    }

    /**
     * 初始化热点
     */
    private void initWifiAp() {
        //1.关闭wifi，关闭热点
        WifiUtils.getInstance(this).disableWifi();
        if(WifiApUtils.isWifiApOn(this))
            WifiApUtils.closeWifiAp(this);

        mWifiApReceiver = new WifiApBroadCastReceiver() {
            @Override
            public void onWifiApEnabled() {
                tv_send_warn.setText(getResources().getString(R.string.str_wait_des));
                pb_wait_send.setVisibility(View.VISIBLE);
                tv_refresh.setVisibility(View.GONE);

            }
            @Override
            public void onWifiApDisable() {
                tv_send_warn.setText(getResources().getString(R.string.str_wait_error));
                pb_wait_send.setVisibility(View.GONE);
                tv_refresh.setVisibility(View.VISIBLE);
            }
        };

        IntentFilter filter = new IntentFilter(WifiApBroadCastReceiver.ACTION_WIFI_AP_STATE_CHANGED);
        registerReceiver(mWifiApReceiver, filter);
        startWifiAp();
    }

    private void startWifiAp() {
        WifiApUtils.isWifiApOn(this); // check Ap state :boolean
        String ssid = TextUtils.isNullOrBlank(android.os.Build.DEVICE) ? Constant.DEFAULT_SSID : android.os.Build.DEVICE;
        WifiApUtils.configApState(this, ssid); // change Ap state :boolean
    }

    /**
     * 初始化View
     */
    private void initView() {
        tv_left = (TextView) findViewById(R.id.tv_left);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_little_title = (TextView) findViewById(R.id.tv_little_title);
        tv_send_warn = (TextView) findViewById(R.id.tv_send_warn);
        pb_wait_send = (ProgressBar) findViewById(R.id.pb_wait_send);
        tv_refresh = (TextView) findViewById(R.id.tv_refresh);
        iv_search = (ImageView) findViewById(R.id.iv_search);
        btn_search_receiver = (LinearLayout) findViewById(R.id.btn_search_receiver);
        btn_qrcode_send = (LinearLayout) findViewById(R.id.btn_qrcode_send);
        iv_receiver = (CircleImageView) findViewById(R.id.iv_receiver);
        tv_receiver_ssid = (TextView) findViewById(R.id.tv_receiver_ssid);
        iv_QRCode = (ImageView) findViewById(R.id.iv_QRCode);
        tv_left.setText(getResources().getString(R.string.str_back));
        tv_title.setText(getResources().getString(R.string.app_name));
        iv_search.setVisibility(View.GONE);
        btn_search_receiver.setPressed(true);
        tv_refresh.setOnClickListener(this);
        btn_search_receiver.setOnClickListener(this);
        btn_qrcode_send.setOnClickListener(this);
        tv_little_title.setVisibility(View.VISIBLE);
        tv_little_title.setText("已选" + AppContext.getAppContext().getFileMapSize() + "个文件");
        iv_QRCode.setImageBitmap(QRCodeUtils.makeQRCodeBitmap());
        iv_QRCode.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_search_receiver:
                iv_QRCode.setVisibility(View.GONE);
                btn_search_receiver.setPressed(true);
                btn_qrcode_send.setPressed(false);
                break;

            case R.id.btn_qrcode_send:          //二位码
                iv_QRCode.setVisibility(View.VISIBLE);
                iv_QRCode.bringToFront();
                btn_qrcode_send.setPressed(true);
                btn_search_receiver.setPressed(false);
                break;

            case R.id.tv_refresh:       //刷新  开启啊热点
                startWifiAp();
                break;

            case R.id.iv_receiver:      //点击头像进入文件发送UI
                sendMessageToReceiver();
                break;
        }
    }

    InetAddress inetAddres;
    DatagramSocket mDatagramSocket;
    /**
     * 接收发送过来的消息
     */
    private void receiveMessageFromReceiver() {
        mUdpReceiveRunnable = new Runnable() {
            @Override
            public void run() {

                try {
                    mDatagramSocket = new DatagramSocket(Constant.DEFAULT_UDP_PORT);
                } catch (SocketException e) {
                    e.printStackTrace();
                }
                while (true){
                    try {
                        byte[] receiveData = new byte[1024];
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        if(mDatagramSocket != null)
                            mDatagramSocket.receive(receivePacket);
                        else
                            mDatagramSocket = new DatagramSocket(Constant.DEFAULT_UDP_PORT);
                        //取得接收方inetAddres,以供发送用
                        InetAddress host = InetAddress.getLocalHost();
                        //接受者的ip
                        inetAddres =  receivePacket.getAddress();
                        Log.i(TAG, "run: inetAddres :" + inetAddres.getHostAddress());
                        Log.i(TAG, "run: host" + host.toString());
                        String response = new String( receivePacket.getData(), "UTF-8").trim();
                        Log.i(TAG, "run: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("msg");
                            //链接热点成功
                            if(msg != null && msg.equals(Constant.MSG_CONNECTION_SUCCESS)) {
                                ssid = jsonObject.getString("ssid");
                                mHandler.obtainMessage(MSG_CONNECTION).sendToTarget();
                            }
                            //文件接收方初始化成功
                            if (msg != null && msg.equals(Constant.MSG_FILE_RECEIVER_INIT_SUCCESS)) {
                                mHandler.obtainMessage(MSG_SEND_FILE,inetAddres.getHostAddress()).sendToTarget();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        AppContext.MAIN_EXECUTOR.execute(mUdpReceiveRunnable);
    }

    //发送给接收方： 发送方初始化成功和发送的文件列表
    private void sendMessageToReceiver() {
        mUdpSendRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (mDatagramSocket != null) {
                        //发送Json数据格式    [msg:"MSG_SENDER_INIT_SUCCESS",fileList:"..."]
                        byte[] sendData;
                        sendData = JsonUtils.getFileListJson().getBytes("UTF-8");
                        Log.i(TAG, "run: sendData :" + sendData.toString());
                        DatagramPacket sendPacket =
                                new DatagramPacket(sendData, sendData.length, inetAddres, Constant.DEFAULT_UDP_PORT);
                        mDatagramSocket.send(sendPacket);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        AppContext.MAIN_EXECUTOR.execute(mUdpSendRunnable);
    }

}
