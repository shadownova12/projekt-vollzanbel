package org.shadownova.vollzanbel.dto

data class NewPlayerCharacter(
    val userId: Long,
    val name: String,
    val avatarId: String = "spark_violet",
    val characterSheet: Any
)
