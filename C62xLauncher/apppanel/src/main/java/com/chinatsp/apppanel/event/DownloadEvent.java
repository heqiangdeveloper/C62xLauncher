package com.chinatsp.apppanel.event;

import com.anarchy.classifyview.Bean.LocationBean;
import com.anarchy.classifyview.event.Event;
import com.huawei.appmarket.launcheragent.StoreAppInfo;

public class DownloadEvent extends Event {
    private LocationBean locationBean;

    public DownloadEvent(LocationBean locationBean) {
        this.locationBean = locationBean;
    }

    public LocationBean getLocationBean() {
        return locationBean;
    }

    public void setLocationBean(LocationBean locationBean) {
        this.locationBean = locationBean;
    }
}
