package com.chinatsp.volcano;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.volcano.state.NormalState;
import com.chinatsp.volcano.state.VolcanoState;
import com.chinatsp.volcano.videos.VolcanoVideoAdapter;
import com.chinatsp.volcano.viewholder.BigCardViewHolder;
import com.chinatsp.volcano.viewholder.SmallCardViewHolder;
import com.chinatsp.volcano.viewholder.VolcanoViewHolder;

import card.service.ICardStyleChange;
import launcher.base.recyclerview.SimpleRcvDecoration;
import launcher.base.utils.view.LayoutParamUtil;


public class VolcanoCardView extends ConstraintLayout implements ICardStyleChange, LifecycleOwner {
    private static final String TAG = "WeatherCardLargeView";

    public VolcanoCardView(@NonNull Context context) {
        super(context);
        init();
    }

    public VolcanoCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VolcanoCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public VolcanoCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }
    private LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);
    private VolcanoController mController;
    private View mLargeCardView;
    private View mSmallCardView;
    private int mSmallWidth;
    private int mLargeWidth;
    private boolean mExpand = false;
    private VolcanoViewHolder mSmallCardViewHolder;
    private VolcanoViewHolder mBigCardViewHolder;

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.card_volcano, this);
        mController = new VolcanoController(this);
        mSmallCardView = findViewById(R.id.layoutSmallCardView);
        mSmallCardViewHolder = new SmallCardViewHolder(mSmallCardView);
        mSmallCardViewHolder.showNormal();
        mSmallWidth = (int) getResources().getDimension(R.dimen.card_width);
        mLargeWidth = (int) getResources().getDimension(R.dimen.card_width_large);
    }

    @Override
    public void expand() {
        mExpand = true;
        if (mLargeCardView == null) {
            mLargeCardView = LayoutInflater.from(getContext()).inflate(R.layout.card_volcano_large, this, false);
            mBigCardViewHolder = new BigCardViewHolder(mLargeCardView);
            initBigCardView(mLargeCardView);
        }
        addView(mLargeCardView);
        mBigCardViewHolder.showNormal();
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
        RecyclerView rcvCardVolcanoVideoList = largeCardView.findViewById(R.id.rcvCardVolcanoVideoList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rcvCardVolcanoVideoList.setLayoutManager(layoutManager);
        VolcanoVideoAdapter adapter = new VolcanoVideoAdapter(getContext());
        rcvCardVolcanoVideoList.setAdapter(adapter);
        adapter.setData(mController.createTestList());
    }

    @Override
    public boolean hideDefaultTitle() {
        return false;
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
        return null;
    }
}
