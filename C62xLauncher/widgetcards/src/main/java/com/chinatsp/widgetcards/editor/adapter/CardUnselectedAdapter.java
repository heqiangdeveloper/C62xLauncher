package com.chinatsp.widgetcards.editor.adapter;

import android.content.Context;
import android.view.View;

import com.chinatsp.widgetcards.R;
import com.chinatsp.entity.BaseCardEntity;
import launcher.base.recyclerview.BaseRcvAdapter;
import launcher.base.recyclerview.BaseViewHolder;

public class CardUnselectedAdapter extends BaseRcvAdapter<BaseCardEntity> {
    public CardUnselectedAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.item_card_unselected;
    }

    @Override
    protected BaseViewHolder<BaseCardEntity> createViewHolder(View view) {
        return new EditorUnselectCardViewHolder(view);
    }

}
