package com.chinatsp.weaher;

import com.chinatsp.weaher.repository.WeatherBean;

public class WeatherTypeRes {

    private int icon;
    private int drawerBg;
    private int smallCardBg;
    private int bigCardBg;
    private int videoSmallBg;
    private int videoBigBg;


    public int getVideoSmallBg() {
        return videoSmallBg;
    }

    public void setVideoSmallBg(int videoSmallBg) {
        this.videoSmallBg = videoSmallBg;
    }

    public int getVideoBigBg() {
        return videoBigBg;
    }

    public void setVideoBigBg(int videoBigBg) {
        this.videoBigBg = videoBigBg;
    }

    public WeatherTypeRes(int icon, int drawerBg, int smallCardBg, int bigCardBg) {
        this.icon = icon;
        this.drawerBg = drawerBg;
        this.smallCardBg = smallCardBg;
        this.bigCardBg = bigCardBg;
    }

    public int getIcon() {
        return icon;
    }

    public int getDrawerBg() {
        return drawerBg;
    }

    public int getSmallCardBg() {
        return smallCardBg;
    }

    public int getBigCardBg() {
        return bigCardBg;
    }




    public WeatherTypeRes(int type) {
        switch (type) {
            case WeatherBean.TYPE_UNKNOWN:
                icon = R.drawable.weather_icon_unkown;
                drawerBg = R.drawable.drawer_weather_bg_wind;
                smallCardBg = R.drawable.card_weather_bg_wind;
                bigCardBg = R.drawable.card_weather_bg_wind_large;
                break;
            case WeatherBean.TYPE_SUNNY:
                icon = R.drawable.weather_icon_sunny;
                drawerBg = R.drawable.drawer_weather_bg_sunny;
                smallCardBg = R.drawable.card_weather_bg_sunny;
                bigCardBg = R.drawable.card_weather_bg_sunny_large;
                videoSmallBg = R.raw.video_sunny_small;
                videoBigBg = R.raw.video_sunny_big;
                break;
            case WeatherBean.TYPE_CLOUDY:
                icon = R.drawable.weather_icon_cloudy;
                drawerBg = R.drawable.drawer_weather_bg_cloudy;
                smallCardBg = R.drawable.card_weather_bg_cloudy;
                bigCardBg = R.drawable.card_weather_bg_cloudy_large;
                videoSmallBg = R.raw.video_cloudy_small;
                videoBigBg = R.raw.video_cloudy_big;
                break;
            case WeatherBean.TYPE_RAIN:
                icon = R.drawable.weather_icon_rain;
                drawerBg = R.drawable.drawer_weather_bg_rain;
                smallCardBg = R.drawable.card_weather_bg_rain;
                bigCardBg = R.drawable.card_weather_bg_rain_large;
                videoSmallBg = R.raw.video_rain_small;
                videoBigBg = R.raw.video_rain_big;
                break;
            case WeatherBean.TYPE_FOG:
                icon = R.drawable.weather_icon_fog;
                drawerBg = R.drawable.drawer_weather_bg_fog;
                smallCardBg = R.drawable.card_weather_bg_fog;
                bigCardBg = R.drawable.card_weather_bg_fog_large;
                videoSmallBg = R.raw.video_fog_small;
                videoBigBg = R.raw.video_fog_big;
                break;
            case WeatherBean.TYPE_SNOW:
                icon = R.drawable.weather_icon_snow;
                drawerBg = R.drawable.drawer_weather_bg_snow;
                smallCardBg = R.drawable.card_weather_bg_snow;
                bigCardBg = R.drawable.card_weather_bg_snow_large;
                videoSmallBg = R.raw.video_snow_small;
                videoBigBg = R.raw.video_snow_big;
                break;
            case WeatherBean.TYPE_THUNDER_SHOWER:
                icon = R.drawable.weather_icon_thunder_shower;
                drawerBg = R.drawable.drawer_weather_bg_thunder_shower;
                smallCardBg = R.drawable.card_weather_bg_thunder_shower;
                bigCardBg = R.drawable.card_weather_bg_thunder_shower_large;
                videoSmallBg = R.raw.video_thunder_shower_small;
                videoBigBg = R.raw.video_thunder_shower_big;
                break;
            case WeatherBean.TYPE_SMOG:
                icon = R.drawable.weather_icon_smog;
                drawerBg = R.drawable.drawer_weather_bg_smoke;
                smallCardBg = R.drawable.card_weather_bg_smoke;
                bigCardBg = R.drawable.card_weather_bg_smoke_large;
                videoSmallBg = R.raw.video_smog_small;
                videoBigBg = R.raw.video_smog_big;
                break;
            case WeatherBean.TYPE_OVERCAST:
                icon = R.drawable.weather_icon_overcast;
                drawerBg = R.drawable.drawer_weather_bg_overcast;
                smallCardBg = R.drawable.card_weather_bg_overcast;
                bigCardBg = R.drawable.card_weather_bg_overcast_large;
                videoSmallBg = R.raw.video_overcast_small;
                videoBigBg = R.raw.video_overcast_big;
                break;
            case WeatherBean.TYPE_WINDY:
                icon = R.drawable.weather_icon_windy;
                drawerBg = R.drawable.drawer_weather_bg_wind;
                smallCardBg = R.drawable.card_weather_bg_wind;
                bigCardBg = R.drawable.card_weather_bg_wind_large;
                videoSmallBg = R.raw.video_windy_small;
                videoBigBg = R.raw.video_windy_big;
                break;
        }
    }

}
