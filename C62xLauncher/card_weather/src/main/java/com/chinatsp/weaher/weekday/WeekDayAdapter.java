package com.chinatsp.weaher.weekday;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.weaher.R;
import com.chinatsp.weaher.WeatherUtil;
import com.iflytek.autofly.weather.entity.WeatherInfo;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import launcher.base.utils.EasyLog;
import launcher.base.utils.property.PropertyUtils;

public class WeekDayAdapter extends RecyclerView.Adapter<WeekDayAdapter.ViewHolder> {

    private List<WeatherInfo> mDayWeatherList = new LinkedList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public WeekDayAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_weather_weekday, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(mDayWeatherList.get(position));
    }

    @Override
    public int getItemCount() {
        return mDayWeatherList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvItemWeatherWeekDay;
        private ImageView ivItemWeatherType;
        private TextView tvItemWeatherWord;
        private TextView tvItemWeatherTemperatureDesc;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemWeatherWeekDay = itemView.findViewById(R.id.tvItemWeatherWeekDay);
            ivItemWeatherType = itemView.findViewById(R.id.ivItemWeatherType);
            tvItemWeatherWord = itemView.findViewById(R.id.tvItemWeatherWord);
            tvItemWeatherTemperatureDesc = itemView.findViewById(R.id.tvItemWeatherTemperatureDesc);
        }

        public void bind(WeatherInfo dayWeatherBean) {
            if (dayWeatherBean == null) {
                return;
            }
            WeatherUtil.logD("bind WeatherInfo: "+dayWeatherBean);
            tvItemWeatherWeekDay.setText(getWeekDayRes(dayWeatherBean));
            tvItemWeatherWord.setText(dayWeatherBean.getWeather());
            tvItemWeatherTemperatureDesc.setText(WeatherUtil.getTemperatureRange(dayWeatherBean, mContext.getResources()));
        }
    }

    private String getWeekDayRes(WeatherInfo dayWeatherBean) {
        String dateStr = dayWeatherBean.getDate();
        if (TextUtils.isEmpty(dateStr)) {
            return "未知";
        }
        LocalDate localDate = LocalDate.parse(dateStr);
        DayOfWeek dayOfWeek = localDate.getDayOfWeek();
        int res= WeatherUtil.getWeekDayTextRes(dayOfWeek);
        return mContext.getString(res);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setDayWeatherList(List<WeatherInfo> dayWeatherList) {
        if (dayWeatherList == null) {
            return;
        }
        if (mDayWeatherList == dayWeatherList) {
            return;
        }
        mDayWeatherList = dayWeatherList;
        notifyDataSetChanged();
    }

}
