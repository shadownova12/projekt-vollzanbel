package org.shadownova.vollzanbel

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.context.WebApplicationContext
import org.shadownova.vollzanbel.dto.*

/** Real HTTP binding, controller, compression, JPA, and isolated database round trip. */
@SpringBootTest
class CharacterDatabaseTest {
    @Autowired lateinit var context: WebApplicationContext
    private val mapper = jacksonObjectMapper()

    @Test fun `create edit and reload a complete character and enforce ownership and validation`() {
        val mvc = MockMvcBuilders.webAppContextSetup(context).build()
        val original = mapper.readValue(javaClass.getResourceAsStream("/character-sheet.json"), CharacterSheet::class.java)
            .copy(name = "Roundtrip Hero")
        mvc.perform(post("/characters").header("X-User-Id", 7301).contentType("application/json")
            .content(mapper.writeValueAsString(CreateCharacterRequest(original.name, characterSheet = original))))
            .andExpect(status().isOk)
        fun load(): PlayerCharacter = mapper.readValue(mvc.perform(get("/characters/${original.name}").header("X-User-Id", 7301))
            .andExpect(status().isOk).andReturn().response.contentAsString, PlayerCharacter::class.java)
        val created = load()
        val edited = original.copy(subclass = "School of Abjuration", background = "Folk Hero", backgroundId = "folk-hero",
            backgroundUrl = "https://dnd5e.wikidot.com/background:folk-hero", backgroundFeature = "Rustic Hospitality",
            toolProficiencies = listOf("Smith's tools", "Vehicles (land)"), setupReviewed = true,
            appliedSetupGrants = setOf("2014:background:folk-hero"),
            profile = CharacterProfile(ideals = "Freedom", bonds = "My village", flaws = "Impatient"))
        mvc.perform(put("/characters/${original.name}").header("X-User-Id", 7301).contentType("application/json")
            .content(mapper.writeValueAsString(UpdateCharacterRequest(created.id, "spark_gold", edited))))
            .andExpect(status().isOk)
        assertEquals(edited, load().characterSheet)
        assertEquals("spark_gold", load().avatarId)
        mvc.perform(get("/characters/${original.name}").header("X-User-Id", 7302)).andExpect(status().isNotFound)
        mvc.perform(post("/characters").header("X-User-Id", 7301).contentType("application/json")
            .content(mapper.writeValueAsString(CreateCharacterRequest(original.name.lowercase(), characterSheet = original))))
            .andExpect(status().isConflict)
        mvc.perform(put("/characters/${original.name}").header("X-User-Id", 7301).contentType("application/json")
            .content(mapper.writeValueAsString(UpdateCharacterRequest(created.id, characterSheet = edited.copy(level = 0)))))
            .andExpect(status().isBadRequest)
        assertEquals(edited, load().characterSheet)
        mvc.perform(delete("/characters/${original.name}").header("X-User-Id", 7301)).andExpect(status().isNoContent)
    }
}
