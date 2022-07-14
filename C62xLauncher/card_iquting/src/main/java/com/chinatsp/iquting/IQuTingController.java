package com.chinatsp.iquting;

import androidx.lifecycle.MutableLiveData;

import com.chinatsp.iquting.IQuTingCardView;
import com.chinatsp.iquting.state.State;
import com.chinatsp.iquting.state.UnLoginState;

public class IQuTingController {
    private IQuTingCardView mView;
    private MutableLiveData<State> mStateMutableLiveData = new MutableLiveData<>(new UnLoginState());
    public IQuTingController(IQuTingCardView view) {
        this.mView = view;
    }

    State getState() {
        return mStateMutableLiveData.getValue();
    }

    void onDestroy() {
        mView = null;
    }
}
