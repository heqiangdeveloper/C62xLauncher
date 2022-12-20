package com.chinatsp.volcano.repository;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.chinatsp.volcano.api.IHomeCardApi;
import com.chinatsp.volcano.api.VolcanoApi;
import com.chinatsp.volcano.api.VolcanoApiParam;
import com.chinatsp.volcano.api.response.VideoListData;
import com.chinatsp.volcano.api.response.VolcanoResponse;
import com.chinatsp.volcano.videos.VolcanoVideo;
import com.oushang.radio.network.errorhandler.ExceptionHandler;
import com.oushang.radio.network.observer.BaseObserver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.IllegalFormatCodePointException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import launcher.base.utils.EasyLog;

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

    private Map<String, IVolcanoLoadListener> mDrawerListeners = new HashMap<>();
    private Set<IVolcanoLoadListener> mCardListeners = new HashSet<>();

    public void registerCallbacks(IVolcanoLoadListener listener) {
        if (listener == null) {
            return;
        }
        EasyLog.i(TAG, "registerCallbacks: " + listener);
        mCardListeners.add(listener);
    }

    public void registerDrawerCallbacks(String tag, IVolcanoLoadListener listener) {
        if (tag == null) {
            return;
        }
        EasyLog.i(TAG, "registerCallbacks: " + tag);
        mDrawerListeners.put(tag, listener);
    }

    public void unregisterCallbacks(IVolcanoLoadListener listener) {
        if (listener == null) {
            return;
        }
        EasyLog.i(TAG, "unregisterCallbacks: " + listener);
        mCardListeners.remove(listener);
    }

    public void notifySuccess(VideoListData data, String source) {
        for (IVolcanoLoadListener listener : mCardListeners) {
            if (listener != null) {
                Log.d(TAG, "notifySuccess : " + listener);
                listener.onSuccess(data, source);
            }
        }
        for (IVolcanoLoadListener listener : mDrawerListeners.values()) {
            if (listener != null) {
                Log.d(TAG, "notifySuccess : " + listener);
                listener.onSuccess(data, source);
            }
        }
    }

    public void notifyFail(String msg) {
        for (IVolcanoLoadListener listener : mCardListeners) {
            if (listener != null) {
                listener.onFail(msg);
            }
        }
        for (IVolcanoLoadListener listener : mDrawerListeners.values()) {
            if (listener != null) {
                listener.onFail(msg);
            }
        }
    }

    private long lastLoadTime;
    private long lastTryTime;
    private final long MIN_INTERVAL = 1000;

    private static int loadCount = 0;

    public void loadFromServer(String source) {
        long nowTime = System.currentTimeMillis();
        long loadInterval = nowTime - lastLoadTime;
        long tryInterval = nowTime - lastTryTime;
        lastTryTime = nowTime;
        if (loadInterval < MIN_INTERVAL && tryInterval < MIN_INTERVAL) {
            EasyLog.w(TAG, "loadFromServer cancel, loadInterval:" + loadInterval + " , tryInterval:" + tryInterval);
            return;
        }
        lastLoadTime = nowTime;
        VolcanoApi volcanoApi = new VolcanoApi();
        IHomeCardApi iHomeCardApi = volcanoApi.create(IHomeCardApi.class);
        VolcanoApiParam params = new VolcanoApiParam("GET");
        params.addQueryField("source", source);
        params.compute();

        Map<String, String> queryParams = params.getQueryParams();
        Map<String, String> header = params.getHeader();
        EasyLog.i(TAG, "loadFromServer " + source);
        loadCount++;
        volcanoApi.ApiSubscribe(iHomeCardApi.getHomeCards(queryParams, header), new BaseObserver<VolcanoResponse>() {
            @Override
            public void onNext(VolcanoResponse volcanoResponse) {
                EasyLog.d(TAG, "loadFromServer success :" + volcanoResponse);
                EasyLog.d(TAG, "loadFromServer loadCount :" + loadCount);
//                if (loadCount == 1) {
//                    String msg = volcanoResponse.getMsg();
//                    VideoListData data = volcanoResponse.getData();
//                    List<VolcanoVideo> list = data.getList();
//                    if (list != null) {
//                        list.clear();
//                    }
//                }
                if (volcanoResponse.getErrno() == VolcanoResponse.CODE_SUCCESS) {
                    VideoListData data = volcanoResponse.getData();
                    List<VolcanoVideo> list = data.getList();
                    if (list == null || list.isEmpty()) {
                        notifyFail("list is empty");
                    } else {
                        saveList(data, source);
                        notifySuccess(data, source);
                    }
                } else {
                    String msg = volcanoResponse.getMsg();
                    notifyFail(msg);
                }

            }

            @Override
            public void onError(ExceptionHandler.ResponeThrowable e) {
                e.printStackTrace();
                notifyFail(e.message);
            }
        });
    }

    private Map<String, Long> lastLoadFromServerTimes = new HashMap<>();
    private final long CACHE_VALID_TIME = 1000 * 60 * 5L;

    private void saveList(VideoListData data, String source) {
        if (TextUtils.isEmpty(source)) {
            source = SOURCE_DOUYIN;
        }
        lastLoadFromServerTimes.put(source, System.currentTimeMillis());
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
        boolean cacheValid = checkCacheValid(source);
        if (!cacheValid) {
            return null;
        }
        EasyLog.d(TAG, "getVideoList from cache, source:" + source);
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

    private boolean checkCacheValid(String source) {
        long now = System.currentTimeMillis();
        if (source == null) {
            return false;
        }
        Long aLong = lastLoadFromServerTimes.get(source);
        long lastTime;
        if (aLong == null) {
            lastTime = 0;
        } else {
            lastTime = aLong;
        }
        long delta = now - lastTime;
        EasyLog.d(TAG, "checkCacheValid : " + delta + "ms" + " source:" + source);
        return delta < CACHE_VALID_TIME;
    }
}
