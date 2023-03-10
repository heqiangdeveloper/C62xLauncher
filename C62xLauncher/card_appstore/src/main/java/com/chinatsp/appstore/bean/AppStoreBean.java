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

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public int getRtnCode() {
        return rtnCode;
    }

    public void setRtnCode(int rtnCode) {
        this.rtnCode = rtnCode;
    }

    public String getRtnDesc() {
        return rtnDesc;
    }

    public void setRtnDesc(String rtnDesc) {
        this.rtnDesc = rtnDesc;
    }
}
