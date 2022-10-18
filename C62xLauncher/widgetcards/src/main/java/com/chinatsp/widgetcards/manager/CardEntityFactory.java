package com.chinatsp.widgetcards.manager;

import android.content.Context;
import android.view.View;

import com.chinatsp.driveinfo.DriveCounselorCardView;
import com.chinatsp.econnect.EConnectCardView;
import com.chinatsp.iquting.IQuTingCardView;
import com.chinatsp.weaher.WeatherCardView;
import com.chinatsp.widgetcards.R;

import card.base.ICardViewCreator;
import card.base.LauncherCard;

public class CardEntityFactory {
    public static LauncherCard create(int type) {
        LauncherCard entity = null;
        switch (type) {
            case CardManager.CardType.NAVIGATION:
                entity = createNavi();
                break;
            case CardManager.CardType.MEDIA:
                entity = createMedia();
                break;
            case CardManager.CardType.I_QU_TING:
                entity = createIQuTing();
                break;
            case CardManager.CardType.VOLCANO:
                entity = createVideo();
                break;
            case CardManager.CardType.WEATHER:
                entity = createWeather();
                break;
            case CardManager.CardType.PHONE:
                entity = createPhone();
                break;
            case CardManager.CardType.VEHICLE_SETTING:
                entity = createVehicleSetting();
                break;
            case CardManager.CardType.E_CONNECT:
                entity = createEConnect();
                break;
            case CardManager.CardType.DRIVE_COUNSELOR:
                entity = createDriveCounselor();
                break;
            case CardManager.CardType.USER_CENTER:
                entity = createUserCenter();
                break;
            case CardManager.CardType.APP_STORE:
                entity = createAppStore();
                break;
            case CardManager.CardType.EMPTY:
                entity = createEmpty();
                break;
            case CardManager.CardType.DEMO:
            default:
                entity = createDemo();
                break;
        }
        return entity;
    }

    private static LauncherCard createDemo() {
        LauncherCard cardEntity = new LauncherCard();
        cardEntity.setName("Demo卡片");
        cardEntity.setType(CardManager.CardType.EMPTY);
        cardEntity.setCanExpand(true);
        return cardEntity;
    }

    private static LauncherCard createEmpty() {
        LauncherCard cardEntity = new LauncherCard();
        cardEntity.setName("空");
        cardEntity.setType(CardManager.CardType.EMPTY);
        cardEntity.setSelectBgRes(R.drawable.card_edit_unselect_default);
        cardEntity.setUnselectBgRes(R.drawable.card_edit_unselect_default);
        cardEntity.setCanExpand(false);
        return cardEntity;
    }

    private static LauncherCard createAppStore() {
        LauncherCard cardEntity = new LauncherCard();
        cardEntity.setName("应用商城");
        cardEntity.setType(CardManager.CardType.APP_STORE);
        cardEntity.setSelectBgRes(R.drawable.card_edit_select_app_store);
        cardEntity.setUnselectBgRes(R.drawable.card_edit_unselect_app_store);
        cardEntity.setCanExpand(true);
        cardEntity.setCardViewCreator(CardViewCreatorFactory.create(CardManager.CardType.APP_STORE));
        return cardEntity;
    }

    private static LauncherCard createUserCenter() {
        LauncherCard cardEntity = new LauncherCard();
        cardEntity.setName("用户中心");
        cardEntity.setType(CardManager.CardType.USER_CENTER);
        cardEntity.setSelectBgRes(R.drawable.card_edit_select_user_center);
        cardEntity.setUnselectBgRes(R.drawable.card_edit_unselect_user_center);
        cardEntity.setCanExpand(false);
        cardEntity.setCardViewCreator(CardViewCreatorFactory.create(CardManager.CardType.USER_CENTER));
        return cardEntity;
    }

    private static LauncherCard createDriveCounselor() {
        LauncherCard cardEntity = new LauncherCard();
        cardEntity.setName("行车顾问");
        cardEntity.setType(CardManager.CardType.DRIVE_COUNSELOR);
        cardEntity.setSelectBgRes(R.drawable.card_edit_select_drive_qa);
        cardEntity.setUnselectBgRes(R.drawable.card_edit_unselect_drive_qa);
        cardEntity.setCanExpand(false);
        cardEntity.setCardViewCreator(CardViewCreatorFactory.create(CardManager.CardType.DRIVE_COUNSELOR));
        return cardEntity;
    }

    private static LauncherCard createVehicleSetting() {
        LauncherCard cardEntity = new LauncherCard();
        cardEntity.setName("车辆设置");
        cardEntity.setType(CardManager.CardType.VEHICLE_SETTING);
        cardEntity.setSelectBgRes(R.drawable.card_edit_select_vehicle_setting);
        cardEntity.setUnselectBgRes(R.drawable.card_edit_unselect_vehicle_setting);
        cardEntity.setCanExpand(false);
        cardEntity.setCardViewCreator(CardViewCreatorFactory.create(CardManager.CardType.VEHICLE_SETTING));
        return cardEntity;
    }


    private static LauncherCard createEConnect() {
        LauncherCard cardEntity = new LauncherCard();
        cardEntity.setName("亿联");
        cardEntity.setType(CardManager.CardType.E_CONNECT);
        cardEntity.setSelectBgRes(R.drawable.card_edit_select_e_connect);
        cardEntity.setUnselectBgRes(R.drawable.card_edit_unselect_e_connect);
        cardEntity.setCanExpand(false);
        cardEntity.setCardViewCreator(CardViewCreatorFactory.create(CardManager.CardType.E_CONNECT));
        return cardEntity;
    }

    private static LauncherCard createPhone() {
        LauncherCard cardEntity = new LauncherCard();
        cardEntity.setName("电话");
        cardEntity.setType(CardManager.CardType.PHONE);
        cardEntity.setSelectBgRes(R.drawable.card_edit_select_phone);
        cardEntity.setUnselectBgRes(R.drawable.card_edit_unselect_phone);
        cardEntity.setCanExpand(true);
        cardEntity.setCardViewCreator(CardViewCreatorFactory.create(CardManager.CardType.PHONE));
        return cardEntity;
    }

    private static LauncherCard createWeather() {
        LauncherCard cardEntity = new LauncherCard();
        cardEntity.setName("天气");
        cardEntity.setType(CardManager.CardType.WEATHER);
        cardEntity.setSelectBgRes(R.drawable.card_edit_select_weather);
        cardEntity.setUnselectBgRes(R.drawable.card_edit_unselect_weather);
        cardEntity.setCanExpand(true);
        cardEntity.setCardViewCreator(CardViewCreatorFactory.create(CardManager.CardType.WEATHER));
        return cardEntity;
    }

    private static LauncherCard createIQuTing() {
        LauncherCard cardEntity = new LauncherCard();
        cardEntity.setName("腾讯爱趣听");
        cardEntity.setType(CardManager.CardType.I_QU_TING);
        cardEntity.setSelectBgRes(R.drawable.card_edit_select_iquting);
        cardEntity.setUnselectBgRes(R.drawable.card_edit_unselect_iquting);
        cardEntity.setCanExpand(true);
        cardEntity.setCardViewCreator(CardViewCreatorFactory.create(CardManager.CardType.I_QU_TING));
        return cardEntity;
    }

    private static LauncherCard createVideo() {
        LauncherCard cardEntity = new LauncherCard();
        cardEntity.setName("火山车娱");
        cardEntity.setType(CardManager.CardType.VOLCANO);
        cardEntity.setSelectBgRes(R.drawable.card_edit_select_douyin);
        cardEntity.setUnselectBgRes(R.drawable.card_edit_unselect_douyin);
        cardEntity.setCanExpand(true);
        cardEntity.setCardViewCreator(CardViewCreatorFactory.create(CardManager.CardType.VOLCANO));
        return cardEntity;
    }


    private static LauncherCard createNavi() {
        LauncherCard cardEntity = new LauncherCard();
        cardEntity.setName("导航");
        cardEntity.setType(CardManager.CardType.NAVIGATION);
        cardEntity.setSelectBgRes(R.drawable.card_edit_select_navi);
        cardEntity.setUnselectBgRes(R.drawable.card_edit_unselect_navi);
        cardEntity.setCanExpand(true);
        cardEntity.setCardViewCreator(CardViewCreatorFactory.create(CardManager.CardType.NAVIGATION));
        return cardEntity;
    }

    private static LauncherCard createMedia() {
        LauncherCard cardEntity = new LauncherCard();
        cardEntity.setName("多媒体");
        cardEntity.setType(CardManager.CardType.MEDIA);
        cardEntity.setSelectBgRes(R.drawable.card_edit_select_media);
        cardEntity.setUnselectBgRes(R.drawable.card_edit_unselect_media);
        cardEntity.setCanExpand(true);
        cardEntity.setCardViewCreator(CardViewCreatorFactory.create(CardManager.CardType.MEDIA));
        return cardEntity;
    }

}
