package com.pt.filetransfer.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.pt.filetransfer.AppContext;
import com.pt.filetransfer.entity.FileGroup;
import com.pt.filetransfer.entity.FileInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 韬 on 2017-05-12.
 */
public class FileUtils {
    private static final String TAG = "FileUtils";

    private static PackageManager pManager;

    private static long size;

    public static List<FileInfo> getFileInfoByType(Context context,int type) throws PackageManager.NameNotFoundException {
        //文件列表
        List<FileInfo> fileInfos = new ArrayList<FileInfo>();
        //内存卡的文件Uri
        Uri fileUri = MediaStore.Files.getContentUri("external");

        if(type == FileInfo.TYPE_APK){
            // 获取图片、应用名、包名
            pManager = context.getPackageManager();
            List<PackageInfo> appList = getAllApps(context);

            for (int i = 0; i < appList.size(); i++) {
                PackageInfo pinfo = appList.get(i);
                FileInfo fileInfo = new FileInfo();
                // 设置图片
                fileInfo.setDrawable(pManager
                        .getApplicationIcon(pinfo.applicationInfo));
                // 设置应用程序名字
                fileInfo.setName(pManager.getApplicationLabel(
                        pinfo.applicationInfo).toString());
                // 设置应用程序的路径
                fileInfo.setPath(pManager.getApplicationInfo(pinfo.applicationInfo.packageName, 0).sourceDir);
                Log.i(TAG, "getFileInfoByType: " + fileInfo.getPath());
                File file = new File(fileInfo.getPath());
                size = getFileSize(file);
                Log.i(TAG, "getFileInfoByType: " + fileInfo.getName() + ":" +  ConvertUtils.convertFileSize(size));
                fileInfo.setSize(size);
                fileInfo.setFileType(FileInfo.TYPE_APK);
                fileInfo.setSuffix(AppContext.EXTENSION_APK);
                long firstInstallTime=pinfo.firstInstallTime;
                Log.i(TAG, "getFileInfoByType: firstInstallTime" + ConvertUtils.convertDateString(firstInstallTime));
                fileInfo.setInstallTime(firstInstallTime);
                fileInfos.add(fileInfo);

            }
        }else{
            //用后缀名筛选
            String suffix[] = null;
            if(type == FileInfo.TYPE_JPG){  //图片后设置
                suffix = new String[2];
                suffix[0] = AppContext.EXTENSION_JPG;
                suffix[1] = AppContext.EXTENSION_JPEG;
            }else if(type == FileInfo.TYPE_AUDIO_VIDEO){    //影音后缀名设置
                suffix = new String[2];
                suffix[0] = AppContext.EXTENSION_MP3;
                suffix[1] = AppContext.EXTENSION_MP4;
            }else if(type == FileInfo.TYPE_DOCUMENT){   //文档后缀名设置
                suffix = new String[2];
                suffix[0] = AppContext.EXTENSION_DOC;
                suffix[1] = AppContext.EXTENSION_PDF;
            }else if(type == FileInfo.TYPE_OTHER){   //其他
                suffix = new String[1];
                suffix[0] = AppContext.EXTENSION_TXT;
            }
            getFileInfoBySuffix(context, fileInfos, fileUri, suffix,type);

        }
        Log.i(TAG, "getFileInfoByType: " + fileInfos.toString());
        return fileInfos;
    }

    /**
     * 根据文件后缀名的到文件信息
     * @param context
     * @param fileInfos
     * @param fileUri
     * @param suffix
     * @param type
     */
    private static void getFileInfoBySuffix(Context context, List<FileInfo> fileInfos, Uri fileUri, String[] suffix, int type) {
        //筛选列，这里只筛选了：文件路径和含后缀的文件名
        String[] projection=new String[]{
                MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.TITLE
        };
        //构造筛选条件语句

        for(int i=0;i<suffix.length;i++)
        {
            List<FileInfo> mFileInfos = new ArrayList<FileInfo>();

            String selection= MediaStore.Files.FileColumns.DATA+" LIKE '%"+suffix[i]+"'";
            //按时间降序条件
            String sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED;

            Cursor cursor = context.getContentResolver().query(fileUri, projection, selection, null, sortOrder);
            try{
                if(cursor != null) {
                    while (cursor.moveToNext()) {
                        String path = cursor.getString(0);
                        FileInfo fileInfo = new FileInfo();
                        fileInfo.setPath(path);
                        String name = cursor.getString(1);
                        fileInfo.setName(name);
                        long size = 0;
                        fileInfo.setSuffix(suffix[i]);
                        File file = new File(path);
                        size = file.length();
                        fileInfo.setSize(size);
                        fileInfo.setFileType(type);
                        fileInfo.setInstallTime(file.lastModified());
                        fileInfos.add(fileInfo);
                        mFileInfos.add(fileInfo);
                    }
                }
            } catch (Exception e){
            }finally {
                cursor.close();
            }
            if(suffix[i] == AppContext.EXTENSION_MP3)
                FileGroup.AVFiles.setMusicFiles(mFileInfos);
            else if(suffix[i] == AppContext.EXTENSION_MP4)
                FileGroup.AVFiles.setMediaFiles(mFileInfos);
            else if(suffix[i] == AppContext.EXTENSION_DOC)
                FileGroup.DOCFiles.setDocFiles(mFileInfos);
            else if(suffix[i] == AppContext.EXTENSION_PDF)
                FileGroup.DOCFiles.setPdfFiles(mFileInfos);
            else if(suffix[i] == AppContext.EXTENSION_TXT)
                FileGroup.OtherFiles.setTxtFles(mFileInfos);
        }

    }

    private static long getFileSize(File f) {

        try {
            if (f.exists()) {
                FileInputStream fis = null;

                fis = new FileInputStream(f);
                return fis.available();
            } else {
                Log.e("获取文件大小", "文件不存在!");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static List<PackageInfo> getAllApps(Context context) {

        List<PackageInfo> apps = new ArrayList<PackageInfo>();
        PackageManager pManager = context.getPackageManager();
        // 获取手机内所有应用
        List<PackageInfo> packlist = pManager.getInstalledPackages(0);
        for (int i = 0; i < packlist.size(); i++) {
            PackageInfo pak = (PackageInfo) packlist.get(i);

            // 判断是否为非系统预装的应用程序
            // 这里还可以添加系统自带的，这里就先不添加了，如果有需要可以自己添加
            // if()里的值如果<=0则为自己装的程序，否则为系统工程自带
            if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {
                // 添加自己已经安装的应用程序
                apps.add(pak);
            }

        }
        return apps;
    }
    /**
     * 得到本地文件路径
     * @param url
     * @return
     */
    public static  File getLocalFilePath(String url){
        String fileName = getFileNameByPath(url);

        String dirPath = "";

        if(fileName.lastIndexOf(AppContext.EXTENSION_APK) > 0){
            dirPath = getSpecifyDirPath(AppContext.EXTENSION_APK);
        }else if(fileName.lastIndexOf(AppContext.EXTENSION_JPG) > 0){
            dirPath = getSpecifyDirPath(AppContext.EXTENSION_JPG);
        }else if(fileName.lastIndexOf(AppContext.EXTENSION_JPEG) > 0){
            dirPath = getSpecifyDirPath(AppContext.EXTENSION_JPEG);
        }else if(fileName.lastIndexOf(AppContext.EXTENSION_MP3) > 0){
            dirPath = getSpecifyDirPath(AppContext.EXTENSION_MP3);
        }else if(fileName.lastIndexOf(AppContext.EXTENSION_MP4) > 0){
            dirPath = getSpecifyDirPath(AppContext.EXTENSION_MP4);
        }else if(fileName.lastIndexOf(AppContext.EXTENSION_DOC) > 0){
            dirPath = getSpecifyDirPath(AppContext.EXTENSION_DOC);
        }else if(fileName.lastIndexOf(AppContext.EXTENSION_PDF) > 0){
            dirPath = getSpecifyDirPath(AppContext.EXTENSION_PDF);
        }else {
            dirPath = getSpecifyDirPath("unknown");
        }

        File dirFile = new File(dirPath);
        if(!dirFile.exists()){
            dirFile.mkdirs();
        }
        File file = new File(dirFile, fileName);
        Log.i(TAG, "getLocalFilePath: " + file.getAbsolutePath());
        return file;
    }

    /**
     * 获取指定的文件夹路径
     * @param type @@See FileInfo.java
     * @return
     */
    public static String getSpecifyDirPath(String type){
        String dirPath = getDirPath();

        if(type.equals(AppContext.EXTENSION_APK)){
            dirPath = dirPath + "apk/";
        }else if(type.equals(AppContext.EXTENSION_JPG)){
            dirPath = dirPath + "jpg/";
        }else if(type.equals(AppContext.EXTENSION_JPEG)){
            dirPath = dirPath + "jpeg/";
        }else if(type.equals(AppContext.EXTENSION_MP3)){
            dirPath = dirPath + "mp3/";
        }else if(type.equals(AppContext.EXTENSION_MP4)){
            dirPath = dirPath + "mp4/";
        }else if(type.equals(AppContext.EXTENSION_DOC)){
            dirPath = dirPath + "doc/";
        }else if(type.equals(AppContext.EXTENSION_PDF)){
            dirPath = dirPath +  "pdf/";
        }else{
            dirPath = dirPath + "unknown/";
        }

        return dirPath;
    }

    /**
     * 根据路径得到文件名称
     * @param path
     * @return
     */
    private static String getFileNameByPath(String path) {
        //截取最后分隔符
        if(path == null || path.equals("")) return "";
           return path.substring(path.lastIndexOf("/") + 1);
    }

    /**
     * 获取文件的根目录
     * @return
     */
    public static String getDirPath(){
        String path = "/mnt/download/fileTransfer/";
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            path = Environment.getExternalStorageDirectory() + "/fileTransfer/";
        }
        return path;
    }


    /**
     * 获取apk包的信息：版本号，名称，图标等
     * @param absPath apk包的绝对路径
     * @param context
     */
    public static Drawable getApkIcon(String absPath,Context context) {

        PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo = pm.getPackageArchiveInfo(absPath,PackageManager.GET_ACTIVITIES);
        if (pkgInfo != null) {
            ApplicationInfo appInfo = pkgInfo.applicationInfo;
        /* 必须加这两句，不然下面icon获取是default icon而不是应用包的icon */
            appInfo.sourceDir = absPath;
            appInfo.publicSourceDir = absPath;
        /* icon1和icon2其实是一样的 */
            return pm.getApplicationIcon(appInfo);// 得到图标信息
        }
        return null;
    }
}
