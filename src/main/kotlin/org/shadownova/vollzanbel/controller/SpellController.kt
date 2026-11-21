package org.shadownova.vollzanbel.controller

import org.shadownova.vollzanbel.repository.Spell
import org.shadownova.vollzanbel.repository.SpellSummary
import org.shadownova.vollzanbel.service.SpellService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.shadownova.vollzanbel.dto.CreateSpellRequest
import org.shadownova.vollzanbel.service.SpellAlreadyExistsException
import java.security.MessageDigest

@RestController
@RequestMapping("/spells")
class SpellController(
    val spellService: SpellService,
    @Value("\${SPELL_ADMIN_TOKEN:}") private val adminToken: String,
) {

    private val log = LoggerFactory.getLogger(SpellController::class.java)

    // GET /spells - returns the list of all spells
    @GetMapping("")
    fun getAllSpells(): List<SpellSummary> {
        log.info("Request: GET /spells - returning all spells")
        return spellService.getAllSpells()
    }

    /** Create a spell in the shared catalog. A server-side token is required; X-User-Id is not authorization. */
    @PostMapping("")
    fun createOfficialSpell(
        @RequestHeader(name = "X-Admin-Token", required = false) presentedToken: String?,
        @RequestBody request: CreateSpellRequest,
    ): ResponseEntity<Any> {
        if (adminToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body<Any>(mapOf("message" to "Shared spell writes are not configured"))
        }
        if (!isAdminTokenValid(presentedToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body<Any>(mapOf("message" to "Admin authorization required"))
        }

        return try {
            val spell = spellService.createOfficialSpell(request)
            val location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/spells/{id}")
                .buildAndExpand(spell.index)
                .toUri()
            ResponseEntity.created(location).body<Any>(spell)
        } catch (error: SpellAlreadyExistsException) {
            ResponseEntity.status(HttpStatus.CONFLICT)
                .body<Any>(mapOf("message" to (error.message ?: "Spell already exists")))
        } catch (error: DataIntegrityViolationException) {
            val existing = request.index.trim().takeIf { it.matches(Regex("[a-z0-9]+(?:-[a-z0-9]+)*")) }
                ?.let(spellService::findSpell)
            if (existing != null) {
                ResponseEntity.status(HttpStatus.CONFLICT)
                    .body<Any>(mapOf("message" to "A spell with that index already exists"))
            } else {
                throw error
            }
        } catch (error: IllegalArgumentException) {
            ResponseEntity.badRequest()
                .body<Any>(mapOf("message" to (error.message ?: "Invalid spell")))
        }
    }

    // GET /spells/{id} - returns a single spell by id or 404 if not found
    @GetMapping("/{id}")
    fun getSpell(@PathVariable id: String): ResponseEntity<Spell> {
        log.info("Request: GET /spells/{}", id)
        val spell = spellService.findSpell(id)
        return if (spell != null) ResponseEntity.ok(spell) else ResponseEntity.notFound().build()
    }

    @GetMapping("/{id}/summary")
    fun getSpellSummary(@PathVariable id: String): ResponseEntity<SpellSummary> {
        log.info("Request: GET /spells/{}/summary", id)
        val spell = spellService.findSpellSummary(id)
        return if (spell != null) ResponseEntity.ok(spell) else ResponseEntity.notFound().build()
    }

    private fun isAdminTokenValid(presentedToken: String?): Boolean {
        if (presentedToken.isNullOrBlank()) return false
        return MessageDigest.isEqual(
            adminToken.toByteArray(Charsets.UTF_8),
            presentedToken.toByteArray(Charsets.UTF_8),
        )
    }
}
