package com.chinatsp.widgetcards.home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.drawer.search.LauncherSearchActivity;
import com.chinatsp.widgetcards.CardIntentService;
import com.chinatsp.widgetcards.R;
import com.chinatsp.widgetcards.editor.ui.CardEditorActivity;
import com.chinatsp.widgetcards.home.smallcard.CardInnerListHelper;
import com.chinatsp.widgetcards.home.smallcard.OnExpandCardInCard;
import com.chinatsp.widgetcards.home.smallcard2.SmallCardRcvManager;
import com.chinatsp.widgetcards.manager.CardManager;
import com.chinatsp.widgetcards.manager.CardNameRes;
import com.chinatsp.widgetcards.manager.Events;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import card.service.ICardStyleChange;
import card.base.LauncherCard;
import launcher.base.routine.ActivityBus;
import launcher.base.utils.EasyLog;

public class CardFrameViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "CardFrameViewHolder";
    private static final int MIN_CLICK_INTERVAL = 700; // ms
    private final int EXPAND_ANIM_DURATION = 200;
    private RecyclerView mRecyclerView;
    private TextView mTvCardName;
    private ImageView mIvCardZoom;
    private View ivCardTopSpace;
    private View mCardInner;
    private boolean mExpandState;
    private LauncherCard mLauncherCard;
    private Resources mResources;
    private boolean mHideTitle = false;

    public CardFrameViewHolder(@NonNull View itemView, RecyclerView recyclerView, View cardInner) {
        super(itemView);
        mResources = itemView.getResources();
        this.mRecyclerView = recyclerView;
        mTvCardName = itemView.findViewById(R.id.tvCardName);
        mIvCardZoom = itemView.findViewById(R.id.ivCardZoom);
        ivCardTopSpace = itemView.findViewById(R.id.ivCardTopSpace);
        mCardInner = cardInner;
    }

    public void bind(int position, LauncherCard cardEntity) {
        mLauncherCard = cardEntity;
        mHideTitle = checkNeedHideTitle();

        resetExpandIcon(cardEntity);
        setTitle(cardEntity.getType());
        if (mOnClickListener == null) {
            mOnClickListener = createListener(cardEntity);
        }
        EasyLog.d(TAG, "bind position:" + position + ", " + cardEntity.getName());
        itemView.setOnTouchListener(mOnTouchListener);
        ivCardTopSpace.setOnClickListener(mOnClickListener);
        itemView.setOnClickListener(v -> {
            EasyLog.d(TAG, "OnClickListener Launcher App......" + mLauncherCard.getName());
            AppLauncherUtil.start(v.getContext(), mLauncherCard.getType());
        });
        if (mExpandState) {
            itemView.setBackgroundResource(R.drawable.card_bg_large);
        } else {
            itemView.setBackgroundResource(R.drawable.card_bg_small);
        }
    }

    View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            int fingerCount = mPointsDown.size();
            EasyLog.d(TAG, "onLongClick " + mLauncherCard.getName() + ",  fingerCount:" + fingerCount + " , itemView :" + itemView);
            if (fingerCount > 1) {
                return false;
            }
            ActivityBus.newInstance(itemView.getContext())
                    .withClass(CardEditorActivity.class)
                    .go();
            return false;
        }
    };

    private boolean checkNeedHideTitle() {
        boolean hideDefault = false;
        if (mCardInner instanceof ICardStyleChange) {
            hideDefault = ((ICardStyleChange) mCardInner).hideDefaultTitle();
        }
        return hideDefault;
    }

    private void resetExpandIcon(LauncherCard cardEntity) {
        if (cardEntity.isCanExpand()) {
            mIvCardZoom.setVisibility(View.VISIBLE);
        } else {
            mIvCardZoom.setVisibility(View.GONE);
        }
    }

    private void setTitle(int type) {
        mTvCardName.setText(CardNameRes.getStringRes(type));
        if (mHideTitle) {
            mTvCardName.setVisibility(View.GONE);
        } else {
            mTvCardName.setVisibility(View.VISIBLE);
        }
    }

    private View.OnClickListener mOnClickListener;

    private static long mLastValidClickExpandTime;
    private static long mLastClickExpandTime;

    private View.OnClickListener createListener(LauncherCard cardEntity) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long now = System.currentTimeMillis();
                long diffToValid = now - mLastValidClickExpandTime;
                long diffToInvalid = now - mLastClickExpandTime;
                mLastClickExpandTime = now;
                if (diffToValid < MIN_CLICK_INTERVAL && diffToInvalid < MIN_CLICK_INTERVAL) {
                    EasyLog.w(TAG, "click expand or collapse view fail: touch too fast."
                            + cardEntity.getName() + " , diff:" + diffToValid + " , diffToInvalid:" + diffToInvalid);
                    return;
                }
                mLastValidClickExpandTime = now;
                EasyLog.d(TAG, "click expand or collapse view :" + cardEntity.getName()
                        + " , diff:" + diffToValid + " , diffToInvalid:" + diffToInvalid);
                if (!cardEntity.isCanExpand()) {
                    return;
                }
                if (view == mIvCardZoom || view == mTvCardName || view == ivCardTopSpace) {
                    ExpandStateManager.getInstance().clickExpandButton(cardEntity, isCardInLeftSide());

                }
            }
        };
    }

    public void expand() {
        mExpandState = true;
        showContentView();
        expandLayout();
    }

    public void collapse() {
        mExpandState = false;
        collapseLayout();
    }


    public void runDelay(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }, EXPAND_ANIM_DURATION);
    }

    public void showContentView() {
        EasyLog.d(TAG, "showContentView : " + mLauncherCard.getName() + " expand :" + mExpandState);
        if (!mHideTitle) {
            mTvCardName.setVisibility(View.VISIBLE);
        }
        resetExpandIcon(mLauncherCard);
        itemView.setVisibility(View.VISIBLE);
        if (mExpandState) {
            itemView.setBackgroundResource(R.drawable.card_bg_large);
        } else {
            itemView.setBackgroundResource(R.drawable.card_bg_small);
        }
    }

    public void hideContentView() {
        EasyLog.d(TAG, "hideContentView : " + mLauncherCard.getName() + " expand :" + mExpandState);
        itemView.setVisibility(View.INVISIBLE);
    }

    // 表示在变成大卡之前, 位于屏幕最右侧
    private boolean scrolledInExpand = false;

    public void dealExpandScroll() {
        HomeCardsAdapter adapter = (HomeCardsAdapter) mRecyclerView.getAdapter();
        if (adapter == null) {
            return;
        }
        int position = adapter.getPositionByCard(mLauncherCard);
        float x = itemView.getX();
        EasyLog.d(TAG, "dealExpandScroll , itemView X: " + x);
        // 3个卡片的位置: 0, 605, 1210
        if (x > 700) {
            // 位于第三个位置时, recyclerView需要左移1个位置
            EasyLog.d(TAG, "dealExpandScroll x: " + x + ", position:" + position);
            LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            if (layoutManager != null) {
                scrolledInExpand = true;
                if (position > 2) {
                    EasyLog.d(TAG, "dealExpandScroll , 位于屏幕最右侧时,  列表头需要移动到其左侧1位");
                    CardScrollUtil.scroll(layoutManager, position - 1);
                }
            }
        }
    }

    public boolean isCardInLeftSide() {
        // 3个卡片的位置: 0, 605, 1210
        return itemView.getX() < 600;
    }


    private void expandLayout() {
        EasyLog.d(TAG, "real expand: " + mLauncherCard.getName());
        itemView.setBackgroundResource(R.drawable.card_bg_large);
        mIvCardZoom.setImageResource(R.drawable.card_icon_collapse);

        if (mCardInner instanceof ICardStyleChange) {
            ((ICardStyleChange) mCardInner).expand();
        }
        mTvCardName.setTextColor(getColor(R.color.card_title_expand));
        runExpandAnimation();
    }

    private void runExpandAnimation() {
        EasyLog.d(TAG, "runExpandAnimation: " + mLauncherCard.getName());

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
//                EasyLog.d(TAG, mLauncherCard.getName() + " ,runExpandAnimation  AnimatedValue:" + value);
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
        EasyLog.d(TAG, "runCollapseAnimation: " + mLauncherCard.getName());

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
//                EasyLog.d(TAG, mLauncherCard.getName() + " ,runCollapseAnimation  AnimatedValue:" + value);
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                EasyLog.d(TAG, "CollapseAnimation end: " + mLauncherCard.getName());
            }
        });
        valueAnimator.start();
    }

    private void collapseLayout() {
        EasyLog.d(TAG, "collapseLayout card: " + mLauncherCard.getName());
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
    private float moveX, moveY;
    private boolean mReadyLongPress = false;
    private long mDownTimestamp;

    private boolean mLongPressTriggered = false;

    View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (!mExpandState) {
                EasyLog.w(TAG, "onTouch , card is in collapse");
//                return false;
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
                    mReadyLongPress = true;
                    mDownTimestamp = System.currentTimeMillis();
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    EasyLog.i(TAG, "ACTION_POINTER_DOWN");
                    mLatestPointerPressTime = System.currentTimeMillis();
                    // 副指针到来时，将副指针的数据记录下来，方便后续使用
                    mPointsDown.put(pointerId, pointerCoordinate);
                    mReadyLongPress = false;
                    break;

                case MotionEvent.ACTION_MOVE:
                    EasyLog.d(TAG, "ACTION_MOVE . mShouldTriggerScreenShot:" + mShouldTriggerScreenShot + ",  pointX:" + pointX + ". Point count:" + event.getPointerCount());
                    if (event.getPointerCount() == 1) {
                        moveX = event.getX();
                        moveY = event.getY();
                    }

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
                    if (event.getPointerCount() == 1) {
                        moveX = event.getX();
                        moveY = event.getY();
                        float deltaY = moveY - downY;
                        float deltaX = moveX - downX;
                        EasyLog.d(TAG, "check up deltaY:" + deltaY + " , deltaX:" + deltaX + " , mReadLongPress:" + mReadyLongPress);
//                        boolean checkAndGoSearchActivity = checkAndGoSearchActivity(deltaY, deltaX);
//                        if (mReadyLongPress && !checkAndGoSearchActivity) {
//                            checkAndPerformLongPress(deltaY, deltaX);
//                        }
                        checkAndPerformClick(deltaY, deltaX);
                        mReadyLongPress = false;
                        return true;
                    }
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

    private final int LONG_PRESS_TIME = 600;

    private void checkAndPerformClick(float deltaY, float deltaX) {
        if (checkTouchMoveTooFar(deltaY, deltaX)) {
            long now = System.currentTimeMillis();
            long touchTime = now - mDownTimestamp;
            if (touchTime < LONG_PRESS_TIME) {
                // 短按
                EasyLog.d(TAG, "checkAndPerformClick Launcher App......" + mLauncherCard.getName());
                AppLauncherUtil.start(itemView.getContext(), mLauncherCard.getType());
            }
        }
    }

    private boolean checkTouchMoveTooFar(float deltaY, float deltaX) {
        return Math.abs(deltaY) < 5 && Math.abs(deltaX) < 5;
    }

    private void resetTouchEvent() {
        mPointsDown.clear();
        mPointsUp.clear();
        mShouldTriggerScreenShot = true;
        mFinalShouldTriggerScreenShot = false;
        mPointsDistance = 0;
        downX = 0;
        downY = 0;
        mReadyLongPress = false;
    }

    private void doSwipe() {
        EasyLog.d(TAG, "do Swipe card.");
        EventBus.getDefault().post(createSwipeEvent(mLauncherCard));
    }

    public LauncherCard getLauncherCard() {
        return mLauncherCard;
    }
}

