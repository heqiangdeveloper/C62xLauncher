package com.chinatsp.widgetcards.home;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.widgetcards.CardIntentService;

import launcher.base.utils.EasyLog;

public class HomeCardRecyclerView extends RecyclerView {

    public HomeCardRecyclerView(@NonNull Context context) {
        super(context);
    }

    public HomeCardRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HomeCardRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private boolean mUserPlayB = false;
    private int mLastX, mLastY;
    private boolean mLongPressTriggered = false;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean enableScroll = enableScrollHorizontal();
        EasyLog.d("HomeCardRecyclerView", "onInterceptTouchEvent enableScroll " + enableScroll);
        if (!enableScroll) {
            return super.onInterceptTouchEvent(event);
        }
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
                    EasyLog.d("HomeCardRecyclerView", "onInterceptTouchEvent ACTION_MOVE " + deltaX + " , " + deltaY);
                    if (deltaX > deltaY) {
                        EasyLog.d("HomeCardRecyclerView", "onInterceptTouchEvent ACTION_MOVE OK");
                        intercepted = true;
                    } else {
                        EasyLog.w("HomeCardRecyclerView", "onInterceptTouchEvent ACTION_MOVE FAIL");
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

    private boolean enableScrollHorizontal() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) getLayoutManager();
        if (layoutManager != null) {
            return layoutManager.canScrollHorizontally();
        }
        return true;
    }


    private float downX, downY, moveX, moveY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                if (!isShowingControlViews()) {
                    scheduleLongPress();
                }
                downX = event.getX();
                downY = event.getY();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                moveX = event.getX();
                moveY = event.getY();
                float deltaY = moveY - downY;
                float deltaX = moveX - downX;
                boolean moveTooFar = checkTouchMoveTooFar(deltaY, deltaX);
                EasyLog.d("HomeCardRecyclerView", "dispatchTouchEvent ACTION_MOVE, deltaY:" + deltaY + ", deltaX:" + deltaX+" , moveTooFar:"+moveTooFar);
                if (moveTooFar) {
                    removeLongPress();
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                removeLongPress();
                break;
            }
        }
        return super.dispatchTouchEvent(event);
    }


    private Handler mLongPressHandler = new Handler(Looper.getMainLooper());

    private void scheduleLongPress() {
        EasyLog.d("HomeCardRecyclerView", "scheduleLongPress");
//        postDelayed(longClickRunnable, ViewConfiguration.getLongPressTimeout());

        mLongPressHandler.postDelayed(longClickRunnable, 600);
    }

    private final Runnable longClickRunnable = new Runnable() {
        @Override
        public void run() {
            mLongPressTriggered = true;
            CardIntentService.start(getContext(), CardIntentService.OP_VALUE_START_CARD_EDIT);
            Log.d("HomeCardRecyclerView", "run() called : " + mLongPressTriggered);
        }
    };

    private void removeLongPress() {
        EasyLog.w("HomeCardRecyclerView", "removeLongPress");
        mLongPressHandler.removeCallbacks(longClickRunnable);
    }


    private boolean checkTouchMoveTooFar(float deltaY, float deltaX) {
        return Math.abs(deltaY) > 5 || Math.abs(deltaX) > 5;
    }

    /**
     * @return true: 正在显示控件组
     */
    private boolean isShowingControlViews() {
        return !canScrollHorizontally(-1);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EasyLog.w("HomeCardRecyclerView", "onAttachedToWindow "+hashCode());
    }
}
