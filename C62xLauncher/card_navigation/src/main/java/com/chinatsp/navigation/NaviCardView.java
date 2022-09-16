package com.chinatsp.navigation;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.autonavi.autoaidlwidget.AutoAidlWidgetManager;
import com.chinatsp.navigation.viewholder.NaviBigCardHolder;
import com.chinatsp.navigation.viewholder.NaviSmallCardHolder;

import card.service.ICardStyleChange;
import launcher.base.utils.view.LayoutParamUtil;


public class NaviCardView extends ConstraintLayout implements ICardStyleChange {
    public NaviCardView(@NonNull Context context) {
        super(context);
        init();
    }

    public NaviCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NaviCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public NaviCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private int mSmallWidth;
    private int mLargeWidth;
    private boolean mExpand = false;
    private View mLargeCardView;
    private View mSmallCardView;
    private NaviSmallCardHolder mSmallCardHolder;
    private NaviBigCardHolder mBigCardHolder;
    private NaviController mController;

    private void init(){
        mSmallWidth = (int) getResources().getDimension(R.dimen.card_width);
        mLargeWidth = (int) getResources().getDimension(R.dimen.card_width_large);
        LayoutInflater.from(getContext()).inflate(R.layout.card_navigation, this);
        AutoAidlWidgetManager.getInstance().init(getContext().getApplicationContext());
        mSmallCardView = findViewById(R.id.layoutSmallCardView);
        mSmallCardHolder = new NaviSmallCardHolder(mSmallCardView);
        mController = new NaviController(this);
        mController.refreshInitView();
    }

    @Override
    public void expand() {
        if (mLargeCardView == null) {
            mLargeCardView = LayoutInflater.from(getContext()).inflate(R.layout.card_navigation_large, this, false);
            mBigCardHolder = new NaviBigCardHolder(mLargeCardView);
        }
        mExpand = true;
        addView(mLargeCardView);
        mLargeCardView.setVisibility(VISIBLE);
        mSmallCardView.setVisibility(GONE);
        LayoutParamUtil.setWidth(mLargeWidth, this);
        runExpandAnim();
    }

    private void runExpandAnim() {
        int alphaAnimDuration = 500;
        int moveAnimValue = -500;
        int moveAnimDuration = 150;
        ObjectAnimator.ofFloat(mLargeCardView, "translationX", moveAnimValue, 0).setDuration(moveAnimDuration).start();
        ObjectAnimator animator = ObjectAnimator.ofFloat(mLargeCardView, "alpha", 0.1f, 1.0f).setDuration(alphaAnimDuration);
        animator.start();
    }


    @Override
    public void collapse() {
        mExpand = false;
        mSmallCardView.setVisibility(VISIBLE);
        mLargeCardView.setVisibility(GONE);
        removeView(mLargeCardView);
        LayoutParamUtil.setWidth(mSmallWidth, this);
    }

    public void refreshState(int state) {
        if (state == NaviController.STATE_IN_NAVIGATION) {
            refreshNavigation();
        } else {
            refreshFreeMode();
        }
    }

    private void refreshNavigation() {
        if (mExpand) {
            mBigCardHolder.refreshNavigation();
        } else {
            mSmallCardHolder.refreshNavigation();
        }
    }

    /**
     * 刷新自由模式UI
     */
    private void refreshFreeMode() {
        if (mExpand) {
            mBigCardHolder.refreshFreeMode();
        } else {
            mSmallCardHolder.refreshFreeMode();
        }
    }

    @Override
    public boolean hideDefaultTitle() {
        return false;
    }
}
