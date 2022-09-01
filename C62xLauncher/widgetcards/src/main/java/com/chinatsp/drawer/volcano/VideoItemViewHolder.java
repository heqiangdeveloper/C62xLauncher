package com.chinatsp.drawer.volcano;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chinatsp.volcano.repository.VolcanoRepository;
import com.chinatsp.volcano.videos.VolcanoSource;
import com.chinatsp.volcano.videos.VolcanoVideo;
import com.chinatsp.widgetcards.R;

import launcher.base.recyclerview.BaseViewHolder;

public class VideoItemViewHolder extends BaseViewHolder<VolcanoVideo> {

    private ImageView ivDrawerVolcanoSourceIcon;
    private TextView tvDrawerVolcanoVideoTitle;

    public VideoItemViewHolder(@NonNull View itemView) {
        super(itemView);
        ivDrawerVolcanoSourceIcon = itemView.findViewById(R.id.ivDrawerVolcanoSourceIcon);
        tvDrawerVolcanoVideoTitle = itemView.findViewById(R.id.tvDrawerVolcanoVideoTitle);
    }

    @Override
    public void bind(int position, VolcanoVideo volcanoVideo) {
        super.bind(position, volcanoVideo);
        if (volcanoVideo == null) {
            return;
        }
        tvDrawerVolcanoVideoTitle.setText(volcanoVideo.getTitle());
        VolcanoSource volcanoSource = VolcanoSource.create(volcanoVideo.getSource());
        ivDrawerVolcanoSourceIcon.setImageResource(volcanoSource.getIconRes());
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(volcanoVideo.getSchema()));
                itemView.getContext().startActivity(intent);
            }
        });
    }
}
