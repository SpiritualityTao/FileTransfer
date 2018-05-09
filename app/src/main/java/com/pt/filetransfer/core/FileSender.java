package com.pt.filetransfer.core;

import android.util.Log;

import com.pt.filetransfer.entity.FileInfo;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by 韬 on 2017-06-06.
 * 文件发送者
 * 1.文件头构造发送
 * 2.文件体构造发送
 * 2.文件发送时各数据的监听回调
 */
public class FileSender implements Runnable{

    private static final String TAG = "FileSender";


    private FileInfo mFileInfo;
    private String receiverIp;
    private int receiverPort;

    //TCP通信socket
    private Socket socket;
    //发送输出流
    private BufferedOutputStream bos;

    //控制线程暂停 恢复
    private final Object lock = new Object();

    boolean isPaused = false;

    //判断此线程是否完毕
    boolean isFinished = false;

    // 设置未执行的线程不执行的标识
    boolean isStop = false;

    public OnSendListener getmOnSendListener() {
        return mOnSendListener;
    }

    public void setmOnSendListener(OnSendListener mOnSendListener) {
        this.mOnSendListener = mOnSendListener;
    }

    //发送进度监听者
    OnSendListener mOnSendListener;
    public FileSender(FileInfo fileInfo, String receiverIp, int receiverPort) {
        mFileInfo = fileInfo;
        this.receiverIp = receiverIp;
        this.receiverPort = receiverPort;
    }

    @Override
    public void run() {
        if (isStop)  return;    //如果是stop则返回
        try {
            //监听者不为空，开始监听并初始化输出流
            if(mOnSendListener != null) mOnSendListener.onStart();
            init();
        } catch (IOException e) {
            if(mOnSendListener != null) mOnSendListener.onFailure(e,mFileInfo);
            e.printStackTrace();
        }
        try {
            sendHeader();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            sendBody();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finishAndClose();
    }

    /**
     * 发送文件头
     */
    private void sendHeader() throws Exception {
        //构造header 总长度为10*1024
        StringBuilder sb = new StringBuilder();
        Log.i(TAG, "sendHeader: mFileInfo" + mFileInfo.toString());
        String jsonStr = FileInfo.toJsonStr(mFileInfo);
        jsonStr = "header" + "::" + jsonStr;
        sb.append(jsonStr);
        int leftLen = 10*1024 - jsonStr.getBytes("UTF-8").length; //对于英文是一个字母对应一个字节，中文的情况下对应两个字节。剩余字节数不应该是字节数
        for(int i=0; i < leftLen; i++){
            sb.append(" ");
        }
        byte[] headbytes = sb.toString().getBytes("UTF-8");
        //发送header
        bos.write(headbytes);
    }

    //发送文件体
    private void sendBody() throws IOException {
        //写入文件
        long fileSize = mFileInfo.getSize();
        //得到文件输入流
        InputStream fis = new FileInputStream(new File(mFileInfo.getPath()));

        //记录文件开始写入时间
        long startTime = System.currentTimeMillis();

        byte[] bytes = new byte[4*1024];
        long total = 0;
        long totalPerTime = 0;
        int len = 0;

        long sTime = System.currentTimeMillis();
        long eTime = 0;
        while((len=fis.read(bytes)) != -1){
            //添加同步锁
            synchronized(lock) {
                if (isPaused) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                bos.write(bytes, 0, len);
                total = total + len;
                totalPerTime = totalPerTime + len;
                eTime = System.currentTimeMillis();
                if(eTime - sTime > 200){ //每大于200ms监听
                    long speed = (long) (totalPerTime/((float)(eTime - sTime) * 0.001));
                    totalPerTime = 0;
                    sTime = eTime;
                    if(mOnSendListener != null) mOnSendListener.onProgress(total,speed );
                }
            }
        }

        //记录文件结束写入时间
        long endTime = System.currentTimeMillis();
        bos.flush();
        //关闭输出流，以防阻塞
        bos.close();
        if(mOnSendListener != null) mOnSendListener.onSuccess();
        isFinished = true;
    }

    public void finishAndClose() {

        if(bos != null){
            try {
                bos.close();
            } catch (IOException e) {

            }
        }

        if(socket != null && socket.isConnected()){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        Log.i(TAG, "FileSender close socket######>>>");
    }


    private void init() throws IOException {
        socket = new Socket(receiverIp,receiverPort);
        OutputStream os = socket.getOutputStream();
        bos = new BufferedOutputStream(os);
    }

    /**
     * 文件传送的监听
     */
    public interface OnSendListener{
        void onStart();
        void onProgress(long progress,long speed);
        void onSuccess();
        void onFailure(Throwable t, FileInfo fileInfo);
    }
}
