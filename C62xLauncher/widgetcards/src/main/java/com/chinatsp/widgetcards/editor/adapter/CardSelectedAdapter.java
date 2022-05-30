package com.chinatsp.widgetcards.editor;

import android.content.Context;
import android.view.View;

import com.chinatsp.widgetcards.R;
import com.chinatsp.widgetcards.adapter.BaseCardEntity;
import com.chinatsp.widgetcards.adapter.CardsAdapter;
import com.chinatsp.widgetcards.adapter.DefaultCardEntity;

public class CardSelectedAdapter extends BaseRcvAdapter<BaseCardEntity> {
    public CardSelectedAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.item_card_selected;
    }

    @Override
    protected BaseViewHolder<BaseCardEntity> createViewHolder(View view) {
        return new EditorHomeCardViewHolder(view);
    }

}
