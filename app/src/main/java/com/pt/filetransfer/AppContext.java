package com.pt.filetransfer;

import android.app.Application;

import com.pt.filetransfer.entity.FileInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 全局的Application Context
 */
public class AppContext extends Application {

    /**
     * 主要的线程池
     */
    public static Executor MAIN_EXECUTOR = Executors.newFixedThreadPool(5);

    /**
     * 文件发送单线程
     */
    public static Executor FILE_SENDER_EXECUTOR = Executors.newSingleThreadExecutor();

    /**
     * 全局应用的上下文
     */
    static AppContext mAppContext;


    //文件发送方
    Map<String, FileInfo> mFileInfoMap = new HashMap<String, FileInfo>(); //采用HashMap结构，文件地址--->>>FileInfo 映射结构，重复加入FileInfo

    Map<String, FileInfo> mReceiverFileInfoMap = new HashMap<String, FileInfo>();

    public static  String EXTENSION_APK = ".apk";
    public static  String EXTENSION_JPG = ".jpg";
    public static  String EXTENSION_JPEG = ".jpeg";
    public static  String EXTENSION_DOC = ".doc";
    public static  String EXTENSION_PDF = ".pdf";
    public static  String EXTENSION_MP3 = ".mp3";
    public static  String EXTENSION_MP4 = ".mp4";
    public static  String EXTENSION_TXT = ".txt";


    @Override
    public void onCreate() {
        super.onCreate();
        this.mAppContext = this;
    }

    /**
     * 获取全局的AppContext
     * @return
     */
    public static AppContext getAppContext(){
        return mAppContext;
    }

    //==========================================================================
    //==========================================================================
    //发送方
    /**
     * 添加一个FileInfo
     * @param fileInfo
     */
    public void addFileInfo(FileInfo fileInfo){

        if(!mFileInfoMap.containsKey(fileInfo.getPath())){
            mFileInfoMap.put(fileInfo.getPath(), fileInfo);
        }
    }

    /**
     * 更新FileInfo
     * @param fileInfo
     */
    public void updateFileInfo(FileInfo fileInfo){
        mFileInfoMap.put(fileInfo.getPath(), fileInfo);
    }

    /**
     * 删除一个FileInfo
     * @param fileInfo
     */
    public void deleteFileInfo(FileInfo fileInfo){

        if(mFileInfoMap.containsKey(fileInfo.getPath())){
            mFileInfoMap.remove(fileInfo.getPath());
        }
    }

    /**
     * 得到选中文件的多少
     * @return
     */
    public int getFileMapSize(){
        return mFileInfoMap.size();
    }

    /**
     * 是否存在FileInfo
     * @param fileInfo
     * @return
     */
    public boolean isExist(FileInfo fileInfo){
        if(mFileInfoMap == null) return false;
        return mFileInfoMap.containsKey(fileInfo.getPath());
    }

    /**
     * 判断文件集合是否有元素
     * @return 有返回true， 反之
     */
    public boolean isFileInfoMapExist(){
        if(mFileInfoMap == null || mFileInfoMap.size() <= 0){
            return false;
        }
        return true;
    }

    /**
     * 获取全局变量中的FileInfoMap
     * @return
     */
    public Map<String, FileInfo> getFileInfoMap(){
        return mFileInfoMap;
    }

    /**
     * 获取即将发送文件列表的总长度
     * @return
     */
    public long getAllSendFileInfoSize(){
        long total = 0;
        for(FileInfo fileInfo : mFileInfoMap.values()){
            if(fileInfo != null){
                total = total + fileInfo.getSize();
            }
        }
        return total;
    }

    //==========================================================================
    //==========================================================================



    //==========================================================================
    //==========================================================================
    //发送方
    /**
     * 添加一个FileInfo
     * @param fileInfo
     */
    public void addReceiverFileInfo(FileInfo fileInfo){
        if(!mReceiverFileInfoMap.containsKey(fileInfo.getPath())){
            mReceiverFileInfoMap.put(fileInfo.getPath(), fileInfo);
        }
    }

    /**
     * 更新FileInfo
     * @param fileInfo
     */
    public void updateReceiverFileInfo(FileInfo fileInfo){
        mReceiverFileInfoMap.put(fileInfo.getPath(), fileInfo);
    }

    /**
     * 删除一个FileInfo
     * @param fileInfo
     */
    public void delReceiverFileInfo(FileInfo fileInfo){
        if(mReceiverFileInfoMap.containsKey(fileInfo.getPath())){
            mReceiverFileInfoMap.remove(fileInfo.getPath());
        }
    }

    /**
     * 是否存在FileInfo
     * @param fileInfo
     * @return
     */
    public boolean isReceiverInfoExist(FileInfo fileInfo){
        if(mReceiverFileInfoMap == null) return false;
        return mReceiverFileInfoMap.containsKey(fileInfo.getPath());
    }

    /**
     * 判断文件集合是否有元素
     * @return 有返回true， 反之
     */
    public boolean isReceiverFileInfoMapExist(){
        if(mReceiverFileInfoMap == null || mReceiverFileInfoMap.size() <= 0){
            return false;
        }
        return true;
    }

    /**
     * 获取全局变量中的FileInfoMap
     * @return
     */
    public Map<String, FileInfo> getReceiverFileInfoMap(){
        return mReceiverFileInfoMap;
    }


    /**
     * 获取即将接收文件列表的总长度
     * @return
     */
    public long getAllReceiverFileInfoSize(){
        long total = 0;
        for(FileInfo fileInfo : mReceiverFileInfoMap.values()){
            if(fileInfo != null){
                total = total + fileInfo.getSize();
            }
        }
        return total;
    }

    public void clearFileInfos(){
        if (mFileInfoMap != null && mFileInfoMap.size() != 0)
             mFileInfoMap.clear();
    }

    public long getFileTotalSize() {
        long fileTotalSize = 0;
        if(mFileInfoMap != null && mFileInfoMap.size() >0) {
            for (FileInfo fileInfo : mFileInfoMap.values()) {
                fileTotalSize += fileInfo.getSize();
            }
        }
        return fileTotalSize;
    }

    //==========================================================================
    //==========================================================================

}
