package com.chinatsp.drawer.volcano;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.drawer.DrawerEntity;
import com.chinatsp.volcano.api.response.VideoListData;
import com.chinatsp.widgetcards.R;

import launcher.base.recyclerview.BaseViewHolder;
import launcher.base.utils.recent.RecentAppHelper;

public class DrawerVolcanoHolder extends BaseViewHolder<DrawerEntity> {
    private RecyclerView rcvDrawerVolcanoVideos;
    private View layoutDrawerVolcanoError;
    private ImageView ivErrorIcon;
    private TextView tvErrorTip;
    private VideoInfoAdapter adapter;
    private VolcanoDrawerController mController;
    public DrawerVolcanoHolder(@NonNull View itemView) {
        super(itemView);
        rcvDrawerVolcanoVideos = itemView.findViewById(R.id.rcvDrawerVolcanoVideos);
        layoutDrawerVolcanoError = itemView.findViewById(R.id.layoutDrawerVolcanoError);
        ivErrorIcon = itemView.findViewById(R.id.ivErrorIcon);
        tvErrorTip = itemView.findViewById(R.id.tvErrorTip);
        initVideoRcv();
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecentAppHelper.launchApp(itemView.getContext(), "com.bytedance.byteautoservice");
            }
        });
        mController = new VolcanoDrawerController(this);
        mController.loadVideoList();
    }

    private void initVideoRcv() {
        Context context = itemView.getContext();
        adapter = new VideoInfoAdapter(context);
        rcvDrawerVolcanoVideos.setLayoutManager(new LinearLayoutManager(context){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        rcvDrawerVolcanoVideos.setAdapter(adapter);
    }

    void refreshFail(String msg) {
        layoutDrawerVolcanoError.setVisibility(View.VISIBLE);
        ivErrorIcon.setImageResource(R.drawable.card_icon_wifi_disconnect);
        tvErrorTip.setText(R.string.card_data_err);
        rcvDrawerVolcanoVideos.setVisibility(View.INVISIBLE);
    }

    void showNetworkError() {
        layoutDrawerVolcanoError.setVisibility(View.VISIBLE);
        ivErrorIcon.setImageResource(com.chinatsp.volcano.R.drawable.card_icon_wifi_disconnect);
        tvErrorTip.setText(R.string.card_network_err);
        rcvDrawerVolcanoVideos.setVisibility(View.INVISIBLE);
    }

    void refreshData(VideoListData videoListData) {
        if (videoListData == null) {
            return;
        }
        layoutDrawerVolcanoError.setVisibility(View.INVISIBLE);
        rcvDrawerVolcanoVideos.setVisibility(View.VISIBLE);
        adapter.setData(videoListData.getList());
    }
}
