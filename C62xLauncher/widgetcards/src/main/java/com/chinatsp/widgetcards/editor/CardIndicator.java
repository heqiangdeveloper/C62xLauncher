package com.chinatsp.widgetcards.editor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import com.chinatsp.widgetcards.R;

public class CardIndicator extends View {
    private static final String TAG = "CardIndicator";
    private int mIndicatorMaxOffset;
    private int mIndicatorCurrentOffset;
    private int mSlideWidth = 32;
    private Bitmap mIndicator;
    private Paint mPaint;
    private Rect mBitmapRect, mDestRect;
    private int mBitmapWidth;
    private int mBitmapHeight;

    public CardIndicator(Context context) {
        super(context);
        init(context);
    }


    public CardIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CardIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CardIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setFilterBitmap(true);
        mPaint.setDither(true);
        post(new Runnable() {
            @Override
            public void run() {
                if (mIndicator == null) {
                    BitmapDrawable drawable =
                            (BitmapDrawable) AppCompatResources.getDrawable(context, R.drawable.card_indicator_slide);
                    if (drawable != null) {
                        mIndicator = drawable.getBitmap();
                        int measuredWidth = getMeasuredWidth();
                        mIndicatorMaxOffset = measuredWidth - mSlideWidth;
                        mIndicatorCurrentOffset = 0;
                        mBitmapWidth = mIndicator.getWidth();
                        mBitmapHeight = mIndicator.getHeight();
                        mBitmapRect = new Rect(0, 0, mBitmapWidth, mBitmapHeight);
                        mDestRect = new Rect(0, 0, mBitmapWidth, mBitmapHeight);
                    }
                }
            }
        });
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (mIndicator == null) {
            return;
        }
        mDestRect.left = mIndicatorCurrentOffset;
        mDestRect.right = mDestRect.left + mBitmapWidth;
        canvas.drawBitmap(mIndicator, mBitmapRect, mDestRect, mPaint);
    }

    public void setIndex(float ratio) {
        mIndicatorCurrentOffset = (int) (mIndicatorMaxOffset * ratio) + 1; // +1 向上修正1像素
        postInvalidate();
    }
}
