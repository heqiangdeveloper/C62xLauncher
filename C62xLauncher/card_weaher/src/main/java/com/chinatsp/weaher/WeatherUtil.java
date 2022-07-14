package com.chinatsp.weaher;

import android.content.res.Resources;
import android.text.TextUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class WeatherUtil {
    public static String getToday() {
        LocalDate date = LocalDate.now();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy.MM.dd  EE");
        return df.format(date);
    }

    public static String fixTemperatureDesc(String origin, Resources resources) {
        if (TextUtils.isEmpty(origin)) {
            return origin;
        }
        return origin + "  " + resources.getString(R.string.symbol_celsius);
    }
}
