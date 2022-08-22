package com.chinatsp.appstore.bean;

import java.util.List;

public class AppStoreBean {
    private List<MaterialBean> adInfos;
    private String requestId;
    private int rtnCode;
    private String rtnDesc;

    public List<MaterialBean> getAdInfos() {
        return adInfos;
    }

    public void setAdInfos(List<MaterialBean> adInfos) {
        this.adInfos = adInfos;
    }
}
