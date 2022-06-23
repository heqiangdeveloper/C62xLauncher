package com.chinatsp.drawer;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.widgetcards.R;

import java.util.LinkedList;
import java.util.List;

import launcher.base.recyclerview.BaseEntity;
import launcher.base.recyclerview.MultiStyleRcvAdapter;
import launcher.base.recyclerview.SimpleRcvDecoration;

public class DrawerCreator {
    private RecyclerView mDrawerContainer;

    public DrawerCreator(RecyclerView drawerContainer) {
        mDrawerContainer = drawerContainer;
    }

    public void initDrawerRcv() {
        DrawerContentAdapter adapter = new DrawerContentAdapter(mDrawerContainer.getContext());
        adapter.addEntities(createEntities());
        LinearLayoutManager layoutManager = new LinearLayoutManager(mDrawerContainer.getContext());
        mDrawerContainer.setLayoutManager(layoutManager);
        if (mDrawerContainer.getItemDecorationCount() == 0) {
            SimpleRcvDecoration decoration = new SimpleRcvDecoration(25, layoutManager);
            mDrawerContainer.addItemDecoration(decoration);
        }
        mDrawerContainer.setAdapter(adapter);
    }

    private List<BaseEntity> createEntities() {
        List<BaseEntity> entities = new LinkedList<>();
        entities.add(new BaseEntity(1, R.layout.drawer_item_search));
        entities.add(new BaseEntity(2, R.layout.drawer_item_apps));
        entities.add(new BaseEntity(3, R.layout.drawer_item_iquting));
        entities.add(new BaseEntity(4, R.layout.drawer_item_toutiao));
        entities.add(new BaseEntity(5, R.layout.drawer_item_drive_counselor));
        return entities;
    }
}
