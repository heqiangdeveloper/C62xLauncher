package com.chinatsp.widgetcards.manager;

import android.content.Context;
import android.view.View;

import com.chinatsp.douyin.DouyinCardView;
import com.chinatsp.drivecounselor.DriveCounselorCardView;
import com.chinatsp.econnect.EConnectCardView;
import com.chinatsp.iquting.IQuTingCardView;
import com.chinatsp.navigation.NaviCardLargeView;
import com.chinatsp.navigation.NaviCardView;
import com.chinatsp.weaher.WeatherCardView;

import card.base.ICardViewCreator;
import card.base.LauncherCard;

public class CardViewCreatorFactory {
    public static ICardViewCreator create(int type) {
        ICardViewCreator creator = null;
        switch (type) {
            case CardManager.CardType.NAVIGATION:
                creator = createNavi();
                break;
            case CardManager.CardType.MEDIA:
                creator = createMedia();
                break;
            case CardManager.CardType.I_QU_TING:
                creator = createIQuTing();
                break;
            case CardManager.CardType.VIDEO:
                creator = createVideo();
                break;
            case CardManager.CardType.WEATHER:
                creator = createWeather();
                break;
            case CardManager.CardType.PHONE:
                creator = createPhone();
                break;
            case CardManager.CardType.VEHICLE_SETTING:
                creator = createVehicleSetting();
                break;
            case CardManager.CardType.E_CONNECT:
                creator = createEConnect();
                break;
            case CardManager.CardType.DRIVE_COUNSELOR:
                creator = createDriveCounselor();
                break;
            case CardManager.CardType.USER_CENTER:
                creator = createUserCenter();
                break;
            case CardManager.CardType.APP_STORE:
                creator = createAppStore();
                break;
            case CardManager.CardType.EMPTY:
                creator = createEmpty();
                break;
            case CardManager.CardType.DEMO:
            default:
                creator = createDemo();
                break;
        }
        return creator;
    }

    private static ICardViewCreator createWeather() {
        return WeatherCardView::new;
    }

    private static ICardViewCreator createVideo() {
        return DouyinCardView::new;
    }

    private static ICardViewCreator createIQuTing() {
        return IQuTingCardView::new;
    }

    private static ICardViewCreator createPhone() {
        return null;
    }

    private static ICardViewCreator createVehicleSetting() {
        return null;
    }

    private static ICardViewCreator createEConnect() {
        return EConnectCardView::new;
    }

    private static ICardViewCreator createDriveCounselor() {
        return DriveCounselorCardView::new;
    }

    private static ICardViewCreator createUserCenter() {
        return null;
    }

    private static ICardViewCreator createAppStore() {
        return null;
    }

    private static ICardViewCreator createEmpty() {
        return null;
    }

    private static ICardViewCreator createDemo() {
        return null;
    }

    private static ICardViewCreator createMedia() {
        return null;
    }

    private static ICardViewCreator createNavi() {
        return NaviCardView::new;
    }
}
