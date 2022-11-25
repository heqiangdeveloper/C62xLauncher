package com.chinatsp.drawer.volcano;

import android.content.Context;

import com.chinatsp.volcano.api.response.VideoListData;
import com.chinatsp.volcano.repository.IVolcanoLoadListener;
import com.chinatsp.volcano.repository.VolcanoRepository;

import launcher.base.network.NetworkObserver;
import launcher.base.network.NetworkStateReceiver;
import launcher.base.network.NetworkUtils;
import launcher.base.utils.EasyLog;

public class VolcanoDrawerController {
    private static final String TAG = "VolcanoDrawerController";
    private DrawerVolcanoHolder mView;
    private Context mContext;

    public VolcanoDrawerController(DrawerVolcanoHolder view) {
        mView = view;
        mContext = mView.itemView.getContext();
        NetworkStateReceiver.getInstance().registerObserver(mNetworkObserver);
        EasyLog.i(TAG, "init hashCode:" + hashCode());
    }

    public void registerListener() {
        IVolcanoLoadListener l = new IVolcanoLoadListener() {
            @Override
            public void onSuccess(VideoListData videoListData, String source) {
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
        };
        VolcanoRepository.getInstance().registerDrawerCallbacks(TAG, l);
    }

    void loadVideoList() {
        EasyLog.d(TAG, "loadVideoList");
        VolcanoRepository volcanoRepository = VolcanoRepository.getInstance();
        String source = volcanoRepository.getCurrentSource();
        VideoListData videoList = volcanoRepository.getVideoList(source);
        if (videoList != null) {
            EasyLog.d(TAG, "loadVideoList from cache. hashCode:" + hashCode());
            mView.refreshData(videoList);
        } else {
            EasyLog.d(TAG, "loadVideoList from server. hashCode:" + hashCode());
            volcanoRepository.loadFromServer(source);
        }
    }

    private NetworkObserver mNetworkObserver = new NetworkObserver() {
        @Override
        public void onNetworkChanged(boolean s) {
            boolean isConnected = NetworkUtils.isNetworkAvailable(mContext);
            EasyLog.d(TAG, "onNetworkChanged , isConnected:" + isConnected + " , hashCode:" + VolcanoDrawerController.this.hashCode());
            if (isConnected) {
                loadVideoList();
            } else {
                mView.showNetworkError();
            }
        }
    };

    public void checkUIState(Context context) {
        boolean isConnected = NetworkUtils.isNetworkAvailable(context);
        if (!isConnected) {
            mView.showNetworkError();
        } else {
            loadVideoList();
        }
    }
}
