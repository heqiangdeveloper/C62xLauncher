package com.chinatsp.appstore.state;

import android.view.View;

public interface AppStoreState {
    void updateSmallCardState(View view);
    void updateBigCardState(View view);

    default void updateViewState(View view, boolean isBig){
        if (isBig) {
            updateBigCardState(view);
        } else {
            updateSmallCardState(view);
        }
    };
}
