package com.chinatsp.apppanel.time;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.anarchy.classifyview.simple.widget.InsertAbleGridView;

public class CalcuTimer extends CountDownTimer {
    private CalcuTimerCallback callback;
    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public CalcuTimer(long millisInFuture, long countDownInterval, CalcuTimerCallback callback) {
        super(millisInFuture, countDownInterval);
        this.callback = callback;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        callback.onCount((int) (millisUntilFinished / 1000));
    }

    //技师完触发
    @Override
    public void onFinish() {
        callback.onFinish();
    }
}
