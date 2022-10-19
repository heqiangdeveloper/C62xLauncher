package com.chinatsp.navigation.viewholder;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.chinatsp.navigation.gaode.bean.GuideInfo;
import com.chinatsp.navigation.gaode.bean.TrafficLaneModel;
import com.chinatsp.navigation.repository.DriveDirection;

public abstract class NaviCardHolder {
    protected View mRootView;
    protected Context mContext;

    public NaviCardHolder(@NonNull View rootView) {
        mRootView = rootView;
        mContext = rootView.getContext();
    }

    public abstract void refreshNavigation();

    public abstract void refreshFreeMode();
    public abstract void setLocation(String myLocationName);

    public abstract void showNetworkError();

    public abstract void hideNetworkError();

    public abstract void refreshNaviGuideInfo(GuideInfo guideInfo, DriveDirection driveDirection);

    public abstract void refreshNaviLaneInfo(TrafficLaneModel trafficLaneModel);
}
