package org.shadownova.vollzanbel

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertThrows
import org.shadownova.vollzanbel.dto.*
import org.shadownova.vollzanbel.service.validateCharacter
import org.springframework.web.server.ResponseStatusException

class CharacterValidationTest {
    @Test fun `reject invalid sheets before persistence`() {
        val sheet = jacksonObjectMapper().readValue(javaClass.getResourceAsStream("/character-sheet.json"), CharacterSheet::class.java)
        validateCharacter("Mage", sheet)
        val invalid = listOf(sheet.copy(level = 0), sheet.copy(level = 21), sheet.copy(characterClass = ""),
            sheet.copy(species = ""), sheet.copy(hp = HitPoints(-1, 10)), sheet.copy(hp = HitPoints(11, 10)),
            sheet.copy(hp = HitPoints(0, 0)), sheet.copy(ac = 0), sheet.copy(stats = sheet.stats.copy(strength = 31)),
            sheet.copy(currency = Currency(gp = -1)), sheet.copy(spellSlots = mapOf(1 to SpellSlotInfo(2, 3))))
        invalid.forEach { value -> assertThrows(ResponseStatusException::class.java) { validateCharacter("Mage", value) } }
        assertThrows(ResponseStatusException::class.java) { validateCharacter(" ", sheet) }
    }
}
