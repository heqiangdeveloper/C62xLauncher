package com.chinatsp.widgetcards;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.view.View;

import com.chinatsp.entity.BaseCardEntity;
import com.chinatsp.widgetcards.editor.CardIndicator;
import com.chinatsp.widgetcards.home.ExpandStateManager;
import com.chinatsp.widgetcards.service.CardsTypeManager;
import com.chinatsp.widgetcards.home.HomeCardsAdapter;

import java.util.List;

import launcher.base.recyclerview.SimpleRcvDecoration;
import launcher.base.component.BaseFragment;
import launcher.base.utils.EasyLog;

public class CardHomeFragment extends BaseFragment {

    private static final String TAG = "CardHomeFragment";
    private HomeCardsAdapter mCardsAdapter;
    private RecyclerView mRcvCards;
    private CardIndicator mCardIndicator;
    private PagerSnapHelper mSnapHelper;

    @Override
    protected void initViews(View rootView) {
        initObservers();
        initCardsRcv(rootView);
        mCardIndicator = rootView.findViewById(R.id.cardIndicator);
        mCardIndicator.setIndex(0);

    }

    private void initObservers() {
        ExpandStateManager.getInstance().register(this, mExpandOb);
        CardsTypeManager.getInstance().registerHomeCardsOb(this, mHomeCardsOb);
    }

    private void releaseObservers() {
        ExpandStateManager.getInstance().unregister(mExpandOb);
        CardsTypeManager.getInstance().unregisterHomeCardsOb(mHomeCardsOb);
    }

    Observer<Boolean> mExpandOb = new Observer<Boolean>() {
        @Override
        public void onChanged(Boolean expand) {
            if (expand && mSnapHelper != null) {

            }
        }
    };
    Observer<List<BaseCardEntity>> mHomeCardsOb = new Observer<List<BaseCardEntity>>() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onChanged(List<BaseCardEntity> baseCardEntities) {
            mCardsAdapter.setCardEntityList(baseCardEntities);
            mCardsAdapter.notifyDataSetChanged();
        }
    };
    private void initCardsRcv(View rootView) {
        EasyLog.d(TAG, "initCardsRcv");
        CardsTypeManager cardsTypeManager = CardsTypeManager.getInstance();
        mRcvCards = rootView.findViewById(R.id.rcvCards);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity()){
            @Override
            public boolean canScrollHorizontally() {
                // 展开时, 禁止滑动
                return !ExpandStateManager.getInstance().getExpandState();
            }
        };
        mSnapHelper = new PagerSnapHelper();
        mSnapHelper.attachToRecyclerView(mRcvCards);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        mRcvCards.setLayoutManager(layoutManager);
        mRcvCards.setItemAnimator(new DefaultItemAnimator());
        if (mRcvCards.getItemDecorationCount() == 0) {
            SimpleRcvDecoration decoration = new SimpleRcvDecoration(30, layoutManager);
            mRcvCards.addItemDecoration(decoration);
        }
        mCardsAdapter = new HomeCardsAdapter(getActivity(), mRcvCards);
        mCardsAdapter.setCardEntityList(cardsTypeManager.createInitCards());
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

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_cards;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseObservers();
    }
}