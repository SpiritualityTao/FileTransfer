package com.pt.filetransfer.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 韬 on 2017-05-13.
 */
public class ConvertUtils {

    public static String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else
            return String.format("%d B", size);
    }


    public static String convertDateString(long mill){
        //mill为你龙类型的时间戳
        Date date=new Date(mill);
        String strs="";
        try {
            //yyyy表示年MM表示月dd表示日
         //yyyy-MM-dd是日期的格式，比如2015-12-12如果你要得到2015年12月12日就换成yyyy年MM月dd日
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd mm:ss");
            //进行格式化
            strs=sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strs;
    }

}
