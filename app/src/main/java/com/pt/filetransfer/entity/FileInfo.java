package com.pt.filetransfer.entity;

import android.graphics.drawable.Drawable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 韬 on 2017-05-11.
 */
public class FileInfo {

    //文件路径
    private String path;
    //文件类型
    private int fileType;
    //文件大小
    private long size;
    //文件安装时间
    private long InstallTime;
    //非必要属性
    //文件显示名称
    private String name;
    //文件缩略图(mp4与apk可能需要)
    private Drawable drawable;
    //文件额外信息
    private String extra;
    //已经处理的（读或者写）
    private long procceed = 0;
    //文件传送的结果
    private boolean isSuccess;
    //文件后缀名
    private String suffix;
    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }


    /**
     * 常见文件拓展名
     */
    public static final String APK = ".apk";
    public static final String JPEG = ".jpeg";
    public static final String JPG = ".jpg";
    public static final String PNG = ".png";
    public static final String MP3 = ".mp3";
    public static final String MP4 = ".mp4";
    public static final String DOC = ".doc";
    public static final String PDF = ".pdf";

    /**
     * 自定义文件类型
     */
    public static final int TYPE_APK = 1;
    public static final int TYPE_JPG = 2;

    public static final int TYPE_OTHER = 5;
    public static final int TYPE_AUDIO_VIDEO = 3;
    public static final int TYPE_DOCUMENT = 4;
    public static final int TYPE_UNKNOWN = 6;


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public long getProcceed() {
        return procceed;
    }

    public void setProcceed(long procceed) {
        this.procceed = procceed;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public long getInstallTime() {
        return InstallTime;
    }

    public void setInstallTime(long installTime) {
        InstallTime = installTime;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "path='" + path + '\'' +
                ", size=" + size +
                ", name='" + name + '\'' +
                ", drawable=" + drawable +
                '}' + "\n";
    }

    public static String toJsonStr(FileInfo fileInfo){
        String jsonStr = "";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("filePath", fileInfo.getPath());
            jsonObject.put("fileName", fileInfo.getName());
            jsonObject.put("fileType", fileInfo.getFileType());
            jsonObject.put("size", fileInfo.getSize());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static FileInfo toObject(String jsonStr){
        FileInfo fileInfo = new FileInfo();
        try {
            JSONObject jsonObject =  new JSONObject(jsonStr);
            String filePath = (String) jsonObject.get("filePath");
            String fileName = (String) jsonObject.get("fileName");
            long size = jsonObject.getLong("size");
            int type = jsonObject.getInt("fileType");

            fileInfo.setPath(filePath);
            fileInfo.setName(fileName);
            fileInfo.setSize(size);
            fileInfo.setFileType(type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return fileInfo;
    }

    public static int getFileType(String path) {

        if(path != null && !path.equals("")){

            if((path.lastIndexOf(APK) == path.length()-4)){
                return TYPE_APK;
            }else  if((path.lastIndexOf(JPG) == path.length()-4)){
                return TYPE_JPG;
            }else  if((path.lastIndexOf(JPEG) == path.length()-5)){
                return TYPE_JPG;
            }else  if((path.lastIndexOf(MP3) == path.length()-4)){
                return TYPE_AUDIO_VIDEO;
            }else  if((path.lastIndexOf(MP4) == path.length()-4)){
                return TYPE_AUDIO_VIDEO;
            }else  if((path.lastIndexOf(DOC) == path.length()-4)){
                return TYPE_DOCUMENT;
            }else  if((path.lastIndexOf(PDF) == path.length()-4)){
                return TYPE_DOCUMENT;
            }else{
                return TYPE_UNKNOWN;
            }

        }
        return  TYPE_UNKNOWN;
    }

    public static String getSuffix(FileInfo fileInfo) {
        String path = fileInfo.getPath();
        if(path != null && !path.equals("")){
            if((path.lastIndexOf(APK) == path.length()-4)){
                return APK;
            }else  if((path.lastIndexOf(JPG) == path.length()-4)){
                return JPG;
            }else  if((path.lastIndexOf(JPEG) == path.length()-5)){
                return JPEG;
            }else  if((path.lastIndexOf(MP3) == path.length()-4)){
                FileGroup.AVFiles.musicFiles.add(fileInfo);
                return MP3;
            }else  if((path.lastIndexOf(MP4) == path.length()-4)){
                FileGroup.AVFiles.mediaFiles.add(fileInfo);
                return MP4;
            }else  if((path.lastIndexOf(DOC) == path.length()-4)){
                FileGroup.DOCFiles.docFiles.add(fileInfo);
                return DOC;
            }else  if((path.lastIndexOf(PDF) == path.length()-4)){
                FileGroup.DOCFiles.pdfFiles.add(fileInfo);
                return PDF;
            }else{
                return "unknown";
            }

        }else {
            return "";
        }
    }
}
