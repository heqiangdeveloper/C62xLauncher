package com.chinatsp.settinglib.manager.cabin

import android.car.hardware.CarPropertyValue
import com.chinatsp.settinglib.ITabStore
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.sign.Origin
import com.chinatsp.vehicle.controller.ICmdCallback
import com.chinatsp.vehicle.controller.annotation.Model
import com.chinatsp.vehicle.controller.bean.CarCmd
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/20 14:08
 * @desc   :
 * @version: 1.0
 */


class CabinManager private constructor() : BaseManager(), ITabStore {

    private var followers: List<BaseManager>? = null

    override val tabSerial: AtomicInteger by lazy {
        AtomicInteger(-1)
    }

    override fun onDispatchSignal(property: CarPropertyValue<*>, origin: Origin): Boolean {
        managers.forEach {
            it.onDispatchSignal(property, origin)
        }
        return true
    }

    override fun isCareSignal(signal: Int, origin: Origin): Boolean {
        val list = managers.filter { it.isCareSignal(signal, origin) }.toList()
        followers = list
        return list.isNotEmpty()
    }

    override fun getOriginSignal(origin: Origin): Set<Int> {
        return careSerials[origin] ?: HashSet()
    }

//    fun onTrailerRemindChanged(onOff: Int, level: Int, dist: Int) {
//        OtherManager.instance.onTrailerRemindChanged(onOff, level, dist)
//    }

    companion object : ISignal {

        override val TAG: String = CabinManager::class.java.simpleName

        val instance: CabinManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            CabinManager()
        }

        val managers: List<BaseManager> by lazy {
            ArrayList<BaseManager>().apply {
                add(WheelManager.instance)
                add(SeatManager.instance)
                add(ACManager.instance)
                add(SafeManager.instance)
                add(MeterManager.instance)
                add(OtherManager.instance)
            }
        }

    }

    override val careSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val keySet = managers.flatMap {
                it.careSerials.keys
            }.toSet()
            keySet.forEach { key ->
                val hashSet = HashSet<Int>()
                managers.forEach { manager ->
                    hashSet.addAll(manager.getOriginSignal(key))
                }
                put(key, hashSet)
            }
        }
    }

    override fun doCarControlCommand(cmd: CarCmd, callback: ICmdCallback?) {
        if (Model.CABIN_WHEEL == cmd.model) {
            WheelManager.instance.doCarControlCommand(cmd, callback)
        }
    }


}