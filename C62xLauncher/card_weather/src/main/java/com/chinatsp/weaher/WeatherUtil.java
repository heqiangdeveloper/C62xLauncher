package com.chinatsp.weaher;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.VideoView;

import com.chinatsp.weaher.repository.WeatherBean;
import com.chinatsp.weaher.type.C62WeatherType;
import com.chinatsp.weaher.type.C62WeatherTypeAdapter;
import com.chinatsp.weaher.type.MoJiWeatherType;
import com.chinatsp.weaher.type.WeatherTypeAdapter;
import com.iflytek.autofly.weather.entity.WeatherInfo;

import java.lang.reflect.Constructor;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import launcher.base.utils.EasyLog;
import launcher.base.utils.recent.RecentAppHelper;

public class WeatherUtil {
    public static final String TAG = "WeatherUtil";

    public static String getToday() {
        LocalDate date = LocalDate.now();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy/MM/dd  EE");
        return df.format(date);
    }

    public static String fixTemperatureDesc(String origin, Resources resources) {
        if (TextUtils.isEmpty(origin)) {
            return origin;
        }
        return origin + "  " + resources.getString(R.string.symbol_celsius);
    }

    public static int getWeekDayTextRes(DayOfWeek dayOfWeek) {
        int[] res = {R.string.weather_monday, R.string.weather_tuesday, R.string.weather_wednesday, R.string.weather_thursday,
                R.string.weather_friday, R.string.weather_saturday, R.string.weather_sunday};
        int index = 0;
        switch (dayOfWeek) {
            case MONDAY:
                index = 0;
                break;
            case TUESDAY:
                index = 1;
                break;
            case WEDNESDAY:
                index = 2;
                break;
            case THURSDAY:
                index = 3;
                break;
            case FRIDAY:
                index = 4;
                break;
            case SATURDAY:
                index = 5;
                break;
            case SUNDAY:
                index = 6;
                break;
        }
        return res[index];
    }

    public static void logD(String message) {
        EasyLog.d(TAG, message);
    }

    public static void logI(String message) {
        EasyLog.i(TAG, message);
    }

    public static void logE(String message) {
        EasyLog.e(TAG, message);
    }

    public static void logV(String message) {
        EasyLog.v(TAG, message);
    }

    public static void logW(String message) {
        EasyLog.w(TAG, message);
    }

    public static String parseTemperature(String temperatureNumStr, Resources resources) {
        try {
            int temperature = (int) Float.parseFloat(temperatureNumStr);
            return temperature + resources.getString(R.string.symbol_celsius);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resources.getString(R.string.weather_desc_unknown);
    }
    public static String getTemperatureRange(WeatherInfo dayWeatherBean, Resources resources) {
        String low = WeatherUtil.parseTemperature(dayWeatherBean.getLow(), resources);
        String high = WeatherUtil.parseTemperature(dayWeatherBean.getHigh(), resources);
        return low + "~" + high;
    }

    public static WeatherTypeRes parseType(String weather) {
        WeatherTypeAdapter weatherTypeAdapter = new C62WeatherTypeAdapter();
        MoJiWeatherType moJiWeatherType = new MoJiWeatherType(weather);
        C62WeatherType c62WeatherType = weatherTypeAdapter.adapter(moJiWeatherType);
        int c62WeatherTypeValue = c62WeatherType.getValue();
        WeatherTypeRes weatherTypeRes = new WeatherTypeRes(c62WeatherTypeValue);
        return weatherTypeRes;
    }

    public static void goApp(Context context) {
        RecentAppHelper.launchApp(context, "com.iflytek.autofly.weather");

    }

    public static void setWeatherDesc(TextView textView, String weather) {
        if (textView == null) {
            return;
        }
        int weatherDescriptionResId = new WeatherDescTranslator().getWeatherDescription(weather);
        if (weatherDescriptionResId > 0) {
            textView.setText(weatherDescriptionResId);
        } else {
            if (weather == null || weather.isEmpty()) {
                textView.setText(R.string.weather_desc_unknown);
            } else {
                textView.setText(weather);

            }
        }
    }

    public static void setDataSource(VideoView videoView, int rawId) {
        if (videoView == null) {
            return;
        }
        if (rawId <= 0) {
            return;
        }

        String packageName = videoView.getContext().getPackageName();
        String uri = "android.resource://" + packageName + "/" + rawId;
        Uri parse = Uri.parse(uri);
        videoView.setVideoURI(parse);
    }

    public static String convertNull(String origin) {
        if (origin == null) {
            return "";
        }
        return origin;
    }

    public static String convertNull(String origin,String placeHolder) {
        if (origin == null) {
            return placeHolder;
        }
        return origin;
    }

    public static void startCardEditor(Context context) {
        Intent intent = new Intent();
        String commandAction = "com.chinatsp.launcher.cardCommandService";
        String opKey = "OP_KEY";
        intent.setPackage("com.chinatsp.launcher");
        intent.setAction(commandAction);
        // 1: ????????????????????????
        // ??????????????????, ????????? CardIntentService
        intent.putExtra(opKey, 1);
        context.startService(intent);
    }
}
