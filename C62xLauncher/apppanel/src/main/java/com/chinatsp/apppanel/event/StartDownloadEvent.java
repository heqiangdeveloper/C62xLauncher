package com.chinatsp.apppanel.event;

import android.graphics.drawable.Drawable;

import com.anarchy.classifyview.Bean.LocationBean;
import com.anarchy.classifyview.event.Event;

import java.util.Locale;

//开始下载事件
public class StartDownloadEvent extends Event {
    private String pkgName;
    private byte[] icon;
    private LocationBean locationBean;

    public StartDownloadEvent(String pkgName, byte[] icon) {
        this.pkgName = pkgName;
        this.icon = icon;
    }

    public StartDownloadEvent(LocationBean locationBean) {
        this.locationBean = locationBean;
    }

    public LocationBean getLocationBean() {
        return locationBean;
    }

    public void setLocationBean(LocationBean locationBean) {
        this.locationBean = locationBean;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public byte[] getIcon() {
        return icon;
    }

    public void setIcon(byte[] icon) {
        this.icon = icon;
    }
}
