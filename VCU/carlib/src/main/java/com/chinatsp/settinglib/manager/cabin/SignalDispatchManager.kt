package com.chinatsp.settinglib.manager.cabin

import android.car.hardware.CarPropertyValue
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.sign.SignalOrigin
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/20 14:08
 * @desc   :
 * @version: 1.0
 */


class SignalDispatchManager private constructor() : BaseManager() {

    companion object {
        val TAG: String = SignalDispatchManager::class.java.simpleName

        val instance: SignalDispatchManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            SignalDispatchManager()
        }
    }

//    private val identity by lazy { System.identityHashCode(this) }

    private val version: AtomicInteger by lazy { AtomicInteger(0) }

    private var concernedSerialManagers: List<BaseManager>? = null

    private val cabinManager: CabinManager by lazy { CabinManager.instance }

    private val seatManager: SeatManager by lazy { SeatManager.instance }

    val managers: List<BaseManager> by lazy {
        listOf(cabinManager, seatManager)
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
            manager.getConcernedSignal(signalOrigin)?.let {
                hashSet.addAll(it)
            }
        }
        return hashSet
    }


}