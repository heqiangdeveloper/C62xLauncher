package com.chinatsp.widgetcards.home;

import android.content.Context;
import android.text.TextUtils;

import com.chinatsp.widgetcards.R;
import com.chinatsp.widgetcards.manager.CardManager;

import card.base.LauncherCard;
import launcher.base.applists.AppLists;
import launcher.base.utils.recent.RecentAppHelper;

public class AppLauncherUtil {
    public static void start(Context context, int type) {
        if (context == null ) {
            return;
        }
        String pkgName = null;
        switch (type) {
            case CardManager.CardType.NAVIGATION:
                pkgName = AppLists.amap;
                break;
            case CardManager.CardType.MEDIA:
                pkgName = AppLists.media;
                break;
            case CardManager.CardType.I_QU_TING:
                pkgName = AppLists.iquting;
                break;
            case CardManager.CardType.VOLCANO:
                pkgName = AppLists.volcano;
                break;
            case CardManager.CardType.WEATHER:
                pkgName = AppLists.weather;
                break;
            case CardManager.CardType.PHONE:
                pkgName = AppLists.btPhone;
                break;
            case CardManager.CardType.VEHICLE_SETTING:
                pkgName = AppLists.vehicleSettings;
                break;
            case CardManager.CardType.E_CONNECT:
                pkgName = AppLists.easyconn;
                break;
            case CardManager.CardType.DRIVE_COUNSELOR:
                pkgName = AppLists.iot;
                break;
            case CardManager.CardType.USER_CENTER:
                pkgName = AppLists.usercenter;
                break;
            case CardManager.CardType.APP_STORE:
                pkgName = AppLists.appmarket;
                break;
            case CardManager.CardType.DEMO:
            default:
                break;
        }
        if (!TextUtils.isEmpty(pkgName)) {
            RecentAppHelper.launchApp(context, pkgName);
        }
    }
}
