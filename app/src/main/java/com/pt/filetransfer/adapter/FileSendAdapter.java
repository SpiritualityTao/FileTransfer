package com.pt.filetransfer.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.pt.filetransfer.R;
import com.pt.filetransfer.entity.FileGroup;
import com.pt.filetransfer.entity.FileInfo;
import com.pt.filetransfer.utils.ConvertUtils;

import java.util.List;

/**
 * Created by 韬 on 2017-06-05.
 */
public class FileSendAdapter extends BaseAdapter{
    private static final String TAG = "FileSendAdapter";

    private LayoutInflater mInflater;
    private Context mContext;

    public List<FileInfo> getmFileSendList() {
        return mFileSendList;
    }

    public void setmFileSendList(List<FileInfo> mFileSendList) {
        this.mFileSendList = mFileSendList;
    }

    private List<FileInfo> mFileSendList;
    public FileSendAdapter(Context context){
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
    }



    @Override
    public int getCount() {
        return mFileSendList.size();
    }

    @Override
    public Object getItem(int position) {
        return mFileSendList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final FileInfo fileInfo = mFileSendList.get(position);
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_sr_file, null); // 设置列表项的布局
            holder.iv_sr_icon = (ImageView) convertView.findViewById(R.id.iv_sr_icon);
            holder.tv_sr_name = (TextView) convertView.findViewById(R.id.tv_sr_name);
            holder.tv_sr_size = (TextView) convertView.findViewById(R.id.tv_sr_size);
            holder.tv_progress = (TextView) convertView.findViewById(R.id.tv_progress);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if(fileInfo.getFileType() == FileInfo.TYPE_APK)
            holder.iv_sr_icon.setImageDrawable(fileInfo.getDrawable());
        else if(fileInfo.getFileType() == FileInfo.TYPE_JPG){
            //设置图片显示
            Glide.with(mContext)
                    .load(fileInfo.getPath())
                    .override(400,400)
                    .centerCrop()
                    .placeholder(R.mipmap.icon_jpg)
                    .into(holder.iv_sr_icon);
        }else if(fileInfo.getFileType() == FileInfo.TYPE_AUDIO_VIDEO){
            if(FileGroup.AVFiles.musicFiles.contains(fileInfo)){
                holder.iv_sr_icon.setImageResource(R.mipmap.icon_file_music);
            }else if (FileGroup.AVFiles.mediaFiles.contains(fileInfo)){
                holder.iv_sr_icon.setImageResource(R.mipmap.icon_file_video);
            }
        }else if(fileInfo.getFileType() == FileInfo.TYPE_DOCUMENT) {
            if(FileGroup.DOCFiles.docFiles.contains(fileInfo)){
                holder.iv_sr_icon.setImageResource(R.mipmap.icon_file_doc);
            }else if (FileGroup.DOCFiles.pdfFiles.contains(fileInfo)){
                holder.iv_sr_icon.setImageResource(R.mipmap.icon_file_pdf);
            }
        }else if(fileInfo.getFileType() == FileInfo.TYPE_UNKNOWN){
            holder.iv_sr_icon.setImageResource(R.mipmap.other);
        }
        holder.tv_sr_name.setText(fileInfo.getName());
        holder.tv_sr_size.setText(ConvertUtils.convertFileSize(fileInfo.getSize()));
        if (fileInfo.isSuccess()) {
            holder.tv_progress.setText("查看");
            holder.tv_progress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext,"文件路径" + fileInfo.getPath(),Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Log.i(TAG, "getView: progress"+ fileInfo.getProcceed());
            Log.i(TAG, "getView: " + (float)(fileInfo.getProcceed() / fileInfo.getSize()) * 100 + "%");
            holder.tv_progress.setText( ConvertUtils.convertFileSize(fileInfo.getProcceed()) + "/" +ConvertUtils.convertFileSize(fileInfo.getSize()) );
        }
        return convertView;
    }

    public final class ViewHolder { // 发送文件item控件
        public ImageView iv_sr_icon; // icon
        public TextView tv_sr_name; // 文件名
        public TextView tv_sr_size; //大小
        public TextView tv_progress;
    }


}
