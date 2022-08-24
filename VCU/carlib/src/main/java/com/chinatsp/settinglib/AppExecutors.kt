package com.chinatsp.settinglib

import android.os.Handler
import android.os.Looper
import com.chinatsp.settinglib.AppExecutors
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * <pre>
 * desc   : 应用的全局线程池 （包括单线程池的磁盘io，多线程池的网络io和主线程）
 * author : common
 * time   : 2018/4/27 下午8:40
</pre> *
 */
class AppExecutors private constructor(
    /**
     * 单线程池
     */
    private val mSingleIO: ExecutorService = Executors.newSingleThreadExecutor(),
    /**
     * 多线程池
     */
    private val mPoolIO: ExecutorService = Executors.newFixedThreadPool(
        Runtime.getRuntime().availableProcessors()
    ),
    /**
     * 主线程
     */
    private val mMainThread: Executor =
        MainThreadExecutor()
) {
    /**
     * 获取单线程池
     * @return
     */
    fun singleIO(): ExecutorService {
        return mSingleIO
    }

    /**
     * 获取磁盘单线程池
     * @return
     */
    fun diskIO(): ExecutorService {
        return mSingleIO
    }

    /**
     * 获取多线程池
     * @return
     */
    fun poolIO(): ExecutorService {
        return mPoolIO
    }

    /**
     * 获取网络请求多线程池
     * @return
     */
    fun networkIO(): ExecutorService {
        return mPoolIO
    }

    /**
     * 获取主线程
     * @return
     */
    fun mainThread(): Executor {
        return mMainThread
    }

    private class MainThreadExecutor : Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())
        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }
    }

    companion object {
        private var sInstance: AppExecutors? = null

        /**
         * 获取线程管理实例
         *
         * @return
         */
        fun get(): AppExecutors? {
            if (sInstance == null) {
                synchronized(AppExecutors::class.java) {
                    if (sInstance == null) {
                        sInstance = AppExecutors()
                    }
                }
            }
            return sInstance
        }
    }
}