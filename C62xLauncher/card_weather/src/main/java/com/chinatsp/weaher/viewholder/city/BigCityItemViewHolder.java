package com.chinatsp.weaher.viewholder.city;

import static com.chinatsp.weaher.WeatherUtil.convertNull;

import android.content.Context;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.weaher.R;
import com.chinatsp.weaher.WeatherTypeRes;
import com.chinatsp.weaher.WeatherUtil;
import com.chinatsp.weaher.repository.WeatherRepository;
import com.chinatsp.weaher.weekday.WeekDayAdapter;
import com.iflytek.autofly.weather.entity.WeatherInfo;

import java.util.List;

import launcher.base.ipc.IOnRequestListener;
import launcher.base.recyclerview.BaseViewHolder;
import launcher.base.recyclerview.SimpleRcvDecoration;

public class BigCityItemViewHolder extends BaseViewHolder<String> {

    private TextView tvCardWeatherLocation;
    private TextView tvCardWeatherWord;
    private TextView tvCardWeatherAirValue;
    private TextView tvCardWeatherAirDesc;
    private TextView tvCardWeatherPmLabel2;
    private TextView tvCardWeatherTemperature;
    private TextView tvCardWeatherTemperatureRange;
    private ImageView ivCardWeatherIcon;
    private ImageView ivWeatherBg;
    private RecyclerView mRcvCardWeatherWeek;
    private final VideoView videoViewCardWeather;

    ;
    private WeekDayAdapter mWeekDayAdapter;
    private Resources mResources;
    private Handler mMainHandler = new Handler(Looper.getMainLooper());

    public BigCityItemViewHolder(@NonNull View itemView) {
        super(itemView);
        mResources = itemView.getResources();
        tvCardWeatherLocation = itemView.findViewById(R.id.tvCardWeatherLocation);
        tvCardWeatherWord = itemView.findViewById(R.id.tvCardWeatherWord);
        tvCardWeatherAirValue = itemView.findViewById(R.id.tvCardWeatherAirValue);
        tvCardWeatherAirDesc = itemView.findViewById(R.id.tvCardWeatherAirDesc);
        tvCardWeatherPmLabel2 = itemView.findViewById(R.id.tvCardWeatherPmLabel2);
        tvCardWeatherTemperature = itemView.findViewById(R.id.tvCardWeatherTemperature);
        tvCardWeatherTemperatureRange = itemView.findViewById(R.id.tvCardWeatherTemperatureRange);
        ivCardWeatherIcon = itemView.findViewById(R.id.ivCardWeatherIcon);
        ivWeatherBg = itemView.findViewById(R.id.ivWeatherBg);
        mRcvCardWeatherWeek = itemView.findViewById(R.id.rcvCardWeatherWeek);
        videoViewCardWeather = itemView.findViewById(R.id.videoViewCardWeather);

        initWeeklyWeather();
    }

    private void initWeeklyWeather() {
        Context context = itemView.getContext();
        mWeekDayAdapter = new WeekDayAdapter(context);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        };
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        if (mRcvCardWeatherWeek.getItemDecorationCount() == 0) {
            SimpleRcvDecoration decoration = new SimpleRcvDecoration(44, layoutManager);
            mRcvCardWeatherWeek.addItemDecoration(decoration);
        }

        mRcvCardWeatherWeek.setLayoutManager(layoutManager);
        mRcvCardWeatherWeek.setAdapter(mWeekDayAdapter);
    }

    @Override
    public void bind(int position, String city) {
        super.bind(position, city);
        loadWeatherInfo(city);
        itemView.setOnClickListener(mOnClickListener);
    }
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            WeatherUtil.goApp(v.getContext());
        }
    };

    private void loadWeatherInfo(String city) {
        WeatherUtil.logD("BigCityItemViewHolder loadWeatherInfo");
        tvCardWeatherLocation.setText(city);
        WeatherRepository.getInstance().loadWeatherByCity(new IOnRequestListener() {
            @Override
            public void onSuccess(Object o) {
                List<WeatherInfo> mData = (List<WeatherInfo>) o;
                if (mData != null && !mData.isEmpty()) {
                    mMainHandler.post(() -> bindWeather(mData));
                }
            }

            @Override
            public void onFail(String msg) {
                mMainHandler.post(() -> updateDefault());
            }
        }, city);
    }

    private void updateDefault() {

    }

    public void bindWeather(List<WeatherInfo> weatherInfoList) {
        WeatherUtil.logI("WeatherBigCardHolder updateWeatherList : " + weatherInfoList);
        mWeekDayAdapter.setDayWeatherList(weatherInfoList);
        if (weatherInfoList == null || weatherInfoList.isEmpty()) {
            return;
        }
        updateWeather(weatherInfoList.get(0));
    }

    public void updateWeather(WeatherInfo weatherInfo) {
        WeatherUtil.setWeatherDesc(tvCardWeatherWord, weatherInfo.getWeather());
        tvCardWeatherAirValue.setText(convertNull(weatherInfo.getPm25()));
        String airQualityLabel = mResources.getString(R.string.weather_air_quality)+" ";
        tvCardWeatherAirDesc.setText(String.format("%s%s", airQualityLabel, convertNull(weatherInfo.getAirQuality())));
        setTemp(tvCardWeatherTemperature, weatherInfo.getTemp());
        tvCardWeatherTemperatureRange.setText(WeatherUtil.getTemperatureRange(weatherInfo, mResources));
        WeatherTypeRes weatherTypeRes = WeatherUtil.parseType(weatherInfo.getWeather());
        ivCardWeatherIcon.setImageResource(weatherTypeRes.getIcon());
        ivWeatherBg.setImageResource(weatherTypeRes.getBigCardBg());
        videoViewCardWeather.setVisibility(View.GONE);
//        WeatherUtil.setDataSource(videoViewCardWeather, weatherTypeRes.getVideoBigBg());
//        videoViewCardWeather.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
//                mp.start();
//                mp.setLooping(true);
//            }
//        });
    }



    private void setTemp(TextView textView, String temp) {
        if (textView == null) {
            return;
        }
        String realTemp = "?";
        if (!TextUtils.isEmpty(temp)) {
            try {
                Float floatValue = Float.valueOf(temp);
                realTemp = ""+ floatValue.intValue();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        textView.setText(realTemp);
    }
}
