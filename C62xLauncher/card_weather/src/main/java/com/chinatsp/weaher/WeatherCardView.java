package com.chinatsp.weaher;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.weaher.repository.WeatherBean;
import com.chinatsp.weaher.viewholder.WeatherBigCardHolder;
import com.chinatsp.weaher.viewholder.WeatherSmallCardHolder;
import com.chinatsp.weaher.weekday.DayWeatherBean;
import com.iflytek.autofly.weather.entity.WeatherInfo;

import java.util.LinkedList;
import java.util.List;

import card.service.ICardStyleChange;
import launcher.base.utils.EasyLog;
import launcher.base.utils.recent.RecentAppHelper;
import launcher.base.utils.view.LayoutParamUtil;


public class WeatherCardView extends ConstraintLayout implements ICardStyleChange, LifecycleOwner {

    private static final String TAG = "WeatherCardView";

    public WeatherCardView(@NonNull Context context) {
        super(context);
        init();
    }

    public WeatherCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WeatherCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public WeatherCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private WeatherCardController mController;

    private int mSmallWidth;
    private int mLargeWidth;

    private RecyclerView mRcvCardWeatherWeek;
    private View mLargeCardView;
    private View mSmallCardView;
    private WeatherSmallCardHolder mSmallCardHolder;
    private WeatherBigCardHolder mBigCardHolder;
    private ImageView ivCardWeatherRefresh;
    private boolean mExpand;
    private LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);

    private void init() {
        WeatherUtil.logI("WeatherCard init:"+hashCode());
        LayoutInflater.from(getContext()).inflate(R.layout.card_weather, this);
        mSmallCardView = findViewById(R.id.layoutSmallCardView);
        mSmallCardHolder = new WeatherSmallCardHolder(mSmallCardView);
        refreshDefault();
        mSmallWidth = (int) getResources().getDimension(R.dimen.card_width);
        mLargeWidth = (int) getResources().getDimension(R.dimen.card_width_large);
        mController = new WeatherCardController(this);
        ivCardWeatherRefresh = findViewById(R.id.ivCardWeatherRefresh);
        ivCardWeatherRefresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mController.requestWeatherInfo();
            }
        });
        //点击空白跳转至天气
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentAppHelper.launchApp(getContext(),"com.iflytek.autofly.weather");
            }
        });
        mController.requestWeatherInfo();
    }

    @Override
    public void expand() {
        mExpand = true;
        if (mLargeCardView == null) {
            mLargeCardView = LayoutInflater.from(getContext()).inflate(R.layout.card_weather_large, this, false);
            mBigCardHolder = new WeatherBigCardHolder(mLargeCardView);
        }
        addView(mLargeCardView);
        mLargeCardView.setVisibility(VISIBLE);
        mSmallCardView.setVisibility(GONE);
        LayoutParamUtil.setWidth(mLargeWidth, this);
        runExpandAnim();
        mBigCardHolder.updateWeatherList(mController.getWeatherList());
    }


    @Override
    public void collapse() {
        mExpand = false;
        mSmallCardView.setVisibility(VISIBLE);
        mLargeCardView.setVisibility(GONE);
        removeView(mLargeCardView);
        LayoutParamUtil.setWidth(mSmallWidth, this);
    }

    @Override
    public boolean hideDefaultTitle() {
        return false;
    }

    private void runExpandAnim() {
        ObjectAnimator.ofFloat(mLargeCardView, "translationX", -500, 0).setDuration(150).start();
        ObjectAnimator.ofFloat(mLargeCardView, "alpha", 0.1f, 1.0f).setDuration(500).start();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mLifecycleRegistry.setCurrentState(Lifecycle.State.CREATED);
        mController.addDataCallback();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mLifecycleRegistry.setCurrentState(Lifecycle.State.DESTROYED);
        mController.removeDataCallback();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
            mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
        } else if (visibility == GONE || visibility == INVISIBLE) {
            mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
            mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
        }
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mLifecycleRegistry;
    }

    public void refreshData(List<WeatherInfo> result) {
        post(new Runnable() {
            @Override
            public void run() {
                if (mExpand) {
                    mBigCardHolder.updateWeatherList(result);
                } else {
                    mSmallCardHolder.updateWeather(result.get(0));
                }
            }
        });
    }

    public void refreshDefault() {
        post(new Runnable() {
            @Override
            public void run() {
                if (mExpand) {
                    mBigCardHolder.updateDefault();
                } else {
                    mSmallCardHolder.updateDefault();
                }
            }
        });
    }

    private ObjectAnimator mObjectAnimator;
    private final int MIN_LOADING_ANIM_TIME = 1000;
    public void showLoading() {
        post(() -> {
            EasyLog.d(TAG, "showLoading");
            ivCardWeatherRefresh.setClickable(false);
            if (mObjectAnimator == null) {
                mObjectAnimator = createLoadingAnimator();
            } else {
                mObjectAnimator.cancel();
            }
            mObjectAnimator.start();
        });
    }

    private ObjectAnimator createLoadingAnimator() {
        EasyLog.d(TAG, "createLoadingAnimator");
        ObjectAnimator animator = ObjectAnimator.ofFloat(ivCardWeatherRefresh, "rotation", 0f, 360f).setDuration(MIN_LOADING_ANIM_TIME);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setRepeatCount(1);
//        animator.setInterpolator(new BounceInterpolator());
        return animator;
    }

    public void hideLoading() {
        post(() -> {
            EasyLog.d(TAG, "hideLoading");
            ivCardWeatherRefresh.setClickable(true);
//            if (mObjectAnimator != null) {
//                if (mObjectAnimator.isRunning()|| mObjectAnimator.isStarted()) {
//                    mObjectAnimator.cancel();
//                }
//            }
        });
    }
}
