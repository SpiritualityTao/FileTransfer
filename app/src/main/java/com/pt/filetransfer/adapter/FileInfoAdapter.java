package com.pt.filetransfer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pt.filetransfer.AppContext;
import com.pt.filetransfer.R;
import com.pt.filetransfer.entity.FileGroup;
import com.pt.filetransfer.entity.FileInfo;
import com.pt.filetransfer.utils.ConvertUtils;

import java.util.List;

/**
 * Created by 韬 on 2017-05-12.
 */
public class FileInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "FileInfoAdapter";

    //文件列表
    private  List<FileInfo> fileInfos;
    //文件类型
    private int type;
    //上下文
    private Context context;
    //布局填充器
    private LayoutInflater mInflater;
    //用于保存checkBox的选择状态
    private boolean[] appChecks;
    private boolean[] musicChecks;
    private boolean[] mediaChecks;
    private boolean[] docChecks;
    private boolean[] pdfChecks;

    private int musicNum;
    private int mediaNum;
    private int docNum;
    private int pdfNum;

    //item类型
    private static final int TYPE_TITLE = 0;
    private static final int TYPE_MUSIC = 1;
    private static final int TYPE_MEDIA= 2;
    private static final int TYPE_DOC = 3;
    private static final int TYPE_PDF = 4;
    private static final int TYPE_TXT = 5;
    //文件集合
    private List<FileInfo> mMusicFiles;
    private List<FileInfo> mMediaFiles;
    private List<FileInfo> mDocFiles;
    private List<FileInfo> mPdfFiles;

    public interface MyItemClickListener {
        public void onItemClick(View view,int postion);
    }


    public MyItemClickListener getmListener() {
        return mListener;
    }

    public void setOnItemClickListener(MyItemClickListener mListener) {
        this.mListener = mListener;
    }

    //item按钮监听器
    private MyItemClickListener mListener;
    public FileInfoAdapter(Context context,List<FileInfo> fileInfos, int type) {
        appChecks = new boolean[fileInfos.size()];
        this.context = context;
        this.fileInfos = fileInfos;
        this.type = type;
        mInflater = LayoutInflater.from(context);
        if(type == FileInfo.TYPE_AUDIO_VIDEO) {
            mMusicFiles = FileGroup.AVFiles.musicFiles;
            mMediaFiles = FileGroup.AVFiles.mediaFiles;
        }else if(type == FileInfo.TYPE_DOCUMENT) {
            mDocFiles = FileGroup.DOCFiles.docFiles;
            mPdfFiles = FileGroup.DOCFiles.pdfFiles;
        }
    }

    public FileInfoAdapter(Context context, int type) {
        this.context = context;
        this.type = type;
        mInflater = LayoutInflater.from(context);
        if(type == FileInfo.TYPE_AUDIO_VIDEO) {
            mMusicFiles = FileGroup.AVFiles.musicFiles;
            mMediaFiles = FileGroup.AVFiles.mediaFiles;
            musicChecks = new boolean[mMusicFiles.size()];
            mediaChecks = new boolean[mMediaFiles.size()];
        }else if(type == FileInfo.TYPE_DOCUMENT) {
            mDocFiles = FileGroup.DOCFiles.docFiles;
            mPdfFiles = FileGroup.DOCFiles.pdfFiles;
            docChecks = new boolean[mDocFiles.size()];
            pdfChecks = new boolean[mPdfFiles.size()];
        }

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View titleView = mInflater.inflate(R.layout.item_titles,parent,false);
        if(type == FileInfo.TYPE_APK) { //APK convertView
            View apkView = mInflater.inflate(R.layout.item_app,parent,false);
            return new ApkViewHolder(apkView);
        }else if(type == FileInfo.TYPE_JPG) { //JPG convertView
            View picView = mInflater.inflate(R.layout.item_pic,parent,false);
            return new JpgViewHolder(picView);
        }else if(type == FileInfo.TYPE_AUDIO_VIDEO) { //Av convertView
            if(viewType == TYPE_MUSIC ){
                View avView = mInflater.inflate(R.layout.item_info, parent, false);
                return new AudioViewHolder(avView);
            }else if(viewType == TYPE_MEDIA){
                View mdView = mInflater.inflate(R.layout.item_info, parent, false);
                return new MediaViewHolder(mdView);
            } else if(viewType == TYPE_TITLE){
                return new TitleViewHolder(titleView);
            }
        }else if(type == FileInfo.TYPE_DOCUMENT) { //doc convertView
            if(viewType == TYPE_DOC ){
                View avView = mInflater.inflate(R.layout.item_info, parent, false);
                return new DocViewHolder(avView);
            }else if(viewType == TYPE_PDF){
                View avView = mInflater.inflate(R.layout.item_info, parent, false);
                return new PdfViewHolder(avView);
            } else if(viewType == TYPE_TITLE){
                return new TitleViewHolder(titleView);
            }
        }if(type == FileInfo.TYPE_OTHER) { //other convertView
            if(viewType == TYPE_TXT ){
                View othView = mInflater.inflate(R.layout.item_app,parent,false);
                return new OtherViewHolder(othView);
            }else if(viewType == TYPE_TITLE){
                return new TitleViewHolder(titleView);
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof ApkViewHolder) { //APK convertView
            FileInfo fileInfo = fileInfos.get(position);
            ApkViewHolder apkViewHolder = (ApkViewHolder)holder;
            if(fileInfos!= null && fileInfos.get(position) != null){
                apkViewHolder.iv_icon.setImageDrawable(fileInfo.getDrawable());
                apkViewHolder.tv_name.setText(fileInfo.getName() == null ? "" : fileInfo.getName());
                apkViewHolder.tv_size.setText(ConvertUtils.convertFileSize(fileInfo.getSize()));
                apkViewHolder.tv_date.setText(ConvertUtils.convertDateString(fileInfo.getInstallTime()));
                apkViewHolder.cb_ok_tick.setChecked(appChecks[position]);
            }
        }else if(holder instanceof JpgViewHolder) { //JPG convertView
            FileInfo fileInfo = fileInfos.get(position);
            JpgViewHolder jpgViewHolder = (JpgViewHolder) holder;
            if(fileInfos!= null && fileInfos.get(position) != null) {
                //设置图片显示
                Glide.with(context)
                        .load(fileInfo.getPath())
                        .override(400,200)
                        .centerCrop()
                        .placeholder(R.mipmap.icon_jpg)
                        .into(jpgViewHolder.iv_icon);
                final int pos = position;
                jpgViewHolder.iv_ok_tick.bringToFront();

            }

        }else if(holder instanceof AudioViewHolder) { //audio convertView
            if (mMusicFiles!= null && musicNum != 0){
                ((AudioViewHolder) holder).tv_name.setText(mMusicFiles.get(position - 1).getName());
                ((AudioViewHolder) holder).tv_size.setText(ConvertUtils.convertFileSize(mMusicFiles.get(position - 1).getSize()));
                ((AudioViewHolder) holder).iv_icon.setImageResource(R.mipmap.icon_file_music);
                ((AudioViewHolder) holder).tv_date.setText(ConvertUtils.convertDateString(mMusicFiles.get(position - 1).getInstallTime()));
                ((AudioViewHolder) holder).cb_ok_tick.setChecked(musicChecks[position - 1]);
            }
        }else if(holder instanceof MediaViewHolder) { //medio convertView
            if (mMediaFiles!= null && mediaNum != 0){
                ((MediaViewHolder) holder).tv_name.setText(mMediaFiles.get(position - musicNum - 2).getName());
                ((MediaViewHolder) holder).tv_size.setText(ConvertUtils.convertFileSize(mMediaFiles.get(position - musicNum - 2).getSize()));
                ((MediaViewHolder) holder).iv_icon.setImageResource(R.mipmap.icon_file_video);
                ((MediaViewHolder) holder).tv_date.setText(ConvertUtils.convertDateString(mMediaFiles.get(position - musicNum - 2).getInstallTime()));
                ((MediaViewHolder) holder).cb_ok_tick.setChecked(mediaChecks[position - musicNum - 2]);
            }
        }else if(holder instanceof DocViewHolder) { //doc convertView
            if (mDocFiles!= null && docNum != 0){
                ((DocViewHolder) holder).tv_name.setText(mDocFiles.get(position - 1).getName());
                ((DocViewHolder) holder).tv_size.setText(ConvertUtils.convertFileSize(mDocFiles.get(position - 1).getSize()));
                ((DocViewHolder) holder).iv_icon.setImageResource(R.mipmap.icon_file_doc);
                ((DocViewHolder) holder).tv_date.setText(ConvertUtils.convertDateString(mDocFiles.get(position - 1).getInstallTime()));
                ((DocViewHolder) holder).cb_ok_tick.setChecked(docChecks[position - 1]);
            }
        } else if(holder instanceof PdfViewHolder) { //pdf convertView
            if (mPdfFiles!= null && pdfNum != 0){
                ((PdfViewHolder) holder).tv_name.setText(mPdfFiles.get(position - docNum - 2).getName());
                ((PdfViewHolder) holder).tv_size.setText(ConvertUtils.convertFileSize(mPdfFiles.get(position - docNum - 2).getSize()));
                ((PdfViewHolder) holder).iv_icon.setImageResource(R.mipmap.icon_file_pdf);
                ((PdfViewHolder) holder).tv_date.setText(ConvertUtils.convertDateString(mPdfFiles.get(position - docNum - 2).getInstallTime()));
                ((PdfViewHolder) holder).cb_ok_tick.setChecked(pdfChecks[position - docNum - 2]);
            }
        }else if(holder instanceof OtherViewHolder) { //other convertView
            
        }else if (holder instanceof TitleViewHolder){
            if(type == FileInfo.TYPE_AUDIO_VIDEO){
                if(position == 0){
                    ((TitleViewHolder) holder).tv_item_title.setText(R.string.str_mp3_desc);
                }else if(position == musicNum + 1){
                    ((TitleViewHolder) holder).tv_item_title.setText(R.string.str_mp4_desc);
                }
            }else  if(type == FileInfo.TYPE_DOCUMENT){
                if(position == 0){
                    ((TitleViewHolder) holder).tv_item_title.setText(R.string.str_word_desc);
                }else if(position == docNum + 1){
                    ((TitleViewHolder) holder).tv_item_title.setText(R.string.str_pdf_desc);
                }
            }
        }
        setUpItemEvent(holder);
    }

    private void setUpItemEvent(RecyclerView.ViewHolder holder) {
        if(mListener != null) {

            final int layoutPosition = holder.getLayoutPosition();
            if (holder instanceof ApkViewHolder){           //apk
                final  ApkViewHolder aHolder = (ApkViewHolder) holder;
                aHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(appChecks[layoutPosition]) {
                            appChecks[layoutPosition] = false;
                            AppContext.getAppContext().deleteFileInfo(fileInfos.get(layoutPosition));
                        }else{
                            //当apk选中时添加FileInfo到全局变量LIst<Map<String,fileInfo>>
                            appChecks[layoutPosition] = true;
                            if(AppContext.getAppContext() == null)
                                Log.i(TAG, "onClick: " + "AppContext == null");
                            else
                              AppContext.getAppContext().addFileInfo(fileInfos.get(layoutPosition));
                        }
                        aHolder.cb_ok_tick.setChecked(appChecks[layoutPosition]);
                        mListener.onItemClick(aHolder.rl_app, layoutPosition);
                    }
                });

            }else if(holder instanceof JpgViewHolder) { //JPG
                final JpgViewHolder jHolder = (JpgViewHolder) holder;
                jHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (jHolder.iv_ok_tick.getVisibility() == View.VISIBLE) {
                            jHolder.iv_ok_tick.setVisibility(View.GONE);
                            AppContext.getAppContext().deleteFileInfo(fileInfos.get(layoutPosition));
                        } else if (jHolder.iv_ok_tick.getVisibility() == View.GONE) {
                            jHolder.iv_ok_tick.setVisibility(View.VISIBLE);
                            AppContext.getAppContext().addFileInfo(fileInfos.get(layoutPosition));
                        }
                        mListener.onItemClick(jHolder.fl_pic, layoutPosition);

                    }
                });
            }else if(holder instanceof AudioViewHolder) { //audio event
                final AudioViewHolder aHolder = (AudioViewHolder) holder;
                aHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(musicChecks[layoutPosition - 1]) {
                            musicChecks[layoutPosition - 1] = false;
                            AppContext.getAppContext().deleteFileInfo(mMusicFiles.get(layoutPosition - 1));
                        }else{
                            //当apk选中时添加FileInfo到全局变量LIst<Map<String,fileInfo>>
                            musicChecks[layoutPosition - 1] = true;
                            if(AppContext.getAppContext() == null)
                                Log.i(TAG, "onClick: " + "AppContext == null");
                            else
                                AppContext.getAppContext().addFileInfo(mMusicFiles.get(layoutPosition - 1));
                        }
                        aHolder.cb_ok_tick.setChecked(musicChecks[layoutPosition - 1]);
                        mListener.onItemClick(aHolder.itemView, layoutPosition - 1);
                        Log.i(TAG, "onClick: " + mMusicFiles.get(layoutPosition - 1).toString());
                    }
                });
            }else if(holder instanceof MediaViewHolder) { //media event
                final MediaViewHolder mHolder = (MediaViewHolder) holder;
                mHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mediaChecks[layoutPosition - musicNum - 2]) {
                            mediaChecks[layoutPosition - musicNum - 2] = false;
                            AppContext.getAppContext().deleteFileInfo(mMediaFiles.get(layoutPosition - musicNum - 2));
                        }else{
                            //当apk选中时添加FileInfo到全局变量LIst<Map<String,fileInfo>>
                            mediaChecks[layoutPosition - musicNum - 2] = true;
                            if(AppContext.getAppContext() == null)
                                Log.i(TAG, "onClick: " + "AppContext == null");
                            else
                                AppContext.getAppContext().addFileInfo(mMediaFiles.get(layoutPosition - musicNum - 2));
                        }
                        mHolder.cb_ok_tick.setChecked(mediaChecks[layoutPosition - musicNum - 2]);
                        mListener.onItemClick(mHolder.itemView, layoutPosition - musicNum - 2);
                        Log.i(TAG, "onClick: " + mMediaFiles.get(layoutPosition - musicNum - 2));
                    }
                });
            }else if(holder instanceof DocViewHolder) { //doc event
                final DocViewHolder dHolder = (DocViewHolder) holder;
                dHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(docChecks[layoutPosition - 1]) {
                            docChecks[layoutPosition - 1] = false;
                            AppContext.getAppContext().deleteFileInfo(mDocFiles.get(layoutPosition - 1));
                        }else{
                            //当apk选中时添加FileInfo到全局变量LIst<Map<String,fileInfo>>
                            docChecks[layoutPosition - 1] = true;
                            if(AppContext.getAppContext() == null)
                                Log.i(TAG, "onClick: " + "AppContext == null");
                            else
                                AppContext.getAppContext().addFileInfo(mDocFiles.get(layoutPosition - 1));
                        }
                        dHolder.cb_ok_tick.setChecked(docChecks[layoutPosition - 1]);
                        mListener.onItemClick(dHolder.itemView, layoutPosition - 1);
                        Log.i(TAG, "onClick: " + mDocFiles.get(layoutPosition - 1));
                    }
                });
            }else if(holder instanceof PdfViewHolder) { //pdf event
                final PdfViewHolder pHolder = (PdfViewHolder) holder;
                pHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(pdfChecks[layoutPosition - docNum -2]) {
                            pdfChecks[layoutPosition - docNum -2] = false;
                            AppContext.getAppContext().deleteFileInfo(mPdfFiles.get(layoutPosition - docNum - 2));
                        }else{
                            //当apk选中时添加FileInfo到全局变量LIst<Map<String,fileInfo>>
                            pdfChecks[layoutPosition - docNum -2] = true;
                            if(AppContext.getAppContext() == null)
                                Log.i(TAG, "onClick: " + "AppContext == null");
                            else
                                AppContext.getAppContext().addFileInfo(mPdfFiles.get(layoutPosition - docNum -2));
                        }
                        pHolder.cb_ok_tick.setChecked(pdfChecks[layoutPosition - docNum -2]);
                        mListener.onItemClick(pHolder.itemView, layoutPosition - docNum -2);
                        Log.i(TAG, "onClick: " + mPdfFiles.get(layoutPosition - docNum -2));
                    }
                });
            }else if(holder instanceof OtherViewHolder) { //other event

            }else if(holder instanceof TitleViewHolder) { //pdf event
                final TitleViewHolder tHolder = (TitleViewHolder) holder;

            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(type == FileInfo.TYPE_AUDIO_VIDEO) {
            musicNum = FileGroup.AVFiles.musicFiles.size();
            mediaNum =  FileGroup.AVFiles.mediaFiles.size();
            if (position == 0 || position == musicNum + 1) {  //第一行或者第一行+
                return TYPE_TITLE;
            } else if(position > 0 && position <= musicNum) {    //第一行标题到第二标题之间
                return TYPE_MUSIC;
            }else if(position > musicNum && position < getItemCount()){
                return TYPE_MEDIA;
            }
        }else if(type == FileInfo.TYPE_DOCUMENT){
            docNum =  FileGroup.DOCFiles.docFiles.size();
            pdfNum =  FileGroup.DOCFiles.pdfFiles.size();
            if (position == 0 || position == docNum + 1) {  //第一行或者第一行+
                return TYPE_TITLE;
            } else if(position > 0 && position <= docNum) {    //第一行标题到第二标题之间
                return TYPE_DOC;
            }else if(position > docNum &&  position < getItemCount() ){
                return TYPE_PDF;
            }
        }else if(type == FileInfo.TYPE_OTHER){
            if(position == 0)
                return TYPE_TITLE;
            else
                return TYPE_TXT;
        }
        return  0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (type == FileInfo.TYPE_AUDIO_VIDEO)
             return  FileGroup.AVFiles.musicFiles.size()+ FileGroup.AVFiles.mediaFiles.size() + 2;
        else if (type == FileInfo.TYPE_DOCUMENT)
            return  FileGroup.DOCFiles.docFiles.size()+ FileGroup.DOCFiles.pdfFiles.size() + 2;
        else if (type == FileInfo.TYPE_OTHER)
            return fileInfos.size() + 1;
        return fileInfos.size();
    }



}

class TitleViewHolder extends RecyclerView.ViewHolder{
    ImageView iv_state;
    TextView tv_item_title;
    RelativeLayout rl_title;
    public TitleViewHolder(View view){
        super(view);
        iv_state = (ImageView) view.findViewById(R.id.iv_state);
        tv_item_title = (TextView) view.findViewById(R.id.tv_item_title);
        rl_title = (RelativeLayout) view.findViewById(R.id.rl_title);
    }
}

class ApkViewHolder extends RecyclerView.ViewHolder{
    RelativeLayout rl_app;
    ImageView iv_icon;
    CheckBox cb_ok_tick;
    TextView tv_name;
    TextView tv_size;
    TextView tv_date;
    public ApkViewHolder(View view){
        super(view);
        rl_app = (RelativeLayout) view.findViewById(R.id.rl_app);
        iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
        cb_ok_tick = (CheckBox) view.findViewById(R.id.cb_ok_tick);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_size = (TextView) view.findViewById(R.id.tv_size);
        tv_date = (TextView) view.findViewById(R.id.tv_date);
    }
}



class JpgViewHolder extends RecyclerView.ViewHolder{
    ImageView iv_icon;
    ImageView iv_ok_tick;
    FrameLayout fl_pic;
    public JpgViewHolder(View view){
        super(view);
        iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
        iv_ok_tick = (ImageView) view.findViewById(R.id.iv_ok_tick);
        fl_pic = (FrameLayout) view.findViewById(R.id.fl_pic);
    }
}

class AudioViewHolder extends RecyclerView.ViewHolder{
    RelativeLayout rl_audio;
    CheckBox cb_ok_tick;
    ImageView iv_icon;
    TextView tv_date;
    TextView tv_name;
    TextView tv_size;
    public AudioViewHolder(View view){
        super(view);
        rl_audio = (RelativeLayout) view.findViewById(R.id.rl_info);
        iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
        cb_ok_tick = (CheckBox) view.findViewById(R.id.cb_ok_tick);
        tv_date = (TextView) view.findViewById(R.id.tv_date);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_size = (TextView) view.findViewById(R.id.tv_size);
    }
}

class MediaViewHolder extends RecyclerView.ViewHolder{
    RelativeLayout rl_media;
    CheckBox cb_ok_tick;
    ImageView iv_icon;
    TextView tv_date;
    TextView tv_name;
    TextView tv_size;
    public MediaViewHolder(View view){
        super(view);
        rl_media = (RelativeLayout) view.findViewById(R.id.rl_info);
        iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
        cb_ok_tick = (CheckBox) view.findViewById(R.id.cb_ok_tick);
        tv_date = (TextView) view.findViewById(R.id.tv_date);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_size = (TextView) view.findViewById(R.id.tv_size);
    }
}

class DocViewHolder extends RecyclerView.ViewHolder{
    RelativeLayout rl_doc;
    ImageView iv_icon;
    CheckBox cb_ok_tick;
    TextView tv_date;
    TextView tv_name;
    TextView tv_size;
    public DocViewHolder(View view){
        super(view);
        rl_doc = (RelativeLayout) view.findViewById(R.id.rl_info);
        iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
        cb_ok_tick = (CheckBox) view.findViewById(R.id.cb_ok_tick);
        tv_date = (TextView) view.findViewById(R.id.tv_date);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_size = (TextView) view.findViewById(R.id.tv_size);
    }
}

class PdfViewHolder extends RecyclerView.ViewHolder{
    RelativeLayout rl_pdf;
    ImageView iv_icon;
    CheckBox cb_ok_tick;
    TextView tv_date;
    TextView tv_name;
    TextView tv_size;
    public PdfViewHolder(View view){
        super(view);
        rl_pdf = (RelativeLayout) view.findViewById(R.id.rl_info);
        iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
        cb_ok_tick = (CheckBox) view.findViewById(R.id.cb_ok_tick);
        tv_date = (TextView) view.findViewById(R.id.tv_date);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_size = (TextView) view.findViewById(R.id.tv_size);
    }
}

class OtherViewHolder extends RecyclerView.ViewHolder{
    RelativeLayout rl_other;
    ImageView iv_icon;
    CheckBox cb_ok_tick;
    TextView tv_name;
    TextView tv_size;
    public OtherViewHolder(View view){
        super(view);
        rl_other = (RelativeLayout) view.findViewById(R.id.rl_info);
        iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
        cb_ok_tick = (CheckBox) view.findViewById(R.id.cb_ok_tick);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_size = (TextView) view.findViewById(R.id.tv_size);
    }
}
