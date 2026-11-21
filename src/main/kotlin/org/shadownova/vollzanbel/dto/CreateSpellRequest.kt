package org.shadownova.vollzanbel.dto

/** Payload for adding a spell to the shared catalog. Writes are admin-only. */
data class CreateSpellRequest(
    val index: String,
    val name: String,
    val level: Int,
    val school: String,
    val source: String,
    val description: String,
    val higherLevel: String = "",
    val range: String,
    val components: String,
    val material: String = "",
    val ritual: Boolean = false,
    val duration: String,
    val concentration: Boolean = false,
    val castingTime: String,
    val url: String,
)
