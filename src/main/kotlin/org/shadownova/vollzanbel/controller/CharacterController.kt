package org.shadownova.vollzanbel.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.fasterxml.jackson.databind.ObjectMapper
import org.shadownova.vollzanbel.dto.PlayerCharacter
import org.shadownova.vollzanbel.dto.CharacterSpellRequest
import org.shadownova.vollzanbel.dto.NewPlayerCharacter
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import org.springframework.http.HttpStatus
import org.shadownova.vollzanbel.repository.CharacterRepository
import org.shadownova.vollzanbel.repository.CharacterRow
import org.shadownova.vollzanbel.repository.CharacterSpell
import org.shadownova.vollzanbel.service.CharacterService

@RestController
@RequestMapping("/characters")
class CharacterController(
    val characterRepository: CharacterRepository,
    val characterService: CharacterService,
    val mapper: ObjectMapper
) {

    @GetMapping
    fun list(@RequestHeader("X-User-Id") userId: Long): Map<String, Any> {
        val names = characterRepository.listNames(userId)
        val result = names.map { mapOf("name" to it) }
        return mapOf("characters" to result)
    }

    @GetMapping("/{name}")
    fun get(@RequestHeader("X-User-Id") userId: Long, @PathVariable name: String): ResponseEntity<PlayerCharacter> {
        val row = characterRepository.findByUserIdAndName(userId, name) ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)

        return ResponseEntity.ok(PlayerCharacter(
            id = row.id, userId = row.userId, name = row.name,
            characterSheet = mapper.readValue(gunzip(row.compressedData), Any::class.java)))
    }

    @PostMapping
    fun newPlayerCharacter(@RequestHeader("X-User-Id") userId: Long, @RequestBody body: NewPlayerCharacter): Map<String, Any> {
        val compressed = gzip(mapper.writeValueAsBytes(body.characterSheet))

        characterRepository.save(
            CharacterRow(
                userId = userId,
                name = body.name,
                compressedData = compressed
            )
        )

        return mapOf("name" to body.name)
    }

    @PutMapping("/{name}")
    fun updatePlayerCharacter(@RequestHeader("X-User-Id") userId: Long, @PathVariable name: String, @RequestBody body: PlayerCharacter): Map<String, Any> {
        val existing = characterRepository.findByUserIdAndName(userId, name)
            ?: throw org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND)
        if (existing.id != body.id) throw org.springframework.web.server.ResponseStatusException(HttpStatus.CONFLICT)
        val compressed = gzip(mapper.writeValueAsBytes(body.characterSheet))
        characterRepository.save(existing.copy(compressedData = compressed))

        return mapOf("name" to name)
    }

    @DeleteMapping("/{name}")
    fun delete(@RequestHeader("X-User-Id") userId: Long, @PathVariable name: String): ResponseEntity<Void> {
        return if (characterService.deleteCharacter(userId, name)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/{characterId}/spells")
    fun getSpells(@PathVariable characterId: Long): ResponseEntity<List<CharacterSpell>> {
        return ResponseEntity.ok(characterService.getSpells(characterId))
    }

    @PostMapping("/{characterId}/spells")
    fun addSpell(@PathVariable characterId: Long, @RequestBody requestBody: CharacterSpellRequest): ResponseEntity<CharacterSpell> {
        return ResponseEntity.ok(characterService.addSpell(characterId, requestBody))
    }

    @DeleteMapping("/{characterId}/spells")
    fun removeSpell(@PathVariable charId: Long, @RequestParam spellIndex: String): ResponseEntity<Void> {
        characterService.removeSpell(charId, spellIndex)
        return ResponseEntity.noContent().build()
    }

    @PutMapping("/{characterId}/spells")
    fun updateSpell(@PathVariable charId: Long, @RequestBody requestBody: CharacterSpellRequest): ResponseEntity<CharacterSpell> {
        return ResponseEntity.ok(characterService.updateSpell(charId, requestBody))
    }

    private fun gzip(input: ByteArray): ByteArray {
        val baos = ByteArrayOutputStream()
        GZIPOutputStream(baos).use { it.write(input) }
        return baos.toByteArray()
    }

    private fun gunzip(bytes: ByteArray): ByteArray {
        GZIPInputStream(ByteArrayInputStream(bytes)).use { gis ->
            return gis.readAllBytes()
        }
    }
}
