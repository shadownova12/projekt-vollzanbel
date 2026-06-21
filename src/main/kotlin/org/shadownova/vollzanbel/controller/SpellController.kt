package org.shadownova.vollzanbel.controller

import org.shadownova.vollzanbel.repository.Spell
import org.shadownova.vollzanbel.repository.SpellSummary
import org.shadownova.vollzanbel.service.SpellService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/spells")
class SpellController(val spellService: SpellService) {

    private val log = LoggerFactory.getLogger(SpellController::class.java)

    // GET /spells - returns the list of all spells
    @GetMapping("")
    fun getAllSpells(): List<SpellSummary> {
        log.info("Request: GET /spells - returning all spells")
        return spellService.getAllSpells()
    }

    // GET /spells/{id} - returns a single spell by id or 404 if not found
    @GetMapping("/{id}")
    fun getSpell(@PathVariable id: String): ResponseEntity<Spell> {
        log.info("Request: GET /spells/{}", id)
        val spell = spellService.findSpell(id)
        return if (spell != null) ResponseEntity.ok(spell) else ResponseEntity.notFound().build()
    }
}