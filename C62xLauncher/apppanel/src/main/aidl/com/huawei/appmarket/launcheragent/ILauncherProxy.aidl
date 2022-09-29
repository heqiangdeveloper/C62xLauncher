// ILauncherProxy.aidl

package com.huawei.appmarket.launcheragent;
import com.huawei.appmarket.launcheragent.StoreAppInfo;
import java.util.List;

//应用商城下载状态通知launcher
interface ILauncherProxy {
    /**
     * 批量通知launcher下载状态
     *
     * @param infos 更新应用集合
     * */
    void notifyAppInfos(in List<StoreAppInfo> infos);

    /**
     * 单应用通知launcher下载状态 安装状态，根据下载进度和 和应用状态判断应用下载完成到安装完成，
     *
     * @param info 更新应用信息
     * */
    void notifyAppInfo(in StoreAppInfo info);

    /**
     * 单应用通知launcher下载状态
     *
     * @param pkgName 更新应用包名
     * @param icon 更新应用图标
     * */
    void notifyAppIcon(String pkgName, in byte[] icon);

    /**
     * 单应用通知launcher下载状态
     *
     * @param pkgName 更新应用包名
     * @param appVersionCode 版本号
     * @param updateType 更新类型
     * */
    void notifyAppUpdate(String pkgName, int appVersionCode, int updateType);

    /**
     * 单应用通知launcher下载状态
     *
     * @param infos 待更新应用集合
     * */
    void notifyAppsUpdate(in List<StoreAppInfo> infos);

    /**
     * 单应用通知launcher下载状态
     *
     * @param pkgName 应用包名
     * @param errorCode 错误吗
     * */
    void notifyAppError(String pkgName, int errorCode);
}