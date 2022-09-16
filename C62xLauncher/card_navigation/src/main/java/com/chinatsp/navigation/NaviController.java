package com.chinatsp.navigation;

public class NaviController {
    private NaviCardView mView;

    public static final int STATE_FREE = 0;
    public static final int STATE_IN_NAVIGATION = 1;
    private int mState = STATE_FREE;
    private boolean isInNavigation; // 是否在导航状态中
    public NaviController(NaviCardView view) {
        mView = view;
    }

    public void refreshInitView() {
        mState = STATE_FREE;
        mView.refreshState(mState);
    }
}
