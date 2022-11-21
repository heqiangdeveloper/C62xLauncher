package com.chinatsp.settinglib.listener


interface IBaseListener {

    fun doNonstopValue(signal: Int, value: Int) {
    }

    fun isCareSignal(signal: Int): Boolean {
        return false
    }
}