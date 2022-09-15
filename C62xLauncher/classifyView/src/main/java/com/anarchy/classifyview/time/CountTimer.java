package com.anarchy.classifyview.time;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.anarchy.classifyview.simple.widget.InsertAbleGridView;

public class CountTimer extends CountDownTimer {
    private RecyclerView recyclerView;

    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public CountTimer(long millisInFuture, long countDownInterval, RecyclerView recyclerView) {
        super(millisInFuture, countDownInterval);
        this.recyclerView = recyclerView;
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
        RelativeLayout relativeLayout;
        InsertAbleGridView insertAbleGridView;
        for(int i = 0; i < recyclerView.getChildCount(); i++){
            relativeLayout = (RelativeLayout) recyclerView.getChildAt(i);
            insertAbleGridView = (InsertAbleGridView) relativeLayout.getChildAt(0);
            if(insertAbleGridView.getChildCount() == 1){//非文件夹
                ImageView iv = (ImageView) relativeLayout.getChildAt(2);
                iv.setVisibility(View.GONE);
            }
        }
    }
}
