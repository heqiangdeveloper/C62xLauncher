package com.chinatsp.vehiclesetting.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class ImageUtils {
    public static Bitmap getScaledBitmap(String path, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        int bitmapW = bitmap.getWidth();
        int bitmapH = bitmap.getHeight();
        float scaleW = reqWidth / (float)bitmapW;
        float scaleH = reqHeight / (float)bitmapH;
//        LogUtils.d(reqWidth+" "+reqHeight);
//        LogUtils.d(bitmapW+" "+bitmapH);
//        LogUtils.d(scaleW+" "+scaleH);
        Matrix matrix = new Matrix();
        matrix.postScale(scaleW, scaleH);// 产生缩放后的Bitmapre对象
        Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapW, bitmapH, matrix, true);
        bitmap.recycle();
        return resizeBitmap;
    }

    public static Bitmap getScaledBitmap(Bitmap bitmap, int reqWidth, int reqHeight) {
        int bitmapW = bitmap.getWidth();
        int bitmapH = bitmap.getHeight();
        float scaleW = reqWidth / (float)bitmapW;
        float scaleH = reqHeight / (float)bitmapH;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleW, scaleH);// 产生缩放后的Bitmapre对象
        Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapW, bitmapH, matrix, true);
        bitmap.recycle();
        return resizeBitmap;
    }

    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height;
            final int halfWidth = width;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
