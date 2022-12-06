package com.chinatsp.widgetcards.editor;

import com.chinatsp.widgetcards.R;
import com.chinatsp.widgetcards.manager.CardManager;

public class CardEditorResUtil {
    public static int getIcon(int type) {
        int res = 0;
        switch (type) {
            case CardManager.CardType.NAVIGATION:
                res = R.drawable.card_edit_logo_navigation;
                break;
            case CardManager.CardType.MEDIA:
                res = R.drawable.card_edit_logo_media;
                break;
            case CardManager.CardType.I_QU_TING:
                res = R.drawable.card_edit_logo_iquting;
                break;
            case CardManager.CardType.VOLCANO:
                res = R.drawable.card_edit_logo_volcano;
                break;
            case CardManager.CardType.WEATHER:
                res = R.drawable.card_edit_logo_weather;
                break;
            case CardManager.CardType.PHONE:
                res = R.drawable.card_edit_logo_phone;
                break;
            case CardManager.CardType.VEHICLE_SETTING:
                res = R.drawable.card_edit_logo_vehicle_setting;
                break;
            case CardManager.CardType.E_CONNECT:
                res = R.drawable.card_edit_logo_econnect;
                break;
            case CardManager.CardType.DRIVE_COUNSELOR:
                res = R.drawable.card_edit_logo_drive_consultant;
                break;
            case CardManager.CardType.USER_CENTER:
                res = R.drawable.card_edit_logo_user_center;
                break;
            case CardManager.CardType.APP_STORE:
                res = R.drawable.card_edit_logo_appstore;
                break;
            case CardManager.CardType.DEMO:
            default:
                break;
        }
        return res;
    }
}
