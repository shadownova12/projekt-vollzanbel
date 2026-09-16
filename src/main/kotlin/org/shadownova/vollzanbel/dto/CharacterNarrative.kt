package org.shadownova.vollzanbel.dto

data class CharacterProfile(
    val appearance: String = "",
    val mannerisms: String = "",
    val backstory: String = "",
)

data class JournalEntry(val id: Int, val title: String, val date: String = "", val body: String = "")
