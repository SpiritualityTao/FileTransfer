package com.pt.filetransfer.utils;

import android.util.Log;

import com.pt.filetransfer.AppContext;
import com.pt.filetransfer.Constant;
import com.pt.filetransfer.entity.FileInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by 韬 on 2017-06-05.
 */
public class JsonUtils {
    private static final String TAG = "JsonUtils";

    /**
     * 生成文件列表Json数据
     * @throws JSONException
     */
    public static String getFileListJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg", Constant.MSG_SENDER_INIT_SUCCESS);
        JSONArray fileList = new JSONArray();


        Map<String ,FileInfo> fileSRMaps = AppContext.getAppContext().getFileInfoMap();
        Log.i(TAG, "FileSendAdapter: " + fileSRMaps.toString());
        for (Map.Entry<String, FileInfo> entry : fileSRMaps.entrySet()) {
            JSONObject fileInfo = new JSONObject();
            fileInfo.put("name",entry.getValue().getName());
            fileInfo.put("suffix",entry.getValue().getSuffix());
            fileInfo.put("size",entry.getValue().getSize());
            fileList.put(fileInfo);
        }
        jsonObject.put("fileList",fileList);
        Log.i(TAG, "fileListJson:jsonObject " + jsonObject);
        Log.i(TAG, "fileListJson:jsonObject.toString" + jsonObject.toString());
        return jsonObject.toString();

    }

    /**
     * 生成二位码Json数据
     * @return
     * @throws JSONException
     */
    public static String getQRCodeJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg", Constant.MSG_QRCODE_FILELIST);
        JSONArray fileList = new JSONArray();


        Map<String ,FileInfo> fileSRMaps = AppContext.getAppContext().getFileInfoMap();
        Log.i(TAG, "FileSendAdapter: " + fileSRMaps.toString());
        for (Map.Entry<String, FileInfo> entry : fileSRMaps.entrySet()) {
            JSONObject fileInfo = new JSONObject();
            fileInfo.put("name",entry.getValue().getName());
            fileInfo.put("suffix",entry.getValue().getSuffix());
            fileInfo.put("size",entry.getValue().getSize());
            fileList.put(fileInfo);
        }
        jsonObject.put("fileList",fileList);
        Log.i(TAG, "fileListJson:jsonObject " + jsonObject);
        Log.i(TAG, "fileListJson:jsonObject.toString" + jsonObject.toString());
        return jsonObject.toString();

    }

    public static List<FileInfo> parseFileList(String fileListJson) throws JSONException {

        List<FileInfo> fileInfos = new ArrayList<FileInfo>();
        JSONArray fileList = new JSONArray(fileListJson);
        for (int i = 0; i < fileList.length(); i++) {
            JSONObject fileJson = fileList.getJSONObject(i);
            Log.i(TAG, "parseFileList: " + fileJson);
            FileInfo fileInfo = new FileInfo();
            fileInfo.setSuffix(fileJson.getString("suffix"));
            fileInfo.setSize(fileJson.getLong("size"));
            fileInfo.setName(fileJson.getString("name"));
            fileInfos.add(fileInfo);
        }

        return fileInfos;
    }

    /**
     * 生成链接成功
     * @return
     * @throws JSONException
     */
    public static String getConnSuccessJson(String ssid) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg", Constant.MSG_CONNECTION_SUCCESS);
        jsonObject.put("ssid",ssid);
        return jsonObject.toString();

    }

    /**
     * 生成链接成功
     * @return
     * @throws JSONException
     */
    public static String getInitSuccess() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg", Constant.MSG_FILE_RECEIVER_INIT_SUCCESS);
        return jsonObject.toString();

    }

}
