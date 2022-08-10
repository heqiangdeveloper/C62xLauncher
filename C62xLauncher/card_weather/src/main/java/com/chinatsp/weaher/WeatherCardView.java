package com.chinatsp.weaher;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.weaher.weekday.DayWeatherBean;
import com.chinatsp.weaher.weekday.WeekDayAdapter;

import java.util.LinkedList;
import java.util.List;

import card.service.ICardStyleChange;
import launcher.base.recyclerview.SimpleRcvDecoration;
import launcher.base.utils.view.LayoutParamUtil;


public class WeatherCardView extends ConstraintLayout implements ICardStyleChange, LifecycleOwner {

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

    private WeatherViewModel mWeatherViewModel;

    private int mSmallWidth;
    private int mLargeWidth;

    private RecyclerView mRcvCardWeatherWeek;
    private View mLargeCardView;
    private View mSmallCardView;
    private LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.card_weather, this);
        mSmallCardView = findViewById(R.id.layoutSmallCardView);
        mSmallWidth = (int) getResources().getDimension(R.dimen.card_width);
        mLargeWidth = (int) getResources().getDimension(R.dimen.card_width_large);
        mWeatherViewModel = WeatherViewModel.getInstance();
        ImageView ivCardWeatherRefresh = findViewById(R.id.ivCardWeatherRefresh);
        ivCardWeatherRefresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mWeatherViewModel.loadWeatherData();
            }
        });
    }

    Observer<WeatherBean> mWeatherBeanObserver = new Observer<WeatherBean>() {
        @Override
        public void onChanged(WeatherBean weatherBean) {
            refreshSmallCardUI(weatherBean);
        }
    };

    @Override
    public void expand() {
        if (mLargeCardView == null) {
            mLargeCardView = LayoutInflater.from(getContext()).inflate(R.layout.card_weather_large, this, false);
            initWeeklyWeather();
        }
        addView(mLargeCardView);
        mLargeCardView.setVisibility(VISIBLE);
        mSmallCardView.setVisibility(GONE);
        LayoutParamUtil.setWidth(mLargeWidth, this);
        runExpandAnim();
    }


    @Override
    public void collapse() {
        mSmallCardView.setVisibility(VISIBLE);
        mLargeCardView.setVisibility(GONE);
        removeView(mLargeCardView);
        LayoutParamUtil.setWidth(mSmallWidth, this);
    }

    @Override
    public boolean hideDefaultTitle() {
        return false;
    }

    private void initWeeklyWeather() {
        mRcvCardWeatherWeek = mLargeCardView.findViewById(R.id.rcvCardWeatherWeek);
        WeekDayAdapter weekDayAdapter = new WeekDayAdapter(getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext()) {
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
        weekDayAdapter.setDayWeatherList(createTest());
        mRcvCardWeatherWeek.setAdapter(weekDayAdapter);

    }

    private void runExpandAnim() {
        ObjectAnimator.ofFloat(mLargeCardView, "translationX", -500, 0).setDuration(150).start();
        ObjectAnimator.ofFloat(mLargeCardView, "alpha", 0.1f, 1.0f).setDuration(500).start();
    }

    private List<DayWeatherBean> createTest() {
        List<DayWeatherBean> testData = new LinkedList<>();
        testData.add(new DayWeatherBean("明天", 1, "多云", "13 ~ 22" + "℃"));
        testData.add(new DayWeatherBean("星期二", 1, "晴天", "21 ~ 31" + "℃"));
        testData.add(new DayWeatherBean("星期三", 1, "雷阵雨", "8 ~ 13" + "℃"));
        testData.add(new DayWeatherBean("星期四", 1, "多云", "16 ~ 25" + "℃"));
        testData.add(new DayWeatherBean("星期五", 1, "晴天", "12 ~ 22" + "℃"));
        testData.add(new DayWeatherBean("星期六", 1, "多云", "22 ~ 25" + "℃"));
        return testData;
    }

    private void refreshSmallCardUI(WeatherBean weatherBean) {
        if (weatherBean == null) {
            return;
        }
        TextView tvCardWeatherCity = findViewById(R.id.tvCardWeatherCity);
        tvCardWeatherCity.setText(weatherBean.getCity());
        TextView tvCardWeatherTemperature = findViewById(R.id.tvCardWeatherTemperature);
        tvCardWeatherTemperature.setText(WeatherUtil.fixTemperatureDesc(weatherBean.getTemperatureDesc(), getResources()));
        TextView tvCardWeatherDate = findViewById(R.id.tvCardWeatherDate);
        tvCardWeatherDate.setText(WeatherUtil.getToday());

        WeatherTypeRes weatherTypeRes = new WeatherTypeRes(weatherBean.getType());
        ImageView ivCardWeatherIcon = findViewById(R.id.ivCardWeatherIcon);
        ivCardWeatherIcon.setImageResource(weatherTypeRes.getIcon());
        ImageView ivWeatherBg = findViewById(R.id.ivWeatherBg);
        ivWeatherBg.setImageResource(weatherTypeRes.getSmallCardBg());
    }
    private void refreshBigCardUI(WeatherBean weatherBean) {
        if (weatherBean == null) {
            return;
        }
        TextView tvCardWeatherCity = findViewById(R.id.tvCardWeatherCity);
        tvCardWeatherCity.setText(weatherBean.getCity());
        TextView tvCardWeatherTemperature = findViewById(R.id.tvCardWeatherTemperature);
        tvCardWeatherTemperature.setText(WeatherUtil.fixTemperatureDesc(weatherBean.getTemperatureDesc(), getResources()));
        TextView tvCardWeatherDate = findViewById(R.id.tvCardWeatherDate);
        tvCardWeatherDate.setText(WeatherUtil.getToday());

        WeatherTypeRes weatherTypeRes = new WeatherTypeRes(weatherBean.getType());
        ImageView ivCardWeatherIcon = findViewById(R.id.ivCardWeatherIcon);
        ivCardWeatherIcon.setImageResource(weatherTypeRes.getIcon());
        ImageView ivWeatherBg = findViewById(R.id.ivWeatherBg);
        ivWeatherBg.setImageResource(weatherTypeRes.getSmallCardBg());
    }
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mLifecycleRegistry.setCurrentState(Lifecycle.State.CREATED);
        mWeatherViewModel.register(this, mWeatherBeanObserver);
        mWeatherViewModel.loadWeatherData();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mLifecycleRegistry.setCurrentState(Lifecycle.State.DESTROYED);
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
}
