package com.anarchy.classifyview.time;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.anarchy.classifyview.simple.widget.InsertAbleGridView;
import com.anarchy.classifyview.util.MyConfigs;

public class CountTimer extends CountDownTimer {
    private RecyclerView recyclerView;
    private SharedPreferences.Editor editor;

    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public CountTimer(long millisInFuture, long countDownInterval, RecyclerView recyclerView,SharedPreferences.Editor editor) {
        super(millisInFuture, countDownInterval);
        this.recyclerView = recyclerView;
        this.editor = editor;
    }

    @Override
    public void onTick(long millisUntilFinished) {

    }

    //技师完触发
    @Override
    public void onFinish() {
        Log.d("CountTimer","onFinish");
        hideDeleteIcon(recyclerView);
    }

    private void hideDeleteIcon(RecyclerView recyclerView){
        editor.putBoolean(MyConfigs.MAINSHOWDELETE,false);
        editor.commit();
        recyclerView.getAdapter().notifyDataSetChanged();
    }
}
