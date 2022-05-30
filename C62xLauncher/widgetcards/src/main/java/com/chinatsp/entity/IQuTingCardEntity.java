package com.chinatsp.entity;

import android.content.Context;
import android.view.View;

import com.chinatsp.iquting.IQuTingCardLargeView;
import com.chinatsp.iquting.IQuTingCardView;

public class IQuTingCardEntity extends BaseCardEntity {

    @Override
    public View getLayout(Context context) {
        return new IQuTingCardView(context);
    }

    @Override
    public View getLargeLayout(Context context) {
        return new IQuTingCardLargeView(context);
    }
}
