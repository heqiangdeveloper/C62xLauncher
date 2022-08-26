package launcher.base.utils.flowcontrol;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import launcher.base.utils.EasyLog;

public abstract class PollingTask {
    private long mInitDelay;
    private long mIntervalMillieSeconds;
    private Disposable mDisposable;
    private String mName;

    protected abstract void executeTask();

    protected abstract boolean enableExit();

    public PollingTask(long initDelay, long intervalMillieSeconds, String name) {
        mInitDelay = initDelay;
        mIntervalMillieSeconds = intervalMillieSeconds;
        mName = name;
    }

    public void execute() {
        EasyLog.d(mName, "PollingTask start execute name: "+mName);
        mDisposable = Observable.interval(mInitDelay, mIntervalMillieSeconds, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        boolean enableExit = enableExit();
                        EasyLog.d(mName, "PollingTask execute name: "+mName + " count:" + aLong
                                + " , enableExit:" + enableExit +" , thread:"+Thread.currentThread().getName());
                        if (enableExit) {
                            stopDispose();
                        } else {
                            executeTask();
                        }
                    }
                });
    }

    private void stopDispose() {
        mDisposable.dispose();
    }
}
