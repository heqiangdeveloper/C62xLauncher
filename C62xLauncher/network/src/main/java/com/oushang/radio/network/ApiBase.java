package com.oushang.radio.network;

import com.oushang.radio.network.errorhandler.HttpErrorHandler;
import com.oushang.radio.network.interceptor.RequestInterceptor;
import com.oushang.radio.network.interceptor.ResponseInterceptor;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class ApiBase {
    protected Retrofit retrofit;
    private static ErrorTransformer sErrorTransformer = new ErrorTransformer();

    private static final int TIME_OUT = 30;

    protected ApiBase(String baseUrl) {
        retrofit = new Retrofit
                .Builder()
                .client(getOkHttpClient())
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create()).build();
    }


    public OkHttpClient getOkHttpClient() {
        X509TrustManager xtm = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };

        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("SSL");

            sslContext.init(null, new TrustManager[]{xtm}, new SecureRandom());

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        SSLSocketFactory sslSocketFactory = new SSLSocketFactoryCompat(xtm);
        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder()
                .callTimeout(TIME_OUT, TimeUnit.SECONDS)
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
                .sslSocketFactory(sslSocketFactory,xtm)
                .followSslRedirects(false)
                .followRedirects(false)//???????????????
                .cookieJar(new LocalCookieJar());


        /*??????????????????????????????????????????*/
        okHttpClient.addInterceptor(new RequestInterceptor(new INetworkRequestInfo() {
            @Override
            public HashMap<String, String> getRequestHeaderMap() {
                return null;
            }

            @Override
            public void addHeardMap(String key, String value) {

            }

            @Override
            public void removeHeardMap(String key) {

            }

            @Override
            public boolean isDebug() {
                return false;
            }
        }));
        /*??????????????????????????????????????????*/
        okHttpClient.addInterceptor(new ResponseInterceptor());
//        okHttpClient.addInterceptor(new com.edog.car.network.interceptor.HttpLoggingInterceptor("FceOnlineRaido"));
//        setLoggingLevel(okHttpClient);
        OkHttpClient httpClient = okHttpClient.build();
        httpClient.dispatcher().setMaxRequestsPerHost(20);
        return httpClient;
    }


    private void setLoggingLevel(OkHttpClient.Builder builder) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                NetworkLog.d("xuyuanlin", "OkHttp====Message:" + message);
            }
        });
        //BODY????????????,NONE???????????????
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(logging);
    }

    /**
     * ????????????????????????????????????
     */
    public void
    ApiSubscribe(Observable observable, Observer observer) {
        observable.subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .unsubscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(sErrorTransformer)
                .subscribe(observer);
    }
    //CookieJar???????????????Cookie???
    class LocalCookieJar implements CookieJar {
        List<Cookie> cookies;
        @Override
        public List<Cookie> loadForRequest(HttpUrl arg0) {
            if (cookies != null)
                return cookies;
            return new ArrayList<Cookie>();
        }

        @Override
        public void saveFromResponse(HttpUrl arg0, List<Cookie> cookies) {
            this.cookies = cookies;
        }

    }
    /**
     * ?????????????????????
     * ???????????????????????????????????????????????????????????????
     * 1???http?????????????????????????????????404???403???socket timeout?????????
     * 2???http?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     * ???????????????????????????????????????????????????????????????????????????????????????????????????
     */
    private static class ErrorTransformer<T> implements ObservableTransformer {

        @Override
        public ObservableSource apply(io.reactivex.Observable upstream) {
            //onErrorResumeNext??????????????????????????????????????????Observable??????????????????Observable?????????????????????
            return (io.reactivex.Observable<T>) upstream
//                   .map(new AppDataErrorHandler())/*?????????????????????????????????*/
                    .onErrorResumeNext(new HttpErrorHandler<T>());/*Http ????????????**/
        }
    }

    //    private ClearableCookieJar cookieJar;
    //
    //    public ClearableCookieJar getCookieJar() {
    //        if (cookieJar == null) {
    //            cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(AppProvider.getInstance().getApp()));
    //        }
    //        return cookieJar;
    //    }
}
