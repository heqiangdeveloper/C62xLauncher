package com.chinatsp.settinglib.manager.lamp

import android.car.hardware.CarPropertyValue
import com.chinatsp.settinglib.ITabStore
import com.chinatsp.settinglib.LogManager
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.manager.cabin.CabinManager
import com.chinatsp.settinglib.sign.Origin
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/20 14:08
 * @desc   :
 * @version: 1.0
 */


class LampManager private constructor() : BaseManager(), ITabStore {

    private var followers: List<out BaseManager>? = null

    override val tabSerial: AtomicInteger by lazy {
        AtomicInteger(-1)
    }

    override fun onDispatchSignal(property: CarPropertyValue<*>, origin: Origin): Boolean {
        managers.forEach {
            LogManager.d(CabinManager.TAG, "LampManager onDispatchSignal ${it::class.java.simpleName}")
            it.onDispatchSignal(property, origin)
        }
        return true
    }

    override fun onHandleSignal(property: CarPropertyValue<*>, origin: Origin): Boolean {
//        followers?.forEach {
//            it.onDispatchSignal(property, origin)
//        }
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

    companion object : ISignal {

        override val TAG: String = LampManager::class.java.simpleName

        val instance: LampManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            LampManager()
        }

        val managers: List<out BaseManager> by lazy {
            ArrayList<BaseManager>().apply {
                add(LightManager.instance)
                add(AmbientLightingManager.instance)
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


}