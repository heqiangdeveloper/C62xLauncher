package com.chinatsp.appstore.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.chinatsp.appstore.AppStoreJump;
import com.chinatsp.appstore.R;
import com.chinatsp.appstore.bean.AppInfo;

import launcher.base.recyclerview.BaseViewHolder;
import launcher.base.utils.glide.GlideHelper;
import launcher.base.utils.property.PropertyUtils;
import launcher.base.utils.recent.RecentAppHelper;

public class AppStoreAppsViewHolder extends BaseViewHolder<AppInfo> {
    private static final String TAG = "AppStoreAppsViewHolder";
    private ImageView mIvAppItemIcon;
    private TextView mTvAppItemName;
    private TextView mTvAppItemDesc;
    public AppStoreAppsViewHolder(@NonNull View itemView) {
        super(itemView);
        mIvAppItemIcon = (ImageView) itemView.findViewById(R.id.ivAppItemIcon);
        mTvAppItemName = (TextView) itemView.findViewById(R.id.tvAppItemName);
        mTvAppItemDesc = (TextView) itemView.findViewById(R.id.tvAppItemDesc);
    }

    @Override
    public void bind(int position, AppInfo appInfo) {
        super.bind(position, appInfo);
        String url = appInfo.getIcon();
        String appName = appInfo.getAppName();
        String appDesc = appInfo.getDescription();
        if(appDesc.length() > 7){
            appDesc = appDesc.substring(0,7) + "...";
        }
        String packageName = appInfo.getPackageName();
        if(TextUtils.isEmpty(packageName)){
            packageName = appInfo.getPackageName();
        }
        if(!TextUtils.isEmpty(url)){
            GlideHelper.loadUrlAlbumCoverRadius(mIvAppItemIcon.getContext(), mIvAppItemIcon, url, 0);
        }else {
            GlideHelper.loadLocalAlbumCoverRadius(mIvAppItemIcon.getContext(),mIvAppItemIcon,R.drawable.icon_default_top,0);
        }
        mTvAppItemName.setText(appName);
        mTvAppItemDesc.setText(appDesc);
        String finalPackageName = packageName;
        mIvAppItemIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isPkgInstalled = PropertyUtils.checkPkgInstalled(v.getContext(),finalPackageName);
                Log.d(TAG,"onClick: " + finalPackageName + ",isPkgInstalled: " + isPkgInstalled);
                //如果该应用已经安装就打开它，否则跳转到下载详情
                if(isPkgInstalled){
                    RecentAppHelper.launchApp(v.getContext(), finalPackageName);
                }else {
                    AppStoreJump.jumpAppMarket(finalPackageName, v.getContext());
                }
            }
        });
    }


}
