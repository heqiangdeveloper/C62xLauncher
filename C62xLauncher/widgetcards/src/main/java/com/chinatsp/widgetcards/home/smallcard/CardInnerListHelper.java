package com.chinatsp.widgetcards.home.smallcard;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.widgetcards.manager.CardManager;

import java.util.LinkedList;
import java.util.List;

import card.base.LauncherCard;
import launcher.base.utils.EasyLog;

public class CardInnerListHelper {
    private LinearLayoutManager mLayoutManager;
    public RecyclerView mRecyclerView;
    private PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
    private SmallCardsAdapter smallCardsAdapter;
    private OnSelectCardListener mOnSelectCardListener;

    public CardInnerListHelper(RecyclerView recyclerView, OnSelectCardListener onSelectCardListener, OnExpandCardInCard mOnExpandCardInCard) {
        mOnSelectCardListener = onSelectCardListener;
        if (recyclerView == null) {
            return;
        }
        mRecyclerView = recyclerView;
        smallCardsAdapter = new SmallCardsAdapter(mRecyclerView.getContext(), mRecyclerView, mOnExpandCardInCard);
        mLayoutManager = new LinearLayoutManager(mRecyclerView.getContext());
        mLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(smallCardsAdapter);
        pagerSnapHelper.attachToRecyclerView(mRecyclerView);
        mRecyclerView.addOnScrollListener(mOnScrollListener);
    }


    RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                try {
                    int currentPosition = ((RecyclerView.LayoutParams) recyclerView.getChildAt(0).getLayoutParams()).getViewAdapterPosition();
                    LauncherCard card = smallCardsAdapter.getItem(currentPosition);
                    EasyLog.d("CardInnerListHelper", "" + currentPosition + " , card:" + card.getName());
                    if (mOnSelectCardListener != null) {
                        mOnSelectCardListener.onSelectCard(card);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    };

    public void showInnerList(int currentSmallCardPosition, int bigCardPosition) {
        if (mRecyclerView == null) {
            return;
        }
        EasyLog.d("CardInnerListHelper", "showInnerList small:" + currentSmallCardPosition + " , big:" + bigCardPosition);
        mRecyclerView.setVisibility(View.VISIBLE);
        List<LauncherCard> smallCardList = getSmallCardList(bigCardPosition);
        smallCardsAdapter.setCardEntityList(smallCardList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(smallCardsAdapter);
        if (currentSmallCardPosition < bigCardPosition) {
            mRecyclerView.scrollToPosition(currentSmallCardPosition);
        } else {
            // 如果小卡位于大卡右侧, 则小卡在剩余小卡列表中的位置要-1
            mRecyclerView.scrollToPosition(currentSmallCardPosition - 1);
        }
    }


    public void hideInnerList() {
        if (mRecyclerView == null) {
            return;
        }
        smallCardsAdapter.clear();
        mRecyclerView.setLayoutManager(null);
        mRecyclerView.setAdapter(null);
        mRecyclerView.setVisibility(View.GONE);
    }

    // 需要剔除大卡,  以及将列表位置定位到当前小卡
    public List<LauncherCard> getSmallCardList(int bigCardPosition) {
        CardManager cardManager = CardManager.getInstance();
        List<LauncherCard> homeList = cardManager.getHomeList();
        List<LauncherCard> result = new LinkedList<>();
        for (int i = 0; i < homeList.size(); i++) {
            LauncherCard launcherCard = homeList.get(i);
            if (i != bigCardPosition) {
                result.add(launcherCard);
            }
        }
        return result;
    }

    public interface OnSelectCardListener {
        void onSelectCard(LauncherCard card);
    }
}
