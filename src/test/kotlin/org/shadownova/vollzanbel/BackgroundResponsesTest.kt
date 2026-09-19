package org.shadownova.vollzanbel

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.shadownova.vollzanbel.dto.BackgroundDetailResponse
import org.shadownova.vollzanbel.dto.toEntity

class BackgroundResponsesTest {
    private val mapper = jacksonObjectMapper()

    @Test
    fun `background mapping keeps compact rules and links full wiki details`() {
        val response = mapper.readValue(
            """
            {
              "index": "folk-hero",
              "name": "Folk Hero",
              "starting_proficiencies": [
                {"index": "animal-handling", "name": "Animal Handling", "url": "/api/2014/proficiencies/animal-handling"},
                {"index": "survival", "name": "Survival", "url": "/api/2014/proficiencies/survival"}
              ],
              "starting_equipment": [
                {"equipment": {"index": "shovel", "name": "Shovel", "url": "/api/2014/equipment/shovel"}, "quantity": 1}
              ],
              "language_options": {"choose": 1, "type": "languages"},
              "feature": {"name": "Rustic Hospitality", "desc": ["You fit in among commoners with ease."]},
              "personality_traits": {"choose": 2},
              "ideals": {"choose": 1},
              "bonds": {"choose": 1},
              "flaws": {"choose": 1},
              "url": "/api/2014/backgrounds/folk-hero"
            }
            """.trimIndent(),
            BackgroundDetailResponse::class.java,
        ).toEntity()

        assertEquals("folk-hero", response.index)
        assertEquals("Animal Handling, Survival", response.proficiencies)
        assertEquals("Choose 1 from languages", response.languages)
        assertEquals("Shovel", response.startingEquipment)
        assertEquals("Rustic Hospitality", response.featureName)
        assertTrue(response.description.contains("Rustic Hospitality"))
        assertTrue(response.description.contains("https://dnd5e.wikidot.com/background:folk-hero"))
        assertEquals("https://dnd5e.wikidot.com/background:folk-hero", response.url)
        assertTrue(response.description.contains("Full background details"))
    }
}
