package com.chinatsp.widgetcards.manager;

import com.chinatsp.widgetcards.R;

import card.base.ICardViewCreator;

public class CardNameRes {
    public static int getStringRes(int cardType) {
        int res = R.string.card_name_default;
        switch (cardType) {
            case CardManager.CardType.NAVIGATION:
                res = R.string.card_name_navigation;
                break;
            case CardManager.CardType.MEDIA:
                res = R.string.card_name_media;
                break;
            case CardManager.CardType.I_QU_TING:
                res = R.string.card_name_iquting;
                break;
            case CardManager.CardType.VOLCANO:
                res = R.string.card_name_volcano;
                break;
            case CardManager.CardType.WEATHER:
                res = R.string.card_name_weather;;
                break;
            case CardManager.CardType.PHONE:
                res = R.string.card_name_phone;
                break;
            case CardManager.CardType.VEHICLE_SETTING:
                res = R.string.card_name_vehicle_setting;
                break;
            case CardManager.CardType.E_CONNECT:
                res = R.string.card_name_e_connection;
                break;
            case CardManager.CardType.DRIVE_COUNSELOR:
                res = R.string.card_name_drive_consultant;
                break;
            case CardManager.CardType.USER_CENTER:
                res = R.string.card_name_user_center;
                break;
            case CardManager.CardType.APP_STORE:
                res = R.string.card_name_app_store;
                break;
            case CardManager.CardType.DEMO:
            default:
                break;
        }
        return res;
    }
}
