package com.chinatsp.appstore.utils;

import android.content.Context;
import android.util.Log;

import com.chinatsp.appstore.AppStoreJump;

import launcher.base.utils.property.PropertyUtils;
import launcher.base.utils.recent.RecentAppHelper;

public class AppStoreUtils {
    public static void jump(Context context, String pkgName){
        boolean isPkgInstalled = PropertyUtils.checkPkgInstalled(context,pkgName);
        //如果该应用已经安装就打开它，否则跳转到下载详情
        if(isPkgInstalled){
            RecentAppHelper.launchApp(context, pkgName);
        }else {
            AppStoreJump.jumpAppMarket(pkgName, context);
        }
    }
}
