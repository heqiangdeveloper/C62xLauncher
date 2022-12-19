package com.chinatsp.vehicle.settings.bean

import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.fragment.CommonlyFragment
import com.chinatsp.vehicle.settings.fragment.cabin.CabinManagerFragment
import com.chinatsp.vehicle.settings.fragment.doors.DoorsManageFragment
import com.chinatsp.vehicle.settings.fragment.adas.DriveManageFragment
import com.chinatsp.vehicle.settings.fragment.lighting.LightingManageFragment
import com.chinatsp.vehicle.settings.fragment.sound.SoundManageFragment

enum class TabPage(val uid: Int, val desc: Int, val className: String) {

    COMMONLY(0,
        R.string.table0,
        //BaseApp.instance.resources.getString(R.string.table0),
        CommonlyFragment::class.java.name),
    ACCESS(1,
        R.string.table1,
        //BaseApp.instance.resources.getString(R.string.table1),
        DoorsManageFragment::class.java.name),
    LIGHTING(2,
        R.string.table2,
        //BaseApp.instance.resources.getString(R.string.table2),
        LightingManageFragment::class.java.name),
    SOUND(3,
        R.string.table3,
        //BaseApp.instance.resources.getString(R.string.table3),
        SoundManageFragment::class.java.name),
    COCKPIT(4,
        R.string.table5,
        //BaseApp.instance.resources.getString(R.string.table5),
        CabinManagerFragment::class.java.name),
    ADAS(5,
        R.string.table4,
        //BaseApp.instance.resources.getString(R.string.table4),
        DriveManageFragment::class.java.name);
//    UPGRADE(6, BaseApp.instance.resources.getString(R.string.table6), SystemFragment::class.java.name);

}