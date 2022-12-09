package launcher.base.utils.flowcontrol;

import java.util.function.Consumer;

import launcher.base.utils.EasyLog;

public abstract class DebounceTask {
    public static final int DEFAULT_INTERVAL_TIME = 1000;
    private  String TAG = "DebounceTask";
    private int mIntervalTime = DEFAULT_INTERVAL_TIME;

    public abstract void execute();
    private long mLastValidExecuteTime;
    private long mLastTryExecuteTime;

    public void emit(){
        long now = System.currentTimeMillis();
        long diffToValid = now - mLastValidExecuteTime;
        long diffToInvalid = now - mLastTryExecuteTime;
        mLastTryExecuteTime = now;
        if (diffToValid < mIntervalTime && diffToInvalid < mIntervalTime) {
            EasyLog.w(TAG, "emit fail: execute too fast."
                 + " , diff:" + diffToValid + " , diffToInvalid:" + diffToInvalid);
            return;
        }
        mLastValidExecuteTime = now;
        EasyLog.d(TAG,"ready execute");
        execute();
    }

    public void setTag(String tag) {
        this.TAG = tag;
    }

    public DebounceTask(int intervalTime) {
        mIntervalTime = intervalTime;
    }
}
