package com.chinatsp.volcano.repository;

import android.content.Context;
import android.os.RemoteException;
import android.text.TextUtils;

import com.chinatsp.volcano.api.IHomeCardApi;
import com.chinatsp.volcano.api.VolcanoApi;
import com.chinatsp.volcano.api.VolcanoApiParam;
import com.chinatsp.volcano.api.response.VideoListData;
import com.chinatsp.volcano.api.response.VolcanoResponse;
import com.oushang.radio.network.errorhandler.ExceptionHandler;
import com.oushang.radio.network.observer.BaseObserver;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import launcher.base.utils.EasyLog;
import retrofit2.http.PUT;

public class VolcanoRepository {
    private final String TAG = "VolcanoRepository";
    private Context mContext;

    public static final String SOURCE_TOUTIAO = "toutiao";
    public static final String SOURCE_DOUYIN = "douyin";
    public static final String SOURCE_XIGUA = "xigua";

    private VideoListData mDouyinList;
    private VideoListData mXiguaList;
    private VideoListData mToutiaoList;
    private String mCurrentSource = SOURCE_TOUTIAO;


    public static VolcanoRepository getInstance() {
        return Holder.repository;
    }

    private VolcanoRepository() {

    }

    public void setCurrentSource(String currentSource) {
        mCurrentSource = currentSource;
    }

    public String getCurrentSource() {
        return mCurrentSource;
    }

    private static class Holder {
        private static VolcanoRepository repository = new VolcanoRepository();
    }

    public void loadFromServer(String source, IVolcanoLoadListener listener) {
        VolcanoApi volcanoApi = new VolcanoApi();
        IHomeCardApi iHomeCardApi = volcanoApi.create(IHomeCardApi.class);
        VolcanoApiParam params = new VolcanoApiParam("GET");
        params.addQueryField("source", source);
        params.compute();

        Map<String, String> queryParams = params.getQueryParams();
        Map<String, String> header = params.getHeader();

//        EasyLog.d(TAG, "queryParams:");
//        queryParams.forEach(new BiConsumer<String, String>() {
//            @Override
//            public void accept(String s, String s2) {
//                EasyLog.i(TAG, s + " --- "+ s2);
//            }
//        });
//
//        EasyLog.d(TAG, "headers:");
//        header.forEach(new BiConsumer<String, String>() {
//            @Override
//            public void accept(String s, String s2) {
//                EasyLog.i(TAG, s + " --- "+ s2);
//            }
//        });
        volcanoApi.ApiSubscribe(iHomeCardApi.getHomeCards(queryParams, header), new BaseObserver<VolcanoResponse>() {
            @Override
            public void onNext(VolcanoResponse volcanoResponse) {
                EasyLog.d(TAG, "getHomeCards success :" + volcanoResponse);
                if (volcanoResponse.getErrno() == VolcanoResponse.CODE_SUCCESS) {
                    saveList(volcanoResponse.getData(), source);
                    if (listener != null) {
                        listener.onSuccess(volcanoResponse.getData());
                    }
                } else {
                    if (listener != null) {
                        listener.onFail(volcanoResponse.getMsg());
                    }
                }

            }

            @Override
            public void onError(ExceptionHandler.ResponeThrowable e) {
                e.printStackTrace();
                if (listener != null) {
                    listener.onFail(e.message);
                }
            }
        });
    }

    private void saveList(VideoListData data, String source) {
        if (TextUtils.isEmpty(source)) {
            source = SOURCE_DOUYIN;
        }
        EasyLog.d(TAG, "saveList , source:" + source);
        switch (source) {
            case SOURCE_DOUYIN:
                mDouyinList = data;
                break;
            case SOURCE_TOUTIAO:
                mToutiaoList = data;
                break;
            case SOURCE_XIGUA:
                mXiguaList = data;
                break;
        }
    }

    public VideoListData getVideoList(String source) {
        EasyLog.d(TAG, "getVideoList , source:" + source);
        VideoListData videoListData;
        switch (source) {
            case SOURCE_TOUTIAO:
                videoListData = mToutiaoList;
                break;
            case SOURCE_XIGUA:
                videoListData = mXiguaList;
                break;
            case SOURCE_DOUYIN:
            default:
                videoListData = mDouyinList;
        }
        return videoListData;
    }
}
