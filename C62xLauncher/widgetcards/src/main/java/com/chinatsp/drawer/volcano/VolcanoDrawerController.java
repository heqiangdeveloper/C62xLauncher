package com.chinatsp.drawer.volcano;

import com.chinatsp.volcano.api.response.VideoListData;
import com.chinatsp.volcano.repository.IVolcanoLoadListener;
import com.chinatsp.volcano.repository.VolcanoRepository;

import launcher.base.network.NetworkObserver;
import launcher.base.network.NetworkStateReceiver;

public class VolcanoDrawerController {
    private DrawerVolcanoHolder mView;

    public VolcanoDrawerController(DrawerVolcanoHolder view) {
        mView = view;
        NetworkStateReceiver.getInstance().registerObserver(mNetworkObserver);
    }
    void loadVideoList() {
        VolcanoRepository volcanoRepository = VolcanoRepository.getInstance();
        String source = volcanoRepository.getCurrentSource();
        VideoListData videoList = volcanoRepository.getVideoList(source);
        if (videoList != null) {
            mView.refreshData(videoList);
        } else {
            volcanoRepository.loadFromServer(source, new IVolcanoLoadListener() {
                @Override
                public void onSuccess(VideoListData videoListData) {
                    mView.refreshData(videoListData);
                }

                @Override
                public void onFail(String msg) {
                    mView.refreshFail(msg);
                }
            });
        }
    }
    private NetworkObserver mNetworkObserver = new NetworkObserver() {
        @Override
        public void onNetworkChanged(boolean isConnected) {
            if (isConnected) {
                loadVideoList();
            } else {
                mView.showNetworkError();
            }
        }
    };
}
