package com.common.xui.widget.imageview;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.common.xui.utils.ThreadUtil;


public class AnimationImageView extends androidx.appcompat.widget.AppCompatImageView {


    public AnimationImageView(Context context) {
        super(context);
    }

    public AnimationImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimationImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void startAnimation(int animationRes) {
        clearAnimation();
        origilDrawable = getDrawable();
        if (origilDrawable instanceof AnimationDrawable) {
            if (((AnimationDrawable) origilDrawable).isRunning()) {
                ((AnimationDrawable) origilDrawable).stop();
            }
        }

        setImageResource(animationRes);
        AnimationDrawable animationDrawable = (AnimationDrawable) getDrawable();
        animationDrawable.start();

//        ThreadUtil.getInstance().getMainThreadHandler().removeCallbacks(resetRunnable);
//        ThreadUtil.getInstance().postToMainThread(resetRunnable, duration + 200);
    }

    private Drawable origilDrawable;

    public void startAnimation(int animationRes, long duration) {
        clearAnimation();
        origilDrawable = getDrawable();
        if (origilDrawable instanceof AnimationDrawable) {
            if (((AnimationDrawable) origilDrawable).isRunning()) {
                ((AnimationDrawable) origilDrawable).stop();
            }
        }

        setImageResource(animationRes);
        AnimationDrawable animationDrawable = (AnimationDrawable) getDrawable();
        animationDrawable.start();

        ThreadUtil.getInstance().getMainThreadHandler().removeCallbacks(resetRunnable);
        ThreadUtil.getInstance().postToMainThread(resetRunnable, duration + 200);
    }

    public void startAnimation(int animationRes, long duration, int setRes) {
        clearAnimation();
        origilDrawable = getDrawable();
        if (origilDrawable instanceof AnimationDrawable) {
            if (((AnimationDrawable) origilDrawable).isRunning()) {
                ((AnimationDrawable) origilDrawable).stop();
            }
        }

        setImageResource(animationRes);
        AnimationDrawable animationDrawable = (AnimationDrawable) getDrawable();
        animationDrawable.start();

        origilDrawable = ContextCompat.getDrawable(getContext(), setRes);
        ThreadUtil.getInstance().getMainThreadHandler().removeCallbacks(resetRunnable);
        ThreadUtil.getInstance().postToMainThread(resetRunnable, duration + 200);
    }

    private Runnable resetRunnable = new Runnable() {
        @Override
        public void run() {

            Drawable drawable = getDrawable();
            if (drawable instanceof AnimationDrawable) {
                if (((AnimationDrawable) drawable).isRunning()) {
                    ((AnimationDrawable) drawable).stop();
                }
            }

            if (origilDrawable != null)
                setImageDrawable(origilDrawable);
        }
    };

    public void stopAnimation(int resId) {
        ThreadUtil.getInstance().getMainThreadHandler().removeCallbacks(resetRunnable);
        clearAnimation();
        Drawable drawable = getDrawable();
        if (drawable instanceof AnimationDrawable) {
            if (((AnimationDrawable) drawable).isRunning()) {
                ((AnimationDrawable) drawable).stop();
            }
        }

        setImageResource(resId);
    }

}
