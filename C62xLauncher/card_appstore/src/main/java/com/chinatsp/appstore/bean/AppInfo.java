package com.chinatsp.appstore.bean;

public class AppInfo {
    private String appId;
    private String appName;
    private String description;
    private String icon;
    private String pkgName;
    private String packageName;

    public AppInfo(String appId, String appName, String description, String icon, String pkgName) {
        this.appId = appId;
        this.appName = appName;
        this.description = description;
        this.icon = icon;
        this.pkgName = pkgName;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }


    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getPackageName() {
        if (pkgName != null) {
            return pkgName;
        }
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
