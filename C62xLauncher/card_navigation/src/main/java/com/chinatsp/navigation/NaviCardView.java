package com.chinatsp.navigation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.autonavi.autoaidlwidget.AutoAidlWidgetManager;
import com.autonavi.autoaidlwidget.AutoAidlWidgetSurfaceView;
import com.autonavi.autoaidlwidget.AutoAidlWidgetView;

import card.service.ICardStyleChange;


public class NaviCardView extends ConstraintLayout implements ICardStyleChange {
    public NaviCardView(@NonNull Context context) {
        super(context);
        init();
    }

    public NaviCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NaviCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public NaviCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.card_navigation, this);
        AutoAidlWidgetManager.getInstance().init(getContext().getApplicationContext());


    }

    @Override
    public void expand() {

    }

    @Override
    public void collapse() {

    }

    @Override
    public boolean hideDefaultTitle() {
        return false;
    }
}
