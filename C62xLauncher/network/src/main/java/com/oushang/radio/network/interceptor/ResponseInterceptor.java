package com.oushang.radio.network.interceptor;


import android.text.TextUtils;

import com.oushang.radio.network.NetworkLog;

import java.io.IOException;
import java.net.SocketTimeoutException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @author xuyuanli
 * @description
 * @time 2021/2/2 15:01
 * @UpdateUser: xuyuanli
 * @UpdateDate: 2021/2/2 15:01
 * @UpdateRemark:
 */
public class ResponseInterceptor implements Interceptor {
    private static final String TAG = "ResponseInterceptor";

    public ResponseInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String url = request.url().toString();
        NetworkLog.d(TAG, "intercept request url=\n" + url);

        Response response = null;
        try {
            response = chain.proceed(request);
        } catch (SocketTimeoutException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
        String method = request.method();


        //重点部分----------针对post请求做处理-----------------------
       /* if ("POST".equals(method)) {//post请求需要拼接
            StringBuilder sb = new StringBuilder();
            if (request.body() instanceof FormBody) {
                FormBody body = (FormBody) request.body();
                for (int i = 0; i < body.size(); i++) {
                    sb.append(body.encodedName(i) + "=" + body.encodedValue(i) + ",");
                }
                sb.delete(sb.length() - 1, sb.length());
                NetworkLog.d(TAG, "post 请求报文： RequestParams:{" + sb.toString() + "}");
            }
        }*/

        //todo:二维码的返回值是head qrcode和图片的inputstream，这块需要特殊处理
        String head = response.header("qrcode_id");
        int code = response.code();
        NetworkLog.d(TAG, "intercept code=" + code);
        //重定向处理，获取登录的重定向url
        if (code == 302) {
            //获取重定向地址
            String redirectUri = response.headers().get("Location");
            NetworkLog.d(TAG, "intercept reponseUrl=" + redirectUri);
//            if (!TextUtils.isEmpty(redirectUri)) {
//                                ResponseBody myBody = ResponseBody.create(response.body().contentType(), redirectUri);
//                                NetworkLog.d(TAG, "intercept myBody=" + myBody.string());
//                                return response.newBuilder().body(myBody).build();
//            }
        }
//                NetworkLog.d(TAG, "intercept head=" + head);
        if (!TextUtils.isEmpty(head)) {
            return response.newBuilder().body(ResponseBody.create(response.body().contentType(), head)).build();
        }
        String rawJson = response.body() == null ? "" : response.body().string();
        NetworkLog.d(TAG,"intercept url=" + url);
        NetworkLog.d(TAG,"intercept rawJson=" + rawJson);
        NetworkLog.d(TAG,"intercept header  X-Signature " +  request.headers().get("X-Signature"));

        return response.newBuilder().body(ResponseBody.create(response.body().contentType(), rawJson)).build();
    }
}
