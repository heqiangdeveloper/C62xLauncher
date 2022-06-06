package com.chinatsp.settinglib.listener.access

import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.optios.SwitchNode

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/31 17:53
 * @desc   :
 * @version: 1.0
 */
interface IDoorListener : IBaseListener{

    fun onDriveAutoLockOptionChanged(value: Int)

    fun onShutDownAutoUnlockOptionChanged(value: Int)

    fun onSwitchChanged(switchNode: SwitchNode, status: Boolean)
}