package com.chinatsp.widgetcards.home;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chinatsp.drawer.adapter.SearchAdapter;
import com.chinatsp.widgetcards.home.smallcard2.HomeCardRcvManager;
import com.chinatsp.widgetcards.home.smallcard2.SmallCardRcvManager;
import com.chinatsp.widgetcards.manager.CardManager;

import java.util.List;

import card.base.LauncherCard;
import launcher.base.utils.EasyLog;

public class ExpandStateManager {
    private static final String TAG = "ExpandStateManager";

    private ExpandStateManager() {
    }

    private static final ExpandStateManager instance = new ExpandStateManager();

    public static ExpandStateManager getInstance() {
        return instance;
    }

    private final MutableLiveData<Boolean> mExpandStateLiveData = new MutableLiveData<>(false);


    public void setExpand(boolean expandState) {
        mExpandStateLiveData.postValue(expandState);
    }

    /**
     * 获取全局的卡片状态
     *
     * @return false: 没有卡片变成大卡.  true: 有一个卡片变成大卡
     */
    public boolean getExpandState() {
        Boolean value = mExpandStateLiveData.getValue();
        return value != null && value;
    }

    public void register(LifecycleOwner lifecycleOwner, Observer<? super Boolean> observer) {
        mExpandStateLiveData.observe(lifecycleOwner, observer);
    }

    public void unregister(Observer<? super Boolean> observer) {
        mExpandStateLiveData.removeObserver(observer);
    }

    private LauncherCard mBigCard;

    public void setBigCard(LauncherCard bigCard) {
        mBigCard = bigCard;
    }

    public void setAnchorSmallCard(LauncherCard anchorSmallCard) {
        mAnchorSmallCard = anchorSmallCard;
    }

    public LauncherCard getAnchorSmallCard() {
        return mAnchorSmallCard;
    }

    // 锚点小卡. 表示在小卡-大卡模式中, 被选定的那张小卡
    private LauncherCard mAnchorSmallCard;

    public void clickExpandButton(LauncherCard card, boolean cardInLeftSide) {
        if (card == null) {
            return;
        }
        boolean isExpandState = getExpandState();
        EasyLog.i(TAG, "clickExpandButton , current expand status: " + isExpandState);
        if (!isExpandState) {
            // 小卡状态, 这里要变大
            expand(card, cardInLeftSide);
            chooseAnotherSmallCard(card, cardInLeftSide);
            dealScroll(card);
        } else {
            if (mBigCard == card) {
                // 说明此卡大卡状态, 这里要变小
                collapse(card, cardInLeftSide);
                resetAnchorSmallCard();
                setBigCard(null);
                setAnchorSmallCard(null);
                setExpand(false);
                dealScroll(card);
            } else {
                // 此卡处于小卡状态, 但有其它卡处于大卡状态. 所以这里的操作是交换大小卡
                expandOnExchange(card, cardInLeftSide);
                setExpand(true);
            }
        }
    }

    private void expand(LauncherCard card, boolean cardInLeftSide) {
        EasyLog.i(TAG, "expand " + card.getName());
        HomeCardRcvManager homeCardRcvManager = HomeCardRcvManager.getInstance();
        CardFrameViewHolder viewHolder = homeCardRcvManager.findViewHoldByCard(card);
        if (viewHolder != null) {
            viewHolder.expand();
        }
        setBigCard(card);
        setExpand(true);

    }


    private void collapse(LauncherCard card, boolean cardInLeftSide) {
        EasyLog.i(TAG, "collapse " + card.getName());
        HomeCardRcvManager homeCardRcvManager = HomeCardRcvManager.getInstance();
        CardFrameViewHolder viewHolder = homeCardRcvManager.findViewHoldByCard(card);
        if (viewHolder != null) {
            viewHolder.collapse();
        }
    }

    private void dealScroll(LauncherCard bigCard) {
        HomeCardRcvManager homeCardRcvManager = HomeCardRcvManager.getInstance();
        CardFrameViewHolder viewHolder = homeCardRcvManager.findViewHoldByCard(bigCard);
        if (viewHolder == null) {
            return;
        }
        viewHolder.runDelay(new Runnable() {
            @Override
            public void run() {
                EasyLog.i(TAG, "dealScroll : " + bigCard.getName());
                viewHolder.dealExpandScroll();
            }
        });
    }

    /**
     * 先将小卡变大,  再将大卡变小
     *
     * @param smallCard      小卡
     * @param cardInLeftSide smallCard是否位于屏幕最左侧
     */
    private void expandOnExchange(LauncherCard smallCard, boolean cardInLeftSide) {
        EasyLog.d(TAG, "expandOnExchange,  " + mAnchorSmallCard.getName() + " , " + mBigCard.getName() + " <-> " + smallCard.getName() + " , cardInLeftSide:" + cardInLeftSide);
        LauncherCard bigCard = mBigCard;
        CardFrameViewHolder viewHolder = HomeCardRcvManager.getInstance().findViewHoldByCard(smallCard);
        if (mAnchorSmallCard == smallCard) {
            // 判断在中卡-小卡模式中, 小卡是否被滑动到了另一个位置
            // 如果小卡没有滑动位置, 可直接将小卡变大
            if (viewHolder != null) {
                viewHolder.showContentView();
            }
            collapse(bigCard, cardInLeftSide);
            expand(smallCard, cardInLeftSide);
            chooseAnotherSmallCard(smallCard, cardInLeftSide);
            dealScroll(smallCard);
        } else {
            // 如果小卡已经滑动了, 则变大的是smallCard对应的卡片, 原 smallCardPositionInHomeList 不用发生变化, 但需要重新显示内容
            CardFrameViewHolder originSmallCardViewHolder = HomeCardRcvManager.getInstance().findViewHoldByCard(mAnchorSmallCard);
            if (originSmallCardViewHolder != null) {
                originSmallCardViewHolder.showContentView();
            }

            if (viewHolder != null) {
                EasyLog.d(TAG, "expandOnExchange  已创建View: " + smallCard.getName());
                exchangeCard(smallCard, bigCard, cardInLeftSide);
            } else {
                // 找不到viewHolder, 说明还没有创建,  这是一张新的卡片..
                int cardIndex = CardManager.getInstance().getHomeList().indexOf(smallCard);
                if (cardIndex < 0) {
                    return;
                }
                EasyLog.w(TAG, "expandOnExchange  未创建View: " + smallCard.getName() + " , scroll cardIndex: " + cardIndex);

                HomeCardRcvManager.getInstance().scrollTo(cardIndex);
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exchangeCard(smallCard, bigCard, cardInLeftSide);
                    }
                }, 500);
            }
        }
    }

    private void exchangeCard(LauncherCard smallCard, LauncherCard bigCard, boolean cardInLeftSide) {
        collapse(bigCard, cardInLeftSide);
        expand(smallCard, cardInLeftSide);
        chooseAnotherSmallCard(smallCard, cardInLeftSide);
        List<LauncherCard> homeList = CardManager.getInstance().getHomeList();
        int cardIndex = homeList.indexOf(smallCard);
        if (cardIndex < 0) {
            return;
        }
        EasyLog.d(TAG, "expandOnExchange cardIndex: " + cardIndex);
        int cardCounts = homeList.size();
        boolean includeDrawer = true;
        int scrollTargetPosition = 1;
        boolean realCardInLeft = cardInLeftSide;
        if (cardIndex == 0) {
            // 如果第一个卡片就是大卡, 那么滑动目标位置只能是1, 大卡只能位于左侧
            scrollTargetPosition = includeDrawer ? 1 : 0;
            realCardInLeft = true;
        } else if (cardIndex == cardCounts - 1) {
            // 如果最后一个卡片是大卡, 那么大卡只能位于右侧,
            scrollTargetPosition = includeDrawer ? (cardIndex + 1) - 1 : cardIndex - 1;
            realCardInLeft = false;
        } else {
            // 如果大卡不是首尾两个,  需要根据cardInLeftSide来计算目标位置
            if (cardInLeftSide) {
                // 大卡位于左侧
                scrollTargetPosition = includeDrawer ? cardIndex + 1 : cardIndex;
            } else {
                // 大卡位于右侧
                scrollTargetPosition = includeDrawer ? (cardIndex + 1) - 1 : cardIndex - 1;
            }
        }

        // 如果目标位置是第5个, 且大卡在右边, 那么大卡即是最后一个位置,  需要单独处理滑动
        if (scrollTargetPosition == cardCounts - 1 && !realCardInLeft) {
            dealScroll(smallCard);
        }
        EasyLog.w(TAG, "expandOnExchange  scroll scrollTargetPosition: " + scrollTargetPosition);
        HomeCardRcvManager.getInstance().scrollTo(scrollTargetPosition);
    }

    /**
     * 重置锚点小卡. 同时需要隐藏备用小卡列表.
     */
    private void resetAnchorSmallCard() {
        SmallCardRcvManager.getInstance().hideSmallCardList();
        if (mAnchorSmallCard == null) {
            return;
        }
        CardFrameViewHolder viewHoldByCard
                = HomeCardRcvManager.getInstance().findViewHoldByCard(mAnchorSmallCard);
        if (viewHoldByCard != null) {
            viewHoldByCard.showContentView();
        }
    }

    private void chooseAnotherSmallCard(LauncherCard card, boolean cardInLeftSide) {
        int bigCardPos = HomeCardRcvManager.getInstance().findPositionByCard(card);
        EasyLog.d(TAG, "chooseAnotherSmallCard bigCardPos:" + bigCardPos);
        if (bigCardPos == 0) {
            EasyLog.w(TAG, "chooseAnotherSmallCard... sth had been wrong.");
        }
        int smallCardPosition;
        boolean realCardInLeftSide = cardInLeftSide;
        if (bigCardPos == CardManager.getInstance().getHomeList().size()) {
            // 如果要变大的卡的位置是6, 意味着它是最后一张卡, 它必然不位于屏幕左侧
            smallCardPosition = bigCardPos - 1;
            realCardInLeftSide = false;

        } else if (bigCardPos == 1) {
            // 如果要变大的卡的位置是1, 意味着它是第一张卡, 它必然位于屏幕左侧
            smallCardPosition = bigCardPos + 1;
            realCardInLeftSide = true;
        } else {
            if (cardInLeftSide) {
                // 当前卡在左边, 意味着右边是小卡.
                smallCardPosition = bigCardPos + 1;
            } else {
                // 当前卡在中间或右边, 所以左边是小卡
                smallCardPosition = bigCardPos - 1;
            }
        }

        CardFrameViewHolder smallCardViewHolder
                = HomeCardRcvManager.getInstance().findViewHoldByPosition(smallCardPosition);
        LauncherCard smallCard = HomeCardRcvManager.getInstance().findCardByPosition(smallCardPosition);
        setAnchorSmallCard(smallCard);
        EasyLog.d(TAG, "chooseAnotherSmallCard smallCard: " + smallCard.getName() + " , smallCardViewHolder:" + smallCardViewHolder);

        if (smallCardViewHolder != null) {
            boolean finalRealCardInLeftSide = realCardInLeftSide;
            smallCardViewHolder.runDelay(new Runnable() {
                @Override
                public void run() {
                    EasyLog.d(TAG, "chooseAnotherSmallCard smallCard: " + smallCard.getName());
                    SmallCardRcvManager.getInstance().showSmallCardList(card, smallCard, finalRealCardInLeftSide);
                    smallCardViewHolder.hideContentView();
                }
            });
        }
    }

}
