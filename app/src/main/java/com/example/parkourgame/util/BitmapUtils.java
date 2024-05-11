package com.example.parkourgame.util;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapUtils {

    public static Bitmap cropBitmap(Bitmap srcBitmap, int left, int top, int width, int height) {
        // 创建一个新的Bitmap，用于存放裁剪后的图像
        Bitmap croppedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        // 获取裁剪区域的像素值
        int[] pixels = new int[width * height];
        srcBitmap.getPixels(pixels, 0, width, left, top, width, height);

        // 将获取到的像素值设置到新的Bitmap中
        croppedBitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        return croppedBitmap;
    }

}