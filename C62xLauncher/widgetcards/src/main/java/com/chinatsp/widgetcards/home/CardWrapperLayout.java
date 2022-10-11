package com.chinatsp.widgetcards.home;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public class CardWrapperLayout extends ConstraintLayout {
    private static final String TAG = "CommonCardLayout";
    private boolean mLongPressTriggered = false;

    public CardWrapperLayout(@NonNull Context context) {
        super(context);
    }

    public CardWrapperLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CardWrapperLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CardWrapperLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int mask = ev.getActionMasked();
        if (mask == MotionEvent.ACTION_DOWN) {
            mLongPressTriggered = false;
        }
        boolean handle = super.dispatchTouchEvent(ev);
        if (mask == MotionEvent.ACTION_DOWN && isLongClickable()) {
            scheduleLongPress();
        }
        if (ev.getActionMasked() == MotionEvent.ACTION_UP
                || ev.getActionMasked() == MotionEvent.ACTION_CANCEL) {
            removeLongPress();
        }
        return handle;
    }

    private void scheduleLongPress() {
        postDelayed(longClickRunnable, ViewConfiguration.getLongPressTimeout());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mLongPressTriggered && event.getActionMasked() == MotionEvent.ACTION_UP) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    private final Runnable longClickRunnable = new Runnable() {
        @Override
        public void run() {
            mLongPressTriggered = performLongClick();
            Log.d(TAG, "run() called" + mLongPressTriggered);
        }
    };

    private void removeLongPress() {
        removeCallbacks(longClickRunnable);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mLongPressTriggered && ev.getActionMasked() == MotionEvent.ACTION_UP) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }
}
