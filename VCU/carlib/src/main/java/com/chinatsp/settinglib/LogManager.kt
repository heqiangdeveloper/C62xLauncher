package com.chinatsp.settinglib

import android.graphics.Color
import timber.log.Timber

/**
 * @author luohong
 */
class LogManager private constructor() {
    enum class Level(var color: Int) {
        VERBOSE(-7829368), INFO(Color.rgb(0, 192, 0)), DEBUG(
            Color.rgb(0, 0, 127)
        ),
        WARNING(Color.rgb(255, 149, 144)), ERROR(-65536);
    }

    interface LogListener {
        fun onAddLog(var1: Level?, var2: String?, var3: String?)
    }

    companion object {
        private const val IS_DEBUG_MODE = true
        private var listener: LogListener? = null
        private const val MSG_PR = "==> "
        private const val TAG_PR = ""
        fun v(TAG: String, msg: String) {
            if (IS_DEBUG_MODE) {
//            Logger.t(TAG).v(PRDFIX + msg);
//                Log.v(TAG_PR + TAG, MSG_PR + msg)
                Timber.tag(TAG_PR + TAG).v("%s%s", MSG_PR, msg)
                listener?.onAddLog(Level.VERBOSE, TAG_PR + TAG, msg)
            }
        }

        fun i(TAG: String, msg: String) {
            if (IS_DEBUG_MODE) {
//            Logger.t(TAG).i(PRDFIX + msg);
//                Log.i(TAG_PR + TAG, MSG_PR + msg)
                Timber.tag(TAG_PR + TAG).i("%s%s", MSG_PR, msg)
                listener?.onAddLog(Level.INFO, TAG_PR + TAG, msg)
            }
        }

        fun d(TAG: String, msg: String) {
            if (IS_DEBUG_MODE) {
//            Logger.t(TAG).d(PRDFIX + msg);
//                Log.d(TAG_PR + TAG, MSG_PR + msg)
                Timber.tag(TAG_PR + TAG).d("%s%s", MSG_PR, msg)
                listener?.onAddLog(Level.DEBUG, TAG_PR + TAG, msg)
            }
        }

        fun w(TAG: String, msg: String) {
            if (IS_DEBUG_MODE) {
//            Logger.t(TAG).w(PRDFIX + msg);
//                Log.w(TAG_PR + TAG, MSG_PR + msg)
                Timber.tag(TAG_PR + TAG).w("%s%s", MSG_PR, msg)
                listener?.onAddLog(Level.WARNING, TAG_PR + TAG, msg)
            }
        }

        fun e(TAG: String, msg: String) {
            if (IS_DEBUG_MODE) {
//            Logger.t(TAG).e(PRDFIX + msg);
//                Log.e(TAG_PR + TAG, MSG_PR + msg)
                Timber.tag(TAG_PR + TAG).v("%s%s", MSG_PR, msg)
                listener?.onAddLog(Level.ERROR, TAG_PR + TAG, msg)
            }
        }

        /**
         * 打印出错堆栈信息
         *
         * @param t Throwable
         */
        fun e(TAG: String, t: Throwable) {
            if (IS_DEBUG_MODE) {
//            Logger.t(TAG).e(PRDFIX + t);
//                Log.e(TAG_PR + TAG, MSG_PR + t)
                Timber.tag(TAG_PR + TAG).e("%s%s", MSG_PR, t.message)
                listener?.onAddLog(Level.ERROR, TAG_PR + TAG, t.toString())
            }
        }

        fun e(TAG: String, msg: String, e: Throwable) {
            if (IS_DEBUG_MODE) {
//            Logger.t(TAG).e(msg, e);
                Timber.tag(TAG_PR + TAG).e(e)
                listener?.onAddLog(Level.ERROR, TAG_PR + TAG, msg)
            }
        }

        fun e(message: String) {
            e(createTag(), message)
        }

        @JvmStatic
        fun d(message: String) {
            d(createTag(), message)
        }

        fun i(message: String) {
            i(createTag(), message)
        }

        fun w(message: String) {
            w(createTag(), message)
        }

        fun v(message: String) {
            v(createTag(), message)
        }

        private fun createTag(): String {
            val elements = Thread.currentThread().stackTrace
            return elements.let {
                if (it.size < 5) "LogManager" else it[4].let {item ->
                    item.fileName.split(Regex.fromLiteral(".")).first()
                }
            }
        }

        fun printStackTrace() {
            val builder = StringBuilder()
            val stack = Thread.currentThread().stackTrace
            for (i in stack.indices) {
                builder.append("\tat ")
                builder.append(stack[i].toString())
                builder.append("\n")
            }
            i(builder.toString())
        }

        fun setListener(listener: LogListener?) {
            this.listener = listener
        }
    }

    init {
        throw Exception()
    }
}