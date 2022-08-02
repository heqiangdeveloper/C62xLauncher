package com.chinatsp.volcano.viewholder;

import android.view.View;

public abstract class VolcanoViewHolder {
    protected View mRootView;

    public VolcanoViewHolder(View rootView) {
        mRootView = rootView;
    }

    public abstract void showNormal();

    public abstract void showDisconnect();

    public abstract void showLogin();
}
