package com.chinatsp.volcano;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

import com.chinatsp.volcano.api.response.VideoListData;
import com.chinatsp.volcano.repository.VolcanoRepository;
import com.chinatsp.volcano.viewholder.BigCardViewHolder;
import com.chinatsp.volcano.viewholder.SmallCardViewHolder;
import com.chinatsp.volcano.viewholder.VolcanoViewHolder;

import card.service.ICardStyleChange;
import launcher.base.utils.EasyLog;
import launcher.base.utils.recent.RecentAppHelper;
import launcher.base.utils.view.LayoutParamUtil;


public class VolcanoCardView extends ConstraintLayout implements ICardStyleChange, LifecycleOwner {
    private static final String TAG = "VolcanoCardView";

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
    private String mSource = VolcanoRepository.SOURCE_TOUTIAO;

    private void init() {
        EasyLog.i(TAG, "InitVolcano :" + hashCode());
        LayoutInflater.from(getContext()).inflate(R.layout.card_volcano, this);
        mController = new VolcanoController(this);
        mSmallCardView = findViewById(R.id.layoutSmallCardView);
        mSmallCardViewHolder = new SmallCardViewHolder(mSmallCardView);
        mSmallCardViewHolder.showNormal();
        mSmallWidth = (int) getResources().getDimension(R.dimen.card_width);
        mLargeWidth = (int) getResources().getDimension(R.dimen.card_width_large);
        switchSource(mSource);

        if (mLargeCardView == null) {
            mLargeCardView = LayoutInflater.from(getContext()).inflate(R.layout.card_volcano_large, this, false);
            mBigCardViewHolder = new BigCardViewHolder(mLargeCardView, this);
        }
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentAppHelper.launchApp(getContext(), "com.bytedance.byteautoservice");
            }
        });
    }

    @Override
    public void expand() {
        mExpand = true;
        long time1 = System.currentTimeMillis();
        addView(mLargeCardView);
        mBigCardViewHolder.showNormal();
        mLargeCardView.setVisibility(VISIBLE);
        mSmallCardView.setVisibility(GONE);
        LayoutParamUtil.setWidth(mLargeWidth, this);
        runExpandAnim();
        long time2 = System.currentTimeMillis();
        EasyLog.d(TAG, "Trace expand cost:" + (time2 - time1) + "ms");
        mController.refreshPageState();
    }

    private void runExpandAnim() {
        int alphaAnimDuration = 500;
        int moveAnimValue = -500;
        int moveAnimDuration = 150;
        ObjectAnimator.ofFloat(mLargeCardView, "translationX", moveAnimValue, 0).setDuration(moveAnimDuration).start();
        ObjectAnimator animator = ObjectAnimator.ofFloat(mLargeCardView, "alpha", 0.1f, 1.0f).setDuration(alphaAnimDuration);
        animator.start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation, boolean isReverse) {
                super.onAnimationEnd(animation, isReverse);
                EasyLog.d(TAG, "runExpandAnim onAnimationEnd");
                if (mBigCardViewHolder != null) {
                    mBigCardViewHolder.init();
                }
            }
        });
    }

    @Override
    public void collapse() {
        mExpand = false;
        mSmallCardView.setVisibility(VISIBLE);
        mLargeCardView.setVisibility(GONE);
        removeView(mLargeCardView);
        LayoutParamUtil.setWidth(mSmallWidth, this);
        mController.refreshPageState();
    }

    public void switchSource(String source) {
        mSource = source;
        mController.setCurrentSource(source);
        mSmallCardViewHolder.onChangeSource(source);
        if (mBigCardViewHolder != null) {
            mBigCardViewHolder.onChangeSource(source);
        }
        EasyLog.d(TAG, "switchSource:" + mSource);
        if (mController != null) {
            mController.loadSourceData(mSource);
        }
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

    public void updateList(VideoListData videoListData) {
        post(() -> {
            if (mExpand) {
                mBigCardViewHolder.updateList(videoListData);
            }
            hideNetWorkError();
            // 无论何种状态, 小卡封面都必须更新为Source对应列表的第一个item
            mSmallCardViewHolder.updateList(videoListData);
        });
    }

    public void showLoading() {
        if (mExpand) {
            mBigCardViewHolder.showLoadingView();
        } else {
            mSmallCardViewHolder.showLoadingView();
        }
    }

    public void hideLoading() {
        if (mExpand) {
            mBigCardViewHolder.hideLoadingView();
        } else {
            mSmallCardViewHolder.hideLoadingView();
        }
    }

    public void showNetWorkError() {
        if (mExpand) {
            mBigCardViewHolder.showNetworkError();
        } else {
            mSmallCardViewHolder.showNetworkError();
        }
    }

    public void hideNetWorkError() {
        if (mExpand) {
            mBigCardViewHolder.hideNetworkError();
        } else {
            mSmallCardViewHolder.hideNetworkError();
        }
    }

    public void showDataError() {
        if (mExpand) {
            mBigCardViewHolder.showDataError();
        } else {
            mSmallCardViewHolder.showDataError();
        }
    }
}
