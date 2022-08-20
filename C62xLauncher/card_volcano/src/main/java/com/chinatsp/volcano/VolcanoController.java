package com.chinatsp.volcano;

import android.os.Handler;
import android.os.RemoteException;

import com.chinatsp.volcano.api.response.VideoListData;
import com.chinatsp.volcano.api.response.VolcanoResponse;
import com.chinatsp.volcano.repository.VolcanoRepository;
import com.chinatsp.volcano.videos.VolcanoVideo;
import com.oushang.radio.network.errorhandler.ExceptionHandler;
import com.oushang.radio.network.observer.BaseObserver;

import java.util.LinkedList;
import java.util.List;

public class VolcanoController {
    private VolcanoCardView mView;
    private VolcanoRepository mRepository;
    public VolcanoController(VolcanoCardView view) {
        this.mView = view;
        mRepository = VolcanoRepository.getInstance();
    }


    void onDestroy() {
        mView = null;
    }

    public List<VolcanoVideo> createTestList() {
        List<VolcanoVideo> songList = new LinkedList<>();
        songList.add(new VolcanoVideo());
        songList.add(new VolcanoVideo());
        songList.add(new VolcanoVideo());
        songList.add(new VolcanoVideo());
        songList.add(new VolcanoVideo());
        songList.add(new VolcanoVideo());
        return songList;
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



    BaseObserver<VideoListData> loadListener = new BaseObserver<VideoListData>() {
        @Override
        public void onError(ExceptionHandler.ResponeThrowable e) throws RemoteException {
            if (mView != null) {
                mView.hideLoading();
            }
        }

        @Override
        public void onNext(VideoListData videoListData) {
            mView.updateList(videoListData);
            if (mView != null) {
                mView.hideLoading();
            }
        }
    };
}
