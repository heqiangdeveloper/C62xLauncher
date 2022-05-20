package com.chinatsp.weaher.weekday;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.weaher.R;

import java.util.LinkedList;
import java.util.List;

public class WeekDayAdapter extends RecyclerView.Adapter<WeekDayAdapter.ViewHolder> {

    private List<DayWeatherBean> mDayWeatherList = new LinkedList<>();
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

        public void bind(DayWeatherBean dayWeatherBean) {
            tvItemWeatherWeekDay.setText(dayWeatherBean.getDayOfWeek());
            tvItemWeatherWord.setText(dayWeatherBean.getWord());
            tvItemWeatherTemperatureDesc.setText(dayWeatherBean.getTemperatureDesc());
        }
    }
    public void setDayWeatherList(List<DayWeatherBean> dayWeatherList) {
        if (dayWeatherList == null) {
            return;
        }
        mDayWeatherList = dayWeatherList;
    }
}
