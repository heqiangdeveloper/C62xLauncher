package com.chinatsp.widgetcards.manager;

import android.content.Context;
import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import card.base.LauncherCard;
import card.db.CardDataBaseService;
import card.db.IQueryListener;
import launcher.base.utils.EasyLog;

public class CardManager {
    private static final String TAG = "CardManager";

    private CardManager() {}

    private static class Holder {
        public static CardManager manager = new CardManager();
    }

    public static CardManager getInstance() {
        return Holder.manager;
    }


    @Retention(RetentionPolicy.SOURCE)
    public @interface CardType {
        int NAVIGATION = 1;
        int MEDIA = 2;
        int I_QU_TING = 3;
        int VOLCANO = 4;
        int WEATHER = 5;
        int PHONE = 6;
        int VEHICLE_SETTING = 7;
        int E_CONNECT = 8;
        int DRIVE_COUNSELOR = 9;
        int USER_CENTER = 10;
        int APP_STORE = 11;
        int EMPTY = 100;
        int DEMO = 101;
    }

    private CardDataBaseService mCardDataBaseService;

    private Map<Integer, LauncherCard> mCardMap = new HashMap<>();
    private final List<LauncherCard> mHomeCardList = new LinkedList<>();
    private final List<LauncherCard> mUnselectCardList = new LinkedList<>();
    private MutableLiveData<List<LauncherCard>> mHomeCardsLiveData = new MutableLiveData<>();
    private Context mContext;

    public void init(Context context) {
        this.mContext = context;
        mCardDataBaseService = new CardDataBaseService(context);
        loadCards();
    }

    /**
     * 加载卡片. 尝试从数据库加载.
     */
    private void loadCards() {
        EasyLog.d(TAG, "loadCards");
        mCardDataBaseService.getAllCards(new IQueryListener() {
            @Override
            public void onSuccess(List<LauncherCard> cardList) {
                onLoadFromDB(cardList);
            }
        });
    }

    /**
     * 从数据加载完成.
     *
     * @param cardList 如果为空, 则说明数据库无卡片内容. 需要创建初始卡片
     */
    private void onLoadFromDB(@NonNull List<LauncherCard> cardList) {
        EasyLog.d(TAG, "onLoadFromDB : " + cardList.size());
        if (cardList.isEmpty()) {
            createInitCards(mContext);
            createUnselectCards(mContext);
            CardResourceHelp.convertCardsResIdToString(mHomeCardList, mContext);
            CardResourceHelp.convertCardsResIdToString(mUnselectCardList, mContext);
            saveCardsToDataBase(false);
            // 创建完初始卡片之后, 保存到数据库
        } else {
            // 从数据库读取到卡片, 将其分拆为home和未选中两个列表
            parseCardFromDB(cardList);
            CardResourceHelp.restoreCardsResId(cardList, mContext);
        }
        putCardsToCache();
        mHomeCardsLiveData.postValue(mHomeCardList);
    }

    public void saveCardsToDataBase(boolean update) {
        List<LauncherCard> totalCards = new LinkedList<>();
        totalCards.addAll(mHomeCardList);
        totalCards.addAll(mUnselectCardList);
        for (int i = 0; i < mHomeCardList.size(); i++) {
            LauncherCard homeCard = mHomeCardList.get(i);
            homeCard.setInHome(true);
        }
        for (int i = 0; i < mUnselectCardList.size(); i++) {
            LauncherCard homeCard = mUnselectCardList.get(i);
            homeCard.setInHome(false);
        }
        for (int i = 0; i < totalCards.size(); i++) {
            LauncherCard card = totalCards.get(i);
            card.setPosition(i);
        }
        if (update) {
            mCardDataBaseService.updateCards(totalCards);
        } else {
            mCardDataBaseService.saveCards(totalCards);
        }
    }

    private void parseCardFromDB(List<LauncherCard> cardList) {
        mHomeCardList.clear();
        mUnselectCardList.clear();
        for (LauncherCard launcherCard : cardList) {
            launcherCard.setCardViewCreator(CardViewCreatorFactory.create(launcherCard.getType()));
            if (launcherCard.isInHome()) {
                mHomeCardList.add(launcherCard);
            } else {
                mUnselectCardList.add(launcherCard);
            }
        }
        mHomeCardList.sort(cardSortComparator);
        mUnselectCardList.sort(cardSortComparator);
        EasyLog.d(TAG, "parseCardFromDB home");
        for (LauncherCard launcherCard : mHomeCardList) {
            EasyLog.i(TAG, launcherCard.getName());
        }
        EasyLog.d(TAG, "parseCardFromDB unselect");
        for (LauncherCard launcherCard : mUnselectCardList) {
            EasyLog.i(TAG, launcherCard.getName());
        }
    }

    public List<LauncherCard> getHomeList() {
        return mHomeCardList;
    }

    public List<LauncherCard> getUnselectCardList() {
        return mUnselectCardList;
    }

    private void putCardsToCache() {
        for (LauncherCard baseCardEntity : mHomeCardList) {
            mCardMap.put(baseCardEntity.getType(), baseCardEntity);
        }
        for (LauncherCard baseCardEntity : mUnselectCardList) {
            mCardMap.put(baseCardEntity.getType(), baseCardEntity);
        }
    }

    public LauncherCard findByType(int type) {
        return mCardMap.get(type);
    }

    public List<LauncherCard> createInitCards(Context context) {
        mHomeCardList.clear();
        CardInitial cardInitial = new CardInitial();
        mHomeCardList.addAll(cardInitial.createInitCards(context));
        return mHomeCardList;
    }

    public List<LauncherCard> createUnselectCards(Context context) {
        mUnselectCardList.clear();
        CardInitial cardInitial = new CardInitial();
        mUnselectCardList.addAll(cardInitial.createUnselectCards(context));
        return mUnselectCardList;
    }

    public void resetCards(List<LauncherCard> newHomeList, List<LauncherCard> newUnselectList) {
        mHomeCardList.clear();
        mHomeCardList.addAll(newHomeList);
        mUnselectCardList.clear();
        mUnselectCardList.addAll(newUnselectList);
        mHomeCardsLiveData.postValue(mHomeCardList);

        saveCardsToDataBase(true);
    }

    private Comparator<LauncherCard> cardSortComparator = new Comparator<LauncherCard>() {
        @Override
        public int compare(LauncherCard o1, LauncherCard o2) {
            return o1.getPosition() - o2.getPosition();
        }
    };

    public void registerHomeCardsOb(LifecycleOwner lifecycleOwner, Observer<List<LauncherCard>> homeCardsOb) {
        mHomeCardsLiveData.observe(lifecycleOwner, homeCardsOb);
    }

    public void unregisterHomeCardsOb(Observer<List<LauncherCard>> homeCardsOb) {
        mHomeCardsLiveData.removeObserver(homeCardsOb);
    }
}
