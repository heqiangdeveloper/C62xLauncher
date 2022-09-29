package com.huawei.appmarket.launcheragent.launcher;

/**
 * 通知launcher 错误类型
 */
public interface ErrorCodeLauncher {
    /**
     * 下载异常中断。
     */
    int DOWNLOAD_ERROR = 1001;

    /**
     * 安装失败。
     */
    int INSTALL_FAIL = 2001;

    /**
     * 未安装错误码
     */
    int APPSTORE_ERROR_CODE_UNINSTALL = 2002;

    /**
     * 更新应用时安装错误码
     */
    int APPSTORE_ERROR_CODE_INSTALLING = 2003;

    /**
     * 更新应用时卸载错误码
     */
    int APPSTORE_ERROR_CODE_UNINSTALLING = 2004;

    /**
     * 未同意协议错误码
     */
    int APPSTORE_ERROR_CODE_PROTOCOL = 3001;

    /**
     * 更新接口无数据返回或者异常错误码
     */
    int APPSTORE_ERROR_CODE_STORE = 4001;

    /**
     * 网络异常
     */
    int APPSTORE_ERROR_CODE_NETWORK = 5001;

    /**
     * 统一性错误，包括null对象啥的
     */
    int APPSTORE_ERROR_CODE_NORMAL = 10001;

}
