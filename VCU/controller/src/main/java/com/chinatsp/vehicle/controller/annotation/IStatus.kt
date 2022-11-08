package com.chinatsp.vehicle.controller.annotation

import androidx.annotation.IntDef

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date : 2022/7/18 15:17
 * @desc : 命令状态
 * @version: 1.0
 */
@IntDef(
    IStatus.INIT,
    IStatus.PREPARED,
    IStatus.RUNNING,
    IStatus.SUCCESS,
    IStatus.FAILED
)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class IStatus {
    companion object {
        const val INIT = 1
        const val PREPARED = 2
        const val RUNNING = 3
        const val SUCCESS = 4
        const val FAILED = 5
    }
}