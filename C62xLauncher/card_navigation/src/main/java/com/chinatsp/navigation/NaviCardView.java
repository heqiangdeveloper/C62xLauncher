package com.chinatsp.navigation;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.chinatsp.navigation.gaode.bean.GuideInfo;
import com.chinatsp.navigation.gaode.bean.TrafficLaneModel;
import com.chinatsp.navigation.repository.DriveDirection;
import com.chinatsp.navigation.viewholder.NaviBigCardHolder;
import com.chinatsp.navigation.viewholder.NaviSmallCardHolder;

import card.service.ICardStyleChange;
import launcher.base.utils.EasyLog;
import launcher.base.utils.view.LayoutParamUtil;


public class NaviCardView extends ConstraintLayout implements ICardStyleChange {
    private static final String TAG = "NaviCardView ";

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
        mSmallCardView = findViewById(R.id.layoutSmallCardView);
        mController = new NaviController(this);
        mSmallCardHolder = new NaviSmallCardHolder(mSmallCardView,mController);
        mController.refreshInitView();
    }

    @Override
    public void expand() {
        if (mLargeCardView == null) {
            mLargeCardView = LayoutInflater.from(getContext()).inflate(R.layout.card_navigation_large, this, false);
            mBigCardHolder = new NaviBigCardHolder(mLargeCardView, mController);
        }
        mExpand = true;
        addView(mLargeCardView);
        mLargeCardView.setVisibility(VISIBLE);
        mSmallCardView.setVisibility(GONE);
        LayoutParamUtil.setWidth(mLargeWidth, this);
        runExpandAnim();
        mController.refreshPageState();
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
        mController.refreshPageState();
    }

    public void refreshState(int state) {
        EasyLog.d(TAG, "refreshState , current state:"+state);
        if (state == NaviController.STATE_IN_NAVIGATION || state == NaviController.STATE_IN_NAVIGATION_MOCK) {
            EasyLog.i(TAG, "refreshState Start navigation");
            refreshNavigation();
        } else {
            EasyLog.i(TAG, "refreshState Start cruise");
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

    public void refreshMyLocation(String myLocationName) {
        if (mExpand) {
            mBigCardHolder.setLocation(myLocationName);
        } else {
            mSmallCardHolder.setLocation(myLocationName);
        }
    }
    public void showNetWorkError() {
        if (mExpand) {
            mBigCardHolder.showNetworkError();
        } else {
            mSmallCardHolder.showNetworkError();
        }
    }

    public void hideNetWorkError() {
        if (mExpand) {
            mBigCardHolder.hideNetworkError();
        } else {
            mSmallCardHolder.hideNetworkError();
        }
    }

    public void refreshGuideInfo(GuideInfo guideInfo, DriveDirection driveDirection) {
        NavigationUtil.logI(TAG + "refreshGuideInfo "+Thread.currentThread().getName() +" "+System.currentTimeMillis());
        NavigationUtil.logD(TAG + " type:"+ guideInfo.getType());
        NavigationUtil.logD(TAG + guideInfo.getCurRoadName());
        NavigationUtil.logD(TAG + " 转向图标: "+guideInfo.getIcon());
        NavigationUtil.logD(TAG + guideInfo.getNextRoadName());
        NavigationUtil.logD(TAG + guideInfo.getEndPOIName());
        NavigationUtil.logD(TAG + guideInfo.getEndPOIAddr());
        if (mExpand) {
            mBigCardHolder.refreshNaviGuideInfo(guideInfo, driveDirection);
        } else {
            mSmallCardHolder.refreshNaviGuideInfo(guideInfo,driveDirection);
        }
    }

    public void refreshLaneInfo(TrafficLaneModel trafficLaneModel) {
        if (mExpand) {
            mBigCardHolder.refreshNaviLaneInfo(trafficLaneModel);
        } else {
            mSmallCardHolder.refreshNaviLaneInfo(trafficLaneModel);
        }
    }
}
