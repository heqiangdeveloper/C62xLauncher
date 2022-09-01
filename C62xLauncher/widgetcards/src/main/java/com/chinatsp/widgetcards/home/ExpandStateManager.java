package com.chinatsp.widgetcards.home;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import launcher.base.utils.EasyLog;

public class ExpandStateManager {
    private static final String TAG = "ExpandStateManager";
    private CardFrameViewHolder mBigCard;

    private ExpandStateManager() {
    }

    private static final ExpandStateManager instance = new ExpandStateManager();

    public static ExpandStateManager getInstance() {
        return instance;
    }

    private final MutableLiveData<Boolean> mExpandStateLiveData = new MutableLiveData<>(false);
    private int mBigPosition = -1;
    private int mSmallCardPosition = -1;

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

    public void setBigCardPosition(int position) {
        EasyLog.d(TAG, "setBigCard: " + position);
        this.mBigPosition = position;
    }

    public int getBigCardPosition() {
        EasyLog.d(TAG, "getBigCard: " + mBigPosition);
        return mBigPosition;
    }

    public int getSmallCardPosition() {
        return mSmallCardPosition;
    }

    public void setSmallCardPosInExpandState(int smallCardPosition) {
        this.mSmallCardPosition = smallCardPosition;
    }

    public void clearSmallCardPosInExpandState() {
        this.mSmallCardPosition = -1;
    }

    public CardFrameViewHolder getBigCard() {
        return mBigCard;
    }

    public void setBigCard(CardFrameViewHolder bigCard) {
        mBigCard = bigCard;
    }
}
