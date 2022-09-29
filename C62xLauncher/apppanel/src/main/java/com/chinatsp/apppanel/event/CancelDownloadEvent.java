package com.chinatsp.apppanel.event;

import com.anarchy.classifyview.event.Event;

public class CancelDownloadEvent extends Event {
    private String packageName;

    public CancelDownloadEvent(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
