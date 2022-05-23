package com.chinatsp.widgetcards.service;

import com.chinatsp.entity.DouyinCardEntity;
import com.chinatsp.entity.NavigationCardEntity;
import com.chinatsp.entity.WeatherCardEntity;
import com.chinatsp.widgetcards.adapter.BaseCardEntity;
import com.chinatsp.widgetcards.adapter.DefaultCardEntity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CardsTypeManager {
    private CardsTypeManager() {

    }

    private static class  Holder{
        public static CardsTypeManager manager = new CardsTypeManager();
    }
    public static CardsTypeManager getInstance() {
        return Holder.manager;
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface CardType {
        int NAVIGATION = 1;
        int MUSIC = 2;
        int RADIO = 3;
        int VIDEO = 4;
        int WEATHER = 5;
        int PHONE = 6;
        int VEHICLE= 7;
        int YI_CONNECT = 8;
        int XINGCHE_QA = 9;
    }

    public static final int MAX_SHOWING = 6;

    private Map<Integer, BaseCardEntity> mCardMap = new HashMap<>();
    private final List<BaseCardEntity> mCardEntityList = new LinkedList<>();

    public List<BaseCardEntity> createInitCards(){
        mCardEntityList.clear();
        addCard(new NavigationCardEntity().setName("导航").setType(CardType.NAVIGATION));
        addCard(new DefaultCardEntity().setName("爱趣听").setType(CardType.RADIO));
        addCard(new DouyinCardEntity().setName("火山车娱").setType(CardType.VIDEO));
        addCard(new WeatherCardEntity().setName("天气").setType(CardType.WEATHER));
        addCard(new DefaultCardEntity().setName("亿联").setType(CardType.YI_CONNECT));
        addCard(new DefaultCardEntity().setName("行车顾问").setType(CardType.XINGCHE_QA));
        return mCardEntityList;
    }

    public BaseCardEntity findByType(int type) {
        return mCardMap.get(type);
    }

    private void addCard(BaseCardEntity cardEntity) {
        mCardMap.put(cardEntity.getType(), cardEntity);
        mCardEntityList.add(cardEntity);
    }
}
