package com.pt.filetransfer.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.pt.filetransfer.R;
import com.pt.filetransfer.utils.StatusBarUtils;



public class BaseActivity extends AppCompatActivity {

    Context mContext;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        StatusBarUtils.setStatuBarAndBottomBarTranslucent(this);
        super.onCreate(savedInstanceState);

    }


    /**
     * 获取上下文
     * @return
     */
    public Context getContext(){
        return mContext;
    }

    /**
     * 显示对话框
     */
    protected void showProgressDialog(){
        if(mProgressDialog == null){
            mProgressDialog = new ProgressDialog(mContext);
        }
        mProgressDialog.setMessage(getResources().getString(R.string.tip_loading));
        mProgressDialog.show();
    }

    /**
     * 隐藏对话框
     */
    protected void hideProgressDialog(){
        if(mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.hide();
            mProgressDialog = null;
        }
    }
}
