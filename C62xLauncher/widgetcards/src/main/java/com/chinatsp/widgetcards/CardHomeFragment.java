package com.chinatsp.widgetcards;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import com.chinatsp.drawer.CollapseController;
import com.chinatsp.drawer.ICollapseListener;
import com.chinatsp.iquting.service.IqutingBindService;
import com.chinatsp.widgetcards.home.CardIndicator;
import com.chinatsp.widgetcards.home.CardScrollUtil;
import com.chinatsp.widgetcards.home.ExpandStateManager;
import com.chinatsp.widgetcards.home.HomeCardRecyclerView;
import com.chinatsp.widgetcards.home.smallcard2.HomeCardRcvManager;
import com.chinatsp.widgetcards.home.smallcard2.SmallCardsAdapter2;
import com.chinatsp.widgetcards.home.smallcard2.SmallCardRcvManager;
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
    private SmallCardsAdapter2 mSmallCardsAdapter;
    private HomeCardRecyclerView mRcvCards;
    private CardIndicator mCardIndicator;
    private PagerSnapHelper mSnapHelper;
    private Handler mUiHandler = new Handler();
    private int mCardDividerWidth;
    private boolean mInitialed = false;
    private CollapseController mCollapseController;

    private RecyclerView rcvSmallCards;


    @Override
    protected void initViews(View rootView) {
        mCollapseController = new CollapseController(getContext(), mDrawerCollapseListener);
        mCardDividerWidth = getResources().getDimensionPixelOffset(R.dimen.card_divider_width);
        CardScrollUtil.setDivider(mCardDividerWidth);
        initObservers();
        initCardsRcv(rootView);
        initSmallCardsRcv(rootView);
        mCardIndicator = rootView.findViewById(R.id.cardIndicator);
        mCardIndicator.setIndex(0);
        EasyLog.d(TAG, "initViews ... Hashcode:" + hashCode());
//        drawerCreator = new DrawerCreator(rootView.findViewById(R.id.rcvDrawerContent));
//        drawerCreator.initDrawerRcv();

        EventBus.getDefault().register(this);

        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                EasyLog.d(TAG, "onTouch TEST");
                return false;
            }
        });
        ExpandStateManager.getInstance().setExpand(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        EasyLog.d(TAG, "onResume");
        mCardsAdapter.notifyItemChanged(0);
        if (mInitialed) {
            collapseControlViews();
        }
        mInitialed = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyLog.d(TAG, "onStop");

    }

    @Override
    public void onPause() {
        super.onPause();
        EasyLog.d(TAG, "onPause");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EasyLog.d(TAG, "onDetach");
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyLog.d(TAG, "onStart");
    }

    private void collapseControlViews() {
        // ???????????????????????????, ??????????????????????????????, ????????????????????????
        boolean showingControlViews = isShowingControlViews();
        EasyLog.d(TAG, "collapseControlViews , showingControlViews:" + showingControlViews);
        if (showingControlViews) {
            scrollToFirstCardSmooth();
        }
    }

    private void initObservers() {
        ExpandStateManager.getInstance().register(this, mExpandOb);
        CardManager.getInstance().registerHomeCardsOb(this, mHomeCardsOb);
        mCollapseController.register();
    }

    private void releaseObservers() {
        ExpandStateManager.getInstance().unregister(mExpandOb);
        CardManager.getInstance().unregisterHomeCardsOb(mHomeCardsOb);
        mCollapseController.unRegister();
    }

    Observer<Boolean> mExpandOb = new Observer<Boolean>() {
        @Override
        public void onChanged(Boolean expand) {
//            mUiHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    onChangeExpandState(expand);
//                }
//            }, 600);
        }
    };

//    private void onChangeExpandState(Boolean expand) {
//        int smallCardPosition = ExpandStateManager.getInstance().getSmallCardPosition();
//        EasyLog.d(TAG, "onChangeExpandState: " + expand + ", smallCardPosition:" + smallCardPosition);
//        int firstCardIndex = mCardsAdapter.isIncludeDrawer() ? 1 : 0;
//        if (smallCardPosition >= firstCardIndex && smallCardPosition < mCardsAdapter.getItemCount()) {
//            EasyLog.d(TAG, "onChangeExpandState: notifyItemChanged , smallCardPosition: " + smallCardPosition);
//            mCardsAdapter.notifyItemChanged(smallCardPosition);
//        }
//        if (!expand) {
//            ExpandStateManager.getInstance().clearSmallCardPosInExpandState();
//        }
//    }

    /**
     * ????????????????????????
     *
     * @param swipeEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void swipeCard(Events.SwipeEvent swipeEvent) {
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
        EasyLog.d(TAG, "swipeCard,  fragment hashcode: " + hashCode());
        EasyLog.d(TAG, "swipeCard, bigCard:" + bigCard.getName() + " , inLeftSide:" + swipeEvent.mBigInLeftSide + " , smallCard:" + smallCard);
        int index = homeList.indexOf(bigCard);
        int index2 = homeList.indexOf(smallCard);
        ListKit.swipeElement(homeList, index, index2);
//        ExpandStateManager.getInstance().setSmallCardPosInExpandState(index);
        mCardsAdapter.notifyDataSetChanged();
        // ???????????????????????????????????????, ???????????????????????????????????????????????????
        SmallCardRcvManager.getInstance().showSmallCardList(bigCard, smallCard, !bigCardInLeft);
    }

    Observer<List<LauncherCard>> mHomeCardsOb = new Observer<List<LauncherCard>>() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onChanged(List<LauncherCard> baseCardEntities) {
            EasyLog.d(TAG, "mHomeCardsOb  onChanged : " + baseCardEntities);
            ExpandStateManager.getInstance().setExpand(false);
            mCardsAdapter.setCardEntityList(baseCardEntities);
            mSmallCardsAdapter.setCardEntityList(baseCardEntities);
            mCardsAdapter.notifyDataSetChanged();
            mSmallCardsAdapter.notifyDataSetChanged();
            rcvSmallCards.setVisibility(View.INVISIBLE);
            if (mCardsAdapter.isIncludeDrawer()) {
                scrollToFirstCard();
            }
        }
    };

    private ICollapseListener mDrawerCollapseListener = new ICollapseListener() {
        @Override
        public void onCollapse() {
            collapseControlViews();
        }
    };

    /**
     * ??????????????????, ????????????????????????2?????????, ?????????1?????????????????????, ???????????????
     */
    private void scrollToFirstCard() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRcvCards.getLayoutManager();
//        if (layoutManager != null) {
//            layoutManager.scrollToPositionWithOffset(1, -mCardDividerWidth / 2);
//        }
        CardScrollUtil.scroll(layoutManager, 1);
    }

    /**
     * ?????????????????????, ?????????????????????????????????????????????, ???????????????????????????.
     */
    private void scrollToFirstCardSmooth() {
        if (mRcvCards != null) {
            // 600: recyclerView??????1??????x??????.
            // ?????????????????????600???, ??????????????????????????????????????????
            mRcvCards.smoothScrollBy(600, 0);
        }
    }

    private void initCardsRcv(View rootView) {
        EasyLog.d(TAG, "initCardsRcv");
        mRcvCards = rootView.findViewById(R.id.rcvCards);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity()) {
            @Override
            public boolean canScrollHorizontally() {
                // ?????????, ????????????
                return !ExpandStateManager.getInstance().getExpandState();
            }
        };
        mSnapHelper = new PagerSnapHelper();
        mSnapHelper.attachToRecyclerView(mRcvCards);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        mRcvCards.setLayoutManager(layoutManager);
        mRcvCards.setItemAnimator(new DefaultItemAnimator() {
            @Override
            public boolean canReuseUpdatedViewHolder(@NonNull RecyclerView.ViewHolder viewHolder) {
                return true;
            }
        });
        if (mRcvCards.getItemDecorationCount() == 0) {
            SimpleRcvDecoration decoration = new SimpleRcvDecoration(mCardDividerWidth, layoutManager);
            mRcvCards.addItemDecoration(decoration);
        }
        mCardsAdapter = new HomeCardsAdapter(getActivity(), mRcvCards);

        mRcvCards.setAdapter(mCardsAdapter);
        mRcvCards.addOnScrollListener(mOnScrollListener);
    }

    RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
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
            EasyLog.d(TAG, "onScrollStateChanged state:" + newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                boolean showingControlViews = isShowingControlViews();
                EasyLog.d(TAG, "onScrollStateChanged , showingControlViews:" + showingControlViews);
                onScrollLeftSide(showingControlViews);
                mRcvCards.setScrolling(false);
            } else {
                mRcvCards.setScrolling(true);
            }
        }
    };

    private void initSmallCardsRcv(View rootView) {
        rcvSmallCards = rootView.findViewById(R.id.rcvSmallCards);
        Context context = getContext();
        mSmallCardsAdapter = new SmallCardsAdapter2(getContext(), rcvSmallCards);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rcvSmallCards.setLayoutManager(layoutManager);
        rcvSmallCards.setAdapter(mSmallCardsAdapter);
        mSnapHelper = new PagerSnapHelper();
        mSnapHelper.attachToRecyclerView(rcvSmallCards);
        SmallCardRcvManager.getInstance().setRecyclerView(rcvSmallCards);
        HomeCardRcvManager.getInstance().setHomeRecyclerView(mRcvCards, mCardsAdapter);
    }

    /**
     * ???????????????????????????
     *
     * @param showingControlViews true: ???????????????, ?????????????????????
     */
    private void onScrollLeftSide(boolean showingControlViews) {
        View viewRightSpace = mRootView.findViewById(R.id.viewRight);
        viewRightSpace.setVisibility(showingControlViews ? View.VISIBLE : View.GONE);
        if (showingControlViews) {
            viewRightSpace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EasyLog.d(TAG, "Click Another");
                    scrollToFirstCardSmooth();
                }
            });
        }
    }

    private boolean isShowingControlViews() {
        return !mRcvCards.canScrollHorizontally(-1);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_cards3;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRcvCards.removeOnScrollListener(mOnScrollListener);
        releaseObservers();
        //?????????????????????????????????
        IqutingBindService.getInstance().removeMediaAndPlayListener();
        EventBus.getDefault().unregister(this);
    }
}
