package com.common.xui.widget.picker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

import com.common.xui.utils.ThreadUtil;

@SuppressLint("AppCompatCustomView")
public class VerSeekBar extends SeekBar {
    private OnSeekBarChangeListener onSeekBarChangeListener;

    public VerSeekBar(Context context) {
        super(context);
    }

    public VerSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VerSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!isEnabled())
        {
            return false;
        }

        //Logcat.i(TAG,  "onTouchEvent setPressed true");
        setPressed(true);
        ThreadUtil.getInstance().getMainThreadHandler().removeCallbacks(resetRunnable);
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                if (onSeekBarChangeListener != null)
                    onSeekBarChangeListener.onStartTrackingTouch(this);
                setProgress(getMax() - (int) (getMax() * event.getY() / getHeight()));
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (onSeekBarChangeListener != null)
                    onSeekBarChangeListener.onStopTrackingTouch(this);
            case MotionEvent.ACTION_MOVE:
                setProgress(getMax() - (int) (getMax() * event.getY() / getHeight()));
                break;
        }
        ThreadUtil.getInstance().getMainThreadHandler().postDelayed(resetRunnable, 500);
        return true;
    }

    private Runnable resetRunnable = new Runnable() {
        @Override
        public void run() {
            //Logcat.i(TAG,  "resetRunnable setPressed false");
            setPressed(false);
        }
    };

    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);
        onSizeChanged(getWidth(), getHeight(), 0, 0);
    }

    private static final String TAG = VerticalSeekBar.class.getSimpleName();

    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
    }

    @Override
    public boolean isPressed() {
        boolean isPressed = super.isPressed();
        //Logcat.i(TAG,  "isPressed " + isPressed);
        return isPressed;
    }


    @Override
    public void setProgressDrawableTiled(Drawable d) {
        super.setProgressDrawableTiled(d);
        onSizeChanged(getWidth(), getHeight(), 0, 0);
    }
}
