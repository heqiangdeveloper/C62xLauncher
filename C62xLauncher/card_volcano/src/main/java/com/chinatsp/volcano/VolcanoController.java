package com.chinatsp.volcano;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.chinatsp.volcano.api.response.VideoListData;
import com.chinatsp.volcano.repository.IVolcanoLoadListener;
import com.chinatsp.volcano.repository.VolcanoRepository;

import java.util.PrimitiveIterator;

import launcher.base.network.NetworkObserver;
import launcher.base.network.NetworkStateReceiver;
import launcher.base.network.NetworkUtils;
import launcher.base.utils.EasyLog;

public class VolcanoController {
    private VolcanoCardView mView;
    private VolcanoRepository mRepository;
    private Handler mHandler = new android.os.Handler(Looper.getMainLooper());
    private static final String TAG = "VolcanoController";

    public VolcanoController(VolcanoCardView view) {
        EasyLog.d(TAG, "VolcanoController init " + hashCode());
        this.mView = view;
        mRepository = VolcanoRepository.getInstance();

        Context context = mView.getContext();
//        mHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                refreshPageState();
//            }
//        });
    }

    private void checkInitState(Context context) {
        boolean isConnected = NetworkUtils.isNetworkAvailable(context);
        if (!isConnected) {
            mView.showNetWorkError();
        }
    }

    void onDestroy() {
        mView = null;
    }

    public void loadSourceData(String source) {
        EasyLog.d(TAG, "loadSourceData " + hashCode() + " ,  source:" + source);
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
            if (NetworkUtils.isNetworkAvailable(mView.getContext())) {
                mView.showDataError();
            } else {
                mView.showNetWorkError();
            }
            if (mView != null) {
                mView.hideLoading();
            }
        }
    };

    private NetworkObserver mNetworkObserver = new NetworkObserver() {
        @Override
        public void onNetworkChanged(boolean s) {
            Context context = mView.getContext().getApplicationContext();
            boolean isConnected = NetworkUtils.isNetworkAvailable(context);
            EasyLog.d(TAG, "NetworkObserver onNetworkChanged s: " + s + " ,isConnected " + isConnected + "   " + VolcanoController.this.hashCode());
            if (s) {
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
        boolean networkAvailable = NetworkUtils.isNetworkAvailable(mView.getContext());
        EasyLog.d(TAG, "refreshPageState " + hashCode() + " ,  networkAvailable:" + networkAvailable);
        if (!networkAvailable) {
            mView.showNetWorkError();
        } else {
            loadSourceData(mRepository.getCurrentSource());
        }
    }

    public void attach() {
        EasyLog.i(TAG, "attach " + hashCode());
        NetworkStateReceiver.getInstance().registerObserver(mNetworkObserver);
        refreshPageState();
    }

    public void detach() {
        EasyLog.w(TAG, "detach " + hashCode());
        NetworkStateReceiver.getInstance().unRegisterObserver(mNetworkObserver);
    }
}
