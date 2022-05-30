package com.chinatsp.entity;

import android.content.Context;
import android.view.View;

import com.chinatsp.navigation.NaviCardLargeView;
import com.chinatsp.navigation.NaviCardView;

public class NavigationCardEntity extends BaseCardEntity {

    @Override
    public View getLayout(Context context) {
        return new NaviCardView(context);
    }

    @Override
    public View getLargeLayout(Context context) {
        return new NaviCardLargeView(context);
    }
}
