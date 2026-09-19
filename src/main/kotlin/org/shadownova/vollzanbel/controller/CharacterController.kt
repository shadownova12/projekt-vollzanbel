package org.shadownova.vollzanbel.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.shadownova.vollzanbel.dto.PlayerCharacter
import org.shadownova.vollzanbel.dto.CharacterSpellRequest
import org.shadownova.vollzanbel.dto.CreateCharacterRequest
import org.shadownova.vollzanbel.dto.UpdateCharacterRequest
import org.shadownova.vollzanbel.dto.CharacterSummary
import org.shadownova.vollzanbel.dto.CharacterListResponse
import org.shadownova.vollzanbel.service.CharacterSnapshot
import org.shadownova.vollzanbel.service.CharacterSnapshotCodec
import org.springframework.http.HttpStatus
import org.shadownova.vollzanbel.repository.CharacterRepository
import org.shadownova.vollzanbel.repository.CharacterRow
import org.shadownova.vollzanbel.repository.CharacterSpell
import org.shadownova.vollzanbel.service.CharacterService
import org.shadownova.vollzanbel.service.validateCharacter
import org.springframework.web.server.ResponseStatusException
import org.springframework.dao.DataIntegrityViolationException

@RestController
@RequestMapping("/characters")
class CharacterController(
    val characterRepository: CharacterRepository,
    val characterService: CharacterService,
    val codec: CharacterSnapshotCodec
) {

    @GetMapping
    fun list(@RequestHeader("X-User-Id") userId: Long): CharacterListResponse =
        CharacterListResponse(characterRepository.listNames(userId).map(::CharacterSummary))

    @GetMapping("/{name}")
    fun get(@RequestHeader("X-User-Id") userId: Long, @PathVariable name: String): ResponseEntity<PlayerCharacter> {
        val row = characterRepository.findByUserIdAndName(userId, name) ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)

        val snapshot = codec.decode(row.compressedData)
        return ResponseEntity.ok(PlayerCharacter(
            id = row.id, userId = row.userId, name = row.name,
            avatarId = snapshot.avatarId, characterSheet = snapshot.characterSheet))
    }

    @PostMapping
    fun createCharacter(@RequestHeader("X-User-Id") userId: Long, @RequestBody body: CreateCharacterRequest): CharacterSummary {
        val name = body.name.trim()
        validateCharacter(name, body.characterSheet)
        if (characterRepository.existsByUserIdAndNameIgnoreCase(userId, name))
            throw ResponseStatusException(HttpStatus.CONFLICT, "A character with this name already exists")
        val compressed = codec.encode(CharacterSnapshot(body.characterSheet, body.avatarId))

        try { characterRepository.save(
            CharacterRow(
                userId = userId,
                name = name,
                compressedData = compressed
            )
        ) } catch (error: DataIntegrityViolationException) {
            if (generateSequence<Throwable>(error) { it.cause }.any { it is java.sql.SQLException && it.sqlState == "23505" })
                throw ResponseStatusException(HttpStatus.CONFLICT, "A character with this name already exists")
            throw error
        }

        return CharacterSummary(name)
    }

    @PutMapping("/{name}")
    fun updatePlayerCharacter(@RequestHeader("X-User-Id") userId: Long, @PathVariable name: String, @RequestBody body: UpdateCharacterRequest): CharacterSummary {
        validateCharacter(name, body.characterSheet)
        val existing = characterRepository.findByUserIdAndName(userId, name)
            ?: throw org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND)
        if (existing.id != body.id) throw org.springframework.web.server.ResponseStatusException(HttpStatus.CONFLICT)
        val compressed = codec.encode(CharacterSnapshot(body.characterSheet, body.avatarId))
        characterRepository.save(existing.copy(compressedData = compressed))

        return CharacterSummary(name)
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

}
