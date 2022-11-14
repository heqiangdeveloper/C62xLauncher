package com.chinatsp.weaher.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.weaher.R;
import com.chinatsp.weaher.WeatherUtil;
import com.chinatsp.weaher.viewholder.city.SmallCityListAdapter;
import com.iflytek.autofly.weather.entity.WeatherInfo;

import java.util.IllegalFormatCodePointException;
import java.util.List;

public class WeatherSmallCardHolder extends WeatherCardHolder{


    private final TextView tvCardWeatherCity;
    private final TextView tvCardWeatherTemperature;
    private final TextView tvCardWeatherDate;
    private final ImageView ivCardWeatherIcon;
    private final ImageView ivWeatherBg;
    private final ImageView ivCardWeatherRefresh;

    private RecyclerView rcvCityList;
    private SmallCityListAdapter mCityListAdapter;

    private OnPageChangedListener mOnPageChangedListener;

    public void setOnPageChangedListener(OnPageChangedListener onPageChangedListener) {
        mOnPageChangedListener = onPageChangedListener;
    }


    public WeatherSmallCardHolder(View rootView) {
        super(rootView);
        tvCardWeatherCity = rootView.findViewById(R.id.tvCardWeatherCity);
        tvCardWeatherTemperature = rootView.findViewById(R.id.tvCardWeatherTemperature);
        tvCardWeatherDate = rootView.findViewById(R.id.tvCardWeatherDate);
        ivCardWeatherIcon = rootView.findViewById(R.id.ivCardWeatherIcon);
        ivWeatherBg = rootView.findViewById(R.id.ivWeatherBg);
        rcvCityList = rootView.findViewById(R.id.rcvCityList);
        ivCardWeatherRefresh = rootView.findViewById(R.id.ivCardWeatherRefresh);
        initCityRcv(rcvCityList);
    }

    private void initCityRcv(RecyclerView rcvCityList) {
        mCityListAdapter = new SmallCityListAdapter(mRootView.getContext());
        rcvCityList.setAdapter(mCityListAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(rcvCityList.getContext());
        rcvCityList.setLayoutManager(layoutManager);
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(rcvCityList);

        rcvCityList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    View snapView = pagerSnapHelper.findSnapView(recyclerView.getLayoutManager());
                    if (snapView != null) {
                        int pos = layoutManager.getPosition(snapView);
                        updatePosition(pos);
                    }
                }
            }
        });
    }

    private void updatePosition(int pos) {
        if (mOnPageChangedListener != null) {
            mOnPageChangedListener.onSelected(pos);
        }
    }

    public void scrollToPosition(int pos) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) rcvCityList.getLayoutManager();
        if (layoutManager != null) {
            layoutManager.scrollToPositionWithOffset(pos,0);
        }
    }

    @Override
    public void updateDefault() {

    }

    @Override
    public void updateWeather(WeatherInfo weatherInfo) {

    }

    private void showCityListRecyclerView() {
        tvCardWeatherCity.setVisibility(View.INVISIBLE);
        tvCardWeatherTemperature.setVisibility(View.INVISIBLE);
        tvCardWeatherDate.setVisibility(View.INVISIBLE);
        ivCardWeatherIcon.setVisibility(View.INVISIBLE);
        ivWeatherBg.setVisibility(View.INVISIBLE);
        ivCardWeatherRefresh.setVisibility(View.INVISIBLE);
    }

    public void updateCityList(List<String> cityList) {
        showCityListRecyclerView();
        mCityListAdapter.setData(cityList);
        WeatherUtil.logI("WeatherSmallCardHolder updateCityList "+ cityList);
    }
}
