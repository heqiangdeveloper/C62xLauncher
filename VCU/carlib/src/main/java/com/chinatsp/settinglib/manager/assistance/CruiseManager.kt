package com.chinatsp.settinglib.manager.assistance

import android.car.hardware.CarPropertyValue
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.IOptionManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.SignalOrigin

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/6/2 11:34
 * @desc   :
 * @version: 1.0
 */
class CruiseManager: BaseManager(), IOptionManager {

    companion object: ISignal {

        override val TAG: String = CruiseManager::class.java.simpleName

        val instance: CruiseManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            CruiseManager()
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
        TODO("Not yet implemented")
    }

    override fun getConcernedSignal(signalOrigin: SignalOrigin): Set<Int> {
        TODO("Not yet implemented")
    }

    override fun doGetRadioOption(radioNode: RadioNode): Int {
        TODO("Not yet implemented")
    }

    override fun doSetRadioOption(radioNode: RadioNode, value: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun unRegisterVcuListener(serial: Int, callSerial: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun onRegisterVcuListener(priority: Int, listener: IBaseListener): Int {
        TODO("Not yet implemented")
    }

    override fun doGetSwitchOption(switchNode: SwitchNode): Boolean {
        TODO("Not yet implemented")
    }

    override fun doSetSwitchOption(switchNode: SwitchNode, status: Boolean): Boolean {
        TODO("Not yet implemented")
    }
}