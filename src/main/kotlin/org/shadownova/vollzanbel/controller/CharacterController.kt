package org.shadownova.vollzanbel.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.fasterxml.jackson.databind.ObjectMapper
import org.shadownova.vollzanbel.dto.CharacterSpellRequest
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import org.springframework.http.HttpStatus
import org.shadownova.vollzanbel.repository.CharacterRepository
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
    fun list(@RequestHeader("X-Device-Id") deviceId: String): Map<String, Any> {
        val names = characterRepository.listNames(deviceId)
        val result = names.map { mapOf("name" to it) }
        return mapOf("characters" to result)
    }

    @GetMapping("/{name}")
    fun get(@RequestHeader("X-Device-Id") deviceId: String, @PathVariable name: String): ResponseEntity<Any> {
        val row = characterRepository.findByDeviceIdAndName(deviceId, name) ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to "Character not found"))

        val compressed = row.compressedData ?: return ResponseEntity.ok(null)

        return try {
            val jsonBytes = gunzip(compressed)
            val obj = mapper.readValue(jsonBytes, Any::class.java)
            ResponseEntity.ok(obj)
        } catch (_: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf("error" to "Failed to decompress character data"))
        }
    }

    @PutMapping("/{name}")
    fun put(@RequestHeader("X-Device-Id") deviceId: String, @PathVariable name: String, @RequestBody body: Any): Map<String, Any> {
        val jsonBytes = mapper.writeValueAsBytes(body)
        val compressed = gzip(jsonBytes)

        characterRepository.save(deviceId, name, compressed)
        return mapOf("name" to name)
    }

    @DeleteMapping("/{name}")
    fun delete(@RequestHeader("X-Device-Id") deviceId: String, @PathVariable name: String): ResponseEntity<Void> {
        characterRepository.delete(deviceId, name)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{characterId}/spells")
    fun getSpells(@PathVariable characterId: Long): ResponseEntity<List<CharacterSpell>> {
        return ResponseEntity.ok(characterService.getSpells(characterId))
    }

    @PostMapping("/{characterId}/spells")
    fun addSpell(@PathVariable charId: Long, @RequestBody requestBody: CharacterSpellRequest): ResponseEntity<CharacterSpell> {
        return ResponseEntity.ok(characterService.addSpell(charId, requestBody))
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

