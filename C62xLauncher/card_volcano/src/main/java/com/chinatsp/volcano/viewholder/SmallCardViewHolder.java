package com.chinatsp.volcano.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinatsp.volcano.R;
import com.chinatsp.volcano.api.response.VideoListData;
import com.chinatsp.volcano.repository.VolcanoRepository;
import com.chinatsp.volcano.videos.VolcanoVideo;

import java.util.List;

import launcher.base.utils.glide.GlideHelper;

public class SmallCardViewHolder extends VolcanoViewHolder{
    private ImageView ivCardVolcanoSourceLogo;
    private ImageView ivCardVolcanoVideoCover;
    private TextView tvCardVolcanoVideoArtist;
    private TextView tvCardVolcanoVideoName;
    private TextView tvCardVolcanoSource;

    public SmallCardViewHolder(View rootView) {
        super(rootView);
        ivCardVolcanoSourceLogo = rootView.findViewById(R.id.ivCardVolcanoSourceLogo);
        ivCardVolcanoVideoCover = rootView.findViewById(R.id.ivCardVolcanoVideoCover);
        tvCardVolcanoVideoArtist = rootView.findViewById(R.id.tvCardVolcanoVideoArtist);
        tvCardVolcanoVideoName = rootView.findViewById(R.id.tvCardVolcanoVideoName);
        tvCardVolcanoSource = rootView.findViewById(R.id.tvCardVolcanoSource);
    }

    @Override
    public void showNormal() {

    }

    @Override
    public void showDisconnect() {

    }

    @Override
    public void showLogin() {

    }

    @Override
    public void updateList(VideoListData videoListData) {
        List<VolcanoVideo> list = videoListData.getList();
        if (list.isEmpty()) {
            return;
        }
        VolcanoVideo volcanoVideo = list.get(0);
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
        int res;
        String sourceName;
        switch (source) {
            case VolcanoRepository.SOURCE_DOUYIN:
                res = R.drawable.card_volcano_type_douyin;
                sourceName = "抖音";
                break;
            case VolcanoRepository.SOURCE_XIGUA:
                res = R.drawable.card_volcano_type_xigua;
                sourceName = "西瓜";
                break;
            case VolcanoRepository.SOURCE_TOUTIAO:
            default:
                res = R.drawable.card_volcano_type_toutiao;
                sourceName = "头条";
        }
        ivCardVolcanoSourceLogo.setImageResource(res);
        tvCardVolcanoSource.setText(sourceName);
    }

    @Override
    public void showLoadingView() {

    }

    @Override
    public void hideLoadingView() {

    }
}
