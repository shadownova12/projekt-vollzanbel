package org.shadownova.vollzanbel.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class CharacterSpellRequest(
    val spellIndex: String,
    @param:JsonProperty("isLearned") @get:JsonProperty("isLearned") val isLearned: Boolean = false,
    @param:JsonProperty("isPrepared") @get:JsonProperty("isPrepared") val isPrepared: Boolean = false
)
