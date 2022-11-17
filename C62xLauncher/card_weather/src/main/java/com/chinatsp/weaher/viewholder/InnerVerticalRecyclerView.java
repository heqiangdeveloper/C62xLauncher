package com.chinatsp.weaher.viewholder;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.RecyclerView;

import launcher.base.utils.EasyLog;

public class InnerVerticalRecyclerView extends RecyclerView {

    public InnerVerticalRecyclerView(@NonNull Context context) {
        super(context);
    }

    public InnerVerticalRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public InnerVerticalRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int mLastX;
    private int mLastY;

    private boolean mUserPlayB = false;

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent e) {
//        if (mUserPlayB) {
//            final int action = e.getAction();
//            int x = (int) e.getX();
//            int y = (int) e.getY();
//            switch (action) {
//                case MotionEvent.ACTION_DOWN:
//                    getParent().requestDisallowInterceptTouchEvent(true);
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    float distanceX = Math.abs(x - mLastX);
//                    float distanceY = Math.abs(y - mLastY);
//                    if (isReadyDrag(distanceX, distanceY)) {
//                        getParent().requestDisallowInterceptTouchEvent(false);
//                    }
//                    break;
//                case MotionEvent.ACTION_UP:
//                    break;
//            }
//            mLastX = x;
//            mLastY = y;
//            EasyLog.i("InnerVerticalRecyclerView", "dispatchTouchEvent PlanA");
//        } else {
//            EasyLog.i("InnerVerticalRecyclerView", "dispatchTouchEvent PlanB");
//        }
//        return super.dispatchTouchEvent(e);
//    }

    private boolean isReadyDrag(float distanceX, float distanceY) {
        return distanceX > distanceY;
    }
}
