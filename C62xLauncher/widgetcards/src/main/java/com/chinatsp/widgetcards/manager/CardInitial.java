package com.chinatsp.widgetcards.manager;

import android.content.Context;

import java.util.LinkedList;
import java.util.List;

import card.base.LauncherCard;

public class CardInitial {
    public List<LauncherCard> createInitCards(Context context) {
        List<LauncherCard> cardList = new LinkedList<>();
        cardList.add(CardEntityFactory.create(CardManager.CardType.NAVIGATION));
        cardList.add(CardEntityFactory.create(CardManager.CardType.I_QU_TING));
        cardList.add(CardEntityFactory.create(CardManager.CardType.VOLCANO));
        cardList.add(CardEntityFactory.create(CardManager.CardType.WEATHER));
        cardList.add(CardEntityFactory.create(CardManager.CardType.E_CONNECT));
        cardList.add(CardEntityFactory.create(CardManager.CardType.DRIVE_COUNSELOR));
//        cardList.add(new DemoCardEntity().setName("DemoCard").setType(CardType.DEMO)
//                .setCanExpand(true)
//        );
        for (LauncherCard launcherCard : cardList) {
            launcherCard.setInHome(true);
        }
        return cardList;
    }
    public List<LauncherCard> createUnselectCards(Context context) {
        List<LauncherCard> cardList = new LinkedList<>();
        cardList.add(CardEntityFactory.create(CardManager.CardType.USER_CENTER));
        cardList.add(CardEntityFactory.create(CardManager.CardType.VEHICLE_SETTING));
        cardList.add(CardEntityFactory.create(CardManager.CardType.PHONE));
        cardList.add(CardEntityFactory.create(CardManager.CardType.MEDIA));
        cardList.add(CardEntityFactory.create(CardManager.CardType.APP_STORE));
        return cardList;
    }

}
