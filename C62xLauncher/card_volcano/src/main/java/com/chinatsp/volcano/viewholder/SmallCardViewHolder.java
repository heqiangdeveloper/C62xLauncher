package com.chinatsp.volcano.viewholder;

import android.content.Intent;
import android.content.res.Resources;
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
import launcher.base.utils.recent.RecentAppHelper;

public class SmallCardViewHolder extends VolcanoViewHolder {
    private ImageView ivCardVolcanoSourceLogo;
    private ImageView ivCardVolcanoVideoCover;
    private TextView tvCardVolcanoVideoArtist;
    private TextView tvCardVolcanoVideoName;
    private TextView tvCardVolcanoSource;
    private VolcanoVideo volcanoVideo;
    private ImageView ivCardVolcanoNetworkErr;
    private TextView tvCardVolcanoNetworkErr;
    private ImageView ivCardVolcanoNetworkErrCloseBtn;
    private View layoutCardVolcanoNetworkErr;
    private View layoutCardVolcanoNormal;
    private Resources mResources;
    private int mCoverWidth, mCoverHeight;

    public SmallCardViewHolder(View rootView) {
        super(rootView);
        ivCardVolcanoSourceLogo = rootView.findViewById(R.id.ivCardVolcanoSourceLogo);
        ivCardVolcanoVideoCover = rootView.findViewById(R.id.ivCardVolcanoVideoCover);
        tvCardVolcanoVideoArtist = rootView.findViewById(R.id.tvCardVolcanoVideoArtist);
        tvCardVolcanoVideoName = rootView.findViewById(R.id.tvCardVolcanoVideoName);
        tvCardVolcanoSource = rootView.findViewById(R.id.tvCardVolcanoSource);
        ivCardVolcanoNetworkErr = rootView.findViewById(R.id.ivCardVolcanoNetworkErr);
        tvCardVolcanoNetworkErr = rootView.findViewById(R.id.tvCardVolcanoNetworkErr);
        ivCardVolcanoNetworkErrCloseBtn = rootView.findViewById(R.id.ivCardVolcanoNetworkErrCloseBtn);
        layoutCardVolcanoNetworkErr = rootView.findViewById(R.id.layoutCardVolcanoNetworkErr);
        layoutCardVolcanoNormal = rootView.findViewById(R.id.layoutCardVolcanoNormal);

        ivCardVolcanoVideoCover.setOnClickListener(mOnClickListener);
        layoutCardVolcanoNetworkErr.setOnClickListener(mOnClickListener);
        ivCardVolcanoNetworkErrCloseBtn.setOnClickListener(mOnClickListener);

        mResources = rootView.getResources();
        mCoverWidth = mResources.getDimensionPixelOffset(R.dimen.card_volcano_cover_width);
        mCoverHeight = mResources.getDimensionPixelOffset(R.dimen.card_volcano_cover_height);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (volcanoVideo != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(volcanoVideo.getSchema()));
                v.getContext().startActivity(intent);
            } else {
                RecentAppHelper.launchApp(v.getContext(), "com.bytedance.byteautoservice");
            }
        }
    };

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
        // 圆角: 10px
        GlideHelper.loadUrlImage(ivCardVolcanoSourceLogo.getContext(), ivCardVolcanoVideoCover,
                volcanoVideo.getCover_url(), mCoverWidth, mCoverHeight, 10);
        tvCardVolcanoVideoName.setText(volcanoVideo.getTitle());
        layoutCardVolcanoNormal.setVisibility(View.VISIBLE);
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
        layoutCardVolcanoNetworkErr.setVisibility(View.INVISIBLE);
        layoutCardVolcanoNormal.setVisibility(View.INVISIBLE);
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
        tvCardVolcanoNetworkErr.setText(R.string.card_network_err);
        layoutCardVolcanoNetworkErr.setVisibility(View.VISIBLE);
        layoutCardVolcanoNormal.setVisibility(View.INVISIBLE);

    }

    @Override
    public void hideNetworkError() {
        layoutCardVolcanoNetworkErr.setVisibility(View.INVISIBLE);
        layoutCardVolcanoNormal.setVisibility(View.VISIBLE);
    }

    @Override
    public void showDataError() {
        ivCardVolcanoNetworkErr.setImageResource(R.drawable.card_icon_date_error);
        tvCardVolcanoNetworkErr.setText(R.string.card_data_err);
        layoutCardVolcanoNetworkErr.setVisibility(View.VISIBLE);
        layoutCardVolcanoNormal.setVisibility(View.INVISIBLE);
    }
}
