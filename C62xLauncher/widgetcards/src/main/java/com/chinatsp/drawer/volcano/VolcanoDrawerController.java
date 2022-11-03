package com.chinatsp.drawer.volcano;

import android.content.Context;

import com.chinatsp.volcano.api.response.VideoListData;
import com.chinatsp.volcano.repository.IVolcanoLoadListener;
import com.chinatsp.volcano.repository.VolcanoRepository;

import java.util.ConcurrentModificationException;

import launcher.base.network.NetworkObserver;
import launcher.base.network.NetworkStateReceiver;
import launcher.base.network.NetworkUtils;

public class VolcanoDrawerController {
    private DrawerVolcanoHolder mView;
    private Context mContext;

    public VolcanoDrawerController(DrawerVolcanoHolder view) {
        mView = view;
        mContext = mView.itemView.getContext();
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
                    if (mView == null) {
                        return;
                    }
                    Context context = mView.itemView.getContext();
                    if (NetworkUtils.isNetworkAvailable(context)) {
                        mView.refreshFail(msg);
                    } else {
                        mView.showNetworkError();
                    }
                }
            });
        }
    }

    private NetworkObserver mNetworkObserver = new NetworkObserver() {
        @Override
        public void onNetworkChanged(boolean s) {
            boolean isConnected = NetworkUtils.isNetworkAvailable(mContext);
            if (isConnected) {
                loadVideoList();
            } else {
                mView.showNetworkError();
            }
        }
    };
}
