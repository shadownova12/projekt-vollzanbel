package org.shadownova.vollzanbel.service

import org.shadownova.vollzanbel.dto.CreateSpellRequest
import org.shadownova.vollzanbel.repository.Spell
import org.shadownova.vollzanbel.repository.SpellRepository
import org.shadownova.vollzanbel.repository.SpellSummary
import org.springframework.stereotype.Service
import java.time.Instant

class SpellAlreadyExistsException(index: String) : RuntimeException("A spell with index '$index' already exists")

@Service
class SpellService(
    val spellRepository: SpellRepository,
    private val spellTextStorageMigration: SpellTextStorageMigration,
) {

    fun getAllSpells(): List<SpellSummary> {
        spellTextStorageMigration.migrateLegacyLargeObjectsIfNeeded()
        return spellRepository.getAllSpellSummaries()
    }

    // Safe lookup that returns null if the spell isn't found. Useful for controllers
    // that want to return 404 instead of throwing an exception.
    fun findSpell(id: String): Spell? {
        spellTextStorageMigration.migrateLegacyLargeObjectsIfNeeded()
        return spellRepository.findById(id).orElse(null)
    }

    fun findSpellSummary(id: String): SpellSummary? {
        spellTextStorageMigration.migrateLegacyLargeObjectsIfNeeded()
        return spellRepository.findSpellSummary(id)
    }

    fun createOfficialSpell(request: CreateSpellRequest): Spell {
        spellTextStorageMigration.migrateLegacyLargeObjectsIfNeeded()

        val index = request.index.trim()
        val name = request.name.trim()
        val school = request.school.trim()
        val source = request.source.trim()
        val url = request.url.trim()

        require(index.matches(Regex("[a-z0-9]+(?:-[a-z0-9]+)*"))) {
            "index must be a lowercase canonical slug, such as 'pulse-wave'"
        }
        require(index.length <= 255) { "index must be 255 characters or fewer" }
        require(name.isNotEmpty()) { "name must not be blank" }
        require(name.length <= 255) { "name must be 255 characters or fewer" }
        require(request.level in 0..9) { "level must be between 0 and 9" }
        require(school.isNotEmpty()) { "school must not be blank" }
        require(school.length <= 255) { "school must be 255 characters or fewer" }
        require(source.isNotEmpty()) { "source must not be blank" }
        require(source.length <= 255) { "source must be 255 characters or fewer" }
        require(request.description.isNotBlank()) { "description must not be blank" }
        require(request.range.isNotBlank()) { "range must not be blank" }
        require(request.components.isNotBlank()) { "components must not be blank" }
        require(request.duration.isNotBlank()) { "duration must not be blank" }
        require(request.castingTime.isNotBlank()) { "castingTime must not be blank" }
        require(url.startsWith("https://") || url.startsWith("http://")) {
            "url must be an http or https URL"
        }
        require(url.length <= 255) { "url must be 255 characters or fewer" }
        require(request.duration.length <= 255) { "duration must be 255 characters or fewer" }
        if (spellRepository.existsById(index)) throw SpellAlreadyExistsException(index)

        val components = request.components
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
        require(components.isNotEmpty()) { "components must contain at least one component" }

        return spellRepository.save(
            Spell(
                index = index,
                name = name,
                level = request.level,
                school = school,
                source = source,
                description = request.description.trim(),
                higherLevel = request.higherLevel.trim(),
                range = request.range.trim(),
                components = components.joinToString(","),
                material = request.material.trim(),
                ritual = request.ritual,
                duration = request.duration.trim(),
                concentration = request.concentration,
                castingTime = request.castingTime.trim(),
                url = url,
                updatedAt = Instant.now(),
            )
        )
    }
}
