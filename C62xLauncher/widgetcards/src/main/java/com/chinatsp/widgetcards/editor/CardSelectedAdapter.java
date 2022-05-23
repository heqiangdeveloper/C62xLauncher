package com.chinatsp.widgetcards.editor;

import android.content.Context;

import com.chinatsp.widgetcards.adapter.CardsAdapter;
import com.chinatsp.widgetcards.adapter.DefaultCardEntity;

public class CardSelectedAdapter extends BaseRcvAdapter<DefaultCardEntity> {
    public CardSelectedAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

}
