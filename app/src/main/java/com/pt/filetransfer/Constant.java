package com.pt.filetransfer;

import com.pt.filetransfer.entity.FileInfo;

import java.util.Comparator;
import java.util.Map;

/**
 * Created by 韬 on 2017-06-03.
 * 应用常量
 */
public class Constant {

    /**
     * 默认的Wifi SSID
     */
    public static final String DEFAULT_SSID = "XD_HOTSPOT";

    /**
     * Wifi连接上时 未分配默认的Ip地址
     */
    public static final String DEFAULT_UNKOWN_IP = "0.0.0.0";

    /**
     * 最大尝试数
     */
    public static final int DEFAULT_TRY_TIME = 10;

    /**
     * 文件传输
     * 默认TCP端口
     */
    public static final int DEFAULT_TCP_PORT = 8080;

    /**
     * 默认UDP端口
     */
    public static final int DEFAULT_UDP_PORT = 8099;
    //发送方接收到链接成功消息
    public static final String MSG_CONNECTION_SUCCESS = "MSG_CONNECTION_SUCCESS";
    //接收方接收到发送方初始化成功的消息
    public static final String MSG_SENDER_INIT_SUCCESS = "MSG_SENDER_INIT_SUCCESS";
    //FileInfoMap 默认的Comparator
    public static final Comparator<Map.Entry<String, FileInfo>> DEFAULT_COMPARATOR =      new Comparator<Map.Entry<String, FileInfo>>() {
        public int compare(Map.Entry<String, FileInfo> o1, Map.Entry<String, FileInfo> o2) {
            if(o1.getValue().getFileType() > o2.getValue().getFileType()){
                return 1;
            } else if(o1.getValue().getFileType() < o2.getValue().getFileType()){
                return -1;
            }else{
                return 0;
            }
        }
    };
    public static final String MSG_FILE_RECEIVER_INIT_SUCCESS = "MSG_FILE_RECEIVER_INIT_SUCCESS";
    public static final String MSG_QRCODE_FILELIST = "MSG_QRCODE_FILELIST";
}
