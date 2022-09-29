package com.chinatsp.navigation.viewholder;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

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
}
