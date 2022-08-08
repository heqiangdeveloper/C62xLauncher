package com.chinatsp.drawer.search;

import android.view.View;

import androidx.annotation.NonNull;

import com.chinatsp.drawer.DrawerEntity;

import launcher.base.recyclerview.BaseViewHolder;
import launcher.base.routine.ActivityBus;

public class DrawerSearchHolder extends BaseViewHolder<DrawerEntity> {
    public DrawerSearchHolder(@NonNull View itemView) {
        super(itemView);
        initClickListeners(itemView);
    }

    private void initClickListeners(View itemView) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityBus.newInstance(itemView.getContext())
                        .withClass(LauncherSearchActivity.class)
                        .go();
            }
        });
    }
}
