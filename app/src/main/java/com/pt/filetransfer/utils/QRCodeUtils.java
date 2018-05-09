package com.pt.filetransfer.utils;

import android.graphics.Bitmap;

import com.xys.libzxing.zxing.encoding.EncodingUtils;

import org.json.JSONException;

/**
 * Created by 韬 on 2017-06-06.
 */
public class QRCodeUtils {

    public static Bitmap makeQRCodeBitmap() {
        String qrCodeData = null;
        try {
            qrCodeData = JsonUtils.getQRCodeJson();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = EncodingUtils.createQRCode(qrCodeData,500,500,null);
        return bitmap;
    }
}
