package com.huawei.appmarket.launcheragent;

import android.os.Parcel;
import android.os.Parcelable;

public class StoreAppInfo implements Parcelable {

    public static final Parcelable.Creator<StoreAppInfo> CREATOR = new Creator<StoreAppInfo>() {
        @Override
        public StoreAppInfo createFromParcel(Parcel in) {
            return new StoreAppInfo(in);
        }

        @Override
        public StoreAppInfo[] newArray(int size) {
            return new StoreAppInfo[size];
        }
    };

    /**
     * App 名称
     */
    private String appName;

    /**
     * App 类型 {@link com.huawei.appmarket.launcheragent.launcher.AppType}
     */
    private int appType;

    /**
     * App 包命
     */
    private String pkgName;

    /**
     * App 版本
     */
    private int versionCode;

    /**
     * App 状态 {@link com.huawei.appmarket.launcheragent.launcher.AppState}
     */
    private int appState;

    /**
     * App 下载进度 取值范范围 0～100
     */
    private int downloadProgress;

    /**
     * App 更新类型 {@link com.huawei.appmarket.launcheragent.launcher.UpdateType}
     */
    private int updateType;

    public StoreAppInfo() {
    }

    protected StoreAppInfo(Parcel in) {
        appName = in.readString();
        pkgName = in.readString();
        versionCode = in.readInt();
        appType = in.readInt();
        appState = in.readInt();
        updateType = in.readInt();
        downloadProgress = in.readInt();
    }

    /**
     * 获取App 名称
     *
     * @return 获取应用名称
     */
    public String getAppName() {
        return appName;
    }

    /**
     * 设置App 名称
     *
     * @param appName app 名称
     */
    public void setAppName(String appName) {
        this.appName = appName;
    }

    /**
     * 获取 App 类型
     *
     * @return 获取app类型
     */
    public int getAppType() {
        return appType;
    }

    /**
     * 设置 App 类型
     *
     * @param appType {@link com.huawei.appmarket.launcheragent.launcher.AppType}
     */
    public void setAppType(int appType) {
        this.appType = appType;
    }

    /**
     * 获取 App 包名
     *
     * @return 获取包名
     */
    public String getPkgName() {
        return pkgName;
    }

    /**
     * 设置App 包名
     *
     * @param pkgName app包名
     */
    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    /**
     * 获取 App 版本号
     *
     * @return 获取应用版本号
     */
    public int getVersionCode() {
        return versionCode;
    }

    /**
     * 设置App 版本号
     *
     * @param versionCode 版本号
     */
    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    /**
     * 获取App 状态
     *
     * @return 获取app状态
     */
    public int getAppState() {
        return appState;
    }

    /**
     * 设置App 状态
     *
     * @param appState {@link com.huawei.appmarket.launcheragent.launcher.AppState}
     */
    public void setAppState(int appState) {
        this.appState = appState;
    }

    /**
     * 获取下载进度
     *
     * @return 获取下载进度
     */
    public int getDownloadProgress() {
        return downloadProgress;
    }

    /**
     * 设置下载进度
     *
     * @param downloadProgress 进度
     */
    public void setDownloadProgress(int downloadProgress) {
        this.downloadProgress = downloadProgress;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(appName);
        dest.writeString(pkgName);
        dest.writeInt(versionCode);
        dest.writeInt(appType);
        dest.writeInt(appState);
        dest.writeInt(updateType);
        dest.writeInt(downloadProgress);
    }

    /**
     * 获取更新类型
     *
     * @return 获取更新类型
     */
    public int getUpdateType() {
        return this.updateType;
    }

    /**
     * 设置更新类新
     *
     * @param updateType 更新类型 {@link com.huawei.appmarket.launcheragent.launcher.UpdateType}
     */
    public void setUpdateType(int updateType) {
        this.updateType = updateType;
    }
}
