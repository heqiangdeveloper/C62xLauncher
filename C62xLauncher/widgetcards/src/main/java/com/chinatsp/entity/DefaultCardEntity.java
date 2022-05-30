package com.chinatsp.entity;

import android.content.Context;
import android.view.View;

public class DefaultCardEntity extends BaseCardEntity {

    @Override
    public View getLayout(Context context) {
        return new View(context);
    }

    @Override
    public View getLargeLayout(Context context) {
        return new View(context);
    }
}
