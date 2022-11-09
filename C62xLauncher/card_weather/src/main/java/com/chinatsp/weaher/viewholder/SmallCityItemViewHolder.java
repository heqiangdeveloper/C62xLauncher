package com.chinatsp.weaher.viewholder;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chinatsp.weaher.R;
import com.chinatsp.weaher.WeatherTypeRes;
import com.chinatsp.weaher.WeatherUtil;
import com.chinatsp.weaher.repository.WeatherBean;
import com.chinatsp.weaher.repository.WeatherRepository;
import com.iflytek.autofly.weather.entity.WeatherInfo;

import java.util.List;

import launcher.base.ipc.IOnRequestListener;
import launcher.base.recyclerview.BaseViewHolder;
import launcher.base.utils.EasyLog;
import launcher.base.utils.recent.RecentAppHelper;

public class SmallCityItemViewHolder extends BaseViewHolder<String> {
    private static final String TAG = "SmallCityItemViewHolder";
    private final TextView tvCardWeatherCity;
    private final TextView tvCardWeatherTemperature;
    private final TextView tvCardWeatherDate;
    private final ImageView ivCardWeatherIcon;
    private final ImageView ivWeatherBg;
    private final Resources mResources;
    private final ImageView ivCardWeatherRefresh;
    private final Handler mMainHandler = new Handler(Looper.getMainLooper());



    public SmallCityItemViewHolder(@NonNull View rootView) {
        super(rootView);
        tvCardWeatherCity = rootView.findViewById(R.id.tvCardWeatherCity);
        tvCardWeatherTemperature = rootView.findViewById(R.id.tvCardWeatherTemperature);
        tvCardWeatherDate = rootView.findViewById(R.id.tvCardWeatherDate);
        ivCardWeatherIcon = rootView.findViewById(R.id.ivCardWeatherIcon);
        ivWeatherBg = rootView.findViewById(R.id.ivWeatherBg);
        ivCardWeatherRefresh = rootView.findViewById(R.id.ivCardWeatherRefresh);
        mResources = rootView.getContext().getResources();
        WeatherUtil.logD("SmallCityItemViewHolder init");
    }

    @Override
    public void bind(int position, String city) {
        super.bind(position, city);
        WeatherUtil.logD("SmallCityItemViewHolder bind");
        loadWeatherInfo(city);
        ivCardWeatherRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                loadWeatherInfo(city);
                WeatherRepository.getInstance().requestCityList();
            }
        });
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WeatherUtil.goApp(itemView.getContext());
            }
        });
    }

    private void loadWeatherInfo(String city) {
        WeatherUtil.logD("SmallCityItemViewHolder loadWeatherInfo");
        showLoading();
        WeatherRepository.getInstance().requestRefreshWeatherInfo(new IOnRequestListener() {
            @Override
            public void onSuccess(Object o) {
                List<WeatherInfo> mData = (List<WeatherInfo>) o;
                if (mData != null && !mData.isEmpty()) {
                    mMainHandler.post(() -> bindWeather(mData.get(0)));
                }
                hideLoading();
            }

            @Override
            public void onFail(String msg) {
                mMainHandler.post(() -> updateDefault());
                hideLoading();
            }
        }, city);
    }

    private void bindWeather(WeatherInfo weatherInfo) {
        WeatherUtil.logD("SmallCityItemViewHolder updateWeather weatherInfo : " + weatherInfo);
        if (weatherInfo == null) {
            return;
        }
        tvCardWeatherCity.setText(weatherInfo.getCity());
        tvCardWeatherTemperature.setText(WeatherUtil.getTemperatureRange(weatherInfo, mResources));
        tvCardWeatherDate.setText(WeatherUtil.getToday());

        WeatherTypeRes weatherTypeRes = WeatherUtil.parseType(weatherInfo.getWeather());
        ivCardWeatherIcon.setImageResource(weatherTypeRes.getIcon());
        ivWeatherBg.setVisibility(View.VISIBLE);
        ivWeatherBg.setImageResource(weatherTypeRes.getSmallCardBg());
    }
    private void updateDefault() {
        WeatherUtil.logD("SmallCityItemViewHolder updateWeather default");
        tvCardWeatherDate.setText(WeatherUtil.getToday());
        WeatherTypeRes weatherTypeRes = new WeatherTypeRes(WeatherBean.TYPE_UNKNOWN);
        ivCardWeatherIcon.setImageResource(weatherTypeRes.getIcon());
        ivWeatherBg.setVisibility(View.INVISIBLE);
    }

    private ObjectAnimator mObjectAnimator;
    private final int MIN_LOADING_ANIM_TIME = 1000;
    public void showLoading() {
        EasyLog.d(TAG, "showLoading");
        mMainHandler.post(() -> {
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
        return animator;
    }

    public void hideLoading() {
        mMainHandler.post(() -> {
            EasyLog.d(TAG, "hideLoading");
//            ivCardWeatherRefresh.setClickable(true);
//            if (mObjectAnimator != null) {
//                if (mObjectAnimator.isRunning()|| mObjectAnimator.isStarted()) {
//                    mObjectAnimator.cancel();
//                }
//            }
        });
    }
}
