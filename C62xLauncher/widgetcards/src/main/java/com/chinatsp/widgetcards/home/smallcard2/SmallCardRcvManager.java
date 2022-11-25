package com.chinatsp.widgetcards.home.smallcard2;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.widgetcards.manager.CardManager;

import java.util.ArrayList;
import java.util.List;

import card.base.LauncherCard;
import launcher.base.utils.EasyLog;
import launcher.base.utils.collection.IndexCheck;

public class SmallCardRcvManager {

    public static SmallCardRcvManager getInstance() {
        return Holder.instance;
    }

    private static class Holder {
        private static SmallCardRcvManager instance = new SmallCardRcvManager();
    }

    private static String TAG = "SmallCardRcvManager";
    private RecyclerView mRecyclerView;
    private HomeCardRcvManager mHomeCardRcvManager;
    private LauncherCard mSmallCardPositionInHomeList;

    private SmallCardRcvManager() {
        mHomeCardRcvManager = HomeCardRcvManager.getInstance();
    }

    public LauncherCard getSmallCardPositionInHomeList() {
        return mSmallCardPositionInHomeList;
    }

    /**
     * 当小卡变大时, 需要记录屏幕中另一个小卡对象
     * @param smallCardPositionInHomeList
     */
    public void setSmallCardPositionInHomeList(LauncherCard smallCardPositionInHomeList) {
        mSmallCardPositionInHomeList = smallCardPositionInHomeList;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }


    /**
     * 指定大卡和小卡, 然后展示小卡列表.
     *
     * @param big            大卡
     * @param small          小卡
     * @param cardInLeftSide 当前大卡是否处于屏幕最左边. true: 大卡处于最左边, 那么小卡就处于右边.
     *                       false: 大卡处于中间或右边, 那么小卡就处于左边.
     *                       3个卡片的位置: 0, 605, 1210.
     */
    public void showSmallCardList(LauncherCard big, LauncherCard small, boolean cardInLeftSide) {
        EasyLog.d(TAG, "expand big: " + big.getName() + " , small:" + small.getName() + " , bigCardInLeftSide: " + cardInLeftSide);
        setLocation(cardInLeftSide);
        resetSmallCardList(big);
        scrollToSmallCard(small);
        setSmallCardPositionInHomeList(small);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * 将小卡列表滚动到小卡显示.
     *
     * @param small 小卡
     */
    private void scrollToSmallCard(LauncherCard small) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        if (layoutManager == null) {
            return;
        }
        SmallCardsAdapter2 adapter = (SmallCardsAdapter2) mRecyclerView.getAdapter();
        if (adapter == null) {
            return;
        }
        List<LauncherCard> smallCardList = adapter.getCardEntityList();
        int position = smallCardList.indexOf(small);
        if (IndexCheck.indexOutOfArray(smallCardList, position)) {
            return;
        }
        layoutManager.scrollToPositionWithOffset(position, 0);
    }

    /**
     * 重设小卡列表, 剔除大卡
     *
     * @param big 需要剔除的卡片
     */
    @SuppressLint("NotifyDataSetChanged")
    private void resetSmallCardList(LauncherCard big) {
        SmallCardsAdapter2 adapter = (SmallCardsAdapter2) mRecyclerView.getAdapter();
        if (adapter == null) {
            return;
        }
        List<LauncherCard> homeList = new ArrayList<>(CardManager.getInstance().getHomeList());
        homeList.remove(big);
        adapter.setCardEntityList(homeList);
        adapter.notifyDataSetChanged();
    }

    /**
     * 设置小卡列表的位置
     */
    private void setLocation(boolean cardInLeftSide) {
        int baseLeftMargin = 68;
        int[] xCoordinate = new int[]{0, 1210}; // 实际上小卡列表不可能显示在中间位置 605
        ConstraintLayout.LayoutParams layoutParams
                = (ConstraintLayout.LayoutParams) mRecyclerView.getLayoutParams();
        if (cardInLeftSide) {
            layoutParams.leftMargin = (baseLeftMargin + xCoordinate[1]);
        } else {
            layoutParams.leftMargin = (baseLeftMargin + xCoordinate[0]);
        }
        mRecyclerView.setLayoutParams(layoutParams);
    }

    public void hideSmallCardList() {
        EasyLog.d(TAG, "hideSmallCardList");
        mRecyclerView.setVisibility(View.INVISIBLE);
        mHomeCardRcvManager.showViewHolder(mSmallCardPositionInHomeList);
    }
}
