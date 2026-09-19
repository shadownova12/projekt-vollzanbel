package org.shadownova.vollzanbel.service

import org.shadownova.vollzanbel.dto.CharacterSheet
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

fun validateCharacter(name: String, sheet: CharacterSheet) {
    fun check(valid: Boolean, message: String) {
        if (!valid) throw ResponseStatusException(HttpStatus.BAD_REQUEST, message)
    }
    check(name.isNotBlank() && name.length <= 100 && name.none { it.isISOControl() }, "Name must contain 1–100 printable characters")
    check(sheet.name.isNotBlank() && sheet.name.length <= 100, "Display name must contain 1–100 characters")
    check(sheet.characterClass.isNotBlank() && sheet.species.isNotBlank(), "Class and species are required")
    check(sheet.level in 1..20, "Level must be 1–20")
    check(sheet.experiencePoints >= 0, "XP cannot be negative")
    check(sheet.hp.max > 0 && sheet.hp.current in 0..sheet.hp.max && sheet.hp.temp >= 0, "HP must be nonnegative and current HP cannot exceed maximum HP")
    check(sheet.ac in 1..100, "AC must be 1–100")
    with(sheet.stats) {
        check(listOf(strength, dexterity, constitution, intelligence, wisdom, charisma).all { it in 1..30 }, "Ability scores must be 1–30")
    }
    check(sheet.proficiencyBonus in 1..20, "Proficiency bonus must be 1–20")
    check(sheet.speed >= 0 && sheet.hitDice.spent >= 0, "Speed and spent hit dice cannot be negative")
    check(sheet.deathSaves.successes in 0..3 && sheet.deathSaves.failures in 0..3, "Death saves must be 0–3")
    check(sheet.exhaustionLevel in 0..6, "Exhaustion must be 0–6")
    check(sheet.spellSlots.all { (level, slots) -> level in 1..9 && slots.max >= 0 && slots.used in 0..slots.max }, "Invalid spell slots")
    check(sheet.inventory.all { it.quantity >= 0 && it.weight >= 0 }, "Equipment quantities and weights cannot be negative")
    with(sheet.currency) { check(listOf(cp, sp, ep, gp, pp).all { it >= 0 }, "Currency cannot be negative") }
    check(sheet.skills.distinctBy { it.name.lowercase() }.size == sheet.skills.size, "Duplicate skills")
    check(sheet.savingThrows.distinctBy { it.ability }.size == sheet.savingThrows.size, "Duplicate saving throws")
    check(sheet.backgroundUrl.isEmpty() || sheet.backgroundUrl.startsWith("https://"), "Background link must use HTTPS")
}
