package com.pt.filetransfer.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pt.filetransfer.R;
import com.pt.filetransfer.adapter.HistoryAdapter;
import com.pt.filetransfer.dao.HistoryDao;
import com.pt.filetransfer.entity.HistoryInfo;

import java.util.List;

public class HistoryFileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "HistoryFileActivity";

    private HistoryDao historyDao;
    //历史记录列表
    private List<HistoryInfo> historyInfoList;

    //topbar
    private TextView tv_left;
    private ImageView iv_search;
    private TextView tv_title;

    //其他 ui
    private ListView lv_history;

    private HistoryAdapter hAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_file);
        initView();
        historyDao = new HistoryDao(this);
        historyDao.open();
        historyInfoList = historyDao.showAllHistoryInfo();
        if(historyInfoList != null) {
            hAdapter = new HistoryAdapter(this,historyInfoList);
            lv_history.setAdapter(hAdapter);
        }else {
            Toast.makeText(this,"暂时没有历史记录",Toast.LENGTH_SHORT).show();
            this.finish();
        }


    }

    private void initView() {
        tv_left = (TextView) findViewById(R.id.tv_left);
        iv_search = (ImageView) findViewById(R.id.iv_search);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_left.setText(getResources().getString(R.string.str_back));
        tv_left.setOnClickListener(this);
        iv_search.setImageResource(R.mipmap.icon_delete);
        iv_search.setOnClickListener(this);
        lv_history = (ListView) findViewById(R.id.lv_history);

    }

    @Override
    protected void onDestroy() {
        if(historyDao != null)
            historyDao.close();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_left:
                this.finish();
                break;
            case R.id.iv_search:
                historyDao.clearAllHistory();
                Toast.makeText(this,"暂时没有历史记录",Toast.LENGTH_SHORT).show();
                this.finish();
                break;
        }
    }

}
