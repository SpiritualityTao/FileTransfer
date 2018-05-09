package com.pt.filetransfer.utils;

import android.content.Context;
import android.content.Intent;

import com.pt.filetransfer.ui.FileChooseActivity;
import com.pt.filetransfer.ui.FileManagerActivity;
import com.pt.filetransfer.ui.HistoryFileActivity;
import com.pt.filetransfer.ui.ReceiveFileActivity;
import com.pt.filetransfer.ui.ReceiverWaitingActivity;
import com.pt.filetransfer.ui.SendFileActivity;
import com.pt.filetransfer.ui.SenderWaitingActivity;

/**
 * UI 导航类 主要控制跳转Acitivity
 *
 * Created by 韬 on 2017-05-08.
 */
public class NavigatorUtils {


    /**
     * 跳转文件选择者UI
     * @param context
     */
    public static void toChooseFileUI(Context context){
        if(context == null){
            throw  new RuntimeException("Context not be null !");
        }

        Intent intent = new Intent(context,FileChooseActivity.class);
        context.startActivity(intent);
    }

    /**
     * 跳转选择文件接收者UI
     * @param context
     */
    public  static  void toReceiverWaitingUI(Context context){
        if(context == null){
            throw  new RuntimeException("Context not be null !");
        }

        Intent intent = new Intent(context,ReceiverWaitingActivity.class);
        context.startActivity(intent);
    }


    public static void toFileManagerUI(Context context){
        if(context == null){
            throw  new RuntimeException("Context not be null !");
        }

        Intent intent = new Intent(context,FileManagerActivity.class);
        context.startActivity(intent);
    }

    /**
     * 跳转发送等待界面
     */
    public static void toSenderWaitingUI(Context context) {
        if(context == null){
            throw  new RuntimeException("Context not be null !");
        }
        Intent intent = new Intent(context,SenderWaitingActivity.class);
        context.startActivity(intent);
    }

    public static void toReceiveFileUI(Context context,String fileList) {
        if(context == null){
            throw  new RuntimeException("Context not be null !");
        }
        Intent intent = new Intent(context,ReceiveFileActivity.class);
        intent.putExtra("fileList",fileList);
        context.startActivity(intent);
    }

    public static void toSendFileUI(Context context, String receiverIp,String ssid) {
        if(context == null){
            throw  new RuntimeException("Context not be null !");
        }
        Intent intent = new Intent(context,SendFileActivity.class);
        intent.putExtra("receiverIp",receiverIp);
        intent.putExtra("ssid",ssid);
        context.startActivity(intent);
    }

    public static void toHistoryFileUI(Context context) {
        if(context == null){
            throw  new RuntimeException("Context not be null !");
        }

        Intent intent = new Intent(context,HistoryFileActivity.class);
        context.startActivity(intent);
    }
}
