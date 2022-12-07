package launcher.base.utils.flowcontrol;

import java.util.function.Consumer;

import launcher.base.utils.EasyLog;

public abstract class DebounceTask {
    public static final int DEFAULT_INTERVAL_TIME = 1000;
    private static final String TAG = "DebounceTask";
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
            EasyLog.w(TAG, "go fail: execute too fast."
                 + " , diff:" + diffToValid + " , diffToInvalid:" + diffToInvalid);
            return;
        }
        mLastValidExecuteTime = now;
        execute();
    }

    public DebounceTask(int intervalTime) {
        mIntervalTime = intervalTime;
    }
}
