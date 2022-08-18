package com.oushang.radio.network.observer;

import android.os.RemoteException;
import android.util.Log;

import com.oushang.radio.network.errorhandler.ExceptionHandler;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @description
 * @author xuyuanli
 * @time 2021/2/2 15:02
 * @UpdateUser:     xuyuanli
 * @UpdateDate:    2021/2/2 15:02
 * @UpdateRemark:
 */
public abstract class BaseObserver<T> implements Observer<T> {
    private static final String TAG = "BaseObserver";
    public BaseObserver() {
    }
    @Override
    public void onError(Throwable e) {
        Log.e(TAG, e.getMessage());
        // todo error somthing

        if(e instanceof ExceptionHandler.ResponeThrowable){
            try {
                onError((ExceptionHandler.ResponeThrowable)e);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        } else {
            try {
                onError(new ExceptionHandler.ResponeThrowable(e, ExceptionHandler.ERROR.UNKNOWN));
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onSubscribe(Disposable d) {
    }


    @Override
    public void onComplete() {
    }


    public abstract void onError(ExceptionHandler.ResponeThrowable e) throws RemoteException;

}
