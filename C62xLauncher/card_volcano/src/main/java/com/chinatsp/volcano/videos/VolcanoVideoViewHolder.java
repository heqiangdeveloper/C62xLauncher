package com.chinatsp.volcano.videos;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;


import com.chinatsp.volcano.R;

import launcher.base.recyclerview.BaseViewHolder;
import launcher.base.utils.glide.GlideHelper;

public class VolcanoVideoViewHolder extends BaseViewHolder<VolcanoVideo> {
    private ImageView mSongCover;
    private TextView tvVolcanoVideoItemTitle;
    private TextView tvVolcanoVideoItemArtist;
    public VolcanoVideoViewHolder(@NonNull View itemView) {
        super(itemView);
        mSongCover = itemView.findViewById(R.id.ivVolcanoVideoItemCover);
        tvVolcanoVideoItemTitle = itemView.findViewById(R.id.tvVolcanoVideoItemTitle);
        tvVolcanoVideoItemArtist = itemView.findViewById(R.id.tvVolcanoVideoItemArtist);
    }

    @Override
    public void bind(int position, VolcanoVideo volcanoVideo) {
        super.bind(position, volcanoVideo);
        if (volcanoVideo == null) {
            return;
        }
        GlideHelper.loadUrlImage(mSongCover.getContext(), mSongCover, volcanoVideo.getCover_url(), 384,216,10);
        tvVolcanoVideoItemTitle.setText(volcanoVideo.getTitle());
//        tvVolcanoVideoItemArtist.setVisibility(View.INVISIBLE);
    }
}
