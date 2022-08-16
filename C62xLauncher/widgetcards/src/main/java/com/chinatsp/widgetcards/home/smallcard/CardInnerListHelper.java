package com.chinatsp.widgetcards.home.smallcard;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.widgetcards.home.CardFrameViewHolder;
import com.chinatsp.widgetcards.home.ExpandStateManager;
import com.chinatsp.widgetcards.manager.CardManager;

import java.util.LinkedList;
import java.util.List;

import card.base.LauncherCard;

public class CardInnerListHelper {
    public RecyclerView mRecyclerView;
    private PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
    private SmallCardsAdapter smallCardsAdapter;

    public CardInnerListHelper(RecyclerView recyclerView) {
        if (recyclerView == null) {
            return;
        }
        mRecyclerView = recyclerView;
        smallCardsAdapter = new SmallCardsAdapter(mRecyclerView.getContext(), mRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mRecyclerView.getContext());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(smallCardsAdapter);
        pagerSnapHelper.attachToRecyclerView(mRecyclerView);
    }

    public void showInnerList(int currentSmallCardPosition, int bigCardPosition) {
        if (mRecyclerView == null) {
            return;
        }
        mRecyclerView.scrollToPosition(0);
        mRecyclerView.setVisibility(View.VISIBLE);
        List<LauncherCard> smallCardList = getSmallCardList(bigCardPosition);
        smallCardsAdapter.setCardEntityList(smallCardList);
        mRecyclerView.scrollToPosition(currentSmallCardPosition);
    }


    public void hideInnerList() {
        if (mRecyclerView == null) {
            return;
        }
        mRecyclerView.setVisibility(View.GONE);
    }

    // 需要剔除大卡,  以及将列表位置定位到当前小卡
    public List<LauncherCard> getSmallCardList(int bigCardPosition) {
        CardManager cardManager = CardManager.getInstance();
        List<LauncherCard> homeList = cardManager.getHomeList();
        List<LauncherCard> result = new LinkedList<>();
        int anchorIndex = 0;
        for (int i = 0; i < homeList.size(); i++) {
            LauncherCard launcherCard = homeList.get(i);
            if (i != bigCardPosition) {
                result.add(launcherCard);
            }
        }
        return result;
    }

}
