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

import com.chinatsp.widgetcards.R;
import com.chinatsp.widgetcards.editor.ui.CardEditorActivity;
import com.chinatsp.widgetcards.home.smallcard.CardInnerListHelper;
import com.chinatsp.widgetcards.home.smallcard.OnExpandCardInCard;
import com.chinatsp.widgetcards.manager.CardManager;
import com.chinatsp.widgetcards.manager.CardNameRes;
import com.chinatsp.widgetcards.manager.Events;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import card.service.ICardStyleChange;
import card.base.LauncherCard;
import launcher.base.routine.ActivityBus;
import launcher.base.utils.EasyLog;
import launcher.base.utils.flowcontrol.StableOnClickListener;
import launcher.base.utils.view.RecyclerViewUtil;

public class CardFrameViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "CardFrameViewHolder";
    private static final int MIN_CLICK_INTERVAL = 700; // ms
    private final int EXPAND_ANIM_DURATION = 200;
    private RecyclerView mRecyclerView;
    private TextView mTvCardName;
    private ImageView mIvCardZoom;
    private View mCardInner;
    private boolean mExpandState;
    private LauncherCard mLauncherCard;
    private CardInnerListHelper mCardInnerListHelper;
    private Resources mResources;
    private boolean mHideTitle = false;

    public CardFrameViewHolder(@NonNull View itemView, RecyclerView recyclerView, View cardInner) {
        super(itemView);
        mResources = itemView.getResources();
        this.mRecyclerView = recyclerView;
        mTvCardName = itemView.findViewById(R.id.tvCardName);
        mIvCardZoom = itemView.findViewById(R.id.ivCardZoom);
        mCardInner = cardInner;
        mCardInnerListHelper = new CardInnerListHelper(
                itemView.findViewById(R.id.viewPager2InSmallCard)
                , mOnSelectCardListener
                , mOnExpandCardInCard);
    }

    public void bind(int position, LauncherCard cardEntity) {
        mLauncherCard = cardEntity;
        mHideTitle = checkNeedHideTitle();

        resetExpandIcon(cardEntity);
        setTitle(cardEntity.getType());
        if (mOnClickListener == null) {
            mOnClickListener = createListener(cardEntity);
        }
        mIvCardZoom.setOnClickListener(mOnClickListener);
        mTvCardName.setOnClickListener(mOnClickListener);
        EasyLog.d(TAG, "bind position:" + position + ", " + cardEntity.getName());
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int fingerCount = mPointsDown.size();
                EasyLog.d(TAG, "onLongClick " + cardEntity.getName() + ",  fingerCount:" + fingerCount + " , itemView :" + itemView);
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

        int smallCardPosition = ExpandStateManager.getInstance().getSmallCardPosition();
        boolean isSmall = (smallCardPosition == position);
        EasyLog.i(TAG, "bind smallCardPosition:" + smallCardPosition + " , position:" + position);
        if (isSmall) {
            showSmallCardsInnerList();
        } else {
            hideSmallCardsInnerList();
        }
        // 如果当前卡片模式已经是小卡模式, 而当前卡片状态又为大卡,  则应当主动将此卡变小
        if (!ExpandStateManager.getInstance().getExpandState() && mExpandState) {
            changeExpandState(false);
        }
//        hideSmallCardsInnerList();
    }

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

    private CardInnerListHelper.OnSelectCardListener mOnSelectCardListener = new CardInnerListHelper.OnSelectCardListener() {
        @Override
        public void onSelectCard(LauncherCard card) {
            if (card == null) {
                return;
            }
            // 滑动监听,  可以判断当前小卡滑动到哪一个具体的卡片
        }
    };
    private OnExpandCardInCard mOnExpandCardInCard = new OnExpandCardInCard() {
        @Override
        public void onExpand(LauncherCard card) {
            // 如果已经有卡处于大卡状态, 那么本次操作实际上是大卡变小, 小卡变大
            if (card == null) {
                return;
            }
            if (mLauncherCard == card) {
                // 小卡内部没有滑动为别的卡片
                changeExpandState(true);
                ExpandStateManager.getInstance().setExpand(true);
            } else {
                // 小卡内部已经滑动到别的卡片
                hideSmallCardsInnerList();
                tryExpandInnerCard(card);
            }
        }
    };

    private void tryExpandInnerCard(LauncherCard card) {
        EasyLog.d(TAG, "tryExpandInnerCard 1: " + card.getName());
        HomeCardsAdapter adapter = (HomeCardsAdapter) mRecyclerView.getAdapter();
        if (adapter == null) {
            return;
        }
        int cardIndex = CardManager.getInstance().getHomeList().indexOf(card);
        if (cardIndex < 0) {
            return;
        }
        if (adapter.isIncludeDrawer()) {
            cardIndex++;
        }

        RecyclerView.ViewHolder viewHolder = adapter.find(card);
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        if (viewHolder == null) {
            // 说明此时viewHolder尚未创建, 需要先将recyclerView移动到后面
            // 当延迟后, 就能找到ViewHolder
            CardScrollUtil.scroll(layoutManager, cardIndex);
            EasyLog.d(TAG, "tryExpandInnerCard 2: " + card.getName() + " , " + viewHolder);
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    expandTargetViewHolder(adapter, card, layoutManager);
                }
            }, 500);
        } else {
            expandTargetViewHolder(adapter, card, layoutManager);
        }

    }

    private void expandTargetViewHolder(HomeCardsAdapter adapter, LauncherCard card, LinearLayoutManager layoutManager) {
        RecyclerView.ViewHolder viewHolder = adapter.find(card);
        if (viewHolder instanceof CardFrameViewHolder) {
            EasyLog.d(TAG, "tryExpandInnerCard 3: " + card.getName());

            ((CardFrameViewHolder) viewHolder).changeExpandState(true);
            List<LauncherCard> homeList = CardManager.getInstance().getHomeList();
            int position = homeList.indexOf(card);
            if (adapter.isIncludeDrawer()) {
                position++;
            }

            CardScrollUtil.scroll(layoutManager, position);
            ExpandStateManager.getInstance().setExpand(true);

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

    private View.OnClickListener createListener(LauncherCard cardEntity) {
        return new StableOnClickListener(MIN_CLICK_INTERVAL, new Consumer<View>() {
            @Override
            public void accept(View view) {
                EasyLog.d(TAG, "click expand or collapse view :" + cardEntity.getName());
                if (!cardEntity.isCanExpand()) {
                    return;
                }
                if (view == mIvCardZoom || view == mTvCardName) {
                    changeExpandState(false);
                }
                ExpandStateManager.getInstance().setExpand(mExpandState);
            }
        });
    }

    private void changeExpandState(boolean exchangeBigAndSmall) {
        EasyLog.i(TAG, "changeExpandState: " + mLauncherCard.getName() + " , is expand: " + mExpandState);
        if (mExpandState) {
            if (!exchangeBigAndSmall) {
                resetCardInnerList();
            }
            collapse();
            dealCollapseScroll(mLauncherCard, exchangeBigAndSmall);
        } else {
            hideSmallCardsInnerList();
            expand();
            collapseAnotherIfNeed();
            HomeCardsAdapter cardsAdapter = (HomeCardsAdapter) mRecyclerView.getAdapter();
            if (cardsAdapter != null) {
                ExpandStateManager.getInstance().setBigCardPosition(cardsAdapter.getPositionByCard(mLauncherCard));
            }
            chooseAnotherSmallCard();
        }
        mExpandState = !mExpandState;
        EasyLog.d(TAG, mLauncherCard.getName() + " expandCheck:" + mExpandState);

    }

    private void resetCardInnerList() {
        int smallCardPosition = ExpandStateManager.getInstance().getSmallCardPosition();
        HomeCardsAdapter adapter = (HomeCardsAdapter) mRecyclerView.getAdapter();
        if (adapter == null) {
            return;
        }
        if (adapter.isIncludeDrawer()) {
            smallCardPosition--;
        }
        if (smallCardPosition >= 0) {
            CardFrameViewHolder smallViewHolder = (CardFrameViewHolder) adapter.find(CardManager.getInstance().getHomeList().get(smallCardPosition));
            smallViewHolder.hideSmallCardsInnerList();
        }
    }

    private void collapseAnotherIfNeed() {
        int bigCardPosition = ExpandStateManager.getInstance().getBigCardPosition();
        HomeCardsAdapter adapter = (HomeCardsAdapter) mRecyclerView.getAdapter();
        if (adapter == null) {
            return;
        }
        int curPosition = adapter.getPositionByCard(mLauncherCard);
        EasyLog.d(TAG, "collapseAnotherIfNeed , bigCardPosition: " + bigCardPosition + " , curPos:" + curPosition);
        if (bigCardPosition > 0 && bigCardPosition != curPosition) {
//            CardFrameViewHolder bigCard = (CardFrameViewHolder) RecyclerViewUtil.findViewHold(mRecyclerView, bigCardPosition);
            CardFrameViewHolder bigCard = (CardFrameViewHolder) adapter.find(
                    CardManager.getInstance().getHomeList().get(adapter.isIncludeDrawer() ? bigCardPosition - 1 : bigCardPosition)
            );

            EasyLog.d(TAG, "collapseAnotherIfNeed , bigCard:" + bigCard);
            if (bigCard != null && bigCard != this && bigCard.mExpandState) {
                EasyLog.i(TAG, "collapseAnotherIfNeed:  " + bigCard.getLauncherCard());
                bigCard.changeExpandState(true);
            }
        }
    }

    private void chooseAnotherSmallCard() {
        HomeCardsAdapter adapter = (HomeCardsAdapter) mRecyclerView.getAdapter();
        if (adapter == null) {
            return;
        }
        int position = adapter.getPositionByCard(mLauncherCard);
        int smallCardPosition;
        if (isCardInLeftSide()) {
            // 当前卡在左边, 意味着右边是小卡.
            smallCardPosition = position + 1;
        } else {
            // 当前卡在中间或右边, 所以左边是小卡
            smallCardPosition = position - 1;
        }
        EasyLog.d(TAG, mLauncherCard.getName() + " chooseAnotherSmallCard, smallCardPosition: " + smallCardPosition + " , cur position:" + position);
        ExpandStateManager.getInstance().setSmallCardPosInExpandState(smallCardPosition);
        int firstCardIndex = adapter.isIncludeDrawer() ? 1 : 0;
        if (smallCardPosition >= firstCardIndex && smallCardPosition < adapter.getItemCount()) {
//            CardFrameViewHolder viewHold = (CardFrameViewHolder) RecyclerViewUtil.findViewHold(mRecyclerView, smallCardPosition);
            CardFrameViewHolder viewHold = (CardFrameViewHolder) adapter.findViewHolderByPosition(smallCardPosition);
            EasyLog.d(TAG, "chooseAnotherSmallCard: notifyItemChanged , smallCardPosition: " + smallCardPosition + "  , viewHold: " + viewHold);

            if (viewHold != null) {
                viewHold.showSmallCardsInnerList();
            }
        }
    }

    /**
     * 展示小卡内部的列表
     */
    private void showSmallCardsInnerList() {
        EasyLog.d(TAG, "showSmallCardsViewPager : " + mLauncherCard.getName());
        HomeCardsAdapter adapter = (HomeCardsAdapter) mRecyclerView.getAdapter();
        if (adapter == null) {
            return;
        }
        int currentBigCardPos = ExpandStateManager.getInstance().getBigCardPosition();
        int currentCardPos = adapter.getPositionByCard(mLauncherCard);
        boolean includeDrawer = adapter.isIncludeDrawer();
        if (includeDrawer) {
            currentCardPos = currentCardPos - 1;
            currentBigCardPos = currentBigCardPos - 1;
        }

        int finalCurrentCardPos = currentCardPos;
        int finalCurrentBigCardPos = currentBigCardPos;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 此处延迟,  是为了等待原大卡的缩小动画结束
                mCardInnerListHelper.showInnerList(finalCurrentCardPos, finalCurrentBigCardPos);
                itemView.setBackground(null);
                mCardInner.setVisibility(View.INVISIBLE);
                mTvCardName.setVisibility(View.INVISIBLE);
                mIvCardZoom.setVisibility(View.INVISIBLE);
            }
        }, EXPAND_ANIM_DURATION);
    }

    private void hideSmallCardsInnerList() {
        EasyLog.d(TAG, "hideSmallCardsViewPager : " + mLauncherCard.getName() + " expand :" + mExpandState);
        if (!mHideTitle) {
            mTvCardName.setVisibility(View.VISIBLE);
        }
        resetExpandIcon(mLauncherCard);
        mCardInnerListHelper.hideInnerList();
        mCardInner.setVisibility(View.VISIBLE);
        if (mExpandState) {
            itemView.setBackgroundResource(R.drawable.card_bg_large);
        } else {
            itemView.setBackgroundResource(R.drawable.card_bg_small);
        }
    }

    // 表示在变成大卡之前, 位于屏幕最右侧
    private boolean scrolledInExpand = false;

    private void dealExpandScroll() {
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

    private boolean isCardInLeftSide() {
        // 3个卡片的位置: 0, 605, 1210
        return itemView.getX() < 600;
    }

    private void dealCollapseScroll(LauncherCard card, boolean exchangeBigAndSmall) {
        if (scrolledInExpand) {
            scrolledInExpand = false;
            LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            HomeCardsAdapter adapter = (HomeCardsAdapter) mRecyclerView.getAdapter();
            if (layoutManager != null && adapter != null) {
                EasyLog.d(TAG, mLauncherCard.getName() + "  dealCollapseScroll :" + card.getName() + ", exchangeBigAndSmall:" + exchangeBigAndSmall);
                int position = adapter.getPositionByCard(card);
                if (position > 2) {
                    // scrolledInExpand:true 表示当前大卡原本是处于屏幕最右侧的小卡, 现在要缩小,  需要将其左侧两位的卡片移动到列表头
                    // 但是如果是小卡-大卡互相切换,  那么当大卡变小时,  需要将其左侧1位的开案移动到列表头
                    int needScrollIndex = exchangeBigAndSmall ? 0 : 1;
                    // 不滑动了.  碰到个问题搞不定:在屏幕最右侧变成大卡后, 再让另外一个小卡变成大卡...即进行大小卡互相切换时, 列表移动位置计算不准确.
                    // 所以干脆不滑动了.
//                    layoutManager.scrollToPositionWithOffset(needScrollIndex, 0);
                }
            }
        }
    }

    private void expand() {
        EasyLog.d(TAG, "real expand: " + mLauncherCard.getName());
//        EasyLog.printStack(TAG + " expand()");
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
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
//                EventBus.getDefault().post(createExpandEvent(cardEntity));
                EasyLog.d(TAG, "ExpandAnimation end: " + mLauncherCard.getName());
                dealExpandScroll();
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

