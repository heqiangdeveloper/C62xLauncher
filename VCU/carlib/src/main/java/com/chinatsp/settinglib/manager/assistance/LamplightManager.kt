package com.chinatsp.settinglib.manager.assistance

import android.car.VehicleAreaSeat
import android.car.hardware.CarPropertyValue
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.SignalOrigin

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/6/7 10:50
 * @desc   :
 * @version: 1.0
 */
class LamplightManager: BaseManager(), ISwitchManager {

    companion object: ISignal {

        override val TAG: String = RoadSignManager::class.java.simpleName

        val instance: RoadSignManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            RoadSignManager()
        }

    }

    override val concernedSerials: Map<SignalOrigin, Set<Int>> by lazy {
        HashMap<SignalOrigin, Set<Int>>().apply {
            val cabinSet = HashSet<Int> ().apply {
            }
            put(SignalOrigin.CABIN_SIGNAL, cabinSet)
        }
    }
    override fun onHandleConcernedSignal(
        property: CarPropertyValue<*>,
        signalOrigin: SignalOrigin
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun isConcernedSignal(signal: Int, signalOrigin: SignalOrigin): Boolean {
        val signals = getConcernedSignal(signalOrigin)
        return signals.contains(signal)
    }

    override fun getConcernedSignal(signalOrigin: SignalOrigin): Set<Int> {
        return concernedSerials[signalOrigin] ?: HashSet()
    }

    override fun doGetSwitchOption(switchNode: SwitchNode): Boolean {
        return false
    }

    override fun doSetSwitchOption(switchNode: SwitchNode, status: Boolean): Boolean {
        return when (switchNode) {
            SwitchNode.ADAS_HMA -> {
                doSetProperty(switchNode.signal, switchNode.obtainValue(status), switchNode.origin, VehicleAreaSeat.SEAT_DRIVER)
            }
            else -> false
        }
    }

    override fun unRegisterVcuListener(serial: Int, callSerial: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun onRegisterVcuListener(priority: Int, listener: IBaseListener): Int {
        TODO("Not yet implemented")
    }

}