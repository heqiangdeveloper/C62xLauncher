package com.chinatsp.iquting.songs;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.chinatsp.iquting.R;

import launcher.base.recyclerview.BaseViewHolder;
import launcher.base.utils.glide.GlideHelper;

public class IQuTingSongViewHolder extends BaseViewHolder<IQuTingSong> {
    private ImageView mSongCover;
    public IQuTingSongViewHolder(@NonNull View itemView) {
        super(itemView);
        mSongCover = itemView.findViewById(R.id.ivIqutingSongItemCover);
    }

    @Override
    public void bind(int position, IQuTingSong iQuTingSong) {
        super.bind(position, iQuTingSong);
        GlideHelper.loadImageUrlAlbumCover(mSongCover.getContext(), mSongCover, R.drawable.test_cover2, 10);
    }
}
