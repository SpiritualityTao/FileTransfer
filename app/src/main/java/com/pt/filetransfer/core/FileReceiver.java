package com.pt.filetransfer.core;

import android.util.Log;

import com.pt.filetransfer.entity.FileInfo;
import com.pt.filetransfer.utils.ConvertUtils;
import com.pt.filetransfer.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by 韬 on 2017-06-06.
 * 文件接收者
 * 1.文件头的接收解析
 * 2.文件体接收写入
 * 3.文件接收监听回调
 */
public class FileReceiver implements Runnable {

    private static final String TAG = "FileReceiver";
    // Socket的输入输出流
    private Socket mSocket;
    //输入流
    private InputStream mInputStream;

    //接收文件的信息（即要写入手机）
    private FileInfo mFileInfo;

    //控制线程暂停 恢复
    private final Object LOCK = new Object();
    boolean isPaused = false;
    String localPath;
    /**
     * 文件接收的监听
     */
    OnReceiveListener mOnReceiveListener;
    public OnReceiveListener getmOnReceiveListener() {
        return mOnReceiveListener;
    }

    public void setmOnReceiveListener(OnReceiveListener mOnReceiveListener) {
        this.mOnReceiveListener = mOnReceiveListener;
    }


    public FileReceiver(Socket socket) {
        this.mSocket = socket;
    }

    @Override
    public void run() {
        //初始化
        try {
            if(mOnReceiveListener != null) mOnReceiveListener.onStart();
            init();
        } catch (Exception e) {
            e.printStackTrace();
            if(mOnReceiveListener != null) mOnReceiveListener.onFailure(e, mFileInfo);
        }

        //解析头部
        try {
            parseHeader();
        } catch (Exception e) {
            e.printStackTrace();
            if(mOnReceiveListener != null) mOnReceiveListener.onFailure(e, mFileInfo);
        }


        //解析主体
        try {
            parseBody();
        } catch (Exception e) {
            e.printStackTrace();
            if(mOnReceiveListener != null) mOnReceiveListener.onFailure(e, mFileInfo);
        }

        //结束
        try {
            finishAndClose();
        } catch (Exception e) {
            e.printStackTrace();
            if(mOnReceiveListener != null) mOnReceiveListener.onFailure(e, mFileInfo);
        }
    }

    /**
     * 初始化Socket
     * @throws IOException
     */
    private void init() throws IOException {
        if (mSocket != null)
            mInputStream = mSocket.getInputStream();
    }

    /**
     * 解析头部
     */
    private void parseHeader() throws IOException {
        //读取header部分 长度为10*1024  发送者定义
        byte[] headerBytes = new byte[10*1024];
        int headTotal = 0;
        int readByte = -1;
        //开始读取header
        while((readByte = mInputStream.read()) != -1){
            headerBytes[headTotal] = (byte) readByte;

            headTotal ++;
            if(headTotal == headerBytes.length){
                break;
            }
        }
        String jsonStr = new String(headerBytes, "UTF-8");
        String[] strArray = jsonStr.split("::");
        jsonStr = strArray[1].trim();
        mFileInfo = FileInfo.toObject(jsonStr);
        Log.i(TAG, "parseHeader: " + mFileInfo.toString());
    }

    /**
     * 解析文件体
     * 1.得到输入流
     * 2.写入手机
     */
    private void parseBody() throws IOException {
        //写入文件
        long fileSize = mFileInfo.getSize();
        File file = FileUtils.getLocalFilePath(mFileInfo.getPath());
        localPath= file.getAbsolutePath();
        long InstallTime = System.currentTimeMillis();
        Log.i(TAG, "parseBody: InstallTime" +ConvertUtils.convertDateString(InstallTime));
        Log.i(TAG, "parseBody: localPath = " + localPath);
        OutputStream bos = new FileOutputStream(file);

        //记录文件开始写入时间
        long startTime = System.currentTimeMillis();

        byte[] bytes = new byte[4*1024];
        long total = 0;
        int len = 0;
        long totalPerTime = 0;
        long sTime = System.currentTimeMillis();
        long eTime = 0;
        while((len=mInputStream.read(bytes)) != -1){
            synchronized(LOCK) {
                if (isPaused) {
                    try {
                        LOCK.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                bos.write(bytes, 0, len);
                total = total + len;
                totalPerTime = totalPerTime + len;
                eTime = System.currentTimeMillis();
                if(eTime - sTime > 200) { //大于200ms 才进行一次监听

                    Log.i(TAG, "sendBody: " + ConvertUtils.convertFileSize((long) (totalPerTime/((float)(eTime - sTime) * 0.001))));
                    long speed = (long) (totalPerTime/((float)(eTime - sTime) * 0.001));
                    totalPerTime = 0;
                    sTime = eTime;
                    if(mOnReceiveListener != null) mOnReceiveListener.onProgress(total, speed,mFileInfo.getName());
                }
            }
        }
        //记录文件结束写入时间
        long endTime = System.currentTimeMillis();

        if(mOnReceiveListener != null) mOnReceiveListener.onSuccess(mFileInfo,localPath,InstallTime);
    }

    /**
     * 完成关闭输入流和Socket
     */
    private void finishAndClose() {
        if(mInputStream != null){
            try {
                mInputStream.close();
            } catch (IOException e) {

            }
        }

        if(mSocket != null && mSocket.isConnected()){
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

   public interface OnReceiveListener{
        void onStart();
        void onProgress(long progress, long speed,String fileName);
        void onSuccess(FileInfo fileInfo,String filePath,long InstallTime);
        void onFailure(Throwable t, FileInfo fileInfo);
    }
}
