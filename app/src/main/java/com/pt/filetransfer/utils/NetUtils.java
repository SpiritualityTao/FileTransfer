package com.pt.filetransfer.utils;

import java.io.IOException;

/**
 * Created by 韬 on 2017-06-04.
 */
public class NetUtils {

    public static boolean pingIpAddress(String serverIp) {
        try {
            Process process = Runtime.getRuntime().exec("/system/bin/ping -c 1 -w 100 " + serverIp);
            int status = process.waitFor();
            if (status == 0) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}
