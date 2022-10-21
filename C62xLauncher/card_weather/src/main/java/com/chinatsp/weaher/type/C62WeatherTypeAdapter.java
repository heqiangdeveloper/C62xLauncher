package com.chinatsp.weaher.type;

import com.chinatsp.weaher.repository.WeatherBean;

import java.io.InputStreamReader;

public class C62WeatherTypeAdapter implements WeatherTypeAdapter {

    @Override
    public C62WeatherType adapter(MoJiWeatherType target) {
        if (isUnknown(target)) {
            return new C62WeatherType(WeatherBean.TYPE_UNKNOWN);
        } else if (isCloudy(target)) {
            return new C62WeatherType(WeatherBean.TYPE_CLOUDY);
        } else if (isFog(target)) {
            return new C62WeatherType(WeatherBean.TYPE_FOG);
        } else if (isRain(target)) {
            return new C62WeatherType(WeatherBean.TYPE_RAIN);
        } else if (isSmoke(target)) {
            return new C62WeatherType(WeatherBean.TYPE_SMOG);
        } else if (isSnow(target)) {
            return new C62WeatherType(WeatherBean.TYPE_SNOW);
        } else if (isSunny(target)) {
            return new C62WeatherType(WeatherBean.TYPE_SUNNY);
        } else if (isThunderShower(target)) {
            return new C62WeatherType(WeatherBean.TYPE_THUNDER_SHOWER);
        } else if (isWind(target)) {
            return new C62WeatherType(WeatherBean.TYPE_WINDY);
        }
        return new C62WeatherType(WeatherBean.TYPE_UNKNOWN);
    }

    @Override
    public boolean isCloudy(MoJiWeatherType target) {
        return equals(target, "多云", "冰雹") || contain(target, "转多云");
    }

    @Override
    public boolean isFog(MoJiWeatherType target) {
        return equals(target, "雾") || contain(target, "转雾");
    }

    @Override
    public boolean isOvercast(MoJiWeatherType target) {
        return equals(target, "阴") || contain(target,"转阴");
    }

    @Override
    public boolean isRain(MoJiWeatherType target) {
        return equals(target, "小雨", "中雨", "大雨", "暴雨")
                || contain(target, "转小雨", "转中雨", "转大雨", "转暴雨")
                || contain(target, "-小雨", "-中雨", "-大雨", "-暴雨")
                || contain(target, "到小雨", "到中雨", "到大雨", "到暴雨")
                ;
    }

    @Override
    public boolean isSmoke(MoJiWeatherType target) {
        return equals(target, "沙尘暴", "沙尘", "浮尘", "扬沙", "霾", "雾霾")
                || contain(target,"转霾", "转雾霾");
    }

    @Override
    public boolean isSnow(MoJiWeatherType target) {
        return equals(target, "小雪", "中雪", "大雪", "暴雪", "雨夹雪")
                || contain(target, "转小雪", "转中雪", "转大雪", "转暴雪", "转雨夹雪")
                || contain(target, "-小雪", "-中雪", "-大雪", "-暴雪" ,"-雨夹雪")
                || contain(target, "到小雪", "到中雪", "到大雪", "到暴雪" ,"到雨夹雪")
                ;
    }

    @Override
    public boolean isSunny(MoJiWeatherType target) {
        return equals(target, "晴") || contain(target,"转晴");
    }

    @Override
    public boolean isThunderShower(MoJiWeatherType target) {
        return equals(target, "雷阵雨", "阵雨", "雷阵雨伴有冰雹")
                || contain(target,"转雷阵雨", "转阵雨");
    }

    @Override
    public boolean isWind(MoJiWeatherType target) {
        return false;
    }

    @Override
    public boolean isUnknown(MoJiWeatherType target) {
        return target == null || target.getValue() == null;
    }

    private boolean equals(MoJiWeatherType target, String... args) {
        if (args == null || target == null || target.getValue() == null) {
            return false;
        }
        boolean match = false;
        for (String arg : args) {
            if (arg != null) {
                match = match || arg.equals(target.getValue());
            }
        }
        return match;
    }

    private boolean contain(MoJiWeatherType target, String... args) {
        if (args == null || target == null || target.getValue() == null) {
            return false;
        }
        boolean match = false;
        for (String arg : args) {
            if (arg != null) {
                match = match || target.getValue().contains(arg);
            }
        }
        return match;
    }
}
