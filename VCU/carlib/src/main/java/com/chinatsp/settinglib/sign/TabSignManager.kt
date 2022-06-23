package com.chinatsp.settinglib.sign

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/26 14:01
 * @desc   :
 * @version: 1.0
 */
class TabSignManager private constructor() {

    val tabSignMap:MutableMap<TabBlock.Type, TabBlock> by lazy {
        HashMap<TabBlock.Type, TabBlock>().also {
            TabBlock.Type.values().forEach { type ->
                it[type] = TabBlock(type)
            }
        }
    }

    companion object {
        val instance: TabSignManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            TabSignManager()
        }
    }
}