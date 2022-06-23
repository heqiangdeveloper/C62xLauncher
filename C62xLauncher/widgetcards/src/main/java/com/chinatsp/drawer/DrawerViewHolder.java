package com.chinatsp.drawer;

import android.view.View;

import androidx.annotation.NonNull;

import launcher.base.recyclerview.BaseEntity;
import launcher.base.recyclerview.BaseViewHolder;
import launcher.base.utils.EasyLog;

public class DrawerViewHolder extends BaseViewHolder<BaseEntity> {
    private static final String TAG = "DrawerViewHolder";

    public DrawerViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void bind(int position, BaseEntity baseEntity) {
        super.bind(position, baseEntity);
        EasyLog.d(TAG, "bind , position:" + position+" , type:"+baseEntity.getViewType());
    }
}
