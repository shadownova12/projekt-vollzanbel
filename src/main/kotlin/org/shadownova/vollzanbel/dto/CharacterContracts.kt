package org.shadownova.vollzanbel.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/** Server identity and presentation metadata, separate from editable game state. */
@JsonIgnoreProperties(ignoreUnknown = true)
data class PlayerCharacter(
    val id: Long,
    val userId: Long,
    val name: String,
    val avatarId: String = "spark_violet",
    val characterSheet: CharacterSheet,
)

/** Creation has no server ID. Ownership comes exclusively from X-User-Id. */
@JsonIgnoreProperties(ignoreUnknown = true)
data class CreateCharacterRequest(
    val name: String,
    val avatarId: String = "spark_violet",
    val characterSheet: CharacterSheet,
)

/** The URL selects the character; id guards against overwriting another record. */
@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateCharacterRequest(
    val id: Long,
    val avatarId: String = "spark_violet",
    val characterSheet: CharacterSheet,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class CharacterSummary(val name: String)

@JsonIgnoreProperties(ignoreUnknown = true)
data class CharacterListResponse(val characters: List<CharacterSummary>)
