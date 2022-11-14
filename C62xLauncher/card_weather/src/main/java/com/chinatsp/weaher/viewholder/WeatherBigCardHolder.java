package com.chinatsp.weaher.viewholder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.weaher.R;
import com.chinatsp.weaher.WeatherTypeRes;
import com.chinatsp.weaher.WeatherUtil;
import com.chinatsp.weaher.repository.WeatherBean;
import com.chinatsp.weaher.viewholder.city.BigCityListAdapter;
import com.chinatsp.weaher.viewholder.city.SmallCityListAdapter;
import com.chinatsp.weaher.viewholder.indicator.PointIndicator;
import com.chinatsp.weaher.weekday.WeekDayAdapter;
import com.iflytek.autofly.weather.entity.WeatherInfo;

import java.util.List;

import launcher.base.recyclerview.SimpleRcvDecoration;
import launcher.base.utils.recent.RecentAppHelper;

public class WeatherBigCardHolder extends WeatherCardHolder{

    private RecyclerView rcvCityList;
    private BigCityListAdapter mCityListAdapter;
    private OnPageChangedListener mOnPageChangedListener;

    public void setOnPageChangedListener(OnPageChangedListener onPageChangedListener) {
        mOnPageChangedListener = onPageChangedListener;
    }

    private ViewGroup mLayoutIndicator;
    private PointIndicator mIndicator;

    public WeatherBigCardHolder(View rootView) {
        super(rootView);
        rcvCityList = rootView.findViewById(R.id.rcvCityList);
        mLayoutIndicator = rootView.findViewById(R.id.layoutIndicator);
        mIndicator = new PointIndicator(mLayoutIndicator);
        initCityRcv(rcvCityList);
    }


    private void initCityRcv(RecyclerView rcvCityList) {
        mCityListAdapter = new BigCityListAdapter(mRootView.getContext());
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
    }

    public void scrollToPosition(int pos) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                LinearLayoutManager layoutManager = (LinearLayoutManager) rcvCityList.getLayoutManager();
                if (layoutManager != null) {
                    layoutManager.scrollToPositionWithOffset(pos, 0);
                    mIndicator.select(pos);
                }
            }
        });

    }

    @Override
    public void updateDefault() {

    }

    @Override
    public void updateWeather(WeatherInfo weatherInfo) {

    }

    @Override
    public void updateCityList(List<String> cityList) {
        mIndicator.reset(cityList.size());
        showCityListRecyclerView();
        mCityListAdapter.setData(cityList);
        WeatherUtil.logI("WeatherBigCardHolder updateCityList "+ cityList);
    }

    private void showCityListRecyclerView() {

    }

    public void updateWeatherList(List<WeatherInfo> weatherInfoList) {

    }

}
