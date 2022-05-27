package com.common.xui.widget.picker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.common.xui.R;

public class VerticalSeekBar extends View {
    int maxValue;
    int minValue;
    int currentValue;
    int count;
    float eachHeight;
    float yCoordinate;

    Paint backgroundPaint;
    OnChangeListener listener;

    public VerticalSeekBar(Context context) {
        this(context, null);
    }

    public VerticalSeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.VerticalSeekBar);
        maxValue = typedArray.getInteger(R.styleable.VerticalSeekBar_max_value, 7);
        minValue = typedArray.getInteger(R.styleable.VerticalSeekBar_min_value, 1);
        currentValue = minValue;
        backgroundPaint = new Paint();
        backgroundPaint.setColor(getResources().getColor(R.color.xui_config_color_red));
        backgroundPaint.setAntiAlias(true);

        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = 14;
        int height = 219;

        int measuredWidth = getSize(widthMode, widthSize, width);
        int measuredHeight = getSize(heightMode, heightSize, height);
        setMeasuredDimension(measuredWidth, measuredHeight);

        count = maxValue - minValue + 1;
        eachHeight = (measuredHeight - measuredWidth) / (float) count;
        yCoordinate = measuredWidth / 2f + eachHeight * (maxValue - currentValue + 1);
        if (currentValue == maxValue) {
            yCoordinate = measuredWidth / 2f;
        } else if (currentValue == minValue) {
            yCoordinate = measuredHeight - measuredWidth / 2f;
        } else {
            yCoordinate = measuredWidth / 2f + eachHeight * (maxValue - currentValue + 1);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();

        backgroundPaint.setColor(getResources().getColor(R.color.xui_config_color_black));
        RectF topRectF = new RectF(0,
                measuredWidth / 2,
                measuredWidth,
                Math.min(Math.max(yCoordinate, getMeasuredWidth() / 2f),
                        getMeasuredHeight() - getMeasuredWidth() / 2f));
        canvas.drawRect(topRectF, backgroundPaint);
        backgroundPaint.setColor(getResources().getColor(yCoordinate == 0 ? R.color.xui_config_color_light_green : R.color.xui_config_color_black));
        RectF topCircle = new RectF(0, 0, measuredWidth, measuredWidth);
        canvas.drawArc(topCircle, 180, 180, false, backgroundPaint);
        backgroundPaint.setColor(getResources().getColor(R.color.xui_config_color_light_green));
        RectF bottomRectF = new RectF(0, Math.max(Math.min(yCoordinate, getMeasuredHeight() - getMeasuredWidth() / 2f), getMeasuredWidth() / 2f), measuredWidth, measuredHeight - measuredWidth / 2);
        canvas.drawRect(bottomRectF, backgroundPaint);
        RectF bottomCircle = new RectF(0, measuredHeight - measuredWidth, measuredWidth, measuredHeight);
        backgroundPaint.setColor(getResources().getColor(yCoordinate == getMeasuredHeight() ? R.color.xui_config_color_black : R.color.xui_config_color_light_green));
        canvas.drawArc(bottomCircle, 0, 180, false, backgroundPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                float y = event.getY();
                if (y < getMeasuredWidth() / 2f) {
                    y = 0;
                } else if (y > getMeasuredHeight() - getMeasuredWidth() / 2f) {
                    y = getMeasuredHeight();
                }
                yCoordinate = y;
                if (listener != null) {
                    getCurrentValue(y);
                    listener.onChange(this, currentValue);
                }
                invalidate();
                break;
            default:
                break;
        }
        return true;
    }

    private int getSize(int mode, int size, int defaultSize) {
        int measuredSize = defaultSize;
        switch (mode) {
            case MeasureSpec.EXACTLY:
                measuredSize = size;
                break;
            case MeasureSpec.AT_MOST:
                break;
            default:
                break;
        }
        return measuredSize;
    }

    public void getCurrentValue(float eventY) {
        float allHeight = eventY - getMeasuredWidth() / 2f;
        int currentValue = maxValue - (int) (allHeight / eachHeight);
        if (currentValue < minValue) {
            currentValue = minValue;
        }
        this.currentValue = currentValue;
    }

    public void setOnChangeListener(OnChangeListener listener) {
        this.listener = listener;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(int currentValue) {
        this.currentValue = currentValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public interface OnChangeListener {
        void onChange(View view, int currentValue);
    }
}
