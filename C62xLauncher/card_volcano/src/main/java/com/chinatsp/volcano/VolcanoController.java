package com.chinatsp.volcano;

import com.chinatsp.volcano.api.response.VideoListData;
import com.chinatsp.volcano.repository.IVolcanoLoadListener;
import com.chinatsp.volcano.repository.VolcanoRepository;

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

    public void setCurrentSource(String source) {
        mRepository.setCurrentSource(source);
    }
}
