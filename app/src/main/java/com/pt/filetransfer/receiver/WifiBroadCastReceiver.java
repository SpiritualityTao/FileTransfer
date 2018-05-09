package com.pt.filetransfer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

/**
 * Created by 韬 on 2017-06-04.
 */
public abstract class WifiBroadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if (intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION)) {
            //signal strength changed
        } else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {//wifi连接上与否
            System.out.println("网络状态改变");
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                onWifiDisconnected();
                System.out.println("wifi网络连接断开");
            } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                onWifiConnected();

            }

        }  else if(intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)){//wifi打开与否  
            int wifistate = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);
            if(wifistate == WifiManager.WIFI_STATE_DISABLED){
                onWifiClose();
                System.out.println("系统关闭wifi");
            }
            else if(wifistate == WifiManager.WIFI_STATE_ENABLED){
                System.out.println("系统开启wifi");
            }
        }
    }

    protected abstract void onWifiClose();

    //当Wifi没有连接成功
    protected abstract void onWifiDisconnected();
    //当Wifi连接
    protected  abstract void onWifiConnected();
}