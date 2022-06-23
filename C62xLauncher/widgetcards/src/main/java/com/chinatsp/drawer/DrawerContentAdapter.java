package com.chinatsp.drawer;

import android.content.Context;
import android.view.View;

import launcher.base.recyclerview.BaseEntity;
import launcher.base.recyclerview.BaseViewHolder;
import launcher.base.recyclerview.MultiStyleRcvAdapter;

public class DrawerContentAdapter extends MultiStyleRcvAdapter<BaseEntity> {

    public DrawerContentAdapter(Context context) {
        super(context);
    }

    @Override
    protected BaseViewHolder<BaseEntity> createViewHolder(View view) {
        return new DrawerViewHolder(view);
    }
}
