package com.chinatsp.widgetcards.home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.widgetcards.R;
import com.chinatsp.widgetcards.editor.ui.CardEditorActivity;
import com.chinatsp.widgetcards.manager.Events;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import card.service.ICardStyleChange;
import card.base.LauncherCard;
import launcher.base.routine.ActivityBus;
import launcher.base.utils.EasyLog;
import launcher.base.utils.flowcontrol.StableOnClickListener;

public class CardFrameViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "CardFrameViewHolder";
    private static final int MIN_CLICK_INTERVAL = 400; // ms
    private final int EXPAND_ANIM_DURATION = 200;
    private RecyclerView mRecyclerView;
    private TextView mTvCardName;
    private ImageView mIvCardZoom;
    private View mCardInner;
    private View mCardLargeInner;
    private boolean mExpandState;
    private LauncherCard mLauncherCard;
    private Resources mResources;

    public CardFrameViewHolder(@NonNull View itemView, RecyclerView recyclerView, View cardInner) {
        super(itemView);
        mResources = itemView.getResources();
        this.mRecyclerView = recyclerView;
        mTvCardName = itemView.findViewById(R.id.tvCardName);
        mIvCardZoom = itemView.findViewById(R.id.ivCardZoom);
        mCardInner = cardInner;
    }

    public void bind(int position, LauncherCard cardEntity) {
        setTitle(cardEntity.getName());
        mLauncherCard = cardEntity;
        if (mOnClickListener == null) {
            mOnClickListener = createListener(cardEntity);
        }

        mIvCardZoom.setOnClickListener(mOnClickListener);
        EasyLog.d(TAG, "bind position:" + position + ", " + cardEntity.getName());
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int fingerCount = mPointsDown.size();
                EasyLog.d(TAG, "onLongClick " + cardEntity.getName() + ",  fingerCount:" + fingerCount);
                if (fingerCount > 1) {
                    return false;
                }
                ActivityBus.newInstance(itemView.getContext())
                        .withClass(CardEditorActivity.class)
                        .go();
                return false;
            }
        });
        itemView.setOnTouchListener(mOnTouchListener);
    }

    private void setTitle(String name) {
        mTvCardName.setText(name);


        if (mCardInner instanceof ICardStyleChange) {
            boolean hideDefaultTitle = ((ICardStyleChange) mCardInner).hideDefaultTitle();
            if (hideDefaultTitle) {
                mTvCardName.setVisibility(View.GONE);
            } else {
                mTvCardName.setVisibility(View.VISIBLE);
            }
        }
    }

    private View.OnClickListener mOnClickListener;

    private View.OnClickListener createListener(LauncherCard cardEntity) {
        return new StableOnClickListener(MIN_CLICK_INTERVAL, new Consumer<View>() {
            @Override
            public void accept(View view) {
                EasyLog.d(TAG, "click expand or collapse view");
                if (view == mIvCardZoom) {
                    changeExpandState();
                }
            }
        });
    }

    private void changeExpandState() {
        if (mExpandState) {
            collapse();
            dealCollapseScroll(getAdapterPosition());
        } else {
            expand();
            CardFrameViewHolder bigCard = ExpandStateManager.getInstance().getBigCard();
            if (bigCard != null && bigCard != this && bigCard.mExpandState) {
                bigCard.changeExpandState();
            }
            ExpandStateManager.getInstance().setBigCard(this);
        }
        mExpandState = !mExpandState;
        ExpandStateManager.getInstance().setExpand(mExpandState);
    }

    private boolean scrolledInExpand = false;

    private void dealExpandScroll(int position) {
        // 3个卡片的位置: 0, 605, 1210
        if (itemView.getX() > 700) {
            // 位于第三个位置时, recyclerView需要左移
            EasyLog.d(TAG, "dealExpandScroll x: " + itemView.getX() + ", position:" + position);
            mRecyclerView.scrollToPosition(position);
            LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            if (layoutManager != null) {
                scrolledInExpand = true;
                if (position > 1) {
                    EasyLog.d(TAG, "dealExpandScroll ,  需要向左移动");
                    layoutManager.scrollToPositionWithOffset(position - 1, 0);
                }
            }
        }
    }

    private boolean isCardInLeftSide() {
        // 3个卡片的位置: 0, 605, 1210
        return itemView.getX() < 600;
    }

    private void dealCollapseScroll(int position) {
        if (scrolledInExpand) {
            scrolledInExpand = false;
            LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            if (layoutManager != null) {
                if (position > 1) {
                    layoutManager.scrollToPositionWithOffset(position - 2, 0);
                }
            }
        }
    }

    private void expand() {
//        itemView.setForeground(mResources.getDrawable(R.drawable.card_bg_large));
        itemView.setBackgroundResource(R.drawable.card_bg_large);
        mIvCardZoom.setImageResource(R.drawable.card_icon_collapse);

        if (mCardInner instanceof ICardStyleChange) {
            ((ICardStyleChange) mCardInner).expand();
        }
        mTvCardName.setTextColor(getColor(R.color.card_blue_lv1));
        runExpandAnimation();
    }

    private void runExpandAnimation() {
        int largeWidth = (int) getDimension(R.dimen.card_width_large);
        int smallWidth = (int) getDimension(R.dimen.card_width);
        ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
        ValueAnimator valueAnimator = ObjectAnimator.ofInt(smallWidth, largeWidth).setDuration(EXPAND_ANIM_DURATION);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                layoutParams.width = value;
                itemView.setLayoutParams(layoutParams);
//                EasyLog.d(TAG, "AnimatedValue:" + value);
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
//                EventBus.getDefault().post(createExpandEvent(cardEntity));
                dealExpandScroll(getAdapterPosition());
            }
        });
        valueAnimator.start();
    }

    private Events.SwipeEvent createSwipeEvent(LauncherCard cardEntity) {
        Events.SwipeEvent swipeEvent = new Events.SwipeEvent();
        swipeEvent.mBigLauncherCard = cardEntity;
        swipeEvent.mBigInLeftSide = isCardInLeftSide();
        return swipeEvent;
    }

    private void runCollapseAnimation() {
        int largeWidth = (int) getDimension(R.dimen.card_width_large);
        int smallWidth = (int) getDimension(R.dimen.card_width);
        ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
        ValueAnimator valueAnimator = ObjectAnimator.ofInt(largeWidth, smallWidth).setDuration(EXPAND_ANIM_DURATION);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                layoutParams.width = value;
                itemView.setLayoutParams(layoutParams);
//                EasyLog.d(TAG, "AnimatedValue:" + value);
            }
        });
        valueAnimator.start();
    }

    private void collapse() {
        itemView.setBackgroundResource(R.drawable.card_bg_small);
        mIvCardZoom.setImageResource(R.drawable.card_icon_expand);

        if (mCardInner instanceof ICardStyleChange) {
            ((ICardStyleChange) mCardInner).collapse();
        }
        mTvCardName.setTextColor(getColor(R.color.white));
        runCollapseAnimation();
    }


    private float getDimension(int dimensionId) {
        Context applicationContext = itemView.getContext().getApplicationContext();
        return applicationContext.getResources().getDimension(dimensionId);
    }

    private int getColor(int colorId) {
        Context applicationContext = itemView.getContext().getApplicationContext();
        return applicationContext.getResources().getColor(colorId);
    }


    private static final int FINGER_NUM_TRIGGER_SCREEN_SHOT = 3;
    private boolean mShouldTriggerScreenShot = true;
    private boolean mFinalShouldTriggerScreenShot = false;
    //    private int mTouchSlop = ViewConfiguration.get().getScaledTouchSlop();
    // 用于记录三指截图时最后一个手指按下的时间以判断手指按下的时间间隔是否超时
    private long mLatestPointerPressTime = 0;
    // 根据业务需要定义超时时间
    private long mTimeout = 1000;

    private Map<Integer, Float[][]> mPointsDown = new HashMap<>();
    private Map<Integer, Float[][]> mPointsUp = new HashMap<>();

    private float mPointsDistance = 0;
    private float downX, downY;

    View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (!mExpandState) {
                EasyLog.w(TAG, "onTouch , card is in collapse");
                return false;
            }
            Float[][] pointerCoordinate = new Float[1][2];
            Float[][] pointerDown;
            final int pointerIndex = event.getActionIndex();
            int pointerId = event.getPointerId(pointerIndex);
            // 获得该指针（手指）的x/y值
            final float pointX = event.getX(pointerIndex);
            final float pointY = event.getY(pointerIndex);
            pointerCoordinate[0][0] = pointX;
            pointerCoordinate[0][1] = pointY;

            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    EasyLog.i(TAG, "ACTION_DOWN");
                    mPointsDown.put(pointerId, pointerCoordinate);
                    downX = pointX;
                    downY = pointY;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    EasyLog.i(TAG, "ACTION_POINTER_DOWN");
                    mLatestPointerPressTime = System.currentTimeMillis();
                    // 副指针到来时，将副指针的数据记录下来，方便后续使用
                    mPointsDown.put(pointerId, pointerCoordinate);
                    break;

                case MotionEvent.ACTION_MOVE:
                    EasyLog.d(TAG, "ACTION_MOVE . mShouldTriggerScreenShot:" + mShouldTriggerScreenShot + ",  pointX:" + pointX);
                    if (event.getPointerCount() == FINGER_NUM_TRIGGER_SCREEN_SHOT) {
                        mPointsDistance = Math.abs(pointX - downX) + Math.abs(pointY - downY);
//                        for (int i = 0; i < event.getPointerCount(); i++) {
//                            pointerId = event.getPointerId(i);
//                            pointerDown = mPointsDown.get(pointerId);
////                            EasyLog.d(TAG, "point:" + pointerId + " , " + Arrays.deepToString(pointerDown));
//                            // move事件需要检测当前的滑动是否符合触发截屏的条件，例如你们业务是否允许反方向滑动/横滑等条件下触发三至截屏等
//                            mFinalShouldTriggerScreenShot = true;
//                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    mPointsUp.put(pointerId, pointerCoordinate);
                    // 前面的条件都满足触发截屏的条件时，还需要根据mPointersDown和mPointersUp这两个数据来计算每个手指滑动的距离是否满足TouchSlop等条件
                    long flashTime = System.currentTimeMillis() - mLatestPointerPressTime;
                    EasyLog.d(TAG, "UPUPUP distance:" + mPointsDistance + " , flashTime: " + flashTime + ", finger count:" + mPointsDown.size());
                    if (mPointsDistance > 100 && flashTime < 1000 && mPointsDown.size() == FINGER_NUM_TRIGGER_SCREEN_SHOT) {
                        doSwipe();
                    }
                    resetTouchEvent();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    resetTouchEvent();
                    break;

            }
            return false;
        }
    };

    private void resetTouchEvent() {
        mPointsDown.clear();
        mPointsUp.clear();
        mShouldTriggerScreenShot = true;
        mFinalShouldTriggerScreenShot = false;
        mPointsDistance = 0;
        downX = 0;
        downY = 0;
    }

    private void doSwipe() {
        EasyLog.d(TAG, "do Swipe card.");
        EventBus.getDefault().post(createSwipeEvent(mLauncherCard));
    }

    public LauncherCard getLauncherCard() {
        return mLauncherCard;
    }
}

