package com.chinatsp.volcano.repository;

import com.chinatsp.volcano.api.response.VideoListData;

public interface IVolcanoLoadListener {
    void onSuccess(VideoListData videoListData, String source);

    void onFail(String msg);
}
