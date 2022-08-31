package com.chinatsp.drawer;

import android.content.Context;
import android.view.View;

import com.chinatsp.drawer.drive.DrawerDriveCounselorHolder;
import com.chinatsp.drawer.iquting.DrawerIqutingHolder;
import com.chinatsp.drawer.search.DrawerSearchHolder;
import com.chinatsp.drawer.volcano.DrawerVolcanoHolder;

import launcher.base.recyclerview.BaseViewHolder;
import launcher.base.recyclerview.MultiStyleRcvAdapter;

public class DrawerContentAdapter extends MultiStyleRcvAdapter<DrawerEntity> {

    public DrawerContentAdapter(Context context) {
        super(context);
    }

    @Override
    protected BaseViewHolder<DrawerEntity> createViewHolder(View view, int viewType) {
        BaseViewHolder<DrawerEntity> viewHolder = null;
        switch (viewType) {
            case DrawerEntity.TYPE_SEARCH:
                viewHolder = new DrawerSearchHolder(view);
                break;
            case DrawerEntity.TYPE_APPS_AND_WEATHER:
                viewHolder = new DrawerAppsAndWeatherHolder(view);
                break;
            case DrawerEntity.TYPE_IQUTING:
                viewHolder = new DrawerIqutingHolder(view);
                break;
            case DrawerEntity.TYPE_TOUTIAO:
                viewHolder = new DrawerVolcanoHolder(view);
                break;
            case DrawerEntity.TYPE_DRIVE_COUNSELOR:
                viewHolder = new DrawerDriveCounselorHolder(view);
                break;
        }
        return viewHolder;
    }
}
