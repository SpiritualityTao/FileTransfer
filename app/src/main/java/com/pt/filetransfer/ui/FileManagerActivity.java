package com.pt.filetransfer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pt.filetransfer.AppContext;
import com.pt.filetransfer.R;
import com.pt.filetransfer.adapter.FileAdapter;
import com.pt.filetransfer.entity.FileInfo;
import com.pt.filetransfer.utils.NavigatorUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileManagerActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "FileManagerActivity";

    private TextView tv_title;
    private ListView list;
    private TextView tv_path;
    private Button btn_ok;
    //
    private List<Map<String, Object>> mData;
    // 得到文件系统的根目录
    private String mDir = Environment.getRootDirectory().getParent();
    private final static int UPLOAD_SUCCESS = 1;

    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPLOAD_SUCCESS:
                    String path = msg.getData().getString("path");
                    Toast.makeText(getApplicationContext(), path + "路径文件上传完毕", Toast.LENGTH_LONG).show();
                    break;

                default:
                    break;
            }
        };
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_file_manager);
        Intent intent = getIntent();
        initView();

        mData = getData();

        FileAdapter fAdapter = new FileAdapter(this,mData);
        Log.i("BaiduMap", "count=" + fAdapter.getCount());
        list.setAdapter(fAdapter);

        // 点击item
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // 如果是文件夹则打开
                if ((Integer) (mData.get(position).get("icon")) == R.mipmap.icon_folder) {
                    mDir = (String) mData.get(position).get("info");
                    mData = getData(); // 点击目录时进入子目录
                    FileAdapter fAdapter = new FileAdapter(getApplicationContext(),mData);
                    list.setAdapter(fAdapter);
                    tv_path.setText("目前路径：" + mDir);
                } else {
                    CheckBox check = ((FileAdapter.ViewHolder) view.getTag()).checkbox;
                    FileInfo fileInfo = new FileInfo();
                    setFileInfo(fileInfo,position);
                    Log.i(TAG, "onItemClick: "  + fileInfo.toString());
                    if (check.isChecked()) { // 判断CheckBox是否选择
                        AppContext.getAppContext().deleteFileInfo(fileInfo);
                        // ，如果是则取消并修改mData中上传为false
                        check.setChecked(false);
                        mData.get(position).remove("isUpload");
                        mData.get(position).put("checkTag", false);
                        // 记录文件是否被点击 标记为上传
                        mData.get(position).put("isUpload", "NO");
                    } else {
                        AppContext.getAppContext().addFileInfo(fileInfo);
                        check.setChecked(true);
                        mData.get(position).remove("isUpload");
                        mData.get(position).put("checkTag", true);
                        mData.get(position).put("isUpload", "YES");
                    }
                    Toast.makeText(FileManagerActivity.this,(String)mData.get(position).get("info"),Toast.LENGTH_SHORT).show();
                    if(AppContext.getAppContext().isFileInfoMapExist()){
                        btn_ok.setTextColor(ContextCompat.getColor(FileManagerActivity.this, R.color.white) );
                        btn_ok.setEnabled(true);
                        tv_title.setText("已选" + AppContext.getAppContext().getFileMapSize() + "个");
                    }else{
                        btn_ok.setTextColor(ContextCompat.getColor(FileManagerActivity.this, R.color.transparent_white) );
                        btn_ok.setEnabled(false);
                        tv_title.setText(R.string.str_mobile_file);
                    }
                }

            }
        });
    }

    private void setFileInfo(FileInfo fileInfo,int position) {
        String path = (String) mData.get(position).get("info");
        String name = (String) mData.get(position).get("title");
        File file = new File(path);
        long size = file.length();
        fileInfo.setInstallTime(file.lastModified());
        fileInfo.setPath(path);
        fileInfo.setFileType(FileInfo.getFileType(path));
        fileInfo.setSuffix(FileInfo.getSuffix(fileInfo));
        fileInfo.setSize(size);
        fileInfo.setName(name);
    }

    private void initView() {
        btn_ok = (Button) findViewById(R.id.btn_ok);
        list = (ListView) findViewById(R.id.listview);
        tv_path = (TextView) findViewById(R.id.tv_path);
        tv_title = (TextView) findViewById(R.id.tv_title);

        tv_path.setText("目前路径：" + mDir);
        btn_ok.setOnClickListener(this);
    }



    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = null;
        File f = new File(mDir); // 打开当前目录
        File[] files = f.listFiles(); // 获取当前目录中文件列表

        if (!mDir.equals("/")) { // 不充许进入/sdcard上层目录

            map = new HashMap<String, Object>(); // 加返回上层目录项
            map.put("title", "Back to .." + f.getParent());
            map.put("icon", R.mipmap.icon_folder);
            map.put("checkTag", false);
            // 信息是存储点击item之后跳转的目录
            map.put("info", f.getParent());
            list.add(map);
        }
        if (files != null) { // 将目录中文件填加到列表中
            Log.i("BaiduMap", "files=" + files.length);
            for (int i = 0; i < files.length; i++) {
                map = new HashMap<String, Object>();
                // 文件或文件夹的名字
                map.put("title", files[i].getName());
                // 信息是存储点击item之后跳转的路径目录
                map.put("info", files[i].getPath());
                if (files[i].isDirectory()) { // 按不同类型显示不同图标,如果是目录则不显示checkbox
                    map.put("icon", R.mipmap.icon_folder);
                    map.put("filetype", "Directoy");
                } else {
                    map.put("icon", R.mipmap.other);
                    map.put("filetype", "notDirectoy");
                }
                map.put("checkTag", false);
                list.add(map);
            }
        }
        Log.i("BaiduMap", "list.size()" + list.size());
        return list;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_ok:
                NavigatorUtils.toSenderWaitingUI(this);
                break;
        }
    }
}
