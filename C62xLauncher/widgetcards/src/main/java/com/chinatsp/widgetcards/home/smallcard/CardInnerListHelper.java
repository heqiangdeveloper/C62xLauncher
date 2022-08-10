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

    public void showViewPager() {
        if (mRecyclerView == null) {
            return;
        }
        mRecyclerView.scrollToPosition(0);
        mRecyclerView.setVisibility(View.VISIBLE);
        smallCardsAdapter.setCardEntityList(getSmallCardList());
    }


    public void hideViewPager() {
        if (mRecyclerView == null) {
            return;
        }
        mRecyclerView.setVisibility(View.GONE);
    }

    public List<LauncherCard> getSmallCardList() {
        CardManager cardManager = CardManager.getInstance();
        List<LauncherCard> homeList = cardManager.getHomeList();
        List<LauncherCard> result = new LinkedList<>();
        for (LauncherCard launcherCard : homeList) {
            if (launcherCard != null) {
//                CardFrameViewHolder bigCard = ExpandStateManager.getInstance().getBigCard();
                result.add(launcherCard);
            }
        }
        return result;
    }
}
