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
import org.shadownova.vollzanbel.repository.SpellRepository
import org.shadownova.vollzanbel.dto.*
import java.time.Instant

/** Real HTTP binding, controller, compression, JPA, and isolated database round trip. */
@SpringBootTest
class CharacterDatabaseTest {
    @Autowired lateinit var context: WebApplicationContext
    @Autowired lateinit var spellRepository: SpellRepository
    private val mapper = jacksonObjectMapper()

    @Test fun `spell catalog stores and returns source attribution`() {
        val source = "D&D 5e 2014 API"
        spellRepository.save(org.shadownova.vollzanbel.repository.Spell(
            index = "source-attribution-test",
            name = "Source Attribution Test",
            level = 1,
            school = "Abjuration",
            source = source,
            description = "",
            higherLevel = "",
            range = "Self",
            components = "V",
            material = "",
            ritual = false,
            duration = "Instantaneous",
            concentration = false,
            castingTime = "1 action",
            url = "",
            updatedAt = Instant.now(),
        ))

        assertEquals(source, spellRepository.findSpellSummary("source-attribution-test").source)
    }

    @Test fun `HTTP saves accept omitted Kotlin defaults and reject explicit null integers`() {
        val mvc = MockMvcBuilders.webAppContextSetup(context).build()
        val sheet = mapper.readTree(javaClass.getResourceAsStream("/character-sheet.json")) as com.fasterxml.jackson.databind.node.ObjectNode
        sheet.put("name", "Sparse Hero")
        sheet.putObject("hitDice").put("expression", "3d6")
        val create = mapper.createObjectNode().put("name", "Sparse Hero")
        create.set<com.fasterxml.jackson.databind.JsonNode>("characterSheet", sheet)
        mvc.perform(post("/characters").header("X-User-Id", 7303).contentType("application/json").content(create.toString()))
            .andExpect(status().isOk)
        fun load() = mapper.readTree(mvc.perform(get("/characters/Sparse Hero").header("X-User-Id", 7303))
            .andExpect(status().isOk).andReturn().response.contentAsString)
        val update = mapper.createObjectNode().put("id", load()["id"].asLong())
        update.set<com.fasterxml.jackson.databind.JsonNode>("characterSheet", sheet)
        mvc.perform(put("/characters/Sparse Hero").header("X-User-Id", 7303).contentType("application/json").content(update.toString()))
            .andExpect(status().isOk)
        assertEquals(0, load()["characterSheet"]["hitDice"]["spent"].asInt())
        (sheet["hitDice"] as com.fasterxml.jackson.databind.node.ObjectNode).putNull("spent")
        mvc.perform(put("/characters/Sparse Hero").header("X-User-Id", 7303).contentType("application/json").content(update.toString()))
            .andExpect(status().isBadRequest)
    }

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
        val photoBytes = java.io.ByteArrayOutputStream()
        javax.imageio.ImageIO.write(java.awt.image.BufferedImage(64, 64, java.awt.image.BufferedImage.TYPE_INT_RGB), "jpeg", photoBytes)
        val photo = "data:image/jpeg;base64," + java.util.Base64.getEncoder().encodeToString(photoBytes.toByteArray())
        mvc.perform(put("/characters/${original.name}").header("X-User-Id", 7301).contentType("application/json")
            .content(mapper.writeValueAsString(UpdateCharacterRequest(created.id, photo, edited))))
            .andExpect(status().isOk)
        assertEquals(edited, load().characterSheet)
        assertEquals(photo, load().avatarId)
        mvc.perform(put("/characters/${original.name}").header("X-User-Id", 7301).contentType("application/json")
            .content(mapper.writeValueAsString(UpdateCharacterRequest(created.id, "data:image/jpeg;base64,invalid", edited))))
            .andExpect(status().isBadRequest)
        assertEquals(photo, load().avatarId)
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
