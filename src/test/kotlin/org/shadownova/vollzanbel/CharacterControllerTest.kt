package org.shadownova.vollzanbel

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito.*
import org.shadownova.vollzanbel.controller.CharacterController
import org.shadownova.vollzanbel.dto.*
import org.shadownova.vollzanbel.service.CharacterSnapshotCodec
import org.shadownova.vollzanbel.repository.CharacterRepository
import org.shadownova.vollzanbel.repository.CharacterRow
import org.shadownova.vollzanbel.service.CharacterService
import org.springframework.web.server.ResponseStatusException
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPOutputStream

class CharacterControllerTest {
    private val repository = mock(CharacterRepository::class.java)
    private val mapper = jacksonObjectMapper()
    private val controller = CharacterController(repository, mock(CharacterService::class.java), CharacterSnapshotCodec(mapper))
    private fun sheet(): CharacterSheet = mapper.readValue(
        javaClass.getResourceAsStream("/character-sheet.json"), CharacterSheet::class.java)

    @Test fun `legacy compressed sheets load with defaults`() {
        `when`(repository.findByUserIdAndName(1, "Mage")).thenReturn(row())
        assertEquals(sheet(), controller.get(1, "Mage").body!!.characterSheet)
        assertEquals("spark_violet", controller.get(1, "Mage").body!!.avatarId)
    }

    @Test fun `creation persists avatar and uses header ownership`() {
        `when`(repository.save(any(CharacterRow::class.java))).thenAnswer {
            val saved = it.arguments[0] as CharacterRow
            assertEquals(8L, saved.userId)
            val snapshot = CharacterSnapshotCodec(mapper).decode(saved.compressedData)
            assertEquals(sheet(), snapshot.characterSheet)
            assertEquals("spark_gold", snapshot.avatarId)
            saved
        }
        assertEquals(CharacterSummary("Mage"), controller.createCharacter(8,
            CreateCharacterRequest("Mage", "spark_gold", sheet())))
    }

    @Test fun `HTTP create binds the typed sheet and rejects malformed nested data`() {
        val mvc = org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup(controller).build()
        val body = mapper.writeValueAsString(CreateCharacterRequest("Mage", characterSheet = sheet()))
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/characters")
            .header("X-User-Id", "1").contentType("application/json").content(body))
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk)
        val invalid = mapper.readTree(body) as com.fasterxml.jackson.databind.node.ObjectNode
        (invalid.get("characterSheet") as com.fasterxml.jackson.databind.node.ObjectNode).put("stats", "invalid")
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/characters")
            .header("X-User-Id", "1").contentType("application/json").content(mapper.writeValueAsString(invalid)))
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isBadRequest)
    }

    private fun row(): CharacterRow {
        val bytes = ByteArrayOutputStream()
        GZIPOutputStream(bytes).use { it.write(mapper.writeValueAsBytes(sheet())) }
        return CharacterRow(42, 1, "Mage", bytes.toByteArray())
    }

    @Test fun `save and load preserve stats spells equipment and traits`() {
        `when`(repository.findByUserIdAndName(1, "Mage")).thenReturn(row())
        val sheet = sheet()
        `when`(repository.save(any(CharacterRow::class.java))).thenAnswer {
            val saved = it.arguments[0] as CharacterRow
            `when`(repository.findByUserIdAndName(1, "Mage")).thenReturn(saved)
            saved
        }
        controller.updatePlayerCharacter(1, "Mage", UpdateCharacterRequest(42, "spark_gold", sheet))
        assertEquals(sheet, controller.get(1, "Mage").body!!.characterSheet)
        assertEquals("spark_gold", controller.get(1, "Mage").body!!.avatarId)
    }

    @Test fun `update cannot overwrite another character id`() {
        `when`(repository.findByUserIdAndName(1, "Mage")).thenReturn(row())
        assertThrows(ResponseStatusException::class.java) {
            controller.updatePlayerCharacter(1, "Mage", UpdateCharacterRequest(99, characterSheet = sheet()))
        }
        verify(repository, never()).save(any(CharacterRow::class.java))
    }

    @Test fun `missing character returns not found`() {
        assertEquals(404, controller.get(2, "Mage").statusCode.value())
    }
}
