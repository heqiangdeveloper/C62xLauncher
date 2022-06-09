package com.chinatsp.settinglib.manager

import android.car.hardware.CarPropertyValue
import com.chinatsp.settinglib.manager.access.AccessManager
import com.chinatsp.settinglib.manager.cabin.CabinManager
import com.chinatsp.settinglib.manager.lamp.LampManager
import com.chinatsp.settinglib.manager.sound.AudioManager
import com.chinatsp.settinglib.sign.SignalOrigin

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/28 18:19
 * @desc   :
 * @version: 1.0
 */
class GlobalManager private constructor() : BaseManager() {

    companion object {
        val TAG: String = GlobalManager::class.java.simpleName

        val instance: GlobalManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            GlobalManager()
        }
    }

    private var concernedSerialManagers: List<BaseManager>? = null


    val managers: List<BaseManager> by lazy {
        ArrayList<BaseManager>().apply {
            add(CabinManager.instance)
            add(AudioManager.instance)
            add(LampManager.instance)
            add(AccessManager.instance)
        }
    }
    override val concernedSerials: Map<SignalOrigin, Set<Int>>
        get() = HashMap()

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
        val hashSet = HashSet<Int>()
        managers.forEach { manager ->
            manager.getConcernedSignal(signalOrigin).let {
                if (it.isNotEmpty()) {
                    hashSet.addAll(it)
                }
            }
        }
        return hashSet
    }
}