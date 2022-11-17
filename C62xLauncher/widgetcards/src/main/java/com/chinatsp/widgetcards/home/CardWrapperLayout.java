package com.chinatsp.widgetcards.home;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.chinatsp.drawer.search.LauncherSearchActivity;

import launcher.base.routine.ActivityBus;
import launcher.base.utils.EasyLog;

public class CardWrapperLayout extends ConstraintLayout {
    private static final String TAG = "CardWrapperLayout";
    private static final float MOVE_Y_GO_SEARCH = 100;
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


    float lastX = 0f;
    float lastY = 0f;
    float moveYOnceTouch;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int mask = ev.getActionMasked();
        boolean handle = super.dispatchTouchEvent(ev);
        if (mask == MotionEvent.ACTION_DOWN) {
            mLongPressTriggered = false;
            lastX = ev.getX();
            lastY = ev.getY();
            moveYOnceTouch = 0;
        }
        if (mask == MotionEvent.ACTION_MOVE) {
            moveYOnceTouch = ev.getY();
        }
        if (mask == MotionEvent.ACTION_DOWN && isLongClickable()) {
            scheduleLongPress();
        }
        if (mask == MotionEvent.ACTION_MOVE && (Math.abs(ev.getX() - lastX) > 5 || Math.abs(ev.getY() - lastY) > 5)) {
            removeLongPress();
        }
        if (ev.getActionMasked() == MotionEvent.ACTION_UP
                || ev.getActionMasked() == MotionEvent.ACTION_CANCEL) {
            removeLongPress();
            float v = moveYOnceTouch - lastY;
            EasyLog.d(TAG, "check move Y:"+v);
            // 向下移动超过MOVE_Y_GO_SEARCH, 即进入搜索页面
            if (v > MOVE_Y_GO_SEARCH) {
                goToSearchActivity();
            }
        }
        return handle;
    }

    private void goToSearchActivity() {
        ActivityBus.newInstance(getContext())
                .withClass(LauncherSearchActivity.class)
                .go();
    }

    private void scheduleLongPress() {
        EasyLog.d(TAG, "scheduleLongPress");
//        postDelayed(longClickRunnable, ViewConfiguration.getLongPressTimeout());
        postDelayed(longClickRunnable, 600);
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
            Log.d(TAG, "run() called : " + mLongPressTriggered);
        }
    };

    private void removeLongPress() {
        EasyLog.w(TAG, "removeLongPress");
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
