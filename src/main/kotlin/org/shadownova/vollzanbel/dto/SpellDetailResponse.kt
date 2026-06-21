package org.shadownova.vollzanbel.dto

import com.fasterxml.jackson.annotation.JsonAlias
import org.shadownova.vollzanbel.repository.Spell
import java.time.Instant

data class SpellDetailResponse(
    val index: String,
    val name: String,
    val desc: List<String>?,

    @JsonAlias("higher_level")
    val higherLevel: List<String>?,

    val range: String?,
    val components: List<String>?,
    val material: String?,
    val ritual: Boolean?,
    val duration: String?,
    val concentration: Boolean?,

    @JsonAlias("casting_time")
    val castingTime: String?,

    val level: Int?,
    val school: School?,
    val url: String?,

    @JsonAlias("updated_at")
    val updatedAt: String?
)

data class School(
    val index: String?,
    val name: String?,
    val url: String?
)

// Mapper to convert API response to the JPA entity used by the app.
fun SpellDetailResponse.toEntity(): Spell {
    return Spell(
        index = this.index,
        name = this.name,
        level = this.level ?: 0,
        school = this.school?.name ?: "",
        description = this.desc?.joinToString("\n\n") ?: "",
        higherLevel = this.higherLevel?.joinToString("\n\n") ?: "",
        range = this.range ?: "",
        components = this.components?.joinToString(",") ?: "",
        material = this.material ?: "",
        ritual = this.ritual ?: false,
        duration = this.duration ?: "",
        concentration = this.concentration ?: false,
        castingTime = this.castingTime ?: "",
        url = this.url ?: "",
        updatedAt = this.updatedAt?.let { Instant.parse(it) } ?: Instant.now()
    )
}

