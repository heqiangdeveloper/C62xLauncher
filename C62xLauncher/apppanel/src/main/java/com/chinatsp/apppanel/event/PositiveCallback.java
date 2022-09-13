package com.chinatsp.apppanel.event;

import com.anarchy.classifyview.Bean.LocationBean;

import java.util.List;

public interface PositiveCallback {
    void onConfirm(int parentIndex,List<LocationBean> selectLists);
}
