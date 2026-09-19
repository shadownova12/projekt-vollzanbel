package org.shadownova.vollzanbel.dto

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.shadownova.vollzanbel.repository.Background
import java.time.Instant

data class BackgroundListResponse(val count: Int, val results: List<BackgroundReference>)
data class BackgroundReference(val index: String, val name: String, val url: String)

@JsonIgnoreProperties(ignoreUnknown = true)
data class BackgroundDetailResponse(
    val index: String,
    val name: String,
    @JsonAlias("starting_proficiencies") val startingProficiencies: List<NamedReference>?,
    @JsonAlias("starting_equipment") val startingEquipment: List<StartingEquipment>?,
    @JsonAlias("language_options") val languageOptions: BackgroundChoice?,
    val feature: BackgroundFeature?,
    val url: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class StartingEquipment(
    val equipment: NamedReference?,
    val quantity: Int?,
)

/** The API represents choices as a structured object; we only retain a compact label. */
@JsonIgnoreProperties(ignoreUnknown = true)
data class BackgroundChoice(
    val choose: Int?,
    val type: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class BackgroundFeature(
    val name: String?,
    val desc: List<String>?,
)

private const val WIKI_BACKGROUND_BASE = "https://dnd5e.wikidot.com/background:"

fun BackgroundDetailResponse.toEntity(): Background {
    val wikiUrl = "$WIKI_BACKGROUND_BASE$index"
    val featureDescription = feature?.desc.orEmpty().joinToString("\n\n").trim()
    val featureName = feature?.name.orEmpty().trim()
    val proficiencies = startingProficiencies.orEmpty()
        .mapNotNull { it.name?.trim()?.takeIf(String::isNotBlank) }
        .joinToString(", ")
    val languages = languageOptions?.let { choice ->
        listOfNotNull(
            choice.choose?.takeIf { it > 0 }?.let { count -> "Choose $count from" },
            choice.type?.trim()?.takeIf(String::isNotBlank),
        ).joinToString(" ")
    }.orEmpty()
    val startingEquipment = startingEquipment.orEmpty()
        .mapNotNull { item ->
            val equipmentName = item.equipment?.name?.trim()?.takeIf(String::isNotBlank) ?: return@mapNotNull null
            val quantity = item.quantity ?: 1
            if (quantity == 1) equipmentName else "$quantity × $equipmentName"
        }
        .joinToString(", ")
    val description = buildString {
        if (proficiencies.isNotBlank()) append("Proficiencies: ").append(proficiencies)
        if (languages.isNotBlank()) {
            if (isNotEmpty()) append("\n")
            append("Languages: ").append(languages)
        }
        if (startingEquipment.isNotBlank()) {
            if (isNotEmpty()) append("\n")
            append("Starting equipment: ").append(startingEquipment)
        }
        if (featureName.isNotBlank()) {
            if (isNotEmpty()) append("\n\n")
            append("Feature: ").append(featureName)
        }
        if (featureDescription.isNotBlank()) {
            if (isNotEmpty()) append("\n\n")
            append(featureDescription)
        }
        if (isNotEmpty()) append("\n\n")
        append("Full background details: ").append(wikiUrl)
    }

    return Background(
        index = index,
        name = name,
        description = description,
        proficiencies = proficiencies,
        languages = languages,
        startingEquipment = startingEquipment,
        featureName = featureName,
        featureDescription = featureDescription,
        url = wikiUrl,
        source = "D&D 5e SRD API",
        updatedAt = Instant.now(),
    )
}
