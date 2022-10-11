package com.chinatsp.settinglib.listener


interface IBaseListener {

//    fun isNeedUpdate(version: Int): Boolean

    fun doNonstopValue(signal: Int, value: Int) {

    }

    fun isCareSignal(signal: Int): Boolean {
        return false
    }
}