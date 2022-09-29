package com.huawei.appmarket.launcheragent.launcher;

/**
 * 通知launcher 更新类型
 */
public interface AppState {
    /**
     * 正在下载
     */
    int DOWNLOADING = 1;

    /**
     * 下载暂停
     */
    int DOWNLOAD_PAUSED = 2;

    /**
     * 可取消下载 （下载中或下载暂停）
     */
    int COULD_BE_CANCELED = 3;

    /**
     * 已安装
     */
    int INSTALLED = 4;

    /**
     * 有更新
     */
    int COULD_UPDATE = 5;

    /**
     * 下载失败
     */
    int DOWNLOAD_FAIL = 60;

    /**
     * 安装中
     */
    int INSTALLING = 70;
}
