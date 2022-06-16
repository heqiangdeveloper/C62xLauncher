package com.chinatsp.widgetcards.manager;

import android.content.Context;
import android.content.res.Resources;

import java.util.List;

import card.base.LauncherCard;

public class CardResourceHelp {
    /**
     * 将卡片的资源由String恢复为id
     */
    private static void restoreResId(LauncherCard card, Resources resources, String pkgName) {
        card.setSelectBgRes(resources.getIdentifier(card.getSelectBgResName(), "drawable", pkgName));
        card.setUnselectBgRes(resources.getIdentifier(card.getUnselectBgResName(), "drawable", pkgName));
    }

    /**
     * 批量将卡片id转换为String, 方便数据库存储
     */
    private static void convertResIdToString(LauncherCard card, Resources resources) {
        card.setSelectBgResName(resources.getResourceEntryName(card.getSelectBgRes()));
        card.setUnselectBgResName(resources.getResourceEntryName(card.getUnselectBgRes()));
    }

    /**
     * 将卡片id转换为String, 方便数据库存储
     */
    public static void convertCardsResIdToString(List<LauncherCard> cards, Context context){
        Resources resources = context.getResources();
        String pkg = context.getPackageName();
        for (LauncherCard card : cards) {
            convertResIdToString(card, resources);
        }
    }

    /**
     * 批量将卡片资源恢复为id
     */
    public static void restoreCardsResId(List<LauncherCard> cards, Context context) {
        Resources resources = context.getResources();
        String pkg = context.getPackageName();
        for (LauncherCard card : cards) {
            restoreResId(card, resources, pkg);
        }
    }

}
