package com.chinatsp.widgetcards.manager;

import com.chinatsp.appstore.AppStoreCardView;
import com.chinatsp.driveinfo.DriveCounselorCardView;
import com.chinatsp.econnect.EConnectCardView;
import com.chinatsp.iquting.IQuTingCardView;
import com.chinatsp.musiclauncher.MediaCardView;
import com.chinatsp.navigation.NaviCardView;
import com.chinatsp.phone.widget.BTPhoneCardView;
import com.chinatsp.usercenter.UserCenterCardView;
import com.chinatsp.vehiclesetting.VehicleSettingCardView;
import com.chinatsp.volcano.VolcanoCardView;
import com.chinatsp.weaher.WeatherCardView;

import card.base.ICardViewCreator;

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
            case CardManager.CardType.VOLCANO:
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
//        return WeatherCardViewTest::new;
    }

    private static ICardViewCreator createVideo() {
        return VolcanoCardView::new;
    }

    private static ICardViewCreator createIQuTing() {
        return IQuTingCardView::new;
    }

    private static ICardViewCreator createPhone() {
        return BTPhoneCardView::new;
    }

    private static ICardViewCreator createVehicleSetting() {
        return VehicleSettingCardView::new;
    }

    private static ICardViewCreator createEConnect() {
        return EConnectCardView::new;
    }

    private static ICardViewCreator createDriveCounselor() {
        return DriveCounselorCardView::new;
    }

    private static ICardViewCreator createUserCenter() {
        return UserCenterCardView::new;
    }

    private static ICardViewCreator createAppStore() {
        return AppStoreCardView::new;
    }

    private static ICardViewCreator createEmpty() {
        return null;
    }

    private static ICardViewCreator createDemo() {
        return null;
    }

    private static ICardViewCreator createMedia() {
        return MediaCardView::new;
    }

    private static ICardViewCreator createNavi() {
        return NaviCardView::new;
    }
}
