package org.shadownova.vollzanbel.dto

data class CreateTraitRequest(
    val index: String,
    val name: String,
    val description: String,
    val speciesName: String,
    val source: String,
    val url: String? = null,
)

data class CreateClassFeatureRequest(
    val index: String,
    val name: String,
    val description: String,
    val className: String,
    val level: Int = 0,
    val source: String,
    val url: String? = null,
)

data class CreateRaceRequest(
    val index: String,
    val name: String,
    val speed: Int = 30,
    val abilityBonuses: String = "",
    val age: String = "",
    val alignment: String = "",
    val size: String = "",
    val sizeDescription: String = "",
    val languages: String = "",
    val languageDescription: String = "",
    val traits: String = "",
    val subraces: String = "",
    val source: String,
    val url: String? = null,
)
