package com.chinatsp.volcano;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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

import com.chinatsp.volcano.api.response.VideoListData;
import com.chinatsp.volcano.repository.VolcanoRepository;
import com.chinatsp.volcano.viewholder.BigCardViewHolder;
import com.chinatsp.volcano.viewholder.SmallCardViewHolder;
import com.chinatsp.volcano.viewholder.VolcanoViewHolder;

import card.service.ICardStyleChange;
import launcher.base.utils.EasyLog;
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
    private View viewLoading;
    private int mSmallWidth;
    private int mLargeWidth;
    private boolean mExpand = false;
    private VolcanoViewHolder mSmallCardViewHolder;
    private VolcanoViewHolder mBigCardViewHolder;
    private String mSource = VolcanoRepository.SOURCE_TOUTIAO;

    private void init() {
        EasyLog.i(TAG,"InitVolcano :"+hashCode());
        LayoutInflater.from(getContext()).inflate(R.layout.card_volcano, this);
        mController = new VolcanoController(this);
        mSmallCardView = findViewById(R.id.layoutSmallCardView);
        viewLoading = findViewById(R.id.viewLoading);
        mSmallCardViewHolder = new SmallCardViewHolder(mSmallCardView);
        mSmallCardViewHolder.showNormal();
        mSmallWidth = (int) getResources().getDimension(R.dimen.card_width);
        mLargeWidth = (int) getResources().getDimension(R.dimen.card_width_large);
        requestData();

        if (mLargeCardView == null) {
            mLargeCardView = LayoutInflater.from(getContext()).inflate(R.layout.card_volcano_large, this, false);
            mBigCardViewHolder = new BigCardViewHolder(mLargeCardView, this);
        }
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
        EasyLog.d(TAG, "Trace expand cost:"+(time2-time1)+"ms");

    }

    private void runExpandAnim() {
        ObjectAnimator.ofFloat(mLargeCardView, "translationX", -500, 0).setDuration(150).start();
        ObjectAnimator animator = ObjectAnimator.ofFloat(mLargeCardView, "alpha", 0.1f, 1.0f).setDuration(500);
        animator.start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation, boolean isReverse) {
                super.onAnimationEnd(animation, isReverse);
                EasyLog.d(TAG,"runExpandAnim onAnimationEnd");
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
    }

    public void requestData() {
        EasyLog.d(TAG, "requestData start, source:" + mSource);
        if (mController != null) {
            mController.loadSourceData(mSource);
        }
    }
    public void switchSource(String source) {
        mSource = source;
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
            } else {
                mSmallCardViewHolder.updateList(videoListData);
            }
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
}
