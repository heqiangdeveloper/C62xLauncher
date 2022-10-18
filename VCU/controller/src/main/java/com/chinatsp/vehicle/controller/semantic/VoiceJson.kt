package com.chinatsp.vehicle.controller.semantic

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
    fun convert(): NlpVoiceModel? {
        if (semantic?.slots != null) {
            val nlpVoiceModel = NlpVoiceModel()
            nlpVoiceModel.service = service
            nlpVoiceModel.operation = operation
            nlpVoiceModel.slots = semantic.slots
            nlpVoiceModel.slots.text = text ?: ""
            nlpVoiceModel.slots.operation = operation ?: ""
            nlpVoiceModel.text = text
            nlpVoiceModel.response = this.toString()
            return nlpVoiceModel
        }
        return null
    }
}