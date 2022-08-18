package com.oushang.radio.network.errorhandler;

import com.oushang.radio.network.NetworkLog;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * @description HttpResponseFunc处理以下两类网络错误：
 * 1、http请求相关的错误，例如：404，403，socket timeout等等；
 * 2、应用数据的错误会抛RuntimeException，最后也会走到这个函数来统一处理；
 * @author xuyuanli
 * @time 2021/2/2 15:01
 * @UpdateUser:     xuyuanli
 * @UpdateDate:    2021/2/2 15:01
 * @UpdateRemark:
 */

public class HttpErrorHandler<T> implements Function<Throwable, Observable<T>> {
    @Override
    public io.reactivex.Observable<T> apply(Throwable throwable) throws Exception {
        NetworkLog.d("HttpErrorHandler","handleException throwable="+throwable);
        return io.reactivex.Observable.error(ExceptionHandler.handleException(throwable));
    }
}
