package com.chinatsp.volcano.viewholder;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinatsp.volcano.R;
import com.chinatsp.volcano.api.response.VideoListData;
import com.chinatsp.volcano.repository.VolcanoRepository;
import com.chinatsp.volcano.videos.VolcanoSource;
import com.chinatsp.volcano.videos.VolcanoVideo;

import java.util.IllegalFormatCodePointException;
import java.util.List;

import launcher.base.routine.ActivityBus;
import launcher.base.utils.glide.GlideHelper;

public class SmallCardViewHolder extends VolcanoViewHolder{
    private ImageView ivCardVolcanoSourceLogo;
    private ImageView ivCardVolcanoVideoCover;
    private TextView tvCardVolcanoVideoArtist;
    private TextView tvCardVolcanoVideoName;
    private TextView tvCardVolcanoSource;
    private VolcanoVideo volcanoVideo;
    private ImageView ivCardVolcanoNetworkErr;
    private TextView tvCardVolcanoNetworkErr;

    public SmallCardViewHolder(View rootView) {
        super(rootView);
        ivCardVolcanoSourceLogo = rootView.findViewById(R.id.ivCardVolcanoSourceLogo);
        ivCardVolcanoVideoCover = rootView.findViewById(R.id.ivCardVolcanoVideoCover);
        tvCardVolcanoVideoArtist = rootView.findViewById(R.id.tvCardVolcanoVideoArtist);
        tvCardVolcanoVideoName = rootView.findViewById(R.id.tvCardVolcanoVideoName);
        tvCardVolcanoSource = rootView.findViewById(R.id.tvCardVolcanoSource);
        ivCardVolcanoNetworkErr = rootView.findViewById(R.id.ivCardVolcanoNetworkErr);
        tvCardVolcanoNetworkErr = rootView.findViewById(R.id.tvCardVolcanoNetworkErr);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (volcanoVideo != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(volcanoVideo.getSchema()));
                    rootView.getContext().startActivity(intent);
                }
            }
        });
    }

    @Override
    public void showNormal() {

    }

    @Override
    public void updateList(VideoListData videoListData) {
        List<VolcanoVideo> list = videoListData.getList();
        if (list.isEmpty()) {
            return;
        }
        volcanoVideo = list.get(0);
        if (volcanoVideo == null) {
            return;
        }
        GlideHelper.loadUrlImage(ivCardVolcanoSourceLogo.getContext(),ivCardVolcanoVideoCover,
                volcanoVideo.getCover_url(),384,216, 10);
        tvCardVolcanoVideoName.setText(volcanoVideo.getTitle());
//        tvCardVolcanoVideoArtist.setVisibility(View.INVISIBLE);
    }

    @Override
    public void init() {

    }

    @Override
    public void onChangeSource(String source) {
        VolcanoSource volcanoSource = VolcanoSource.create(source);
        ivCardVolcanoSourceLogo.setImageResource(volcanoSource.getIconRes());
        tvCardVolcanoSource.setText(volcanoSource.getName());
    }

    @Override
    public void showLoadingView() {
//        ivCardVolcanoSourceLogo.setVisibility(View.INVISIBLE);
//        ivCardVolcanoVideoCover.setVisibility(View.INVISIBLE);
//        tvCardVolcanoVideoName.setVisibility(View.INVISIBLE);
    }

    @Override
    public void hideLoadingView() {
//        ivCardVolcanoSourceLogo.setVisibility(View.VISIBLE);
//        ivCardVolcanoVideoCover.setVisibility(View.VISIBLE);
//        tvCardVolcanoVideoName.setVisibility(View.VISIBLE);
    }

    @Override
    public void showNetworkError() {
        ivCardVolcanoNetworkErr.setImageResource(R.drawable.card_icon_wifi_disconnect);
        ivCardVolcanoNetworkErr.setVisibility(View.VISIBLE);
        tvCardVolcanoNetworkErr.setText(R.string.card_network_err);
        tvCardVolcanoNetworkErr.setVisibility(View.VISIBLE);
        ivCardVolcanoSourceLogo.setVisibility(View.INVISIBLE);
        tvCardVolcanoSource.setVisibility(View.INVISIBLE);
        ivCardVolcanoVideoCover.setVisibility(View.INVISIBLE);
        tvCardVolcanoVideoName.setVisibility(View.INVISIBLE);
    }

    @Override
    public void hideNetworkError() {
        ivCardVolcanoNetworkErr.setVisibility(View.INVISIBLE);
        tvCardVolcanoNetworkErr.setVisibility(View.INVISIBLE);
        ivCardVolcanoSourceLogo.setVisibility(View.VISIBLE);
        tvCardVolcanoSource.setVisibility(View.VISIBLE);
        ivCardVolcanoVideoCover.setVisibility(View.VISIBLE);
        tvCardVolcanoVideoName.setVisibility(View.VISIBLE);
    }

    @Override
    public void showDataError() {
        ivCardVolcanoNetworkErr.setImageResource(R.drawable.card_icon_date_error);
        ivCardVolcanoNetworkErr.setVisibility(View.VISIBLE);
        tvCardVolcanoNetworkErr.setText(R.string.card_data_err);
        tvCardVolcanoNetworkErr.setVisibility(View.VISIBLE);
        ivCardVolcanoSourceLogo.setVisibility(View.INVISIBLE);
        tvCardVolcanoSource.setVisibility(View.INVISIBLE);
        ivCardVolcanoVideoCover.setVisibility(View.INVISIBLE);
        tvCardVolcanoVideoName.setVisibility(View.INVISIBLE);
    }
}
