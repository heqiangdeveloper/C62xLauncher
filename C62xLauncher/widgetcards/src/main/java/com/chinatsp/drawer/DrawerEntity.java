package com.chinatsp.drawer;

import launcher.base.recyclerview.BaseEntity;

public class DrawerEntity extends BaseEntity {
    public static final int TYPE_SEARCH = 0;
    public static final int TYPE_APPS_AND_WEATHER = 1;
    public static final int TYPE_IQUTING = 2;
    public static final int TYPE_TOUTIAO = 3;
    public static final int TYPE_DRIVE_COUNSELOR = 4;
    public DrawerEntity(int viewType, int itemLayoutId) {
        super(viewType, itemLayoutId);
    }
}
