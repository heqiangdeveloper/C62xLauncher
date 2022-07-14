package com.chinatsp.widgetcards;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;

import com.chinatsp.drawer.DrawerCreator;
import com.chinatsp.widgetcards.home.CardIndicator;
import com.chinatsp.widgetcards.home.ExpandCardsViewHolder;
import com.chinatsp.widgetcards.home.ExpandStateManager;
import com.chinatsp.widgetcards.manager.CardManager;
import com.chinatsp.widgetcards.home.HomeCardsAdapter;
import com.chinatsp.widgetcards.manager.Events;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import card.base.LauncherCard;
import launcher.base.recyclerview.SimpleRcvDecoration;
import launcher.base.component.BaseFragment;
import launcher.base.utils.EasyLog;
import launcher.base.utils.collection.ListKit;

public class CardHomeFragment extends BaseFragment {

    private static final String TAG = "CardHomeFragment";
    private HomeCardsAdapter mCardsAdapter;
    private RecyclerView mRcvCards;
    private CardIndicator mCardIndicator;
    private PagerSnapHelper mSnapHelper;
    private ExpandCardsViewHolder mExpandCardsViewHolder;

    @Override
    protected void initViews(View rootView) {
        initObservers();
        initCardsRcv(rootView);
        mCardIndicator = rootView.findViewById(R.id.cardIndicator);
        mCardIndicator.setIndex(0);

        DrawerCreator drawerCreator = new DrawerCreator(rootView.findViewById(R.id.rcvDrawerContent));
        drawerCreator.initDrawerRcv();

        EventBus.getDefault().register(this);
    }

    private void initObservers() {
        ExpandStateManager.getInstance().register(this, mExpandOb);
        CardManager.getInstance().registerHomeCardsOb(this, mHomeCardsOb);
    }

    private void releaseObservers() {
        ExpandStateManager.getInstance().unregister(mExpandOb);
        CardManager.getInstance().unregisterHomeCardsOb(mHomeCardsOb);
    }

    Observer<Boolean> mExpandOb = new Observer<Boolean>() {
        @Override
        public void onChanged(Boolean expand) {
            if (expand && mSnapHelper != null) {

            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCardExpand(Events.SwipeEvent swipeEvent) {
        if (swipeEvent == null) {
            return;
        }
        List<LauncherCard> homeList = CardManager.getInstance().getHomeList();
        LauncherCard bigCard = swipeEvent.mBigLauncherCard;
        boolean bigCardInLeft = swipeEvent.mBigInLeftSide;
        LauncherCard smallCard;

        if (bigCardInLeft) {
            smallCard = ListKit.findNext(bigCard, homeList);
        } else {
            smallCard = ListKit.findPrev(bigCard, homeList);
        }
        EasyLog.d(TAG, "onCardExpand, bigCard:" + bigCard.getName() + " , inLeftSide:" + swipeEvent.mBigInLeftSide + " , smallCard:" + smallCard);
        int index = homeList.indexOf(bigCard);
        int index2 = homeList.indexOf(smallCard);
        int start, end;
        if (index > index2) {
            start = index2;
            end = index;
        } else {
            start = index;
            end = index2;
        }
        ListKit.swipeElement(homeList, index, index2);
        mCardsAdapter.notifyDataSetChanged();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    private void onCardCollapse() {
        if (mExpandCardsViewHolder == null) {
            return;
        }
        mExpandCardsViewHolder.release((ViewGroup) mRootView);
    }
    Observer<List<LauncherCard>> mHomeCardsOb = new Observer<List<LauncherCard>>() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onChanged(List<LauncherCard> baseCardEntities) {
            EasyLog.d(TAG,"mHomeCardsOb  onChanged : "+baseCardEntities);
            mCardsAdapter.setCardEntityList(baseCardEntities);
            mCardsAdapter.notifyDataSetChanged();
        }
    };
    private void initCardsRcv(View rootView) {
        EasyLog.d(TAG, "initCardsRcv");
        CardManager cardManager = CardManager.getInstance();
        mRcvCards = rootView.findViewById(R.id.rcvCards);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity()){
            @Override
            public boolean canScrollHorizontally() {
                // 展开时, 禁止滑动
//                return true;
                return !ExpandStateManager.getInstance().getExpandState();
            }

        };
        mSnapHelper = new PagerSnapHelper();
        mSnapHelper.attachToRecyclerView(mRcvCards);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        mRcvCards.setLayoutManager(layoutManager);
        setItemAnimator();
        if (mRcvCards.getItemDecorationCount() == 0) {
            SimpleRcvDecoration decoration = new SimpleRcvDecoration(30, layoutManager);
            mRcvCards.addItemDecoration(decoration);
        }
        mCardsAdapter = new HomeCardsAdapter(getActivity(), mRcvCards);
        mCardsAdapter.setCardEntityList(cardManager.getHomeList());
        mRcvCards.setAdapter(mCardsAdapter);
        mRcvCards.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int offset = recyclerView.computeHorizontalScrollOffset();
                int range = recyclerView.computeHorizontalScrollRange();
                int extent = recyclerView.computeHorizontalScrollExtent();
                float ratio = offset * 1.0f / (range - extent);
                mCardIndicator.setIndex(ratio);
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    private void setItemAnimator() {
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setAddDuration(200);
        defaultItemAnimator.setChangeDuration(200);
        defaultItemAnimator.setMoveDuration(200);
        defaultItemAnimator.setRemoveDuration(200);
        mRcvCards.setItemAnimator(defaultItemAnimator);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_cards;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseObservers();
        EventBus.getDefault().unregister(this);
    }
}