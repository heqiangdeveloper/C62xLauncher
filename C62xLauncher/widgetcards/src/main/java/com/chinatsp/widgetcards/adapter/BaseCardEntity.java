package com.chinatsp.widgetcards.adapter;

import android.content.Context;
import android.view.View;

public abstract class BaseCardEntity {

    private int type;
    private String name;
    private int logoDrawableRes;

    private boolean expandState;

    public String getName() {
        return name;
    }

    public BaseCardEntity setName(String name) {
        this.name = name;
        return this;
    }

    public int getLogoDrawableRes() {
        return logoDrawableRes;
    }

    public BaseCardEntity setLogoDrawableRes(int logoDrawableRes) {
        this.logoDrawableRes = logoDrawableRes;
        return this;
    }

    public int getType() {
        return type;
    }

    public BaseCardEntity setType(int type) {
        this.type = type;
        return this;
    }

    public abstract View getLayout(Context context);

    public abstract View getLargeLayout(Context context);

    public void setExpandState(boolean expandState) {
        this.expandState = expandState;
    }

    public boolean isExpandState(){
        return expandState;
    }
}
