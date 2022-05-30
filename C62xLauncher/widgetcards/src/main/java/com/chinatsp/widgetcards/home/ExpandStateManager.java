package com.chinatsp.widgetcards.home;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

public class ExpandStateManager {
    private ExpandStateManager() {
    }

    private static final ExpandStateManager instance = new ExpandStateManager();

    public static ExpandStateManager getInstance() {
        return instance;
    }

    private final MutableLiveData<Boolean> mExpandStateLiveData = new MutableLiveData<>(false);

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
    public void unregister(LifecycleOwner lifecycleOwner, Observer<? super Boolean> observer) {
        mExpandStateLiveData.observe(lifecycleOwner, observer);
    }
}
