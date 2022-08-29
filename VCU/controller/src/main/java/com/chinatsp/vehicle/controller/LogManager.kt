package com.chinatsp.vehicle.controller

import android.graphics.Color
import android.util.Log

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
        private const val TAG_PR = "VCU-"
        fun v(TAG: String, msg: String) {
            if (IS_DEBUG_MODE) {
                Log.v(TAG_PR + TAG, MSG_PR + msg)
            }
        }

        fun i(TAG: String, msg: String) {
            if (IS_DEBUG_MODE) {
                Log.i(TAG_PR + TAG, MSG_PR + msg)
            }
        }

        fun d(TAG: String, msg: String) {
            if (IS_DEBUG_MODE) {
                Log.d(TAG_PR + TAG, MSG_PR + msg)
            }
        }

        fun w(TAG: String, msg: String) {
            if (IS_DEBUG_MODE) {
                Log.w(TAG_PR + TAG, MSG_PR + msg)
            }
        }

        fun e(TAG: String, msg: String) {
            if (IS_DEBUG_MODE) {
                Log.e(TAG_PR + TAG, MSG_PR + msg)
            }
        }

        /**
         * 打印出错堆栈信息
         *
         * @param t Throwable
         */
        fun e(TAG: String, t: Throwable) {
            if (IS_DEBUG_MODE) {
                Log.e(TAG_PR + TAG, MSG_PR + t)
            }
        }

        fun e(TAG: String, msg: String?, e: Throwable?) {
            if (IS_DEBUG_MODE) {
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
                if (it.size < 5) "LogManager" else it[4].let { item ->
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