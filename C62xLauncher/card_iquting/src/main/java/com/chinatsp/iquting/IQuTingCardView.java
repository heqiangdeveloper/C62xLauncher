package com.chinatsp.iquting;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.iquting.songs.IQuTingSong;
import com.chinatsp.iquting.songs.IQuTingSongsAdapter;
import com.chinatsp.iquting.state.NetWorkDisconnectState;
import com.chinatsp.iquting.state.NormalState;
import com.chinatsp.iquting.state.IQuTingState;

import java.util.List;

import card.service.ICardStyleChange;
import launcher.base.recyclerview.SimpleRcvDecoration;
import launcher.base.utils.glide.GlideHelper;
import launcher.base.utils.view.LayoutParamUtil;


public class IQuTingCardView extends ConstraintLayout implements ICardStyleChange, LifecycleOwner {
    private static final String TAG = "WeatherCardLargeView";

    public IQuTingCardView(@NonNull Context context) {
        super(context);
        init();
    }

    public IQuTingCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IQuTingCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public IQuTingCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private IQuTingController mController;
    private NormalSmallCardViewHolder mNormalSmallCardViewHolder;
    private NormalBigCardViewHolder mNormalBigCardViewHolder;
    private int mSmallWidth;
    private int mLargeWidth;

    private View mLargeCardView;
    private View mSmallCardView;
    private LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);
    private IQuTingState mState = new NormalState();
    private boolean mExpand = false;

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.card_iquting, this);
        mSmallCardView = findViewById(R.id.layoutSmallCardView);
        mSmallWidth = (int) getResources().getDimension(R.dimen.card_width);
        mLargeWidth = (int) getResources().getDimension(R.dimen.card_width_large);

        mNormalSmallCardViewHolder = new NormalSmallCardViewHolder();
        mController = new IQuTingController(this);
        mState.updateViewState(this, mExpand);

        mNormalSmallCardViewHolder.updateMediaInfo();
    }

    @Override
    public void expand() {
        mExpand = true;
        if (mLargeCardView == null) {
            mLargeCardView = LayoutInflater.from(getContext()).inflate(R.layout.card_iquting_large, this, false);
            initBigCardView(mLargeCardView);
            mNormalBigCardViewHolder.updateSongs(mController.createTestList());
        }
        addView(mLargeCardView);
        mState.updateViewState(this, mExpand);

        mLargeCardView.setVisibility(VISIBLE);
        mSmallCardView.setVisibility(GONE);

        LayoutParamUtil.setWidth(mLargeWidth, this);
        runExpandAnim();
    }

    private void runExpandAnim() {
        ObjectAnimator.ofFloat(mLargeCardView, "translationX", -500, 0).setDuration(150).start();
        ObjectAnimator.ofFloat(mLargeCardView, "alpha", 0.1f, 1.0f).setDuration(500).start();
    }

    @Override
    public void collapse() {
        mExpand = false;
        mSmallCardView.setVisibility(VISIBLE);
        mLargeCardView.setVisibility(GONE);
        removeView(mLargeCardView);
        LayoutParamUtil.setWidth(mSmallWidth, this);
    }

    private void initBigCardView(View largeCardView) {
        RecyclerView rcvCardIQuTingSongList = largeCardView.findViewById(R.id.rcvCardIQuTingSongList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rcvCardIQuTingSongList.setLayoutManager(layoutManager);
        // 间隔24px
        SimpleRcvDecoration divider = new SimpleRcvDecoration(24,layoutManager );
        if (rcvCardIQuTingSongList.getItemDecorationCount() <= 0) {
            rcvCardIQuTingSongList.addItemDecoration(divider);
        }
        IQuTingSongsAdapter adapter = new IQuTingSongsAdapter(getContext());
        rcvCardIQuTingSongList.setAdapter(adapter);

        mNormalBigCardViewHolder = new NormalBigCardViewHolder(mLargeCardView);
        mNormalBigCardViewHolder.setSongsAdapter(adapter);
    }

    @Override
    public boolean hideDefaultTitle() {
        return false;
    }

    private class NormalSmallCardViewHolder {
        private ImageView mIvCover;

        NormalSmallCardViewHolder() {
            mIvCover = findViewById(R.id.ivIQuTingCover);
        }

        void updateMediaInfo() {
            GlideHelper.loadImageUrlAlbumCover(getContext(), mIvCover, R.drawable.test_cover2, 10);
        }
    }

    private class NormalBigCardViewHolder {
        private View itemView;
        private IQuTingSongsAdapter mIQuTingSongsAdapter;
        private ImageView ivIQuTingCoverBig;

        NormalBigCardViewHolder(View largeCardView) {
            itemView = largeCardView;
            ivIQuTingCoverBig = itemView.findViewById(R.id.ivIQuTingCoverBig);
            updateCover();
        }

        public void setSongsAdapter(IQuTingSongsAdapter IQuTingSongsAdapter) {
            mIQuTingSongsAdapter = IQuTingSongsAdapter;
        }

        public void updateSongs(List<IQuTingSong> songList) {
            mIQuTingSongsAdapter.setData(songList);
        }

        public void updateCover() {
            GlideHelper.loadCircleImage(getContext(), ivIQuTingCoverBig, R.drawable.test_cover2);
        }
    }

    private void changeState(IQuTingState newState) {
        mState = newState;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mLifecycleRegistry.setCurrentState(Lifecycle.State.CREATED);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mLifecycleRegistry.setCurrentState(Lifecycle.State.DESTROYED);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
            mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
        } else if (visibility == GONE || visibility == INVISIBLE) {
            mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
            mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
        }
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mLifecycleRegistry;
    }
}
