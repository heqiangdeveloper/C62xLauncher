package com.chinatsp.settinglib.manager

import com.chinatsp.settinglib.listener.IManager
import com.chinatsp.settinglib.optios.Progress

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/6/7 10:26
 * @desc   :
 * @version: 1.0
 */
interface IProgressManager : IManager {

    /**
     * @param   node 进度类型
     * @return 返回当前类型进度值
     */
    fun doGetProgress(node: Progress): Int

    /**
     * @param   node 进度类型
     * @param   value 当前拖动条的进度值
     * @return  返回接口调用是否成功
     */
    fun doSetProgress(node: Progress, value: Int): Boolean

}