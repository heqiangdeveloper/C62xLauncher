package com.chinatsp.apppanel.event;

import com.anarchy.classifyview.Bean.LocationBean;
import com.anarchy.classifyview.event.Event;

public class UpdateEvent extends Event {
    private LocationBean locationBean;

    public UpdateEvent(LocationBean locationBean) {
        this.locationBean = locationBean;
    }

    public LocationBean getLocationBean() {
        return locationBean;
    }

    public void setLocationBean(LocationBean locationBean) {
        this.locationBean = locationBean;
    }
}
