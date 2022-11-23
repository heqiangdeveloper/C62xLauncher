package com.common.xui.widget.picker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

import com.common.xui.R;
import com.common.xui.utils.ThreadUtil;

@SuppressLint("AppCompatCustomView")
public class VerSeekBar extends SeekBar {
    private OnSeekBarChangeListener onSeekBarChangeListener;
    private int mWidth, mHeight;
    private Paint mShadowPaint, mBorderPaint;
    private float mBorderWidth = 2.6f;       // 描边宽度
    private float mShadowRadius = 10f;
    private float radius = 70;

    public VerSeekBar(Context context) {
        super(context);
        initPaint();
    }

    private void initPaint() {
        mBorderWidth = getResources().getDimension(R.dimen.border_width);
        mShadowRadius = getResources().getDimension(R.dimen.shade_width);
        radius = getResources().getDimension(R.dimen.seekbar_bg_radius_small);
        // 初始化光晕效果画笔
        mShadowPaint = new Paint();
        mShadowPaint.setStyle(Paint.Style.FILL);
        mShadowPaint.setColor(getContext().getColor(R.color.seekbar_start_color));
        mShadowPaint.setAlpha(200);
        mShadowPaint.setMaskFilter(new BlurMaskFilter(mShadowRadius - 1, BlurMaskFilter.Blur.OUTER));
        // 抖动处理
        mShadowPaint.setDither(true);
        //描边
        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStrokeWidth(mBorderWidth);
        mBorderPaint.setAlpha(170);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setColor(getContext().getColor(R.color.seekbar_start_color));
    }

    public VerSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public VerSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initPaint();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }

    @Override
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        super.setOnSeekBarChangeListener(l);
        this.onSeekBarChangeListener = l;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.rotate(-90);
        canvas.translate(-getHeight(), 0);
        super.onDraw(canvas);

        if (isPressed())
            drawShade(canvas);
    }

    private int error = 5;

    private void drawShade(Canvas canvas) {
        int offset = 17;
        int progress = getProgress();
        int max = getMax();

        max =  max < 1 ? 1 : max;// max 不能为0
        progress = progress < 0 ? 0 : progress;// progress 不能小于0
        // 当最大进度小于10，刻度变化太大，放大10倍
        if (max < 10) {
            max = max * 10;
            progress = progress * 10;
        }
        // a 误差偏移量，两端的偏移量最大，中间偏移量为0。取值范围 -offset ~ +offset
        int max2 = max / 2;
        int a = (max2 - progress) * offset / max2;
        float topProgress = mHeight * progress / max + a;

        float top = mHeight - mShadowRadius - mBorderWidth;
        float left = mShadowRadius;
        float right = mWidth - mShadowRadius;
        float bottom = mShadowRadius + mBorderWidth;

//        Logcat.e("mWidth: " + mWidth + "  mHeight: " + mHeight + "  topProgress: " + topProgress + "  offset: " + offset);

        Path path = new Path();
        if (topProgress <= offset) {
            return;
        } else {
            path.moveTo(bottom + radius, left);
            path.quadTo(bottom + error + 4, left + error, bottom + 1, left + radius);
            path.lineTo(bottom - 1, right - radius);
            path.quadTo(bottom + error + 5, right - error, bottom + radius - 2, right - 1);

            path.lineTo(top - radius, right);
            path.quadTo(top - error - 3, right - error-2, top-1, right - radius - 2);
            path.lineTo(top - 10, left + radius);
            path.quadTo(top - error - 6, left + error, top - radius, left);
            path.close();

            Path oPath = new Path();
            oPath.moveTo(bottom, left);
            oPath.lineTo(bottom, right);
            oPath.lineTo(topProgress, right);
            oPath.lineTo(topProgress, left);
            oPath.close();

            path.op(oPath, Path.Op.INTERSECT);

//        } else if (top < mHeight - mShadowRadius - radius) {
//            // 顶部未进入半圆
//            path.moveTo(bottom + radius, left);
//            path.quadTo(bottom + mBorderWidth, left + mBorderWidth, bottom, left + radius);
//            path.lineTo(bottom, right - radius);
//            path.quadTo(bottom + mBorderWidth, right - mBorderWidth, bottom + radius, right);
//            path.lineTo(top, right);
//            path.lineTo(top, left);
//            path.close();
        }

        canvas.drawPath(path, mShadowPaint);
        canvas.drawPath(path, mBorderPaint);

    }

    private boolean isTouched;

    @Override
    public boolean isPressed() {
        return isTouched;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isTouched = true;
                invalidate();
                if (onSeekBarChangeListener != null)
                    onSeekBarChangeListener.onStartTrackingTouch(this);
                setProgress(getMax() - (int) (getMax() * event.getY() / getHeight()));
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isTouched = false;
                invalidate();
                if (onSeekBarChangeListener != null)
                    onSeekBarChangeListener.onStopTrackingTouch(this);
                setProgress(getMax() - (int) (getMax() * event.getY() / getHeight()));
                break;
            case MotionEvent.ACTION_MOVE:
                isTouched = true;
                setProgress(getMax() - (int) (getMax() * event.getY() / getHeight()));
                break;
        }
        return true;
    }

    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);
        onSizeChanged(getWidth(), getHeight(), 0, 0);
    }

    private static final String TAG = VerticalSeekBar.class.getSimpleName();

    @Override
    public void setProgressDrawableTiled(Drawable d) {
        super.setProgressDrawableTiled(d);
        onSizeChanged(getWidth(), getHeight(), 0, 0);
    }
}
