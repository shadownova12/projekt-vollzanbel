package org.shadownova.vollzanbel.dto

data class CharacterSpellRequest(
    val spellIndex: String,
    val isLearned: Boolean = false,
    val isPrepared: Boolean = false
)