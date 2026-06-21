package org.shadownova.vollzanbel.service

import org.shadownova.vollzanbel.repository.Spell
import org.shadownova.vollzanbel.repository.SpellRepository
import org.shadownova.vollzanbel.repository.SpellSummary
import org.springframework.stereotype.Service

@Service
class SpellService(val spellRepository: SpellRepository) {

    fun getAllSpells(): List<SpellSummary> {
        return spellRepository.getAllSpellSummaries()
    }

    // Safe lookup that returns null if the spell isn't found. Useful for controllers
    // that want to return 404 instead of throwing an exception.
    fun findSpell(id: String): Spell? {
        return spellRepository.findById(id).orElse(null)
    }
}