package com.common.xui.widget.picker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.common.xui.R;

public class VerticalSeekBar extends View {
    private final int min = 0;
    private int max = 100;
    private int steep = 10;
    private int cornerRadius = 10;
    private int progressSweep = 0;
    private int progress = 50;
    private boolean enabled = true;
    private boolean touchEnabled = true;
    private boolean firstRun = true;
    private int scrWidth;
    private int scrHeight;
    private int backgroundColor;
    private int progressColor;
    private Paint mProgressPaint;
    private OnValuesChangeListener mOnValuesChangeListener;

    public VerticalSeekBar(Context context) {
        super(context);
        init(context, null);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        progressColor = ContextCompat.getColor(context, R.color.xui_config_color_light_green);
        backgroundColor = ContextCompat.getColor(context, R.color.xui_config_color_black);

        if (attrs != null) {
            try {
                TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.VerticalSeekBar, 0, 0);
                max = a.getInteger(R.styleable.VerticalSeekBar_klvs_max, max);
                steep = a.getColor(R.styleable.VerticalSeekBar_klvs_steep, steep);
                enabled = a.getBoolean(R.styleable.VerticalSeekBar_klvs_enabled, enabled);
                touchEnabled = a.getBoolean(R.styleable.VerticalSeekBar_klvs_touchEnabled, touchEnabled);
                progress = a.getInteger(R.styleable.VerticalSeekBar_klvs_progress, progress);
                cornerRadius = a.getInteger(R.styleable.VerticalSeekBar_klvs_cornerRadius, cornerRadius);
                progressColor = a.getColor(R.styleable.VerticalSeekBar_klvs_progressColor, progressColor);
                backgroundColor = a.getColor(R.styleable.VerticalSeekBar_klvs_backgroundColor, backgroundColor);
                a.recycle();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        progress = Math.min(progress, max);
        progress = Math.max(progress, min);

        makeProgressColor();

        scrHeight = context.getResources().getDisplayMetrics().heightPixels;

    }

    private void makeProgressColor() {
        mProgressPaint = new Paint();
        mProgressPaint.setColor(progressColor);
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        scrWidth = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        scrHeight = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        mProgressPaint.setStrokeWidth(scrWidth);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        makeCustomPaint(canvas);
        if (firstRun) {
            firstRun = false;
            setProgress(progress);
        }
    }

    Rect rectBg = new Rect();

    private void makeCustomPaint(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setAlpha(255);
        canvas.translate(0, 0);
        RectF rf = new RectF(0, 0, scrWidth, scrHeight);
        Path mPath = new Path();
        mPath.addRoundRect(new RectF(0, 0, scrWidth, scrHeight), cornerRadius, cornerRadius, Path.Direction.CCW);
        canvas.clipPath(mPath, Region.Op.INTERSECT);
        paint.setColor(backgroundColor);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.vioce_bg_blue);
        rectBg.left = 0;
        rectBg.top = 0;
        rectBg.right = bitmap.getWidth();
        rectBg.bottom = bitmap.getHeight();
        canvas.drawBitmap(bitmap, rectBg, rf, paint);

        //canvas.drawRect(0, 0, scrWidth, scrHeight, paint);

        canvas.drawLine(canvas.getWidth() / 2f, canvas.getHeight(), canvas.getWidth() / 2f, progressSweep, mProgressPaint);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (enabled) {
            this.getParent().requestDisallowInterceptTouchEvent(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mOnValuesChangeListener != null)
                        mOnValuesChangeListener.onStartTrackingTouch(this);

                    if (touchEnabled)
                        updateOnTouch(event);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (touchEnabled)
                        updateOnTouch(event);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (mOnValuesChangeListener != null)
                        mOnValuesChangeListener.onStopTrackingTouch(this);
                    setPressed(false);
                    this.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
            return true;
        }
        return false;
    }

    private void updateOnTouch(MotionEvent event) {
        setPressed(true);
        double mTouch = convertTouchEventPoint(event.getY());
        int progress = (int) Math.round(mTouch);
        updateProgress(progress);
    }

    private double convertTouchEventPoint(float yPos) {
        float wReturn;
        if (yPos > (scrHeight * 2)) {
            wReturn = scrHeight * 2;
            return wReturn;
        } else if (yPos < 0) {
            wReturn = 0;
        } else {
            wReturn = yPos;
        }

        return wReturn;
    }

    private void updateProgress(int progress) {
        this.progressSweep = progress;
        progress = Math.min(progress, scrHeight);
        progress = Math.max(progress, 0);

        this.progress = progress * (max - min) / scrHeight + min;
        this.progress = max + min - this.progress;
        if (this.progress != max && this.progress != min) {
            this.progress = this.progress - (this.progress % steep) + (min % steep);
        }
        if (mOnValuesChangeListener != null) {
            mOnValuesChangeListener
                    .onPointsChanged(this, this.progress);
        }

        invalidate();
    }

    private void updateProgressByValue(int value) {
        progress = value;

        progress = Math.min(progress, max);
        progress = Math.max(progress, min);
        progressSweep = (progress - min) * scrHeight / (max - min);
        progressSweep = scrHeight - progressSweep;

       /* if (mOnValuesChangeListener != null) {
            mOnValuesChangeListener
                    .onPointsChanged(this, progress);
        }*/

        invalidate();
    }

    public interface OnValuesChangeListener {
        void onPointsChanged(VerticalSeekBar view, int progress);

        void onStartTrackingTouch(VerticalSeekBar seekBar);

        void onStopTrackingTouch(VerticalSeekBar seekBar);
    }

    public void setProgress(int progress) {
        progress = Math.min(progress, max);
        progress = Math.max(progress, min);
        updateProgressByValue(progress);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isTouchEnabled() {
        return touchEnabled;
    }

    public int getProgress() {
        return progress;
    }

    public int getMax() {
        return max;
    }

    public int getSteep() {
        return steep;
    }

    public int getCornerRadius() {
        return cornerRadius;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public int getProgressColor() {
        return progressColor;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setMax(int mMax) {
        if (mMax <= min)
            throw new IllegalArgumentException("Max should not be less than zero");
        this.max = mMax;
    }

    public void setCornerRadius(int mRadius) {
        this.cornerRadius = mRadius;
        invalidate();
    }

    public void setStep(int step) {
        steep = step;
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        invalidate();
        requestLayout();
    }

    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
        makeProgressColor();
        invalidate();
    }

    public void setTouchEnabled(boolean touchEnabled) {
        this.touchEnabled = touchEnabled;
    }

    public void setOnBoxedPointsChangeListener(OnValuesChangeListener onValuesChangeListener) {
        mOnValuesChangeListener = onValuesChangeListener;
    }
}
