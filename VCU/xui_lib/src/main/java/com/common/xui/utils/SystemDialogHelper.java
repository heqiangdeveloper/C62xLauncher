package com.common.xui.utils;

import android.os.Handler;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

public class SystemDialogHelper {
    Timer timer = new Timer();
    private OnCountDownListener mListener;

    /**
     * 启动计时器
     * @param time 规定的时间
     * @param listener 回调
     */
    public void timeSchedule(Long time,OnCountDownListener listener) {
        timer.schedule(task, time);
        this.mListener = listener;
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
              mListener.onFinished();
            }
            super.handleMessage(msg);
        };
    };

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            // 发送消息
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };

    /**
     * 计时时监听接口
     *
     * @author xx
     */
    public interface OnCountDownListener {
        /**
         * 计时结束
         */
        void onFinished();
    }

    /**
     * 取消计时
     */
    public void cancel() {
        timer.cancel();
    }

    /**
     * 资源回收
     */
    public void recycle() {
        cancel();
        mListener = null;
    }
}
