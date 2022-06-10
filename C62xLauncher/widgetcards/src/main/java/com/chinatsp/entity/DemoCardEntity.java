package com.chinatsp.entity;

import android.content.Context;
import android.view.View;

import card.demo.DemoCardView;

public class DemoCardEntity extends BaseCardEntity {

    @Override
    public View getLayout(Context context) {
        return new DemoCardView(context);
    }

    @Override
    public View getLargeLayout(Context context) {
        return new DemoCardView(context);
    }
}
