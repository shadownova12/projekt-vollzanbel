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

//        val compressed = row.compressedData
        val json = gunzip(row.compressedData).toString(Charsets.UTF_8)
        println(json)

         return ResponseEntity.ok(PlayerCharacter(
             id = row.id,
             userId = row.userId,
             name = row.name,
             characterSheet = mapper.readValue(gunzip(row.compressedData), Any::class.java)))
//        return try {
////            val jsonBytes = gunzip(compressed)
////            val obj = mapper.readValue(jsonBytes, Any::class.java)
//            ResponseEntity.ok(obj)
//        } catch (_: Exception) {
//            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf("error" to "Failed to decompress character data"))
//        }
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
        val compressed = gzip(mapper.writeValueAsBytes(body.characterSheet))

        characterRepository.save(
            CharacterRow(
                id = body.id,
                userId = userId,
                name = name,
                compressedData = compressed
            )
        )

        return mapOf("name" to name)
    }

    @DeleteMapping("/{name}")
    fun delete(@RequestHeader("X-User-Id") userId: Long, @PathVariable name: String): ResponseEntity<Void> {
        characterRepository.deleteByUserIdAndName(userId, name)
        return ResponseEntity.noContent().build()
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

