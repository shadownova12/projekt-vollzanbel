package org.shadownova.vollzanbel.dto

/** Character-private reference for an option entered outside the shared catalog. */
data class CharacterCustomOption(
    val id: String,
    val category: String,
    val name: String,
    val source: String = "",
    val page: String = "",
    val url: String = "",
    val notes: String = "",
)
