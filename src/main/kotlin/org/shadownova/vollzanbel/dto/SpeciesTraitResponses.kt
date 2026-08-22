package org.shadownova.vollzanbel.dto

import com.fasterxml.jackson.annotation.JsonAlias
import org.shadownova.vollzanbel.repository.SpeciesTrait
import java.time.Instant

data class SpeciesTraitListResponse(val count: Int, val results: List<SpeciesTraitReference>)
data class SpeciesTraitReference(val index: String, val name: String, val url: String)

data class SpeciesTraitDetailResponse(
    val index: String,
    val races: List<SpeciesReference>?,
    val subraces: List<SpeciesReference>?,
    val name: String,
    val desc: List<String>?,
    val url: String?,
    @JsonAlias("updated_at") val updatedAt: String?,
)

data class SpeciesReference(val index: String?, val name: String?, val url: String?)

fun SpeciesTraitDetailResponse.toEntity() = SpeciesTrait(
    index = index,
    name = name,
    description = desc?.joinToString("\n\n") ?: "",
    speciesName = (races.orEmpty() + subraces.orEmpty())
        .mapNotNull { it.name }
        .distinct()
        .joinToString(", ")
        .ifBlank { "Unknown" },
    url = url,
    source = "D&D 5e SRD API",
    updatedAt = updatedAt?.let { Instant.parse(it) } ?: Instant.now(),
)
