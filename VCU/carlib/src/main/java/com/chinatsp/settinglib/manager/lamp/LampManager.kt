package com.chinatsp.settinglib.manager.lamp

import android.car.hardware.CarPropertyValue
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.sign.SignalOrigin

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/20 14:08
 * @desc   :
 * @version: 1.0
 */


class LampManager private constructor() : BaseManager() {

    private var concernedSerialManagers: List<out BaseManager>? = null

    private val lightManager: LightManager by lazy { LightManager.instance }


    override fun onHandleConcernedSignal(
        property: CarPropertyValue<*>,
        signalOrigin: SignalOrigin
    ): Boolean {
        concernedSerialManagers?.forEach {
            it.onDispatchSignal(property.propertyId, property, signalOrigin)
        }
        return true
    }

    override fun isConcernedSignal(signal: Int, signalOrigin: SignalOrigin): Boolean {
        val list = managers.filter { it.isConcernedSignal(signal, signalOrigin) }.toList()
        concernedSerialManagers = list
        return list.isNotEmpty()
    }

    override fun getConcernedSignal(signalOrigin: SignalOrigin): Set<Int> {
        return concernedSerials[signalOrigin] ?: HashSet()
    }

    companion object : ISignal {

        override val TAG: String = LampManager::class.java.simpleName

        val instance: LampManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            LampManager()
        }

        val managers: List<out BaseManager> by lazy {
            ArrayList<BaseManager>().apply {
                add(LightManager.instance)
            }
        }

    }

    override val concernedSerials: Map<SignalOrigin, Set<Int>> by lazy {
        HashMap<SignalOrigin, Set<Int>>().apply {
            val keySet = managers.flatMap {
                it.concernedSerials.keys
            }.toSet()
            keySet.forEach { key ->
                val hashSet = HashSet<Int>()
                managers.forEach { manager ->
                    hashSet.addAll(manager.getConcernedSignal(key))
                }
                put(key, hashSet)
            }
        }
    }


}