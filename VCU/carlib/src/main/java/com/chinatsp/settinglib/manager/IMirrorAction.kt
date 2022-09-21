package com.chinatsp.settinglib.manager

import androidx.annotation.IntDef
import com.chinatsp.settinglib.Constant

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/9/20 17:38
 * @desc   :
 * @version: 1.0
 */
@IntDef(Constant.ANGLE_SAVE, Constant.ANGLE_ADJUST, Constant.DEFAULT)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class IMirrorAction()
