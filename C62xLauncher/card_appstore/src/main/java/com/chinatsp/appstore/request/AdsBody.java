package com.chinatsp.appstore.request;

public class AdsBody {
    public String requestId;
    public String apiVersion;
    public String appSign;
    public String pkgName;
    public AdSlot adSlot;
    public DeviceInfo deviceInfo;
    public MediaInfo mediaInfo;
    public NetworkInfo networkInfo;

    public AdsBody(String requestId, String apiVersion, String appSign, String pkgName, AdSlot adSlot, DeviceInfo deviceInfo, MediaInfo mediaInfo, NetworkInfo networkInfo) {
        this.requestId = requestId;
        this.apiVersion = apiVersion;
        this.appSign = appSign;
        this.pkgName = pkgName;
        this.adSlot = adSlot;
        this.deviceInfo = deviceInfo;
        this.mediaInfo = mediaInfo;
        this.networkInfo = networkInfo;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getAppSign() {
        return appSign;
    }

    public void setAppSign(String appSign) {
        this.appSign = appSign;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public AdSlot getAdSlot() {
        return adSlot;
    }

    public void setAdSlot(AdSlot adSlot) {
        this.adSlot = adSlot;
    }

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public MediaInfo getMediaInfo() {
        return mediaInfo;
    }

    public void setMediaInfo(MediaInfo mediaInfo) {
        this.mediaInfo = mediaInfo;
    }

    public NetworkInfo getNetworkInfo() {
        return networkInfo;
    }

    public void setNetworkInfo(NetworkInfo networkInfo) {
        this.networkInfo = networkInfo;
    }

    public static class AdSlot{
        public int adCount;
        public String sceneId;
        public String slotId;

        public AdSlot(int adCount, String sceneId, String slotId) {
            this.adCount = adCount;
            this.sceneId = sceneId;
            this.slotId = slotId;
        }
    }

    public static class DeviceInfo{
        public String androidApiLevel;
        public String country;
        public String deviceModel;
        public String locale;
        public String os;
        public String deviceId;
        public String deviceType;

        public DeviceInfo(String androidApiLevel, String country, String deviceModel, String locale, String os, String deviceId, String deviceType) {
            this.androidApiLevel = androidApiLevel;
            this.country = country;
            this.deviceModel = deviceModel;
            this.locale = locale;
            this.os = os;
            this.deviceId = deviceId;
            this.deviceType = deviceType;
        }
    }

    public static class MediaInfo{
        public String mediaPkgName;
        public String mediaVersion;

        public MediaInfo(String mediaPkgName, String mediaVersion) {
            this.mediaPkgName = mediaPkgName;
            this.mediaVersion = mediaVersion;
        }
    }

    public static class NetworkInfo{
        public String carrier;
        public String connectType;

        public NetworkInfo(String carrier, String connectType) {
            this.carrier = carrier;
            this.connectType = connectType;
        }
    }
}
