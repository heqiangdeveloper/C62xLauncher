package com.chinatsp.settinglib.manager.adas

import android.car.VehicleAreaSeat
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.Origin

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/6/2 11:34
 * @desc   :
 * @version: 1.0
 */
class RoadSignManager : BaseManager(), ISwitchManager {

    companion object : ISignal {

        override val TAG: String = RoadSignManager::class.java.simpleName

        val instance: RoadSignManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            RoadSignManager()
        }

    }

    override val careSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val cabinSet = HashSet<Int>().apply {
            }
            put(Origin.CABIN, cabinSet)
        }
    }


    override fun doGetSwitchOption(node: SwitchNode): SwitchState? {
        return null
    }

    override fun doSetSwitchOption(node: SwitchNode, status: Boolean): Boolean {
        return when (node) {
            SwitchNode.ADAS_TSR -> {
                writeProperty(
                    node.set.signal,
                    node.value(status),
                    node.set.origin,
                    VehicleAreaSeat.SEAT_DRIVER
                )
            }
            else -> false
        }
    }


}