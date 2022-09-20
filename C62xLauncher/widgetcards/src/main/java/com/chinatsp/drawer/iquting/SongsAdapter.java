package com.chinatsp.drawer.iquting;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.chinatsp.widgetcards.R;

import launcher.base.recyclerview.BaseRcvAdapter;
import launcher.base.recyclerview.BaseViewHolder;

class SongsAdapter extends BaseRcvAdapter<SongItem> {
    public SongsAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.drawer_item_iquting_song_item;
    }

    @Override
    protected SongViewHolder createViewHolder(View view) {
        return new SongViewHolder(view);
    }

    public static class SongViewHolder extends BaseViewHolder<SongItem> {
        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        void bind(SongItem songItem) {

        }
    }

}
