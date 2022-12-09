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

    val user: String = ""
    val presetUser: String = ""

    fun convert(): VoiceModel {
        val nlpVoiceModel = VoiceModel()
        nlpVoiceModel.service = service ?: "default"
        nlpVoiceModel.operation = operation
        nlpVoiceModel.text = text
        if (semantic?.slots != null) {
            nlpVoiceModel.slots = semantic.slots
        } else {
            nlpVoiceModel.slots = Slots(UUID.randomUUID().toString())
        }
        nlpVoiceModel.slots.text = text ?: ""
        nlpVoiceModel.slots.operation = operation ?: ""
        nlpVoiceModel.response = this.toString()
        nlpVoiceModel.slots.user = user

        nlpVoiceModel.slots.presetUser = presetUser
        return nlpVoiceModel
    }
}