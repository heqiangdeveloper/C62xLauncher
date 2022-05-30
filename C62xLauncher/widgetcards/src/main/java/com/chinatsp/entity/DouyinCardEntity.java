package com.chinatsp.entity;

import android.content.Context;
import android.view.View;

import com.chinatsp.douyin.DouyinCardLargeView;
import com.chinatsp.douyin.DouyinCardView;

public class DouyinCardEntity extends BaseCardEntity {

    @Override
    public View getLayout(Context context) {
        return new DouyinCardView(context);
    }

    @Override
    public View getLargeLayout(Context context) {
        return new DouyinCardLargeView(context);
    }
}
