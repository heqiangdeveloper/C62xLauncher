package com.huawei.appmarket.launcheragent.launcher;

/**
 * 通知launcher 更新类型
 */
public interface UpdateType {
    /**
     * 强制更新,如果有新版本，则在更新后才能使用
     */
    int FORCE = 1;

    /**
     * 非强制更新,如果有新版本，不必更新后才能使用
     */
    int OPTION = 2;
}
