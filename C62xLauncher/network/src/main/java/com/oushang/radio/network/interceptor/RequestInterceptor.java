package com.oushang.radio.network.interceptor;

import com.oushang.radio.network.INetworkRequestInfo;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @description
 * @author xuyuanli
 * @time 2021/2/2 15:01
 * @UpdateUser:     xuyuanli
 * @UpdateDate:    2021/2/2 15:01
 * @UpdateRemark:
 */
public class RequestInterceptor implements Interceptor {
    private static final String TAG = "RequestInterceptor";
    private INetworkRequestInfo mNetworkRequestInfo;
    public RequestInterceptor(INetworkRequestInfo networkRequestInfo) {
        this.mNetworkRequestInfo = networkRequestInfo;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request()
                .newBuilder()
                //付费专辑必须要提供User-Agent
                .removeHeader("User-Agent")//移除旧的
                .addHeader("User-Agent", System.getProperty("http.agent"));//添加真正的头部;
       /* if(mNetworkRequestInfo != null) {
            for(String key:mNetworkRequestInfo.getRequestHeaderMap().keySet()){
                if(!TextUtils.isEmpty(mNetworkRequestInfo.getRequestHeaderMap().get(key))) {
                    builder.addHeader(key, mNetworkRequestInfo.getRequestHeaderMap().get(key));
                }
            }
        }*/

        return chain.proceed(builder.build());
    }
}