package com.chinatsp.drawer;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.widgetcards.R;

import java.util.LinkedList;
import java.util.List;

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

    private List<DrawerEntity> createEntities() {
        List<DrawerEntity> entities = new LinkedList<>();
        entities.add(new DrawerEntity(DrawerEntity.TYPE_SEARCH, R.layout.drawer_item_search));
        entities.add(new DrawerEntity(DrawerEntity.TYPE_APPS_AND_WEATHER, R.layout.drawer_item_apps_and_weather));
        entities.add(new DrawerEntity(DrawerEntity.TYPE_IQUTING, R.layout.drawer_item_iquting));
        entities.add(new DrawerEntity(DrawerEntity.TYPE_TOUTIAO, R.layout.drawer_item_toutiao));
        entities.add(new DrawerEntity(DrawerEntity.TYPE_DRIVE_COUNSELOR, R.layout.drawer_item_drive_counselor));
        return entities;
    }
}
