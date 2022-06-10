package com.chinatsp.widgetcards.editor.drag;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ClipData;
import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.widgetcards.R;

import java.util.List;

import launcher.base.recyclerview.BaseRcvAdapter;

import launcher.base.utils.EasyLog;
import launcher.base.utils.collection.IndexCheck;

public class DragHelper {
    private static final String TAG = "DragHelper";
    private static final int ANIMATE_DURATION = 200;
    private ViewGroup mRootContainer;
    private RecyclerView mRecyclerView1;
    private RecyclerView mRecyclerView2;
    private BaseRcvAdapter mAdapter1;
    private BaseRcvAdapter mAdapter2;
    private Context mContext;
    private DragSwipeView mDragView;
    private DragSwipeView mSwipeTargetView;
    private View mSelectedView;
    private View mTargetItemView;
    private IOnSwipeFinish mOnSwipeFinish;

    public DragHelper(ViewGroup rootContainer) {
        mRootContainer = rootContainer;
        this.mContext = rootContainer.getContext();
        addDragView();
        addSwipeTargetView();
    }

    private void addDragView() {
        mDragView = new DragSwipeView(mContext);
        mDragView.setVisibility(View.GONE);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(100, 100);
        mDragView.setLayoutParams(layoutParams);
        mRootContainer.addView(mDragView, -1);
    }

    private void addSwipeTargetView() {
        mSwipeTargetView = new DragSwipeView(mContext);
        mSwipeTargetView.setVisibility(View.GONE);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(100, 100);
        mSwipeTargetView.setLayoutParams(layoutParams);
        mRootContainer.addView(mSwipeTargetView);
    }

    public void setRecyclerView1(RecyclerView recyclerView1) {
        mRecyclerView1 = recyclerView1;
        mAdapter1 = (BaseRcvAdapter) mRecyclerView1.getAdapter();
    }

    public void setRecyclerView2(RecyclerView recyclerView2) {
        mRecyclerView2 = recyclerView2;
        mAdapter2 = (BaseRcvAdapter) mRecyclerView2.getAdapter();
    }

    private RecyclerView.OnItemTouchListener mOnItemTouchListener = new RecyclerView.SimpleOnItemTouchListener() {

        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
//            EasyLog.d(TAG, "mRecyclerView1 , onInterceptTouchEvent:" + e.getAction());
            return true;
        }

        @Override
        public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent event) {
//            EasyLog.d(TAG, "mRecyclerView1 , onTouchEvent:" + event.getAction());
//            mMainGestureDetector.onTouchEvent(event);
            dealTouchEvent(event);
        }

    };


    public void initTouchListener(IOnSwipeFinish onSwipeFinish) {
//        mRecyclerView1.setOnDragListener(mOnDragListener);
        mRecyclerView1.addOnItemTouchListener(mOnItemTouchListener);
        setOnSwipeFinish(onSwipeFinish);
    }

    private float mDownX;
    private float mDownY;

    private void dealTouchEvent(MotionEvent event) {
//        EasyLog.i(TAG, "dealTouchEvent: " + event.getAction());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                View pressedView = findChildView(mRecyclerView1, event.getX(), event.getY());
                mSelectedView = pressedView;
                EasyLog.d(TAG, "dealTouchEvent onDown. Found pressedView:" + pressedView);
                if (pressedView == null) {
                    return;
                }
                startDrag();
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = event.getX() - mDownX;
                float dy = event.getY() - mDownY;
                dealMove((int) dx, (int) dy);
                checkSwipeTarget(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                dealEventUp(event);
                flushListState(event);
                break;
        }
    }

    private void dealEventUp(MotionEvent event) {
        if (mTargetItemView != null) {
            mTargetItemView.setBackgroundColor(mContext.getColor(android.R.color.transparent));
        }
        View swipeView = findTargetView(event.getX(), event.getY());
        if (swipeView != null) {
            mNeedSwiped = true;
            dealSwipe(swipeView);
        }
    }

    private void checkSwipeTarget(MotionEvent event) {
        View targetView = findTargetView(event.getX(), event.getY());
        showTargetHighlight(targetView);
    }

    private void showTargetHighlight(View targetView) {
        if (mTargetItemView == targetView) {
            return;
        }
        if (mTargetItemView != null) {
            mTargetItemView.setBackgroundColor(mContext.getColor(android.R.color.transparent));
        }
        mTargetItemView = targetView;
        EasyLog.i(TAG, "showTargetHighlight :" + mRecyclerView1.getChildAdapterPosition(mTargetItemView));
        if (mTargetItemView != null) {
            mTargetItemView.setBackgroundColor(mContext.getColor(R.color.card_blue_default));
        }
    }

    private boolean mNeedSwiped = false;

    private void dealSwipe(View targetView) {
        if (targetView != null) {
            mTargetItemView = targetView;
            resizeAndLocateView(mSwipeTargetView, targetView);
            mSwipeTargetView.bringToFront();
            mSwipeTargetView.setBackgroundDrawable(new CardDragDrawable(targetView));
            mSwipeTargetView.setVisibility(View.VISIBLE);
            mSwipeTargetView.setPositionInList(mRecyclerView1.getChildAdapterPosition(targetView));
            targetView.setVisibility(View.INVISIBLE);
            // 新位置
            mDragViewCurrentX = (int) mSwipeTargetView.getX();
            mDragViewCurrentY = (int) mSwipeTargetView.getY();

            mSwipeTargetView.animate().x(mDragViewOriginX).y(mDragViewOriginY).setDuration(ANIMATE_DURATION).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                }
            }).start();
        }
    }

    // 检测碰撞
    private View findTargetView(float dX, float dY) {
        View targetView = findChildView(mRecyclerView1, dX, dY);
        if (targetView == null) {
            return null;
        }
        if (mSelectedView == targetView) {
            // 仍在选中的卡片View范围内
            EasyLog.w(TAG, "findTargetView, it is self...");
            return null;
        }
//        targetView.setBackgroundColor(mContext.getColor(R.color.card_blue_default));
        return targetView;
    }

    private int mDragViewOriginX;
    private int mDragViewOriginY;
    private int mDragViewCurrentX;
    private int mDragViewCurrentY;

    private void startDrag() {
        if (mSelectedView == null) {
            return;
        }
        resizeAndLocateView(mDragView, mSelectedView);
        mDragViewCurrentX = mDragViewOriginX = (int) mDragView.getX();
        mDragViewCurrentY = mDragViewOriginY = (int) mDragView.getY();
        mDragView.bringToFront();
        int position = mRecyclerView1.getChildAdapterPosition(mSelectedView);
        EasyLog.i(TAG, "startDrag : " + position);
        mDragView.setPositionInList(position);
        mDragView.setBackgroundDrawable(new CardDragDrawable(mSelectedView));
        mDragView.setVisibility(View.VISIBLE);
        mSelectedView.setVisibility(View.INVISIBLE);
    }

    private void dealMove(int dX, int dY) {
//        float minMove = 10;
//        if (Math.abs(dX) < minMove && Math.abs(dY) < minMove) {
//            return;
//        }
        if (mSelectedView == null) {
            return;
        }
        mDragView.setX(mDragViewOriginX + dX);
        mDragView.setY(mDragViewOriginY + dY);
    }

    private void resizeAndLocateView(View target, View anchor) {
        if (target == null || anchor == null) {
            return;
        }
        ViewGroup.LayoutParams selectedViewLayoutParams = anchor.getLayoutParams();
        ViewGroup.LayoutParams layoutParams = target.getLayoutParams();
        layoutParams.width = selectedViewLayoutParams.width;
        layoutParams.height = selectedViewLayoutParams.height;
        int[] location = new int[2];
        anchor.getLocationInWindow(location);
        anchor.getLocationOnScreen(location);

        target.setX(location[0]);
        target.setY(location[1]);
    }

    // 处理松开手指. 如果没有发现可交换的targetView, 就返回原位置.
    private void flushListState(MotionEvent event) {
        mDragView.animate().x(mDragViewCurrentX).y(mDragViewCurrentY).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mDragView.setVisibility(View.GONE);
                mSelectedView.setVisibility(View.VISIBLE);
                if (mNeedSwiped) {
                    mSwipeTargetView.setVisibility(View.GONE);
                    mTargetItemView.setVisibility(View.VISIBLE);
                    int p1 = mDragView.getPositionInList();
                    int p2 = mSwipeTargetView.getPositionInList();
                    changeItemPosition(mAdapter1, p1, p2);
                }
                mNeedSwiped = false;
            }
        }).setDuration(ANIMATE_DURATION).start();
    }

    // 操作取消, 返回原来的位置
    private void dealCancel() {
        mDragView.animate().x(mDragViewOriginX).y(mDragViewOriginY).setDuration(ANIMATE_DURATION).start();
    }

    @SuppressWarnings("rawtypes")
    private void changeItemPosition(BaseRcvAdapter adapter, int position1, int position2) {
        EasyLog.d(TAG, "changeItemPosition:" + position1 + ", " + position2);
        List data = adapter.getData();
        if (data == null) {
            return;
        }
        if (IndexCheck.indexOutOfArray(data, position1) || IndexCheck.indexOutOfArray(data, position2)) {
            return;
        }
        EasyLog.d(TAG, "changeItemPosition:" + data.size());
        if (mOnSwipeFinish != null) {
            mOnSwipeFinish.onSwipeHome(position1, position2);
        }
//        Object temp1 = data.get(position1);
//        Object temp2 = data.get(position2);
//        data.set(position1, temp2);
//        data.set(position2, temp1);
//        mAdapter1.notifyDataSetChanged();
    }

    private View findChildView(RecyclerView recyclerView, float x, float y) {
        // first check elevated views, if none, then call RV
        return recyclerView.findChildViewUnder(x, y);
    }

    public void setOnSwipeFinish(IOnSwipeFinish onSwipeFinish) {
        mOnSwipeFinish = onSwipeFinish;
    }
}
