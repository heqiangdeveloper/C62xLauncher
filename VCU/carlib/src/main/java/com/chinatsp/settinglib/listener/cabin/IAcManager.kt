package com.chinatsp.settinglib.listener.cabin

import com.chinatsp.settinglib.listener.IManager

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/23 16:12
 * @desc   :
 * @version: 1.0
 */
interface IAcManager: IManager {

    fun obtainAutoAridStatus():Boolean

    fun obtainAutoWindStatus():Boolean

    fun obtainAutoDemistStatus():Boolean

    fun obtainAutoComfortOption():Int

}