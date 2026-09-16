package org.shadownova.vollzanbel.dto

data class Spell(
    val index: String,
    val name: String,
    val desc: List<String>,
    val higherLevel: List<String> = emptyList(),
    val classes: List<String> = emptyList(),
    val subclasses: List<String> = emptyList(),
    val range: String,
    val components: List<String>,
    val ritual: Boolean = false,
    val duration: String,
    val concentration: Boolean = false,
    val castingTime: String,
    val level: Int,
    val school: String,
    val isLearned: Boolean = false,
    val isActive: Boolean = false,
    val isLocked: Boolean = true,
)

data class SpellSlotInfo(val max: Int = 0, val used: Int = 0)

data class SpellcastingInfo(
    val ability: String = "",
    val focus: String = "",
    val materialComponents: String = "",
)
