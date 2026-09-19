package org.shadownova.vollzanbel.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class EquipmentItem(
    val index: String,
    val name: String,
    val equipmentCategory: String,
    val gearCategory: String? = null,
    val cost: ItemCost,
    val weight: Double,
    val desc: List<String> = emptyList(),
    val special: List<String> = emptyList(),
    val contents: List<String> = emptyList(),
    val properties: List<String> = emptyList(),
    val damage: String = "",
    val quantity: Int = 1,
    @param:JsonProperty("isLocked") @get:JsonProperty("isLocked") val isLocked: Boolean = true,
    @param:JsonProperty("isCustom") @get:JsonProperty("isCustom") val isCustom: Boolean = false,
    val equipped: Boolean = false,
    val attuned: Boolean = false,
    val charges: TrackedResource? = null,
)

data class ItemCost(
    val quantity: Int,
    val unit: String,
)

data class Currency(
    val cp: Int = 0,
    val sp: Int = 0,
    val ep: Int = 0,
    val gp: Int = 0,
    val pp: Int = 0,
)
