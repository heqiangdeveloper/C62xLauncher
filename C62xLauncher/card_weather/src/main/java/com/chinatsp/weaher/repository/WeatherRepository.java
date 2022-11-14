package com.chinatsp.weaher.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.chinatsp.weaher.WeatherUtil;
import com.chinatsp.weaher.repository.cache.IWeatherCache;
import com.chinatsp.weaher.repository.cache.WeatherCache;
import com.iflytek.autofly.weather.entity.WeatherInfo;
import com.iflytek.weathercontrol.WeatherRemoteControl;

import java.util.IllegalFormatCodePointException;
import java.util.List;

import launcher.base.ipc.BaseRepository;
import launcher.base.ipc.BaseRemoteConnector;
import launcher.base.ipc.IConnectListener;
import launcher.base.ipc.IOnRequestListener;
import launcher.base.ipc.IRemoteDataCallback;
import launcher.base.ipc.RemoteProxy;
import launcher.base.utils.EasyLog;
import launcher.base.utils.flowcontrol.PollingTask;

public class WeatherRepository extends BaseRepository {

    private static class Holder {
        private static WeatherRepository instance = new WeatherRepository();
    }

    private WeatherRepository() {

    }

    public static WeatherRepository getInstance() {
        return Holder.instance;
    }


    private IWeatherCache mCache = new WeatherCache();

    @Override
    public void init(@NonNull Context context) {
        super.init(context);
    }

    @Override
    protected BaseRemoteConnector createRemoteConnector(Context context) {
        WeatherUtil.logD("WeatherRepository createRemoteConnector");
        WeatherRemoteControl.init(context);
        WeatherRemoteControl.setDebug(true);
        WeatherConnectProxy remoteProxy = new WeatherConnectProxy(WeatherRemoteControl.getInstance());
        return new WeatherRemoteConnector(remoteProxy);
    }

    /**
     * 获取默认城市天气. 首先从缓存取数据, 随后再从远程进程获取最新的数据.
     * 缓存机制是为了保证快速响应请求.
     */
    public void loadDefaultWeather(IOnRequestListener onRequestListener) {
        List<WeatherInfo> mCacheDefaultWeather = mCache.getDefaultWeather();
        if (mCacheDefaultWeather != null) {
            WeatherUtil.logD("WeatherRepository loadDefaultWeather success from cache.");
            if (onRequestListener != null) {
                onRequestListener.onSuccess(mCacheDefaultWeather);
            }
        }
        if (mRemoteConnector != null) {
            if (mRemoteConnector.isServiceConnect()) {
                WeatherUtil.logD("WeatherRepository loadDefaultWeather ....");
                mRemoteConnector.requestData(new IOnRequestListener() {
                    @Override
                    public void onSuccess(Object o) {
                        List<WeatherInfo> mData = (List<WeatherInfo>) o;
                        mCache.saveDefaultWeather(mData);
                        if (onRequestListener != null) {
                            onRequestListener.onSuccess(mData);
                        }
                    }

                    @Override
                    public void onFail(String msg) {
                        if (onRequestListener != null) {
                            onRequestListener.onFail(msg);
                        }
                    }
                });
            } else {
                String disconnectMsg = "WeatherRepository loadDefaultWeather, mRemoteConnector disconnect.";
                WeatherUtil.logW(disconnectMsg);
                if (onRequestListener != null) {
                    onRequestListener.onFail(disconnectMsg);
                }
            }
        } else {
            String errMsg = "WeatherRepository loadDefaultWeather, mRemoteConnector is NULL.";
            WeatherUtil.logW(errMsg);
            if (onRequestListener != null) {
                onRequestListener.onFail(errMsg);
            }
        }
    }

    public void loadWeatherByCity(IOnRequestListener onRequestListener, String city) {
        List<WeatherInfo> cacheWeatherByCity = mCache.getWeatherByCity(city);
        if (cacheWeatherByCity != null) {
            WeatherUtil.logD("WeatherRepository loadWeatherByCity success from cache. city:"+city);
            if (onRequestListener != null) {
                onRequestListener.onSuccess(cacheWeatherByCity);
            }
        }
        if (mRemoteConnector != null) {
            if (mRemoteConnector.isServiceConnect()) {
                WeatherUtil.logD("WeatherRepository loadWeatherByCity from remote , city:" + city);
                if (mRemoteConnector instanceof WeatherRemoteConnector) {
                    IOnRequestListener innerListener = new IOnRequestListener() {
                        @Override
                        public void onSuccess(Object o) {
                            List<WeatherInfo> mData = (List<WeatherInfo>) o;
                            mCache.saveCityWeather(city, mData);
                            if (onRequestListener != null) {
                                onRequestListener.onSuccess(mData);
                            }
                        }

                        @Override
                        public void onFail(String msg) {
                            if (onRequestListener != null) {
                                onRequestListener.onFail(msg);
                            }
                        }
                    };
                    ((WeatherRemoteConnector) mRemoteConnector).requestCityWeather(innerListener, city);
                }
            } else {
                String disconnectMsg = "WeatherRepository loadWeatherByCity , mRemoteConnector disconnect.";
                WeatherUtil.logW(disconnectMsg);
                if (onRequestListener != null) {
                    onRequestListener.onFail(disconnectMsg);
                }
            }
        } else {
            String errMsg = "WeatherRepository loadWeatherByCity, mRemoteConnector is NULL.";
            WeatherUtil.logW(errMsg);
            if (onRequestListener != null) {
                onRequestListener.onFail(errMsg);
            }
        }
    }

    public void requestCityList() {
        if (mRemoteConnector instanceof WeatherRemoteConnector) {
            ((WeatherRemoteConnector) mRemoteConnector).requestCityList();
        }
    }

    public void saveToCache(List<String> cityList) {
        if (mCache != null) {
            mCache.saveCityList(cityList);
        }
    }

    public List<String> getCityFromCache() {
        if (mCache != null) {
            return mCache.getCityList();
        }
        return null;
    }

    public void registerDataCallback(IRemoteDataCallback remoteDataCallback) {
        if (mRemoteConnector != null) {
            mRemoteConnector.registerRemoteDataCallbacks(remoteDataCallback);
        }
    }

    public void unregisterDataCallback(IRemoteDataCallback remoteDataCallback) {
        if (mRemoteConnector != null) {
            mRemoteConnector.unregisterRemoteDataCallbacks(remoteDataCallback);
        }
    }

    @Override
    protected void destroy() {
        super.destroy();
    }
}
