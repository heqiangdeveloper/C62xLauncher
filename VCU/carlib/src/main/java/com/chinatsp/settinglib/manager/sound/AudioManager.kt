package com.chinatsp.settinglib.manager.sound

import android.car.hardware.CarPropertyValue
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.cabin.CabinManager
import com.chinatsp.settinglib.sign.SignalOrigin

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/20 14:08
 * @desc   :
 * @version: 1.0
 */


class AudioManager private constructor() : BaseManager() {

    companion object {
        val TAG: String = AudioManager::class.java.simpleName

        val instance: AudioManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            AudioManager()
        }

        val managers: List<out BaseManager> by lazy {
            ArrayList<BaseManager>().apply {
                add(VoiceManager.instance)
            }
        }
    }

    private var concernedSerialManagers: List<BaseManager>? = null

    val managers: List<BaseManager> by lazy {
        listOf(VoiceManager.instance)
    }

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