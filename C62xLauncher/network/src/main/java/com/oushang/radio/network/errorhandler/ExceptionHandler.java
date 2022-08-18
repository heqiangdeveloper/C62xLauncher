package com.oushang.radio.network.errorhandler;

import android.net.ParseException;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.oushang.radio.network.NetworkLog;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.io.IOException;
import java.net.ConnectException;

import retrofit2.HttpException;

/**
 * @author xuyuanli
 * @description
 * @time 2021/2/2 15:02
 * @UpdateUser: xuyuanli
 * @UpdateDate: 2021/2/2 15:02
 * @UpdateRemark:
 */
public class ExceptionHandler {
    private static final String TAG = "ExceptionHandler";
    /**
     * 200	请求成功
     * 201	创建成功
     * 202	更新成功
     * 301	请求永久重定向
     * 302	请求临时重定向
     * 304	未更改
     * 400	请求地址不存在或者包含不支持的参数
     * 401	未授权，因为未成功进行身份认证
     * 403	被禁止访问，因为没有访问特定资源的权限
     * 404	请求的资源不存在
     * 422	服务器理解请求，但其中包含非法参数（如包含XSS攻击风险参数）
     * 429	调用请求频率超出限额
     * 500	服务器内部错误
     */

    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;
    private static final int REQUEST_TIMEOUT = 408;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int BAD_GATEWAY = 502;
    private static final int SERVICE_UNAVAILABLE = 503;
    private static final int GATEWAY_TIMEOUT = 504;

    public static ResponeThrowable handleException(Throwable e) throws IOException {
        NetworkLog.d(TAG, "handleException e=" + e);
        ResponeThrowable ex;
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            ErrorResponse errorResponse = null;
            if (httpException.response()!=null&&httpException.response().body()!=null){
                errorResponse= new Gson().fromJson(httpException.response().body().toString(),ErrorResponse.class);
            }
            ex = new ResponeThrowable(e, ERROR.HTTP_ERROR);
            if (errorResponse!=null){
                ex.message= errorResponse.getMessage();
                ex.code=errorResponse.getStatus();
            }else {
                switch (httpException.code()) {
                    case UNAUTHORIZED:
                    case FORBIDDEN:
                    case NOT_FOUND:
                    case REQUEST_TIMEOUT:
                    case GATEWAY_TIMEOUT:
                    case INTERNAL_SERVER_ERROR:
                    case BAD_GATEWAY:
                    case SERVICE_UNAVAILABLE:
                    default:
                        ex.message = "网络错误";
                        break;
                }
            }
            return ex;
        } else if (e instanceof ServerException) {
            ServerException resultException = (ServerException) e;
            ex = new ResponeThrowable(resultException, resultException.code);
            ex.message = resultException.message;
            return ex;
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException) {
            ex = new ResponeThrowable(e, ERROR.PARSE_ERROR);
            ex.message = "解析错误";
            return ex;
        } else if (e instanceof ConnectException) {
            ex = new ResponeThrowable(e, ERROR.NETWORD_ERROR);
            ex.message = "连接失败";
            return ex;
        } else if (e instanceof javax.net.ssl.SSLHandshakeException) {
            ex = new ResponeThrowable(e, ERROR.SSL_ERROR);
            ex.message = "证书验证失败";
            return ex;
        } else if (e instanceof ConnectTimeoutException) {
            ex = new ResponeThrowable(e, ERROR.TIMEOUT_ERROR);
            ex.message = "连接超时";
            return ex;
        } else if (e instanceof java.net.SocketTimeoutException) {
            ex = new ResponeThrowable(e, ERROR.TIMEOUT_ERROR);
            ex.message = "连接超时";
            return ex;
        } else {
            ex = new ResponeThrowable(e, ERROR.UNKNOWN);
            ex.message = "未知错误";
            return ex;
        }
    }




    /**
     * todo:约定异常
     * 这块需要和client重新定义，待定
     */
    public class ERROR {
        /**
         * 未知错误
         */
        public static final int UNKNOWN = 1000;
        /**
         * 解析错误
         */
        public static final int PARSE_ERROR = 1001;
        /**
         * 网络错误
         */
        public static final int NETWORD_ERROR = 1002;
        /**
         * 协议出错
         */
        public static final int HTTP_ERROR = 1003;

        /**
         * 证书出错
         */
        public static final int SSL_ERROR = 1005;

        /**
         * 连接超时
         */
        public static final int TIMEOUT_ERROR = 1006;
    }

    public static class ResponeThrowable extends Exception {
        public int code;
        public String message;
        public Throwable mThrowable;

        public ResponeThrowable(Throwable throwable, int code) {
            super(throwable);
            this.code = code;
            mThrowable=throwable;
        }
    }

    public class ServerException extends RuntimeException {
        public int code;
        public String message;
    }
}

