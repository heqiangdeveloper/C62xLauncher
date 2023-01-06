package com.chinatsp.vehicle.controller.semantic

import java.util.*

data class VoiceJson(
    val answer: Answer?,
    val category: String?,
    val cid: String?,
    val getanswer_time: Double?,
    val jsDebug: JsDebug?,
    val moreResults: List<Any>?,
    val normal_text: String?,
    val operation: String?,
    val rc: Int?,
    val score: Any?,
    val searchSemantic: SearchSemantic?,
    val search_semantic: SearchSemantic?,
    val semantic: Semantic?,
    val semantic_info: SemanticInfo?,
    val service: String?,
    val sid: String?,
    val text: String?,
    val uuid: String?,
    val version: String?,
) {

    val user: String? = ""
    val presetUser: String? = ""

    fun convert(): VoiceModel {
        val voiceModel = VoiceModel()
        voiceModel.service = service ?: "default"
        voiceModel.operation = operation ?: ""
        voiceModel.text = text ?: ""
        voiceModel.slots = semantic?.slots ?: Slots(UUID.randomUUID().toString())
        voiceModel.slots.text = text ?: ""
        voiceModel.slots.operation = operation ?: ""
        voiceModel.response = this.toString()
        voiceModel.slots.user = user ?: ""
        voiceModel.slots.presetUser = presetUser ?: ""
        return voiceModel
    }
}