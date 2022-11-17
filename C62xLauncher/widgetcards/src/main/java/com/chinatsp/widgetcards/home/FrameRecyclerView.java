package com.chinatsp.widgetcards.home;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import launcher.base.utils.EasyLog;

public class FrameRecyclerView extends RecyclerView {

    public FrameRecyclerView(@NonNull Context context) {
        super(context);
    }

    public FrameRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FrameRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    private boolean mUserPlayB = false;
    private int mLastX, mLastY;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (mUserPlayB) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                super.onInterceptTouchEvent(event);
                return false;
            }
            return true;
        } else {
            int x = (int) event.getX();
            int y = (int) event.getY();
            boolean intercepted = false;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    mLastX = (int) event.getX();
                    mLastY = (int) event.getY();
                    super.onInterceptTouchEvent(event);
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    int deltaX = Math.abs(x - mLastX);
                    int deltaY = Math.abs(y - mLastY);
                    EasyLog.d("FrameRecyclerView","onInterceptTouchEvent ACTION_MOVE "+deltaX +" , "+deltaY);
                    if (deltaX > deltaY) {
                        EasyLog.d("FrameRecyclerView", "onInterceptTouchEvent ACTION_MOVE OK");
                        intercepted = true;
                    } else {
                        EasyLog.w("FrameRecyclerView", "onInterceptTouchEvent ACTION_MOVE FAIL");
                    }
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    break;
                }
            }
            return intercepted;
        }
    }


}
