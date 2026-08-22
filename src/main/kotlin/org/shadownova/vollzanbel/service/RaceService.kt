package org.shadownova.vollzanbel.service

import org.shadownova.vollzanbel.dto.CreateRaceRequest
import org.shadownova.vollzanbel.repository.Race
import org.shadownova.vollzanbel.repository.RaceRepository
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class RaceService(private val repository: RaceRepository) {
    fun getAllRaces(): List<Race> = repository.findAllByOrderByNameAsc()
    fun findRace(index: String): Race? = repository.findById(index).orElse(null)

    fun createRace(request: CreateRaceRequest): Race {
        require(request.index.isNotBlank()) { "index must not be blank" }
        require(request.name.isNotBlank()) { "name must not be blank" }
        require(request.speed >= 0) { "speed must not be negative" }
        require(request.source.isNotBlank()) { "source must not be blank" }
        require(!repository.existsById(request.index)) { "A race with index '${request.index}' already exists" }

        return repository.save(
            Race(
                index = request.index,
                name = request.name,
                speed = request.speed,
                abilityBonuses = request.abilityBonuses,
                age = request.age,
                alignment = request.alignment,
                size = request.size,
                sizeDescription = request.sizeDescription,
                languages = request.languages,
                languageDescription = request.languageDescription,
                traits = request.traits,
                subraces = request.subraces,
                url = request.url,
                source = request.source,
                updatedAt = Instant.now(),
            )
        )
    }
}
