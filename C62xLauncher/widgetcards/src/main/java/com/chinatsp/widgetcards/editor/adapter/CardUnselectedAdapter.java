package com.chinatsp.widgetcards.editor.adapter;

import android.content.Context;
import android.view.View;

import com.chinatsp.widgetcards.R;

import card.base.LauncherCard;
import launcher.base.recyclerview.BaseRcvAdapter;
import launcher.base.recyclerview.BaseViewHolder;

public class CardUnselectedAdapter extends BaseRcvAdapter<LauncherCard> {
    public CardUnselectedAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.item_card_unselected;
    }

    @Override
    protected BaseViewHolder<LauncherCard> createViewHolder(View view) {
        return new EditorUnselectCardViewHolder(view);
    }

}
