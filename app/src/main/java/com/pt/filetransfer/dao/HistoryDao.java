package com.pt.filetransfer.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.pt.filetransfer.entity.FileInfo;
import com.pt.filetransfer.entity.HistoryInfo;
import com.pt.filetransfer.utils.ConvertUtils;
import com.pt.filetransfer.utils.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 韬 on 2017-05-22.
 */
public class HistoryDao {

    private static final String TAG = "HistoryDao";

    private static final String DB_NAME = "fileTransfer.db";
    private static final int DB_VERSION = 1;

    private Context context;
    private SQLiteDatabase db;
    private DatabaseHelper dbOpenHelper;

    public HistoryDao(Context context) {
        this.context = context;
    }

    /**
     * 打开数据库
     * @throws SQLiteException
     */
    public void open() throws SQLiteException {
        dbOpenHelper = new DatabaseHelper(context,DB_NAME , null, DB_VERSION);
        try {
            db = dbOpenHelper.getWritableDatabase();
        }catch (SQLiteException ex) {
            db = dbOpenHelper.getReadableDatabase();
        }
    }

    /**
     * 关闭数据库
     */
    public void close(){
        if(db != null){
            db.close();
            db = null;
        }
    }

    /**
     * 添加历史文件信息
     * @param fileInfo            文件信息
     * @param senderName           文件发送者
     * @param time                  文件发送时间
     */
    public  void addHistoryInfo(FileInfo fileInfo,String filePath,String senderName,long time,int actionType){
        Log.i(TAG, "addHistoryInfo: fileInfo " + fileInfo.toString() );
        Log.i(TAG, "addHistoryInfo: senderName " + senderName );
        Log.i(TAG, "addHistoryInfo: time " + ConvertUtils.convertDateString(time));
        Log.i(TAG, "addHistoryInfo: actionType " + actionType );
        ContentValues values = new ContentValues();
        values.put("fName",fileInfo.getName());
        if(actionType == HistoryInfo.ACTION_SNED)
            values.put("fPath",fileInfo.getPath());
        else if (actionType == HistoryInfo.ACTION_RECEIVE)
            values.put("fPath",filePath);
        values.put("fType",fileInfo.getFileType());
        values.put("actionType",actionType);
        values.put("senderName",senderName);
        values.put("time",time);
        db.insert(DatabaseHelper.HISTORY_INFO,null,values);
    }


    /**
     * 展示所有的历史记录
     * @return
     */
    public List<HistoryInfo> showAllHistoryInfo(){
        List<HistoryInfo> historyInfos = new ArrayList<HistoryInfo>();
        Cursor cursor = db.query(DatabaseHelper.HISTORY_INFO,new String[]
                        {"fName","fPath","fType","actionType","senderName","time"},
                null,null,null,null,null);
        int count = cursor.getCount();
        if(count == 0){
            Log.i(TAG, "showAllHistoryInfo: 历史记录为空");
            return null;
        }else {
            HistoryInfo hisInfo;
            cursor.moveToFirst();
            for (int i = 0; i < count; i++) {
                hisInfo = new HistoryInfo();
                hisInfo.setName(cursor.getString(0));
                hisInfo.setPath(cursor.getString(1));
                hisInfo.setType(cursor.getInt(2));
                hisInfo.setActionType(cursor.getInt(3));
                hisInfo.setSendName(cursor.getString(4));
                hisInfo.setTime(cursor.getLong(5));
                Log.i(TAG, "showAllHistoryInfo: historyInfos = " + hisInfo.toString());
                historyInfos.add(hisInfo);
                cursor.moveToNext();
            }
            return historyInfos;
        }
    }

    /**
     * 清空历史记录
     */
    public void clearAllHistory(){
        db.delete(DatabaseHelper.HISTORY_INFO,null,null);
    }
}
