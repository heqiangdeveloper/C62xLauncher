package com.chinatsp.iquting.songs;

import android.content.Context;
import android.view.View;

import com.chinatsp.iquting.R;
import com.tencent.wecarflow.contentsdk.bean.BaseSongItemBean;

import launcher.base.recyclerview.BaseRcvAdapter;
import launcher.base.recyclerview.BaseViewHolder;

public class IQuTingSongsAdapter extends BaseRcvAdapter<BaseSongItemBean> {
    private IQuTingSongViewHolder mIQuTingSongViewHolder;
    public IQuTingSongsAdapter(Context context) {
        super(context);
    }
    private final static int MAXSONGS = 30;//最大30首

    @Override
    protected int getLayoutRes() {
        return R.layout.item_iquting_song;
    }

    @Override
    protected BaseViewHolder<BaseSongItemBean> createViewHolder(View view) {
        mIQuTingSongViewHolder = new IQuTingSongViewHolder(view);
        return mIQuTingSongViewHolder;
    }

    public void updatePlayStatus(int position,boolean isPlaying){
        mIQuTingSongViewHolder.updateSelectItem(position,isPlaying);
    }

    @Override
    public int getItemCount() {
        if(getData().size() > MAXSONGS){
            return MAXSONGS;
        }
        return super.getItemCount();
    }
}
