package launcher.base.async;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AsyncSchedule {
    // 4个常驻线程
    // 无线程数上线. 保证最大吞吐量
    // 任务执行完成后, 线程立即销毁
    // LinkedBlockingQueue 保证不会拒绝任务
    private static final ThreadPoolExecutor mExecutor = new ThreadPoolExecutor(
            4, Integer.MAX_VALUE, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<>()
    );

    public static void execute(Runnable runnable) {
        mExecutor.execute(runnable);
    }
}
