package com.pt.filetransfer.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pt.filetransfer.R;
import com.pt.filetransfer.utils.NavigatorUtils;


public class MainActivity extends Activity implements View.OnClickListener {

    private Button btn_send;
    private Button btn_receive;
    private TextView tv_recent_file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
    }

    @Override
    public void onBackPressed() {
        this.finish();
        super.onBackPressed();
    }

    private void initData() {
        btn_send = (Button) findViewById(R.id.btn_send);
        btn_receive = (Button) findViewById(R.id.btn_receive);
        tv_recent_file = (TextView) findViewById(R.id.tv_recent_file);
        btn_send.setOnClickListener(this);
        btn_receive.setOnClickListener(this);
        tv_recent_file.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                NavigatorUtils.toChooseFileUI(this);
                break;

            case R.id.btn_receive:
                NavigatorUtils.toReceiverWaitingUI(this);
                break;
            case R.id.tv_recent_file:
                NavigatorUtils.toHistoryFileUI(this);
                break;
        }
    }
}
