package com.chinatsp.weaher.viewholder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.weaher.R;
import com.chinatsp.weaher.WeatherUtil;
import com.chinatsp.weaher.viewholder.city.SmallCityListAdapter;
import com.chinatsp.weaher.viewholder.indicator.PointIndicator;
import com.iflytek.autofly.weather.entity.WeatherInfo;

import java.util.List;

import launcher.base.utils.EasyLog;

public class WeatherSmallCardHolder extends WeatherCardHolder {


    private final TextView tvCardWeatherCity;
    private final TextView tvCardWeatherTemperature;
    private final TextView tvCardWeatherDate;
    private final ImageView ivCardWeatherIcon;
    private final ImageView ivWeatherBg;
    private final ImageView ivCardWeatherRefresh;

    private InnerVerticalRecyclerView rcvCityList;
    private SmallCityListAdapter mCityListAdapter;

    private OnPageChangedListener mOnPageChangedListener;
    private ViewGroup mLayoutIndicator;
    private PointIndicator mIndicator;

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
        mLayoutIndicator = rootView.findViewById(R.id.layoutIndicator);
        mIndicator = new PointIndicator(mLayoutIndicator);

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
        mIndicator.select(pos);
        if (mOnPageChangedListener != null) {
            mOnPageChangedListener.onSelected(pos);
        }
        EasyLog.d("updatePosition", "updatePosition pos:"+pos);
        updateRcvStatus(pos);
    }

    public void scrollToPosition(int pos) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) rcvCityList.getLayoutManager();
        if (layoutManager != null) {
            layoutManager.scrollToPositionWithOffset(pos, 0);
            mIndicator.select(pos);
            EasyLog.d("scrollToPosition", "scrollToPosition pos:"+pos);
            updateRcvStatus(pos);
        }
    }

    private void updateRcvStatus(int pos) {
        if (pos > 0) {
            rcvCityList.setScrollTop(false);
        } else if (pos == 0) {
            rcvCityList.setScrollTop(true);
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
        mIndicator.reset(cityList.size());
        showCityListRecyclerView();
        mCityListAdapter.setData(cityList);
        WeatherUtil.logI("WeatherSmallCardHolder updateCityList " + cityList);
        rcvCityList.setScrollTop(true);
    }

}
