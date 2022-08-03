package com.chinatsp.iquting.songs;

import android.content.Context;
import android.view.View;

import com.chinatsp.iquting.R;

import launcher.base.recyclerview.BaseRcvAdapter;
import launcher.base.recyclerview.BaseViewHolder;

public class IQuTingSongsAdapter extends BaseRcvAdapter<IQuTingSong> {

    public IQuTingSongsAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.item_iquting_song;
    }

    @Override
    protected BaseViewHolder<IQuTingSong> createViewHolder(View view) {
        return new IQuTingSongViewHolder(view);
    }
}
