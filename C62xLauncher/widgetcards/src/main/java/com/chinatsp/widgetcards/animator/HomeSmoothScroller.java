package com.chinatsp.widgetcards.animator;

import android.content.Context;

import androidx.recyclerview.widget.LinearSmoothScroller;

public class HomeSmoothScroller extends LinearSmoothScroller {
    public HomeSmoothScroller(Context context) {
        super(context);
    }

    @Override
    protected int getHorizontalSnapPreference() {
        return SNAP_TO_START;
    }
}
