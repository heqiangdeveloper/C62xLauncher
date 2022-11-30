package com.chinatsp.widgetcards.home.smallcard2;

import android.os.Handler;
import android.os.Looper;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.widgetcards.home.CardFrameViewHolder;
import com.chinatsp.widgetcards.home.CardScrollUtil;
import com.chinatsp.widgetcards.home.ExpandStateManager;
import com.chinatsp.widgetcards.home.HomeCardsAdapter;
import com.chinatsp.widgetcards.manager.CardManager;

import java.util.List;

import card.base.LauncherCard;
import kotlin.jvm.internal.PropertyReference0Impl;
import launcher.base.utils.EasyLog;

public class HomeCardRcvManager {
    private HomeCardRcvManager() {

    }

    private static class Holder {
        private static HomeCardRcvManager instance = new HomeCardRcvManager();
    }

    public static HomeCardRcvManager getInstance() {
        return Holder.instance;
    }

    private static String TAG = "HomeCardRcvManager";
    private RecyclerView mHomeRecyclerView;
    private HomeCardsAdapter mAdapter;


    public void setHomeRecyclerView(RecyclerView homeRecyclerView, HomeCardsAdapter adapter) {
        mHomeRecyclerView = homeRecyclerView;
        this.mAdapter = adapter;
    }

    public void showViewHolder(LauncherCard card) {
        if (card == null) {
            return;
        }
        CardFrameViewHolder viewHolder = (CardFrameViewHolder) mAdapter.find(card);
        if (viewHolder != null) {
            viewHolder.showContentView();
        }
    }

    public CardFrameViewHolder findViewHoldByCard(LauncherCard card) {
        return (CardFrameViewHolder) mAdapter.find(card);
    }

    public CardFrameViewHolder findViewHoldByPosition(int position) {
        return (CardFrameViewHolder) mAdapter.findViewHolderByPosition(position);
    }

    public int findPositionByCard(LauncherCard card) {
        return mAdapter.getPositionByCard(card);
    }

    public LauncherCard findCardByPosition(int position) {
        return mAdapter.getCardByPosition(position);
    }

    public void scrollTo(int position) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mHomeRecyclerView.getLayoutManager();
        CardScrollUtil.scroll(layoutManager, position);
    }
}
