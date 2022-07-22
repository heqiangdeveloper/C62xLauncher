package com.chinatsp.vehicle.controller.annotation

import androidx.annotation.IntDef
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date : 2022/7/18 15:17
 * @desc :
 * @version: 1.0
 */
@IntDef(IStatus.ERROR, IStatus.INIT, IStatus.START, IStatus.RUNNING, IStatus.SUCCESS, IStatus.FAILED)
@Retention(RetentionPolicy.SOURCE)
annotation class IStatus {
    companion object {
        const val ERROR: Int = -1
        const val INIT: Int = 1
        const val START: Int = 2
        const val RUNNING: Int = 3
        const val SUCCESS: Int = 4
        const val FAILED: Int = 5
    }
}