package com.chinatsp.vehicle.settings.bean

import com.chinatsp.settinglib.BaseApp
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.fragment.CommonlyFragment
import com.chinatsp.vehicle.settings.fragment.cabin.CabinManagerFragment
import com.chinatsp.vehicle.settings.fragment.doors.DoorsManageFragment
import com.chinatsp.vehicle.settings.fragment.drive.DriveManageFragment
import com.chinatsp.vehicle.settings.fragment.lighting.LightingManageFragment
import com.chinatsp.vehicle.settings.fragment.sound.SoundManageFragment

enum class TabPage(val uid: Int, val desc: String, val className: String) {

    COMMONLY(0,
        BaseApp.instance.resources.getString(R.string.table0),
        CommonlyFragment::class.java.name),
    ACCESS(1,
        BaseApp.instance.resources.getString(R.string.table1),
        DoorsManageFragment::class.java.name),
    LIGHTING(2,
        BaseApp.instance.resources.getString(R.string.table2),
        LightingManageFragment::class.java.name),
    SOUND(3,
        BaseApp.instance.resources.getString(R.string.table3),
        SoundManageFragment::class.java.name),
    COCKPIT(4,
        BaseApp.instance.resources.getString(R.string.table5),
        CabinManagerFragment::class.java.name),
    ADAS(5,
        BaseApp.instance.resources.getString(R.string.table4),
        DriveManageFragment::class.java.name);
//    UPGRADE(6, BaseApp.instance.resources.getString(R.string.table6), SystemFragment::class.java.name);

    companion object {

        @JvmStatic
        fun getPage(position: Int): TabPage {
            return values()[position]
        }

        @JvmStatic
        fun size(): Int {
            return values().size
        }

        @JvmStatic
        fun getPageNames(): Array<String?> {
            val pages = values()
            val pageNames = arrayOfNulls<String>(pages.size)
            for (i in pages.indices) {
                pageNames[i] = pages[i].name
            }
            return pageNames
        }

    }


}