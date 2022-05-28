package com.chinatsp.settinglib.manager;

import android.car.media.CarAudioManager;
import android.media.AudioAttributes;

import com.chinatsp.settinglib.LogManager;

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date : 2022/5/27 15:39
 * @desc :
 * @version: 1.0
 */
public class SoundManager {

    private static final String TAG = SoundManager.class.getSimpleName();

    private static volatile SoundManager instance;

    private CarAudioManager mCarAudioManager;

    private final int STREAM_CRUISE = AudioAttributes.USAGE_NOTIFICATION_EVENT;//10
    private final int STREAM_SYSTEM = AudioAttributes.USAGE_ASSISTANT;//16
    private final int STREAM_MEDIA = AudioAttributes.USAGE_MEDIA;//1
    private final int STREAM_PHONE = AudioAttributes.USAGE_VOICE_COMMUNICATION;//2
    private final int STREAM_NAVI = AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE;//12

    public static SoundManager getInstance() {
        if (null == instance) {
            synchronized (SoundManager.class) {
                if (null == instance) {
                    instance = new SoundManager();
                }
            }
        }
        return instance;
    }

    private SoundManager() {

    }

    public void initAudioManager(CarAudioManager audioManager) {
        this.mCarAudioManager = audioManager;
    }

    private int getStreamMaxVolume(int type) {
        int result = -1;
        try {
            result = mCarAudioManager.getGroupMaxVolume(mCarAudioManager.getVolumeGroupIdForUsage(type));
            LogManager.Companion.d(TAG, "getStreamMaxVolume type:" + type + ", result:" + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public int getSystemMaxVolume() {
        return getStreamMaxVolume(STREAM_SYSTEM);
    }

    public int getMediaMaxVolume() {
        return getStreamMaxVolume(STREAM_MEDIA);
    }

    public int getPhoneMaxVolume() {
        return getStreamMaxVolume(STREAM_PHONE);
    }

    public int getNaviMaxVolume() {
        return getStreamMaxVolume(STREAM_NAVI);
    }

    public int getCruiseMaxVolume() {
        return getStreamMaxVolume(STREAM_CRUISE);
    }

    private void setStreamVolume(int type, int volume) {
        try {
            LogManager.Companion.d(TAG, "setStreamVolume B type " + type + " volume " + volume);
            mCarAudioManager.setGroupVolume(mCarAudioManager.getVolumeGroupIdForUsage(type), volume, 0);
            LogManager.Companion.d(TAG, "setStreamVolume E type " + type + " volume " + volume);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMediaVolume(int volume) {
        setStreamVolume(STREAM_MEDIA, volume);
    }

    public void setPhoneVolume(int volume) {
        setStreamVolume(STREAM_PHONE, volume);
    }

    public void setCruiseVolume(int volume) {
        setStreamVolume(STREAM_CRUISE, volume);
    }

    public void setNaviVolume(int volume) {
        setStreamVolume(STREAM_NAVI, volume);
    }

    public void setSystemVolume(int volume) {
        setStreamVolume(STREAM_SYSTEM, volume);
    }

    private int getStreamVolume(int type) {
        try {
            return mCarAudioManager.getGroupVolume(mCarAudioManager.getVolumeGroupIdForUsage(type));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getMediaVolume() {
        return getStreamVolume(STREAM_MEDIA);
    }

    public int getSystemVolume() {
        return getStreamVolume(STREAM_SYSTEM);
    }


    public int getPhoneVolume() {
        return getStreamVolume(STREAM_PHONE);
    }

    public int getNaviVolume() {
        return getStreamVolume(STREAM_NAVI);
    }

    public int getCruiseVolume() {
        return getStreamVolume(STREAM_CRUISE);
    }

}
