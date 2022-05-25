package com.chinatsp.settinglib.listener


interface IBaseListener {

    fun isNeedUpdate(version: Int): Boolean
}