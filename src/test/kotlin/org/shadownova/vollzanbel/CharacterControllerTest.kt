package org.shadownova.vollzanbel

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito.*
import org.shadownova.vollzanbel.controller.CharacterController
import org.shadownova.vollzanbel.dto.PlayerCharacter
import org.shadownova.vollzanbel.repository.CharacterRepository
import org.shadownova.vollzanbel.repository.CharacterRow
import org.shadownova.vollzanbel.service.CharacterService
import org.springframework.web.server.ResponseStatusException
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPOutputStream

class CharacterControllerTest {
    private val repository = mock(CharacterRepository::class.java)
    private val mapper = jacksonObjectMapper()
    private val controller = CharacterController(repository, mock(CharacterService::class.java), mapper)
    private fun row(): CharacterRow {
        val bytes = ByteArrayOutputStream()
        GZIPOutputStream(bytes).use { it.write(mapper.writeValueAsBytes(mapOf("name" to "Mage"))) }
        return CharacterRow(42, 1, "Mage", bytes.toByteArray())
    }

    @Test fun `save and load preserve stats spells equipment and traits`() {
        `when`(repository.findByUserIdAndName(1, "Mage")).thenReturn(row())
        val sheet = mapOf("stats" to mapOf("strength" to 12), "spells" to listOf("Light"), "inventory" to listOf("Staff"), "speciesTraits" to listOf("Darkvision"))
        `when`(repository.save(any(CharacterRow::class.java))).thenAnswer {
            val saved = it.arguments[0] as CharacterRow
            `when`(repository.findByUserIdAndName(1, "Mage")).thenReturn(saved)
            saved
        }
        controller.updatePlayerCharacter(1, "Mage", PlayerCharacter(42, 1, "Mage", characterSheet = sheet))
        assertEquals(sheet, controller.get(1, "Mage").body!!.characterSheet)
    }

    @Test fun `update cannot overwrite another character id`() {
        `when`(repository.findByUserIdAndName(1, "Mage")).thenReturn(row())
        assertThrows(ResponseStatusException::class.java) {
            controller.updatePlayerCharacter(1, "Mage", PlayerCharacter(99, 1, "Mage", characterSheet = emptyMap<String, Any>()))
        }
        verify(repository, never()).save(any(CharacterRow::class.java))
    }

    @Test fun `missing character returns not found`() {
        assertEquals(404, controller.get(2, "Mage").statusCode.value())
    }
}
