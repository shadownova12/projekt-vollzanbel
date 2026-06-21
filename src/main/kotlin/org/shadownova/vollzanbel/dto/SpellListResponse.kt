package org.shadownova.vollzanbel.dto

data class SpellListResponse(
    val count: Int,
    val results: List<SpellReference>
)

data class SpellReference(
    val index: String,
    val name: String,
    val level: Int,
    val url: String
)