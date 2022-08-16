package com.chinatsp.volcano.videos;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;


import com.chinatsp.volcano.R;

import launcher.base.recyclerview.BaseViewHolder;
import launcher.base.utils.glide.GlideHelper;

public class VolcanoVideoViewHolder extends BaseViewHolder<VolcanoVideo> {
    private ImageView mSongCover;
    public VolcanoVideoViewHolder(@NonNull View itemView) {
        super(itemView);
        mSongCover = itemView.findViewById(R.id.ivVolcanoVideoItemCover);
    }

    @Override
    public void bind(int position, VolcanoVideo volcanoVideo) {
        super.bind(position, volcanoVideo);
        GlideHelper.loadLocalAlbumCoverRadius(mSongCover.getContext(), mSongCover, R.drawable.card_douyin_test_cover, 10);
    }
}
