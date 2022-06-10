package com.chinatsp.widgetcards.editor.drag;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * <p/>
 * Date: 16/6/2 15:41
 * Author: zhendong.wu@shoufuyou.com
 * <p/>
 */
public class CardDragDrawable extends Drawable {
    final private View mView;
    final private Bitmap mBitmap;
    private boolean showShadow;
    final private Paint mPaint;
    public CardDragDrawable(View view){
        mView  = view;
        mView.setDrawingCacheEnabled(true);
        mView.buildDrawingCache();
        mBitmap = mView.getDrawingCache();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setShadowLayer(5,8,8,0xFF808080);
    }
    @Override
    public void draw(Canvas canvas) {
        if(mBitmap == null) {
            mView.draw(canvas);
        }else {
            if(showShadow){
                canvas.drawBitmap(mBitmap, 0, 0, mPaint);
            }else {
                canvas.drawBitmap(mBitmap, 0, 0, null);
            }
        }
    }

    @Override
    public void setAlpha(int alpha) {
        //nothing
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {//nothing
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public int getIntrinsicWidth() {
        return mView.getWidth();
    }

    @Override
    public int getIntrinsicHeight() {
        return mView.getHeight();
    }

    public void showShadow(){
        showShadow = true;
        invalidateSelf();
    }

    public Paint getPaint(){
        return mPaint;
    }
}

