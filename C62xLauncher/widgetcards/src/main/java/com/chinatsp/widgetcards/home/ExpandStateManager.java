package com.chinatsp.widgetcards.home;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import launcher.base.utils.EasyLog;

public class ExpandStateManager {
    private static final String TAG = "ExpandStateManager";

    private ExpandStateManager() {
    }

    private static final ExpandStateManager instance = new ExpandStateManager();

    public static ExpandStateManager getInstance() {
        return instance;
    }

    private final MutableLiveData<Boolean> mExpandStateLiveData = new MutableLiveData<>(false);
    private CardFrameViewHolder mBigCard;
    public void setExpand(boolean expandState) {
        mExpandStateLiveData.postValue(expandState);
    }

    public boolean getExpandState() {
        Boolean value = mExpandStateLiveData.getValue();
        return value != null && value;
    }

    public void register(LifecycleOwner lifecycleOwner, Observer<? super Boolean> observer) {
        mExpandStateLiveData.observe(lifecycleOwner, observer);
    }
    public void unregister(Observer<? super Boolean> observer) {
        mExpandStateLiveData.removeObserver(observer);
    }

    public void setBigCard(CardFrameViewHolder cardFrameViewHolder) {
        EasyLog.d(TAG,"setBigCard: "+ cardFrameViewHolder.getLauncherCard().getName());
        this.mBigCard = cardFrameViewHolder;
    }

    public CardFrameViewHolder getBigCard() {
        if (mBigCard != null) {
            EasyLog.d(TAG, "getBigCard: " + mBigCard.getLauncherCard().getName());
        } else {
            EasyLog.d(TAG, "getBigCard: " + null);

        }
        return mBigCard;
    }
}
