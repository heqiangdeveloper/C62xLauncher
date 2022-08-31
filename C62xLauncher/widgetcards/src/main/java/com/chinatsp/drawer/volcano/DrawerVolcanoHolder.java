package com.chinatsp.drawer.volcano;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.drawer.DrawerEntity;
import com.chinatsp.volcano.api.response.VideoListData;
import com.chinatsp.volcano.repository.IVolcanoLoadListener;
import com.chinatsp.volcano.repository.VolcanoRepository;
import com.chinatsp.widgetcards.R;

import kotlin.jvm.internal.PropertyReference0Impl;
import launcher.base.recyclerview.BaseViewHolder;
import launcher.base.utils.EasyLog;

public class DrawerVolcanoHolder extends BaseViewHolder<DrawerEntity> {
    private RecyclerView rcvDrawerVolcanoVideos;
    private View tvDrawerVolcanoLoginSlogan;
    private VideoInfoAdapter adapter;
    public DrawerVolcanoHolder(@NonNull View itemView) {
        super(itemView);
        rcvDrawerVolcanoVideos = itemView.findViewById(R.id.rcvDrawerVolcanoVideos);
        tvDrawerVolcanoLoginSlogan = itemView.findViewById(R.id.tvDrawerVolcanoLoginSlogan);
        initVideoRcv();
        loadVideoList();
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

    private void loadVideoList() {
        VolcanoRepository volcanoRepository = VolcanoRepository.getInstance();
        String source = volcanoRepository.getCurrentSource();
        VideoListData videoList = volcanoRepository.getVideoList(source);
        if (videoList != null) {
           refreshData(videoList);
        } else {
            volcanoRepository.loadFromServer(source, new IVolcanoLoadListener() {
                @Override
                public void onSuccess(VideoListData videoListData) {
                    refreshData(videoListData);
                }

                @Override
                public void onFail(String msg) {
                    refreshFail(msg);
                }
            });
        }
    }

    private void refreshFail(String msg) {
        // todo: 获取不到数据的情况

    }

    private void refreshData(VideoListData videoListData) {
        if (videoListData == null) {
            return;
        }
        tvDrawerVolcanoLoginSlogan.setVisibility(View.INVISIBLE);
        rcvDrawerVolcanoVideos.setVisibility(View.VISIBLE);
        EasyLog.d("refreshData", "xxxxrefreshData :"+videoListData);
        adapter.setData(videoListData.getList());
    }
}
