package com.chinatsp.navigation.repository;

import com.chinatsp.navigation.gaode.bean.Address;
import com.chinatsp.navigation.gaode.bean.GaoDeResponse;
import com.chinatsp.navigation.gaode.bean.GuideInfo;
import com.chinatsp.navigation.gaode.bean.MapStatus;
import com.chinatsp.navigation.gaode.bean.NavigationStatus;
import com.chinatsp.navigation.gaode.bean.RoadInfo;

public interface INaviCallback {
    void receiveMyLocation(GaoDeResponse<Address> gaoDeResponse);
    void receiveNavigationStatus(GaoDeResponse<NavigationStatus> gaoDeResponse);
    void receiveCurRoadInfo(GaoDeResponse<RoadInfo> gaoDeResponse);
    void receiveNaviGuideInfo(GaoDeResponse<GuideInfo> gaoDeResponse);

    void receiveMapStatus(GaoDeResponse<MapStatus> gaoDeResponse);
}
