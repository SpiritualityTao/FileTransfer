package com.pt.filetransfer.entity;

/**
 * Created by 韬 on 2017-06-07.
 */
public class HistoryInfo {

    public static final int ACTION_SNED = 0;
    public static final int ACTION_RECEIVE = 1;


    private String name;
    private String path;
    private int type;
    //操作类型 发送or接收
    private int actionType;
    private String sendName;
    private long time;

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSendName() {
        return sendName;
    }

    public void setSendName(String sendName) {
        this.sendName = sendName;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "HistoryInfo{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", type=" + type +
                ", actionType=" + actionType +
                ", sendName='" + sendName + '\'' +
                ", time=" + time +
                '}' + "\n";
    }
}
