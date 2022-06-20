package com.anarchy.classifyview.event;

public class AppInstallStatusEvent extends Event {
    private int status;//0卸载 1安装
    private String packageName;

    public AppInstallStatusEvent(int status, String packageName) {
        this.status = status;
        this.packageName = packageName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
