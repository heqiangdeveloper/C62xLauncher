package com.chinatsp.appstore.adapter;

import android.content.Context;
import android.view.View;

import com.chinatsp.appstore.R;
import com.chinatsp.appstore.bean.AppInfo;

import launcher.base.recyclerview.BaseRcvAdapter;
import launcher.base.recyclerview.BaseViewHolder;

public class AppStoreAppsAdapter extends BaseRcvAdapter<AppInfo> {
    private AppStoreAppsViewHolder mAppStoreAppsViewHolder;
    public AppStoreAppsAdapter(Context context) {
        super(context);
    }
    private final static int MAXSONGS = 6;//最大6个

    @Override
    protected int getLayoutRes() {
        return R.layout.item_appstore_apps;
    }

    @Override
    protected BaseViewHolder<AppInfo> createViewHolder(View view) {
        mAppStoreAppsViewHolder = new AppStoreAppsViewHolder(view);
        return mAppStoreAppsViewHolder;
    }

    @Override
    public int getItemCount() {
        if(getData().size() > MAXSONGS){
            return MAXSONGS;
        }
        return super.getItemCount();
    }
}
