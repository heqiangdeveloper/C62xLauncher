package com.chinatsp.launcher;

import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.view.GestureDetector;
import android.view.MotionEvent;

import launcher.base.utils.EasyLog;

public class SlideGestureListener implements GestureDetector.OnGestureListener {
    private static final String TAG = "SlideGestureListener";
    private final OnGestureAction mGestureAction;
    private final Context mContext;
    private final int mBottomEdge;

    public SlideGestureListener(OnGestureAction gestureAction, Context context) {
        mGestureAction = gestureAction;
        mContext = context;
        Resources resources = mContext.getResources();
        mBottomEdge = resources.getDimensionPixelOffset(R.dimen.card_height);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        boolean matchSlideUp = matchSlideUp(e1, e2, velocityX, velocityY);
        if (matchSlideUp && mGestureAction != null) {
            mGestureAction.goAppPanel();
        }
        return false;
    }

    private boolean matchSlideUp(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e1 == null || e2 == null) {
            // InnerVerticalRecyclerView , 在dispatchTouchEvent内如果检测到多点触控, 就会返回false, 即不分发.
            // 在这种情况下,  e1和e2会是null
            return false;
        }
        int moveX = (int) (e2.getX() - e1.getX());
        int moveY = (int) (e2.getY() - e1.getY());
        EasyLog.d(TAG, "matchSlideUp velocityX:" + velocityX + " , velocityY:" + velocityY + " , moveX:" + moveX + " , moveY:" + moveY);
        if (e1.getY() < mBottomEdge) {
            return false;
        }
        if (moveY > 0) {
            return false;
        }
        if (Math.abs(moveX) > (-moveY)) {
            return false;
        }
        if (moveY > -50) {
            return false;
        }
        EasyLog.i(TAG, "matchSlideUp OK!");
        return true;
    }
}
