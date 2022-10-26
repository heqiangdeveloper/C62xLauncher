package com.chinatsp.volcano;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.chinatsp.volcano.api.response.VideoListData;
import com.chinatsp.volcano.repository.IVolcanoLoadListener;
import com.chinatsp.volcano.repository.VolcanoRepository;

import launcher.base.network.NetworkObserver;
import launcher.base.network.NetworkStateReceiver;
import launcher.base.network.NetworkUtils;

public class VolcanoController {
    private VolcanoCardView mView;
    private VolcanoRepository mRepository;
    private Handler mHandler = new android.os.Handler(Looper.getMainLooper());
    public VolcanoController(VolcanoCardView view) {
        this.mView = view;
        mRepository = VolcanoRepository.getInstance();
        NetworkStateReceiver.getInstance().registerObserver(mNetworkObserver);
        Context context = mView.getContext();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                refreshPageState();
            }
        });
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
            mView.showDataError();
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
                loadSourceData(mRepository.getCurrentSource());
            } else {
                mView.showNetWorkError();
            }
        }
    };

    public void setCurrentSource(String source) {
        mRepository.setCurrentSource(source);
    }

    public void refreshPageState() {
        if (!NetworkUtils.isNetworkAvailable(mView.getContext())) {
            mView.showNetWorkError();
        } else {
            loadSourceData(mRepository.getCurrentSource());
        }
    }
}
