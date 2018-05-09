package com.pt.filetransfer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pt.filetransfer.R;
import com.pt.filetransfer.entity.FileInfo;
import com.pt.filetransfer.entity.HistoryInfo;
import com.pt.filetransfer.utils.ConvertUtils;

import java.io.File;
import java.util.List;

/**
 * Created by 韬 on 2017-06-07.
 */
public class HistoryAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<HistoryInfo> historyInfoList;
    private Context mContext;

    public HistoryAdapter(Context context, List<HistoryInfo> historyInfoList) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        this.historyInfoList = historyInfoList;
    }

    @Override
    public int getCount() {
        return historyInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return historyInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HistoryInfo history = historyInfoList.get(position);
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_history, null); // 设置列表项的布局
            holder.iv_his_icon = (ImageView) convertView.findViewById(R.id.iv_his_icon);
            holder.tv_his_name = (TextView) convertView.findViewById(R.id.tv_his_name);
            holder.tv_his_size = (TextView) convertView.findViewById(R.id.tv_his_size);
            holder.tv_his_time = (TextView) convertView.findViewById(R.id.tv_his_time);
            holder.tv_actionName = (TextView) convertView.findViewById(R.id.tv_actionName);
            holder.tv_actionType = (TextView) convertView.findViewById(R.id.tv_actionType);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_his_name.setText(history.getName());
        File file = new File(history.getPath());
        long size = file.length();
        holder.tv_his_size.setText(ConvertUtils.convertFileSize(size));
        holder.tv_his_time.setText(ConvertUtils.convertDateString(history.getTime()));
        holder.tv_actionName.setText("操作对象:" + history.getSendName());
        if(history.getActionType() == HistoryInfo.ACTION_RECEIVE)
             holder.tv_actionType.setText("操作：接收");
        else
            holder.tv_actionType.setText("操作：发送");
        if(history.getType() == FileInfo.TYPE_JPG){
            Glide.with(mContext)
                    .load(history.getPath())
                    .override(200,200)
                    .centerCrop()
                    .placeholder(R.mipmap.icon_jpg)
                    .into(holder.iv_his_icon);
        }else if(history.getType() == FileInfo.TYPE_AUDIO_VIDEO){
            if(history.getPath().lastIndexOf(".mp3") == history.getPath().length() - 4){
                holder.iv_his_icon.setImageResource(R.mipmap.icon_file_music);
            } else if(history.getPath().lastIndexOf(".mp4") == history.getPath().length() - 4){
                holder.iv_his_icon.setImageResource(R.mipmap.icon_file_video);
            }
        }else if(history.getType() == FileInfo.TYPE_DOCUMENT){
            if(history.getPath().lastIndexOf(".doc") == history.getPath().length() - 4){
                holder.iv_his_icon.setImageResource(R.mipmap.icon_file_doc);
            } else if(history.getPath().lastIndexOf(".pdf") == history.getPath().length() - 4){
                holder.iv_his_icon.setImageResource(R.mipmap.icon_file_pdf);
            }
        }

        return convertView;
    }

    class ViewHolder{
        public ImageView iv_his_icon; // icon
        public TextView tv_his_name; // 文件名
        public TextView tv_his_size; //大小
        private TextView tv_his_time;
        private TextView tv_actionName;
        private TextView tv_actionType;
    }
}
