package com.chinatsp.widgetcards.service;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.chinatsp.entity.DemoCardEntity;
import com.chinatsp.entity.DouyinCardEntity;
import com.chinatsp.entity.DriveCounselorEntity;
import com.chinatsp.entity.EConnectEntity;
import com.chinatsp.entity.IQuTingCardEntity;
import com.chinatsp.entity.NavigationCardEntity;
import com.chinatsp.entity.WeatherCardEntity;
import com.chinatsp.widgetcards.CardHomeFragment;
import com.chinatsp.widgetcards.R;
import com.chinatsp.entity.BaseCardEntity;
import com.chinatsp.entity.DefaultCardEntity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import launcher.base.utils.EasyLog;

public class CardsTypeManager {
    private static final String TAG = "CardsTypeManager";

    private CardsTypeManager() {

    }


    private static class Holder {
        public static CardsTypeManager manager = new CardsTypeManager();
    }

    public static CardsTypeManager getInstance() {
        return Holder.manager;
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface CardType {
        int NAVIGATION = 1;
        int MEDIA = 2;
        int RADIO = 3;
        int VIDEO = 4;
        int WEATHER = 5;
        int PHONE = 6;
        int VEHICLE_SETTING = 7;
        int E_CONNECT = 8;
        int XINGCHE_QA = 9;
        int USER_CENTER = 10;
        int APP_STORE = 11;
        int EMPTY = 100;
        int DEMO = 101;
    }

    public static final int MAX_SHOWING = 6;

    private Map<Integer, BaseCardEntity> mHomeCardMap = new HashMap<>();
    private final List<BaseCardEntity> mHomeCardList = new LinkedList<>();
    private final List<BaseCardEntity> mUnselectCardList = new LinkedList<>();
    private MutableLiveData<List<BaseCardEntity>> mHomeCardsLiveData = new MutableLiveData<>();

    public List<BaseCardEntity> getHomeList() {
        if (mHomeCardList.isEmpty()) {
            createInitCards();
        }
        EasyLog.d(TAG, "HomeList size:" + mHomeCardList.size());
        return mHomeCardList;
    }
    public List<BaseCardEntity> getUnselectCardList() {
        if (mUnselectCardList.isEmpty()) {
            createUnselectCards();
        }
        EasyLog.d(TAG, "HomeList size:" + mUnselectCardList.size());
        return mUnselectCardList;
    }

    public BaseCardEntity findByType(int type) {
        return mHomeCardMap.get(type);
    }

    private void addHomeCard(BaseCardEntity cardEntity) {
        mHomeCardMap.put(cardEntity.getType(), cardEntity);
        mHomeCardList.add(cardEntity);
    }
    private void addUnselectCard(BaseCardEntity cardEntity) {
        mUnselectCardList.add(cardEntity);
    }


    public List<BaseCardEntity> createInitCards() {
        mHomeCardList.clear();
        addHomeCard(new NavigationCardEntity().setName("导航").setType(CardType.NAVIGATION)
                .setSelectBgRes(R.drawable.card_edit_select_navi)
                .setUnselectBgRes(R.drawable.card_edit_unselect_navi)
        );
        addHomeCard(new IQuTingCardEntity().setName("腾讯爱趣听").setType(CardType.RADIO)
                .setSelectBgRes(R.drawable.card_edit_select_iquting)
                .setUnselectBgRes(R.drawable.card_edit_unselect_iquting)
        );
        addHomeCard(new DouyinCardEntity().setName("火山车娱").setType(CardType.VIDEO)
                .setSelectBgRes(R.drawable.card_edit_select_douyin)
                .setUnselectBgRes(R.drawable.card_edit_unselect_douyin)
        );
        addHomeCard(new WeatherCardEntity().setName("天气").setType(CardType.WEATHER)
                .setSelectBgRes(R.drawable.card_edit_select_weather)
                .setUnselectBgRes(R.drawable.card_edit_unselect_weather)
        );
        addHomeCard(new EConnectEntity().setName("亿联").setType(CardType.E_CONNECT)
                .setSelectBgRes(R.drawable.card_edit_select_e_connect)
                .setUnselectBgRes(R.drawable.card_edit_unselect_e_connect)
                .setCanExpand(false)
        );
        addHomeCard(new DriveCounselorEntity().setName("行车顾问").setType(CardType.XINGCHE_QA)
                .setCanExpand(false)
                .setSelectBgRes(R.drawable.card_edit_select_drive_qa)
                .setUnselectBgRes(R.drawable.card_edit_unselect_drive_qa)
        );
//        addHomeCard(new DemoCardEntity().setName("DemoCard").setType(CardType.DEMO)
//                .setCanExpand(true)
//        );
        return mHomeCardList;
    }

    public void refreshHomeList() {
        mHomeCardsLiveData.postValue(mHomeCardList);
    }
    public List<BaseCardEntity> createUnselectCards() {
        mUnselectCardList.clear();
        addUnselectCard(new DefaultCardEntity().setName("个人中心").setType(CardType.USER_CENTER)
                .setSelectBgRes(R.drawable.card_edit_select_user_center)
                .setUnselectBgRes(R.drawable.card_edit_unselect_user_center)
        );
        addUnselectCard(new DefaultCardEntity().setName("车辆设置").setType(CardType.VEHICLE_SETTING)
                .setSelectBgRes(R.drawable.card_edit_select_vehicle_setting)
                .setUnselectBgRes(R.drawable.card_edit_unselect_vehicle_setting)
        );
        addUnselectCard(new DefaultCardEntity().setName("电话").setType(CardType.PHONE)
                .setSelectBgRes(R.drawable.card_edit_select_phone)
                .setUnselectBgRes(R.drawable.card_edit_unselect_phone)
        );
        addUnselectCard(new DefaultCardEntity().setName("多媒体").setType(CardType.MEDIA)
                .setSelectBgRes(R.drawable.card_edit_select_media)
                .setUnselectBgRes(R.drawable.card_edit_unselect_media)
        );
        addUnselectCard(new DefaultCardEntity().setName("应用商城").setType(CardType.APP_STORE)
                .setSelectBgRes(R.drawable.card_edit_select_app_store)
                .setUnselectBgRes(R.drawable.card_edit_unselect_app_store)
        );
        addUnselectCard(new DefaultCardEntity().setName("空").setType(CardType.EMPTY)
                .setSelectBgRes(R.drawable.card_edit_unselect_default)
                .setUnselectBgRes(R.drawable.card_edit_unselect_default)
        );
        addUnselectCard(new DefaultCardEntity().setName("空").setType(CardType.EMPTY)
                .setSelectBgRes(R.drawable.card_edit_unselect_default)
                .setUnselectBgRes(R.drawable.card_edit_unselect_default)
        );
        addUnselectCard(new DefaultCardEntity().setName("空").setType(CardType.EMPTY)
                .setSelectBgRes(R.drawable.card_edit_unselect_default)
                .setUnselectBgRes(R.drawable.card_edit_unselect_default)
        );
        addUnselectCard(new DefaultCardEntity().setName("空").setType(CardType.EMPTY)
                .setSelectBgRes(R.drawable.card_edit_unselect_default)
                .setUnselectBgRes(R.drawable.card_edit_unselect_default)
        );
        return mUnselectCardList;
    }

    public void registerHomeCardsOb(LifecycleOwner lifecycleOwner, Observer<List<BaseCardEntity>> homeCardsOb) {
        mHomeCardsLiveData.observe(lifecycleOwner, homeCardsOb);
    }

    public void unregisterHomeCardsOb(Observer<List<BaseCardEntity>> homeCardsOb) {
        mHomeCardsLiveData.removeObserver(homeCardsOb);
    }
}
