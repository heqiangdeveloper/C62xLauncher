package com.chinatsp.widgetcards.editor.drag;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.entity.BaseCardEntity;
import com.chinatsp.widgetcards.R;

import launcher.base.recyclerview.BaseRcvAdapter;

import launcher.base.utils.EasyLog;

public class DragHelper {
    private static final String TAG = "DragHelper";
    private static final int ANIMATE_DURATION = 300;
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
    private IDragItemView mDragItemView;
    private float mScaleX = 1f;
    private float mScaleY = 1f;
    private volatile boolean mRunningAnimate;
    private final IEnableDragStrategy mEnableDragStrategy;

    public DragHelper(ViewGroup rootContainer, IEnableDragStrategy enableDragStrategy) {
        mRootContainer = rootContainer;
        this.mContext = rootContainer.getContext();
        mEnableDragStrategy = enableDragStrategy;
        addDragView();
        addSwipeTargetView();
    }

    public void initTouchListener(IOnSwipeFinish onSwipeFinish) {
        mRecyclerView1.addOnItemTouchListener(mOnItemTouchListener);
        mRecyclerView2.addOnItemTouchListener(mOnItemTouchListener);
        setOnSwipeFinish(onSwipeFinish);
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
            dealTouchEvent(rv, event);
        }
    };


    private float mDownX;
    private float mDownY;

    private void dealTouchEvent(RecyclerView rv, MotionEvent event) {
//        EasyLog.i(TAG, "dealTouchEvent: " + event.getAction());
        if (mRunningAnimate) {
            return;
        }
        if (!enableDrag(rv, mSelectedView) && event.getAction() != MotionEvent.ACTION_DOWN) {
            return;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                DragViewWrapper dragViewWrapper = findDragView(rv, event.getX(), event.getY());
                if (dragViewWrapper == null) {
                    return;
                }
                mSelectedView = dragViewWrapper.getView();
                EasyLog.d(TAG, "dealTouchEvent onDown. Found pressedView:" + mSelectedView);
                mDragItemView = new DragItemViewHelp(mSelectedView);
                startDrag(dragViewWrapper);
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = event.getX() - mDownX;
                float dy = event.getY() - mDownY;
                dealMove((int) dx, (int) dy);
                showTargetHighlight(rv, event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                dealEventUp(rv, event);
                break;
        }
    }

    private boolean enableDrag(RecyclerView rv, View selectedView) {
        if (rv == null || selectedView == null) {
            return false;
        }
        return mEnableDragStrategy.enableDrag((BaseRcvAdapter<BaseCardEntity>) rv.getAdapter(), mDragView.getPositionInList());
    }

    private void dealEventUp(RecyclerView rv, MotionEvent event) {
        if (mTargetItemView != null) {
            mTargetItemView.setBackgroundColor(mContext.getColor(android.R.color.transparent));
        }
        boolean needSwipe = false;
        DragViewWrapper targetViewWrapper = findTargetView(event);
        if (targetViewWrapper != null) {
            boolean swipeInRcv2 = targetViewWrapper.getRecyclerView() == rv && rv == mRecyclerView2;
            boolean enableSwipe = mEnableDragStrategy.enableSwipe(targetViewWrapper, rv, mSelectedView);
            needSwipe = !swipeInRcv2 && enableSwipe;
            if (swipeInRcv2) {
                // 不允许在列表2之间交换卡片
                EasyLog.w(TAG, "findTargetView, cannot swipe items between rcv2.");
            }
            if (needSwipe){
                setTargetViewState(targetViewWrapper);
            }

        }
        boolean swipeInDiffRcv = isSwipeInDiffRcv(mDragView, mSwipeTargetView);
        EasyLog.i(TAG, "dealEventUp , needSwipe: " + needSwipe + " , swipeInDiffRcv:" + swipeInDiffRcv);
        computeSwipeScale(needSwipe, swipeInDiffRcv);
        if (needSwipe) {
            swipeCard(targetViewWrapper, swipeInDiffRcv);
        } else {
            moveDragViewToNewPosition(false, false);
        }
    }

    private void computeSwipeScale(boolean needSwipe, boolean swipeInDiffRcv) {
        if (!swipeInDiffRcv || !needSwipe) {
            mScaleX = 1f;
            mScaleY = 1f;
            return;
        }
        float dragWidth = mDragView.getLayoutParams().width;
        float dragHeight = mDragView.getLayoutParams().height;
        float swipeWidth = mSwipeTargetView.getLayoutParams().width;
        float swipeHeight = mSwipeTargetView.getLayoutParams().height;
        if (dragWidth == 0 || swipeWidth == 0) {
            mScaleX = 1f;
        } else {
            mScaleX = dragWidth / swipeWidth;
        }
        if (dragHeight == 0 || swipeHeight == 0) {
            mScaleY = 1f;
        } else {
            mScaleY = dragHeight / swipeHeight;
        }
        EasyLog.i(TAG, "computeSwipeScale , scaleX: " + mScaleX + " , scaleY: " + mScaleY);
    }

    private void swipeCard(DragViewWrapper targetViewWrapper, boolean swipeInDiffRcv) {
        // 被拖拽的卡片的新位置
        mDragViewCurrentX = (int) mSwipeTargetView.getX();
        mDragViewCurrentY = (int) mSwipeTargetView.getY();
        moveTargetViewToNewPosition(targetViewWrapper, swipeInDiffRcv);
        moveDragViewToNewPosition(true, swipeInDiffRcv);
    }

    private boolean isSwipeInDiffRcv(DragSwipeView dragView, DragSwipeView swipeTargetView) {
        if (dragView != null && swipeTargetView != null) {
            return dragView.getRecyclerView() != swipeTargetView.getRecyclerView();
        }
        return false;
    }

    private void moveTargetViewToNewPosition(DragViewWrapper targetViewWrapper, boolean diffRcv) {
        if (targetViewWrapper != null) {
            targetViewWrapper.getView().setVisibility(View.INVISIBLE);
            int[] newLocation = computeTargetNewLocation(targetViewWrapper);
            mSwipeTargetView.animate()
                    .scaleX(mScaleX)
                    .scaleY(mScaleY)
                    .x(newLocation[0])
                    .y(newLocation[1])
                    .setDuration(ANIMATE_DURATION)
                    .start();
        }
    }

    /**
     * 计算 目标View即将被交换到新位置的坐标
     */
    private int[] computeTargetNewLocation(DragViewWrapper targetViewWrapper) {
        View targetView = targetViewWrapper.getView();
        int[] newLocation = new int[2];
        mSelectedView.getLocationOnScreen(newLocation);
        boolean diffRcv = targetViewWrapper.getRecyclerView() != mDragView.getRecyclerView();
        if (!diffRcv) {
            return newLocation;
        }
        newLocation[0] = newLocation[0] + (mSelectedView.getWidth() - mTargetItemView.getWidth()) / 2;
        newLocation[1] = newLocation[1] + (mSelectedView.getHeight() - mTargetItemView.getHeight()) / 2;
        return newLocation;
    }

    /**
     * 计算 DragView即将移动到新位置的坐标
     *
     * @param diffRcv
     */
    private int[] computeDragNewLocation(boolean diffRcv) {
        int[] newLocation = new int[]{mDragViewCurrentX, mDragViewCurrentY};
        if (!diffRcv) {
            return newLocation;
        }
        newLocation[0] = newLocation[0] + (mTargetItemView.getWidth() - mSelectedView.getWidth()) / 2;
        newLocation[1] = newLocation[1] + (mTargetItemView.getHeight() - mSelectedView.getHeight()) / 2;
        return newLocation;
    }

    private void setTargetViewState(DragViewWrapper targetViewWrapper) {
        if (targetViewWrapper != null) {
            mSwipeTargetView.bringToFront();
            mTargetItemView = targetViewWrapper.getView();
            resizeAndLocateView(mSwipeTargetView, targetViewWrapper.getView());
            mSwipeTargetView.setRecyclerView(targetViewWrapper.getRecyclerView());
            mSwipeTargetView.setBackground(new CardDragDrawable(targetViewWrapper.getView()));
            mSwipeTargetView.setVisibility(View.VISIBLE);
            mSwipeTargetView.setPositionInList(targetViewWrapper.getRecyclerView().getChildAdapterPosition(targetViewWrapper.getView()));
        }
    }

    private void moveDragViewToNewPosition(boolean needSwipe, boolean diffRcv) {
        int[] newLocation = computeDragNewLocation(diffRcv);
        mRunningAnimate = true;
        mDragView.animate()
                .x(newLocation[0])
                .y(newLocation[1])
                .scaleX(1f / mScaleX)
                .scaleY(1f / mScaleY)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        onDragEnd(needSwipe);
                        mRunningAnimate = false;
                    }
                }).setDuration(ANIMATE_DURATION).start();
    }

    private void onDragEnd(boolean needSwipe) {
        mDragView.setVisibility(View.GONE);
        mSelectedView.setVisibility(View.VISIBLE);
        mDragItemView.restore();
        mSwipeTargetView.setScaleX(1f);
        mSwipeTargetView.setScaleY(1f);
        mDragView.setScaleX(1f);
        mDragView.setScaleY(1f);
        if (needSwipe) {
            mSwipeTargetView.setVisibility(View.GONE);
            mTargetItemView.setVisibility(View.VISIBLE);
            changeItemPosition(mDragView, mSwipeTargetView);
        }
    }

    private void showTargetHighlight(RecyclerView rv, MotionEvent event) {
        DragViewWrapper viewWrapper = findTargetView(event);

        if (mTargetItemView != null) {
            mTargetItemView.setBackgroundColor(mContext.getColor(android.R.color.transparent));
        }
        if (viewWrapper == null) {
            return;
        }
        boolean swipeInRcv2 = viewWrapper.getRecyclerView() == rv && rv == mRecyclerView2;
        if (swipeInRcv2) {
            // 不允许在列表2之间交换卡片
            EasyLog.w(TAG, "showTargetHighlight, cannot swipe items between rcv2.");
            return;
        }
        if (!mEnableDragStrategy.enableSwipe(viewWrapper, rv, mSelectedView)) {
            EasyLog.w(TAG, "showTargetHighlight, cannot swipe items which type is empty.");
            return;
        }
        if (mSelectedView == viewWrapper.getView()) {
            // 仍在选中的卡片View范围内, 所以无需交换卡片
            EasyLog.w(TAG, "showTargetHighlight, it is self...");
            return;
        }
        mTargetItemView = viewWrapper.getView();
//        EasyLog.i(TAG, "showTargetHighlight :" + mTargetRecyclerView.getChildAdapterPosition(mTargetItemView));
        if (mTargetItemView != null) {
            mTargetItemView.setBackgroundColor(mContext.getColor(R.color.card_blue_default));
        }
    }

    // 检测碰撞
    private DragViewWrapper findTargetView(MotionEvent event) {
        float rawX = event.getRawX();
        float rawY = event.getRawY();
        int x;
        int y;
        View targetView = null;
        RecyclerView rcv = null;
        RecyclerView[] recyclerViews = new RecyclerView[]{mRecyclerView1, mRecyclerView2};
        for (int i = 0; i < recyclerViews.length; i++) {
            RecyclerView tRcv = recyclerViews[i];
            x = (int) (rawX - tRcv.getX());
            y = (int) (rawY - tRcv.getY());
            targetView = tRcv.findChildViewUnder(x, y);
            rcv = tRcv;
            if (targetView != null) {
                break;
            }
        }
        if (targetView == null) {
            EasyLog.w(TAG, "findTargetView failed, nothing be found.");
            return null;
        }

        DragViewWrapper targetViewWrapper = new DragViewWrapper(targetView, rcv);
        return targetViewWrapper;
    }

    /**
     * 根据坐标查询被拖拽的View
     *
     * @param rv
     * @param dx
     * @param dy
     * @return
     */
    private DragViewWrapper findDragView(RecyclerView rv, float dx, float dy) {
        DragViewWrapper dragViewWrapper = null;
        EasyLog.i(TAG, "findDragView : " + dx + " , " + dy);
        View dragView = rv.findChildViewUnder(dx, dy);
        if (dragView != null) {
            dragViewWrapper = new DragViewWrapper(dragView, rv);
        }
        return dragViewWrapper;
    }

    private int mDragViewOriginX;
    private int mDragViewOriginY;
    private int mDragViewCurrentX;
    private int mDragViewCurrentY;

    private void startDrag(DragViewWrapper dragViewWrapper) {
        if (dragViewWrapper == null) {
            return;
        }
        resizeAndLocateView(mDragView, mSelectedView);
        mDragViewCurrentX = mDragViewOriginX = (int) mDragView.getX();
        mDragViewCurrentY = mDragViewOriginY = (int) mDragView.getY();
        mDragView.bringToFront();
        mDragView.setRecyclerView(dragViewWrapper.getRecyclerView());
        int position = dragViewWrapper.getRecyclerView().getChildAdapterPosition(mSelectedView);
        EasyLog.i(TAG, "startDrag : " + position);
        mDragView.setPositionInList(position);
        mDragView.setBackgroundDrawable(new CardDragDrawable(mSelectedView));
        mDragView.setVisibility(View.VISIBLE);
        mDragItemView.becomeEmpty();
    }

    private void dealMove(int dX, int dY) {
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

    }

    @SuppressWarnings("rawtypes")
    private void changeItemPosition(DragSwipeView dragView, DragSwipeView swipeTargetView) {
        int position1 = dragView.getPositionInList();
        int position2 = swipeTargetView.getPositionInList();
        EasyLog.d(TAG, "changeItemPosition:" + position1 + ", " + position2);

        if (mOnSwipeFinish != null) {
            mOnSwipeFinish.onSwipe(position1, dragView.getRecyclerView(), position2, swipeTargetView.getRecyclerView());
        }
    }

    public void setOnSwipeFinish(IOnSwipeFinish onSwipeFinish) {
        mOnSwipeFinish = onSwipeFinish;
    }
}
