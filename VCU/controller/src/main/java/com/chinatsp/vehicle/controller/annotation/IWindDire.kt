package com.chinatsp.vehicle.controller.annotation

import androidx.annotation.IntDef
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/10/10 13:23
 * @desc   :
 * @version: 1.0
 */
@IntDef(
    IWindDire.HEAD,
    IWindDire.BODY,
    IWindDire.FOOT
)
@Retention(RetentionPolicy.SOURCE)
annotation class IWindDire {
    companion object {
        const val HEAD = 0x1 shl 0
        const val BODY = 0x1 shl 1
        const val FOOT = 0x1 shl 2
    }

}