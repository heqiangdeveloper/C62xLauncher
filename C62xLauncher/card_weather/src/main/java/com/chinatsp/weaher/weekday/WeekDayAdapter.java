package com.chinatsp.weaher.weekday;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.weaher.R;
import com.iflytek.autofly.weather.entity.WeatherInfo;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
            tvItemWeatherWeekDay.setText(getWeekDay(dayWeatherBean));
            tvItemWeatherWord.setText(dayWeatherBean.getWeather());
            String tempRange = dayWeatherBean.getTempRange();
            if (TextUtils.isEmpty(tempRange)) {
                tempRange = dayWeatherBean.getTemp();
            }
            tvItemWeatherTemperatureDesc.setText(tempRange);
        }
    }

    private String getWeekDay(WeatherInfo dayWeatherBean) {
        String dateStr = dayWeatherBean.getDate();
        LocalDate localDate = LocalDate.parse(dateStr);
        DayOfWeek dayOfWeek = localDate.getDayOfWeek();
        return dayOfWeek.name();
    }

    public void setDayWeatherList(List<WeatherInfo> dayWeatherList) {
        if (dayWeatherList == null) {
            return;
        }
        mDayWeatherList = dayWeatherList;
    }
}
