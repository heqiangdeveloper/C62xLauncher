package com.chinatsp.apppanel.event;

import com.chinatsp.apppanel.bean.LocationBean;

import java.util.List;

public interface PositiveCallback {
    void onConfirm(int parentIndex,List<LocationBean> selectLists);
}
