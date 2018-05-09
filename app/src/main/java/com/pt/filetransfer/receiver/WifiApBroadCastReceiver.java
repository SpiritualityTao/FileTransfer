package com.pt.filetransfer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

/**
 * Wifi热点监听广播
 * 热点可用
 */
public abstract class WifiApBroadCastReceiver extends BroadcastReceiver {

    public static final String ACTION_WIFI_AP_STATE_CHANGED = "android.net.wifi.WIFI_AP_STATE_CHANGED";


    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        String action = intent.getAction();
        if(action.equals(ACTION_WIFI_AP_STATE_CHANGED)){ //Wifi AP state changed
            // 得到Wifi状态
            int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            //便携式热点的状态为：10---正在关闭；11---已关闭；12---正在开启；13---已开启
            if (WifiManager.WIFI_STATE_ENABLED == state % 10) {
                // Wifi is enabled
                onWifiApEnabled();
            }else if(WifiManager.WIFI_STATE_ENABLING == state % 10){
                // Wifi is enabling
            }else if(WifiManager.WIFI_STATE_DISABLED == state % 10){
                onWifiApDisable();
                // Wifi is disable
            }else if(WifiManager.WIFI_STATE_DISABLING == state % 10){
                // Wifi is disabling
            }
        }

    }

    /**
     * 热点可用
     */
    public abstract void onWifiApEnabled();
    /**
     * 热点可用
     */
    public abstract void onWifiApDisable();
}
