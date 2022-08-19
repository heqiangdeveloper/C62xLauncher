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
                .followRedirects(false)//拦截重定向
                .cookieJar(new LocalCookieJar());


        /*可以统一添加网络参数到请求头*/
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
        /*网络请求返回的时候的数据处理*/
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
        //BODY打印信息,NONE不打印信息
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(logging);
    }

    /**
     * 封装线程管理和订阅的过程
     */
    public void
    ApiSubscribe(Observable observable, Observer observer) {
        observable.subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .unsubscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(sErrorTransformer)
                .subscribe(observer);
    }
    //CookieJar是用于保存Cookie的
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
     * 处理错误的变换
     * 网络请求的错误处理，其中网络错误分为两类：
     * 1、http请求相关的错误，例如：404，403，socket timeout等等；
     * 2、http请求正常，但是返回的应用数据里提示发生了异常，表明服务器已经接收到了来自客户端的请求，但是由于
     * 某些原因，服务器没有正常处理完请求，可能是缺少参数，或者其他原因；
     */
    private static class ErrorTransformer<T> implements ObservableTransformer {

        @Override
        public ObservableSource apply(io.reactivex.Observable upstream) {
            //onErrorResumeNext当发生错误的时候，由另外一个Observable来代替当前的Observable并继续发射数据
            return (io.reactivex.Observable<T>) upstream
//                   .map(new AppDataErrorHandler())/*返回的数据统一错误处理*/
                    .onErrorResumeNext(new HttpErrorHandler<T>());/*Http 错误处理**/
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
