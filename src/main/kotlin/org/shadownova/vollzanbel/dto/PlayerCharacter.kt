package org.shadownova.vollzanbel.dto

data class PlayerCharacter(
    val id: Long,
    val userId: Long,
    val name: String,
    val avatarId: String = "spark_violet",
//    val data: ByteArray
    val characterSheet: Any
)
