package com.chinatsp.vehicle.settings.bean

import com.chinatsp.vehicle.settings.App
import com.chinatsp.vehicle.settings.R


enum class TabPage(val position: Int, val description: String) {

    STOCK(0, App.instance.resources.getString(R.string.table0)),
    DOOR_WINDOW(1, App.instance.resources.getString(R.string.table1)),
    LIGHT_EFFECT(2, App.instance.resources.getString(R.string.table2)),
    SOUND_EFFECT(3, App.instance.resources.getString(R.string.table3)),
    COCKPIT(4, App.instance.resources.getString(R.string.table5)),
    DRIVE_HELP(5, App.instance.resources.getString(R.string.table4)),
    SYS_OS(6, App.instance.resources.getString(R.string.table6));

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