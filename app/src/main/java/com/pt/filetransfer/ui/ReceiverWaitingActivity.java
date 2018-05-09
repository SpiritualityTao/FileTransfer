package com.pt.filetransfer.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pt.filetransfer.AppContext;
import com.pt.filetransfer.Constant;
import com.pt.filetransfer.R;
import com.pt.filetransfer.receiver.WifiBroadCastReceiver;
import com.pt.filetransfer.utils.JsonUtils;
import com.pt.filetransfer.utils.NavigatorUtils;
import com.pt.filetransfer.utils.NetUtils;
import com.pt.filetransfer.utils.WifiUtils;
import com.xys.libzxing.zxing.activity.CaptureActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReceiverWaitingActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "ReceiverWaitingActivity";
    private static final int MSG_SCAN = 0;
    private static final int MSG_RECEIVE_FILE = 1;
    private static final int MSG_CREATE_UDP = 2;
    private static final int MSG_CLOSE_UDP = 3;
    //得到扫描Wifi结果
    List<ScanResult> scanResults;
    //广播监听Wifi连接成功与否
    private WifiBroadCastReceiver mWifiReceiver;

    boolean isCreate = false;

    //控件
    private CircleImageView iv_other_side;
    private ProgressBar pb_wait_receive;
    private TextView tv_receive_warn;
    private TextView tv_ssid;
    //topbar
    private TextView tv_left;
    private TextView tv_title;
    private TextView tv_little_title;
    private ImageView iv_search;

    private LinearLayout btn_search_sender;
    private LinearLayout btn_qrcode_scan;
    private Button btn_open_wifi;
    //发送udp的Runnable
    Runnable mUdpRunnable;



    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_SCAN:
                    getScanResult();
                    break;
                case MSG_RECEIVE_FILE:
                    String fileList = (String) msg.obj;
                    closeSocket();
                    NavigatorUtils.toReceiveFileUI(ReceiverWaitingActivity.this,fileList);
                    break;
                case MSG_CREATE_UDP:

                    if(!isCreate){
                        tv_ssid.setVisibility(View.VISIBLE);
                        tv_ssid.setText(WifiUtils.getInstance(ReceiverWaitingActivity.this).getCurWifiSSID());
                        iv_other_side.setVisibility(View.VISIBLE);
                        sendMessageToSender();
                        isCreate = true;
                    }
                    break;
                case MSG_CLOSE_UDP:

                    break;
            }
        }
    };
    private boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver_waiting);
        initView();
        init();

    }

    @Override
    protected void onResume() {
        openSocket();
        super.onResume();
    }

    private void openSocket() {
        Log.i(TAG, "openSocket: ");
        int serverPort = Constant.DEFAULT_UDP_PORT;
        if (mDatagramSocket == null) {
            try {
                mDatagramSocket = new DatagramSocket(null);
                mDatagramSocket.setReuseAddress(true);    //    DatagramSocket的setReuseAddress(true)方法执行后，可以允许多个DatagramSocket

                mDatagramSocket.bind(new InetSocketAddress(serverPort));
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(mWifiReceiver != null){
            unregisterReceiver(mWifiReceiver);
            mWifiReceiver = null;
        }
        closeSocket();
        WifiUtils.getInstance(this).closeWifi();
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mWifiReceiver != null){
            unregisterReceiver(mWifiReceiver);
            mWifiReceiver = null;
        }
    }

    private void init() {
        //Wifi是否打开
        if(!WifiUtils.getInstance(this).isWifiEnable()){
            WifiUtils.getInstance(this).openWifi();
        }
        if(mHandler != null)
            mHandler.obtainMessage(MSG_SCAN).sendToTarget();

    }

    private void initView() {
        tv_left = (TextView) findViewById(R.id.tv_left);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_little_title = (TextView) findViewById(R.id.tv_little_title);
        iv_search = (ImageView) findViewById(R.id.iv_search);

        iv_search.setVisibility(View.GONE);
        tv_left.setText(getResources().getString(R.string.str_back));
        tv_title.setText(getResources().getString(R.string.app_name));
        tv_little_title.setVisibility(View.VISIBLE);
        tv_little_title.setText(getResources().getString(R.string.str_send_title));

        iv_other_side = (CircleImageView) findViewById(R.id.iv_other_side);
        pb_wait_receive = (ProgressBar) findViewById(R.id.pb_wait_receive);
        tv_receive_warn = (TextView) findViewById(R.id.tv_receive_warn);
        tv_ssid = (TextView) findViewById(R.id.tv_ssid);
        btn_search_sender = (LinearLayout) findViewById(R.id.btn_search_sender);
        btn_open_wifi = (Button) findViewById(R.id.btn_open_wifi);
        btn_qrcode_scan = (LinearLayout) findViewById(R.id.btn_qrcode_scan);
        tv_left.setOnClickListener(this);
        btn_open_wifi.setOnClickListener(this);
        btn_search_sender.setOnClickListener(this);
        btn_qrcode_scan.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_left:
                finish();
                break;
            case R.id.btn_open_wifi:
                break;
            case R.id.btn_search_sender:
                break;
            case R.id.btn_qrcode_scan:
                startActivityForResult(new Intent(this, CaptureActivity.class),0);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Bundle bundle = data.getExtras();
            String qrCodeData = bundle.getString("result");     //得到二维码数据
            Log.i(TAG, "onActivtyResult: " + qrCodeData );
            JSONObject json = null;
            try {
                json = new JSONObject(qrCodeData);
                String msg = json.getString("msg");
                JSONArray fileList = json.getJSONArray("fileList");
                if(msg != null && msg.equals(Constant.MSG_QRCODE_FILELIST)) {
                    closeSocket();
                    NavigatorUtils.toReceiveFileUI(ReceiverWaitingActivity.this, fileList.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 得到扫描Wifi列表
     */
    public void getScanResult() {
        //扫描的得到没有密码的
        WifiUtils.getInstance(this).startScan();
        scanResults = WifiUtils.getInstance(this).getScanResultList();
        Log.i(TAG, "getScanResult: " + scanResults.toString());
        scanResults = filterNoPass();
        Log.i(TAG, "getScanResult: " + scanResults.toString());
        Log.i(TAG, "getScanResult: " + scanResults.size());
        if (scanResults != null && scanResults.size() > 0){
            final List<String> SSIDs = new ArrayList<String>();
            for (int i = 0; i < scanResults.size(); i++) {
                final ScanResult scanResult = scanResults.get(i);
                SSIDs.add(scanResult.SSID);
            }

            mWifiReceiver = new WifiBroadCastReceiver() {
                @Override
                protected void onWifiClose() {
                    closeSocket();
                    isCreate = false;
                    Log.i(TAG, "onWifiClose: " );
                    btn_open_wifi.setVisibility(View.VISIBLE);
                    mHandler.obtainMessage(MSG_CLOSE_UDP).sendToTarget();
                }

                @Override
                protected void onWifiDisconnected() {
                    Log.i(TAG, "onWifiDisconnected: " );
                    closeSocket();
                    isCreate=false;
                    isConnected = false;
                    tv_ssid.setVisibility(View.GONE);
                    iv_other_side.setVisibility(View.GONE);
                    mHandler.obtainMessage(MSG_CLOSE_UDP).sendToTarget();
                }

                @Override
                protected void onWifiConnected() {
                    isConnected = true;
                    if(!WifiUtils.getInstance(ReceiverWaitingActivity.this).isCurrentWifi(SSIDs)){
                        Log.i(TAG, "onWifiConnected: not equels" + SSIDs.toString());
//                        if(WifiUtils.getInstance(ReceiverWaitingActivity.this).disconnectCurrentNetwork())
//                            connectWifi(scanResults.get(0));
                    }else {
                        Log.i(TAG, "onWifiConnected: equels" + SSIDs.toString());
                        pb_wait_receive.setVisibility(View.GONE);
                        tv_receive_warn.setText(getResources().getString(R.string.str_wait_send_des));
                    }
                    mHandler.obtainMessage(MSG_CREATE_UDP).sendToTarget();
                }
            };
            IntentFilter filter = new IntentFilter();
            filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(mWifiReceiver, filter);
//            if(isConnected){
//
//            }else{
//                connectWifi(scanResult);
//            }
        }else {
            Log.i(TAG, "getScanResult: " + scanResults.size());
            tv_ssid.setVisibility(View.GONE);
            iv_other_side.setVisibility(View.GONE);
            mHandler.sendEmptyMessageDelayed(MSG_SCAN,2000);
        }

    }

    /**
     * 发送UDP给热点设备
     * 告诉自己已经连接成功
     * 然后接收发送方初始化成功跳转至ReceiveFileActivity UI界面
     */
    DatagramSocket mDatagramSocket;
    private void sendMessageToSender() {
        mUdpRunnable = new Runnable() {
            
            @Override
            public void run() {
                if(isConnected) {
                    int serverPort = Constant.DEFAULT_UDP_PORT;
                    String serverIp = getServerIp();
                    try {
                        Log.i(TAG, "run: serverIp :" + serverIp);
                        //UDP发送的数据包Socket  参数是端口
                        openSocket();
                        byte[] receiveData = new byte[1024];
                        byte[] sendData;
                        InetAddress ipAddress = InetAddress.getByName(serverIp);
                        Log.i(TAG, "run: " + ipAddress.toString());
                        //发送链接成功热点消息给发送方
                        sendData = JsonUtils.getConnSuccessJson(android.os.Build.DEVICE).getBytes("UTF-8");
                        DatagramPacket sendPacket =
                                new DatagramPacket(sendData, sendData.length, ipAddress, serverPort);
                        mDatagramSocket.send(sendPacket);
                        Log.i(TAG, "Send Msg To SenderWaing######>>>" + sendData.toString());
                        //监听发送方消息
                        while (true) {
                            if(mDatagramSocket != null) {
                                //1.如果接到到消息时文件发送列表则跳转至接收文件界面
                                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                                mDatagramSocket.receive(receivePacket);
                                String response = new String(receivePacket.getData(), "UTF-8").trim();
                                Log.i(TAG, "run: " + response);
                                JSONObject json = new JSONObject(response);
                                String msg = json.getString("msg");
                                JSONArray fileList = json.getJSONArray("fileList");
                                Log.i(TAG, "run:\n json:" + json + "\nmsg:" + msg + "\nfileList:" + fileList);
                                if (msg != null && msg.equals(Constant.MSG_SENDER_INIT_SUCCESS)) {
                                    mHandler.obtainMessage(MSG_RECEIVE_FILE, fileList.toString()).sendToTarget();
                                }
                            }else{
                                break;
                            }
                        }
                    } catch (SocketException e) {
                        e.printStackTrace();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        AppContext.MAIN_EXECUTOR.execute(mUdpRunnable);
    }

    /**
     *  //得到当前热点ip地址
     * @return
     */
    private String getServerIp() {

        String serverIp = WifiUtils.getInstance(ReceiverWaitingActivity.this).getIpAddressFromHotspot();
        int count = 0;
        //未知IP以及，次数小于最大次数
        while (serverIp.equals(Constant.DEFAULT_UNKOWN_IP) && count < Constant.DEFAULT_TRY_TIME) {
            try {
                Thread.sleep(1000);
                serverIp = WifiUtils.getInstance(ReceiverWaitingActivity.this).getIpAddressFromHotspot();
                Log.i(TAG, "run: serverIP" + serverIp);
                count++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        count = 0;
        while (!NetUtils.pingIpAddress(serverIp) && count < Constant.DEFAULT_TRY_TIME) {
            try {
                Thread.sleep(500);
                Log.i(TAG, "try to ping ----->>>" + serverIp + " - " + count);
                count++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        return serverIp;
    }


    private List<ScanResult> filterNoPass() {
        if(scanResults == null || scanResults.size() == 0){
            return scanResults;
        }

        List<ScanResult> resultList = new ArrayList<>();
        for(ScanResult scanResult : scanResults){
            if( scanResult.capabilities != null && scanResult.capabilities.equals("[WPS][ESS]")){
                resultList.add(scanResult);
            }
        }

        return resultList;
    }

    private void connectWifi(ScanResult scanResult){
        String ssid = scanResult.SSID;
        WifiUtils.getInstance(ReceiverWaitingActivity.this).openWifi();
        WifiUtils.getInstance(ReceiverWaitingActivity.this).addNetwork(WifiUtils.createWifiCfg(ssid, null, WifiUtils.WIFICIPHER_NOPASS));
    }

    /**
     * 关闭UDP Socket 流
     */
    private void closeSocket(){
        Log.i(TAG, "closeSocket: ");
        if(mDatagramSocket != null){
            mDatagramSocket.disconnect();
            mDatagramSocket.close();
            mDatagramSocket = null;
        }
    }
}

