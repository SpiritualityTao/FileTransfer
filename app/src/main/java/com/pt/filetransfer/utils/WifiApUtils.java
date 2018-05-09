package com.pt.filetransfer.utils;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.lang.reflect.Method;

/**
 * Created by 韬 on 2017-06-03.
 * 热点工具类
 * 1.权限
 * <uses-permission android:name="android.permission.CHANGE_NETWORK_STATE" />
 * <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
 * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
 * 2.封装方法
 * openWifiAp
 * isWifiApOn
 * closeWifiAp
 */
public class WifiApUtils {

    //判断热点是否可用
    public static boolean isWifiApOn(Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        try {
            Method method = wifimanager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifimanager);
        }
        catch (Throwable ignored) {}
        return false;
    }

    //关闭热点
    public static void closeWifiAp(Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        try {
            Method method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(wifimanager, null, false);
        } catch (Throwable ignored) {
        }
    }

    /**
     * 开启热点
     * @param context
     * @param apName 热点名
     * @return
     */
    public static boolean configApState(Context context, String apName) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        WifiConfiguration wificonfiguration ;
        try {
            wificonfiguration = new WifiConfiguration();
            wificonfiguration.SSID = apName;    //创建热点的名称
            if(isWifiApOn(context)) {   // 如果热点开启，关掉它
                wifimanager.setWifiEnabled(false);
                closeWifiAp(context);// if ap is on and then disable ap
            }
            Method method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(wifimanager, wificonfiguration, !isWifiApOn(context));
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
