package org.shadownova.vollzanbel.dto

import com.fasterxml.jackson.annotation.JsonAlias
import org.shadownova.vollzanbel.repository.Race
import java.time.Instant

data class RaceListResponse(val count: Int, val results: List<RaceReference>)
data class RaceReference(val index: String, val name: String, val url: String)

data class RaceDetailResponse(
    val index: String,
    val name: String,
    val speed: Int?,
    @JsonAlias("ability_bonuses") val abilityBonuses: List<AbilityBonus>?,
    val age: String?,
    val alignment: String?,
    val size: String?,
    @JsonAlias("size_description") val sizeDescription: String?,
    val languages: List<NamedReference>?,
    @JsonAlias("language_desc") val languageDescription: String?,
    val traits: List<NamedReference>?,
    val subraces: List<NamedReference>?,
    val url: String?,
    @JsonAlias("updated_at") val updatedAt: String?,
)

data class AbilityBonus(
    @JsonAlias("ability_score") val abilityScore: NamedReference?,
    val bonus: Int?,
)

data class NamedReference(val index: String?, val name: String?, val url: String?)

fun RaceDetailResponse.toEntity() = Race(
    index = index,
    name = name,
    speed = speed ?: 0,
    abilityBonuses = abilityBonuses.orEmpty()
        .mapNotNull { bonus -> bonus.abilityScore?.name?.let { "$it +${bonus.bonus ?: 0}" } }
        .joinToString(", "),
    age = age ?: "",
    alignment = alignment ?: "",
    size = size ?: "",
    sizeDescription = sizeDescription ?: "",
    languages = languages.orEmpty().mapNotNull { it.name }.joinToString(", "),
    languageDescription = languageDescription ?: "",
    traits = traits.orEmpty().mapNotNull { it.name }.joinToString(", "),
    subraces = subraces.orEmpty().mapNotNull { it.name }.joinToString(", "),
    url = url,
    source = "D&D 5e SRD API",
    updatedAt = updatedAt?.let { Instant.parse(it) } ?: Instant.now(),
)
