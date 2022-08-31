package com.chinatsp.volcano.repository;

import com.chinatsp.volcano.api.response.VideoListData;

public interface IVolcanoLoadListener {
    void onSuccess(VideoListData videoListData);

    void onFail(String msg);
}
