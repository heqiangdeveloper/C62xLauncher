package com.chinatsp.iquting;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import card.service.ICardStyleChange;


public class IQuTingCardView extends ConstraintLayout implements ICardStyleChange {
    private static final String TAG = "WeatherCardLargeView";

    public IQuTingCardView(@NonNull Context context) {
        super(context);
        init();
    }

    public IQuTingCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IQuTingCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public IQuTingCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }
    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.card_iquting, this);
    }

    @Override
    public void expand() {

    }

    @Override
    public void collapse() {

    }
}
