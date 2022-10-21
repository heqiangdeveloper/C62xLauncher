package com.chinatsp.weaher.type;

public interface WeatherTypeAdapter {
    C62WeatherType adapter(MoJiWeatherType target);

    boolean isCloudy(MoJiWeatherType target);

    boolean isFog(MoJiWeatherType target);

    boolean isOvercast(MoJiWeatherType target);

    boolean isRain(MoJiWeatherType target);

    boolean isSmoke(MoJiWeatherType target);

    boolean isSnow(MoJiWeatherType target);

    boolean isSunny(MoJiWeatherType target);

    boolean isThunderShower(MoJiWeatherType target);

    boolean isWind(MoJiWeatherType target);

    boolean isUnknown(MoJiWeatherType target);
}
