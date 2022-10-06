package com.chinatsp.settinglib.service

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/9/21 18:57
 * @desc   :
 * @version: 1.0
 */
data class Web(
    val intent: Intent,
)

data class Intent(
    val bislocalresult: String,
    val `data`: Data,
    val demand_semantic: DemandSemantic,
    val nlocalconfidencescore: String,
    val normal_text: String,
    val operation: String,
    val rc: Int,
    val score: String,
    val search_semantic: Any,
    val semantic: Semantic,
    val service: String,
    val text: String,
    val version: String,
)

data class Data(
    val result: Any,
)

data class DemandSemantic(
    val name: String,
    val operation: String,
    val service: String,
)

data class Semantic(
    val slots: Slots,
)

data class Slots(
    val name: String,
)
