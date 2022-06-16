package com.chinatsp.widgetcards.editor.adapter;

import android.content.Context;
import android.view.View;

import com.chinatsp.widgetcards.R;

import card.base.LauncherCard;
import launcher.base.recyclerview.BaseRcvAdapter;
import launcher.base.recyclerview.BaseViewHolder;

public class CardSelectedAdapter extends BaseRcvAdapter<LauncherCard> {
    private View.OnTouchListener mOnTouchListener;

    public CardSelectedAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.item_card_selected;
    }

    @Override
    protected BaseViewHolder<LauncherCard> createViewHolder(View view) {
        return new EditorHomeCardViewHolder(view);
    }
}
