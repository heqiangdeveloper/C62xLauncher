package com.chinatsp.weaher.viewstate;

import androidx.lifecycle.LiveData;

import com.chinatsp.weaher.WeatherUtil;

import java.util.HashSet;
import java.util.Set;

public class CardViewState {
    private static final String TAG = "CardViewState";

    private CardViewState() {

    }

    private static class Holder{
        private static CardViewState instance = new CardViewState();
    }
    public static CardViewState getInstance() {
        return Holder.instance;
    }

    private Set<IOnChangeState> mListeners = new HashSet<>();

    private int mPageIndex;

    public int getPageIndex() {
        return mPageIndex;
    }

    public void setPageIndex(int pageIndex) {
        mPageIndex = pageIndex;
        notifyListeners(mPageIndex);
    }

    public void addListener(IOnChangeState onChangeState) {
        if (onChangeState == null) {
            return;
        }
        mListeners.add(onChangeState);
    }
    public void removeListener(IOnChangeState onChangeState) {
        if (onChangeState == null) {
            return;
        }
        mListeners.remove(onChangeState);
    }

    public void notifyListeners(int index) {
        for (IOnChangeState listener : mListeners) {
            if (listener != null) {
                WeatherUtil.logI(TAG+" notifyListeners:"+listener);
                listener.onPageChange(index);
            }
        }
    }
}
