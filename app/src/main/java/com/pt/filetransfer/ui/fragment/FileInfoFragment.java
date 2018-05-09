package com.pt.filetransfer.ui.fragment;


import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.pt.filetransfer.AppContext;
import com.pt.filetransfer.R;
import com.pt.filetransfer.adapter.FileInfoAdapter;
import com.pt.filetransfer.entity.FileInfo;
import com.pt.filetransfer.ui.FileChooseActivity;
import com.pt.filetransfer.ui.view.DividerItemDecoration;
import com.pt.filetransfer.utils.FileUtils;

import java.util.List;


/**
 * 文件信息Fragment
 */
public class FileInfoFragment extends Fragment {
    private static final String TAG = "FileInfoFragment";
    //文件信息列表
    private List<FileInfo> mFileInfos;
    //文件信息RecyclerView适配器
    private FileInfoAdapter mAdapter;

    //根据传进参数类型构造不同的Fragment
    private int type;
    //Fragment控件
    //1.文件信息网格布局
    private RecyclerView rv_fileinfo;
    //2.加载
    private ProgressBar pb;

    private FileChooseActivity fcActivity;



    public FileInfoFragment() {
    }

    public  void setType(int type) {
        this.type = type;
    }

    //根据类型得到Fragment实例
    public static FileInfoFragment newInstance(int type) {
        FileInfoFragment fragment = new FileInfoFragment();
        fragment.setType(type);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getActivity() != null)
          fcActivity = (FileChooseActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_apk, container, false);

        pb = (ProgressBar) rootView.findViewById(R.id.pb);
        rv_fileinfo = (RecyclerView) rootView.findViewById(R.id.rv_fileInfo);
        //根据文件类型设置不同布局
        if(type == FileInfo.TYPE_APK){  //应用
            rv_fileinfo.setLayoutManager(new LinearLayoutManager(getContext()));
        }else if(type == FileInfo.TYPE_JPG){        //图片
            rv_fileinfo.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        } else if(type == FileInfo.TYPE_AUDIO_VIDEO){  //影音
            rv_fileinfo.setLayoutManager(new LinearLayoutManager(getContext()));
        }else if(type == FileInfo.TYPE_DOCUMENT){  //文档
            rv_fileinfo.setLayoutManager(new LinearLayoutManager(getContext()));
        }else if(type == FileInfo.TYPE_OTHER){  //其他
            hideProgressBar();
            rv_fileinfo.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        Log.i(TAG, "onCreateView: after execute");
        //根据类型给网格布局添加数据
        if(type == FileInfo.TYPE_APK){  //应用
           new GetFileInfoTask(getContext(),type).executeOnExecutor(AppContext.MAIN_EXECUTOR);
        }else if(type == FileInfo.TYPE_JPG){  //图片
            new GetFileInfoTask(getContext(),type).executeOnExecutor(AppContext.MAIN_EXECUTOR);
        }else if(type == FileInfo.TYPE_AUDIO_VIDEO){  //音频
            new GetFileInfoTask(getContext(),type).executeOnExecutor(AppContext.MAIN_EXECUTOR);
        }else if(type == FileInfo.TYPE_DOCUMENT){  //视频
            new GetFileInfoTask(getContext(),type).executeOnExecutor(AppContext.MAIN_EXECUTOR);
        }

        //添加分割线
        rv_fileinfo.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL_LIST));
        //设置item变化的动画
        rv_fileinfo.setItemAnimator(new DefaultItemAnimator());
        Log.i(TAG, "onCreateView: before execute");
        return rootView;
    }

    /**
     * 隐藏加载
     */
    private void hideProgressBar() {
        if(pb != null && pb.isShown() )
            pb.setVisibility(View.GONE);
    }

    /**
     * 显示加载
     */
    private void showProgressBar() {
        if(pb != null)
            pb.setVisibility(View.VISIBLE);
    }

    class GetFileInfoTask extends AsyncTask<String,Integer,List<FileInfo>>{

        private Context context = null;
        private int type = FileInfo.TYPE_APK;
        private List<FileInfo> fileInfos = null;

        /**
         * 根据类型不同执行不同任务，即得到不同的文件信息
         * @param context
         * @param type
         */
        public GetFileInfoTask(Context context,int type) {
            this.context = context;
            this.type = type;
        }

        /**
         * 执行任务前，即显示加载条
         */
        @Override
        protected void onPreExecute() {
            showProgressBar();
            super.onPreExecute();
        }

        /**
         * 后台执行任务，加载FileInfo，返回文件信息列表
         * @param params
         * @return
         */
        @Override
        protected List<FileInfo> doInBackground(String... params) {
            //得到文件信息列表
            if(type == FileInfo.TYPE_APK){
                try {
                    fileInfos = FileUtils.getFileInfoByType(context,type);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

            }else if(type == FileInfo.TYPE_JPG){
                try {
                    fileInfos = FileUtils.getFileInfoByType(context,type);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }else if(type == FileInfo.TYPE_AUDIO_VIDEO){
                try {
                    fileInfos = FileUtils.getFileInfoByType(context,type);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }else if(type == FileInfo.TYPE_DOCUMENT) {
                try {
                    fileInfos = FileUtils.getFileInfoByType(context, type);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
                mFileInfos = fileInfos;
            Log.i(TAG, "doInBackground: " + fileInfos.toString());
            return fileInfos;
        }


        /**
         * 执行任务后，将传来的文件列表信息设置在控件GridView上
         * @param fileInfos
         */
        @Override
        protected void onPostExecute(List<FileInfo> fileInfos) {
            hideProgressBar();
            Log.i(TAG, "onPostExecute: " + fileInfos.toString());
            Log.i(TAG, "onPostExecute: " + type);
            if(type == FileInfo.TYPE_APK){
                mAdapter = new FileInfoAdapter(context,fileInfos, FileInfo.TYPE_APK);
                rv_fileinfo.setAdapter(mAdapter);
            }else if(type == FileInfo.TYPE_JPG){
                mAdapter = new FileInfoAdapter(context,fileInfos, FileInfo.TYPE_JPG);
                rv_fileinfo.setAdapter(mAdapter);
            }else if(type == FileInfo.TYPE_AUDIO_VIDEO) {
                mAdapter = new FileInfoAdapter(context, FileInfo.TYPE_AUDIO_VIDEO);
                rv_fileinfo.setAdapter(mAdapter);
            }else if(type == FileInfo.TYPE_DOCUMENT){
                mAdapter = new FileInfoAdapter(context, FileInfo.TYPE_DOCUMENT);
                rv_fileinfo.setAdapter(mAdapter);
            }else if(type == FileInfo.TYPE_OTHER){
            }

            mAdapter.setOnItemClickListener(new FileInfoAdapter.MyItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    fcActivity.updateUI(position);
                }
            });
        }
    }


}
