package com.pt.filetransfer.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pt.filetransfer.AppContext;
import com.pt.filetransfer.R;
import com.pt.filetransfer.entity.FileInfo;
import com.pt.filetransfer.ui.fragment.FileInfoFragment;
import com.pt.filetransfer.utils.NavigatorUtils;

/**
 * 文件选择界面
 */
public class FileChooseActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "FileChooseActivity";
    //当前的Fragment
    private FileInfoFragment mCurrentFragment;
    //
    private FileInfoFragment mApkInfoFragment;
    private FileInfoFragment mJpgInfoFragment;
    private FileInfoFragment mAVInfoFragment;
    private FileInfoFragment mDocInfoFragment;
    private FileInfoFragment mOthInfoFragment;
    //topbar
    private TextView tv_left;
    private ImageView iv_search;
    private TextView tv_title;

    //bottom ui
    private Button btn_preview;
    private Button btn_ok;

    //其他 ui
    private ViewPager view_pager;
    private TabLayout tab_layout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_choose);

        findViewById();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 初始化View
     */
    private void initView() {

        Log.i(TAG, "initView: ");
        
        tv_title.setText(R.string.str_mobile_file);
        iv_search.setVisibility(View.VISIBLE);
        mApkInfoFragment = FileInfoFragment.newInstance(FileInfo.TYPE_APK);
        mJpgInfoFragment = FileInfoFragment.newInstance(FileInfo.TYPE_JPG);
        mAVInfoFragment = FileInfoFragment.newInstance(FileInfo.TYPE_AUDIO_VIDEO);
        mDocInfoFragment = FileInfoFragment.newInstance(FileInfo.TYPE_DOCUMENT);
        mOthInfoFragment =  FileInfoFragment.newInstance(FileInfo.TYPE_OTHER);
        mCurrentFragment = mApkInfoFragment;

        //得到TAB标签
//        String[] titles = getResources().getStringArray(R.array.array_tags);
        String[] titles = new String[5];
        titles[0] = "应用";
        titles[1] = "图片";
        titles[2] = "影音";
        titles[3] = "文档";
        titles[4] = "其他";
        for (int i = 0; i < titles.length; i++) {
            Log.i(TAG, "initView: " + titles[i]);
        }

        view_pager.setAdapter(new ResPagerAdapter(getSupportFragmentManager(),titles));
        view_pager.setOffscreenPageLimit(5);
        tab_layout.setTabMode(TabLayout.MODE_FIXED);
        tab_layout.setupWithViewPager(view_pager);

    }



    private void findViewById() {
        tv_left = (TextView) findViewById(R.id.tv_left);
        iv_search = (ImageView) findViewById(R.id.iv_search);
        tv_title = (TextView) findViewById(R.id.tv_title);
        btn_preview = (Button) findViewById(R.id.btn_preview);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        view_pager = (ViewPager) findViewById(R.id.view_pager);
        tab_layout = (TabLayout) findViewById(R.id.tab_layout);
        tv_left.setText(getResources().getString(R.string.str_all_file));
        tv_left.setOnClickListener(this);
        btn_preview.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_left:
                NavigatorUtils.toFileManagerUI(this);
                break;
            case R.id.btn_preview:
                break;
            case  R.id.btn_ok:
                SendingFilesToReceiver();
                break;
        }
    }

    private void SendingFilesToReceiver() {
        NavigatorUtils.toSenderWaitingUI(this);
        Log.i(TAG, "SendingFilesToReceiver: " + AppContext.getAppContext().getFileInfoMap().toString());
    }

    public void updateUI(int position) {
        if(AppContext.getAppContext().isFileInfoMapExist()){
            btn_ok.setTextColor(ContextCompat.getColor(this, R.color.white) );
            btn_ok.setEnabled(true);
            tv_title.setText("已选" + AppContext.getAppContext().getFileMapSize() + "个");
        }else{
            btn_ok.setTextColor(ContextCompat.getColor(this, R.color.transparent_white) );
            btn_ok.setEnabled(false);
            tv_title.setText(R.string.str_mobile_file);
        }
    }



    /**
     * 资源的PagerAdapter
     */
    class ResPagerAdapter extends FragmentPagerAdapter {
        String[] sTitleArray;

        public ResPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public ResPagerAdapter(FragmentManager fm, String[] titles) {
            this(fm);
            this.sTitleArray = titles;
            for (int i = 0; i < titles.length; i++) {
                Log.i(TAG, "ResPagerAdapter: " + titles[i]);
            }
        }


        @Override
        public Fragment getItem(int position) {
            if(position == 0){ //应用
            }else if(position == 1){ //图片
                mCurrentFragment = mJpgInfoFragment;
            }else if(position == 2){ //影音
                mCurrentFragment = mAVInfoFragment;
            }else if(position == 3){ //文档
                mCurrentFragment = mDocInfoFragment;
            }else if(position == 4){
                mCurrentFragment = mOthInfoFragment;
            }
            return mCurrentFragment;
        }

        @Override
        public int getCount() {
            return sTitleArray.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return sTitleArray[position];
        }

    }
}
