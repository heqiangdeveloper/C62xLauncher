package com.chinatsp.volcano;

import com.chinatsp.volcano.api.response.VideoListData;
import com.chinatsp.volcano.repository.IVolcanoLoadListener;
import com.chinatsp.volcano.repository.VolcanoRepository;

import launcher.base.network.NetworkObserver;
import launcher.base.network.NetworkStateReceiver;

public class VolcanoController {
    private VolcanoCardView mView;
    private VolcanoRepository mRepository;
    public VolcanoController(VolcanoCardView view) {
        this.mView = view;
        mRepository = VolcanoRepository.getInstance();
        NetworkStateReceiver.getInstance().registerObserver(mNetworkObserver);
    }



    void onDestroy() {
        mView = null;
    }

    public void loadSourceData(String source) {
        VideoListData videoListData = mRepository.getVideoList(source);
        if (videoListData != null) {
            mView.updateList(videoListData);
        } else {
            mView.showLoading();
            mRepository.loadFromServer(source, loadListener);
        }
    }

    IVolcanoLoadListener loadListener = new IVolcanoLoadListener() {
        @Override
        public void onSuccess(VideoListData videoListData) {
            mView.updateList(videoListData);
            if (mView != null) {
                mView.hideLoading();
            }
        }

        @Override
        public void onFail(String msg) {
            if (mView != null) {
                mView.hideLoading();
            }
        }
    };

    private NetworkObserver mNetworkObserver = new NetworkObserver() {
        @Override
        public void onNetworkChanged(boolean isConnected) {
            if (isConnected) {
                mView.hideNetWorkError();
            } else {
                mView.showNetWorkError();
            }
        }
    };

    public void setCurrentSource(String source) {
        mRepository.setCurrentSource(source);
    }
}
