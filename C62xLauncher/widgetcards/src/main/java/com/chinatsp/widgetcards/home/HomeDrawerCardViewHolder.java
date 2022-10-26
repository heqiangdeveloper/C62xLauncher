package com.chinatsp.widgetcards.home;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.drawer.DrawerContentAdapter;
import com.chinatsp.drawer.DrawerEntity;
import com.chinatsp.widgetcards.R;

import java.util.LinkedList;
import java.util.List;

import launcher.base.recyclerview.SimpleRcvDecoration;
import launcher.base.utils.EasyLog;

public class HomeDrawerCardViewHolder extends RecyclerView.ViewHolder {

    private RecyclerView rcvDrawerContent;
    private DrawerContentAdapter mAdapter;

    public HomeDrawerCardViewHolder(@NonNull View itemView) {
        super(itemView);
        EasyLog.i("HomeDrawerCardViewHolder", "init "+hashCode());
        rcvDrawerContent = itemView.findViewById(R.id.rcvDrawerContent);
        initDrawerRcv();
    }
    public void initDrawerRcv() {
        mAdapter = new DrawerContentAdapter(rcvDrawerContent.getContext());
        mAdapter.addEntities(createEntities());
        LinearLayoutManager layoutManager = new LinearLayoutManager(rcvDrawerContent.getContext());
        rcvDrawerContent.setLayoutManager(layoutManager);
        if (rcvDrawerContent.getItemDecorationCount() == 0) {
            SimpleRcvDecoration decoration = new SimpleRcvDecoration(25, layoutManager);
            rcvDrawerContent.addItemDecoration(decoration);
        }
        rcvDrawerContent.setAdapter(mAdapter);
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

    public void bind(int position) {
        EasyLog.i("HomeDrawerCardViewHolder", "bind "+hashCode());
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }
}
