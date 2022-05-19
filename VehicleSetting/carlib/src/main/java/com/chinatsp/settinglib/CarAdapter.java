package com.chinatsp.settinglib;

import android.car.hardware.constant.IcDisplay;
import android.car.hardware.constant.MCU;

public class CarAdapter {

    public static class Constants {
        public static final int Language_ZH = 0;
        public static final int Language_EN = 1;

        public static final int BEEP_VOLUME_LEVEL_CLOSE = 0;
        public static final int BEEP_VOLUME_LEVEL_LOW = 1;
        public static final int BEEP_VOLUME_LEVEL_MIDDLE = 2;
        public static final int BEEP_VOLUME_LEVEL_HIGH = 3;

        public static final int YB_THEME1 = IcDisplay.THEME_1;
        public static final int YB_THEME2 = IcDisplay.THEME_2;
        public static final int YB_THEME3 = IcDisplay.THEME_3;

        public static final int THEME_NIGHT = 0;
        public static final int THEME_DAY = 1;
        public static final int THEME_AUTO = 2;

        public static final int THEME_NIGHT_MCU = MCU.NIGHT_MODE_NIGHT;
        public static final int THEME_DAY_MCU = MCU.NIGHT_MODE_DAYTIME;
        public static final int THEME_AUTO_MCU = MCU.NIGHT_MODE_AUTO;
    }

    public void onCarServiceBound(boolean isBound) {
    }

    public void onShowModeChange(int showModeStatus) {
    }

    public void onLittleLightStatusChange(boolean isNight) {
    }

    public void onAccStatusChange(boolean isAccOn) {
    }

    public void onPowerStatusChange(int powerStatus) {
    }

    public void onBrightnessChange(int brightness) {
    }

    public void onReverseStatusChange(boolean isReverseOn) {
    }

    public void onAvmStatusChange(boolean isAvmOn) {
    }

    public void onApaStatusChange(boolean isApaOn) {
    }

    public void onCanidLostStatusChange(boolean isCanLost) {
    }

    public void onCameraSwitchChange(boolean isOn) {
    }

    public void onPhotoReqChange(boolean isOn) {
    }

    public void onBatteryLevChange(int battery) {
    }

    public void onChargingStatusChange(boolean isCharging) {
    }

}
