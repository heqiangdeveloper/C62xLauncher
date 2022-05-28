package com.chinatsp.settinglib.manager.cabin

import android.car.hardware.CarPropertyValue
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.sign.SignalOrigin
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/20 14:08
 * @desc   :
 * @version: 1.0
 */


class CabinManager private constructor(): BaseManager() {

//    private val version: AtomicInteger by lazy { AtomicInteger(0) }
//
//    private val selfSerial by lazy { System.identityHashCode(this) }
//
//    private val listenerStore by lazy { HashMap<Int, WeakReference<IBaseListener>>() }

    private var concernedSerialManagers: List<out BaseManager>? = null

    private val acManager: ACManager by lazy { ACManager.instance }

    private val seatManager: SeatManager by lazy { SeatManager.instance }

    val managers: List<out BaseManager> by lazy {
        ArrayList<BaseManager>().apply {
            add(acManager)
            add(seatManager)
        }
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

    override fun getConcernedSignal(signalOrigin: SignalOrigin): Set<Int>? {
        val hashSet = HashSet<Int>()
        managers.forEach {
            manager ->
            manager.getConcernedSignal(signalOrigin)?.let {
                hashSet.addAll(it)
            }
        }
        return hashSet
    }

    companion object: ISignal {

        override val TAG: String = CabinManager::class.java.simpleName

        val instance: CabinManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            CabinManager()
        }

        override val mcuConcernedSerial: Set<Int>by lazy {
            val hashSet = HashSet<Int>()
            hashSet.apply {
            }
        }

        override val cabinConcernedSerial: Set<Int> by lazy {
            val hashSet = HashSet<Int>()
            hashSet.apply {
                addAll(ACManager.cabinConcernedSerial)
                addAll(SeatManager.cabinConcernedSerial)
            }
        }
        override val hvacConcernedSerial: Set<Int> by lazy {
            val hashSet = HashSet<Int>()
            hashSet.apply {
                addAll(ACManager.hvacConcernedSerial)
                addAll(SeatManager.hvacConcernedSerial)
            }
        }
    }


}