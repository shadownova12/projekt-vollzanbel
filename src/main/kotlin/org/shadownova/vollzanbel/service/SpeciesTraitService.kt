package org.shadownova.vollzanbel.service

import org.shadownova.vollzanbel.repository.SpeciesTrait
import org.shadownova.vollzanbel.repository.SpeciesTraitRepository
import org.shadownova.vollzanbel.dto.CreateTraitRequest
import java.time.Instant
import org.springframework.stereotype.Service

@Service
class SpeciesTraitService(private val repository: SpeciesTraitRepository) {
    fun getAllSpeciesTraits(): List<SpeciesTrait> = repository.findAllByOrderBySpeciesNameAscNameAsc()
    fun findSpeciesTrait(index: String): SpeciesTrait? = repository.findById(index).orElse(null)

    fun createTrait(request: CreateTraitRequest): SpeciesTrait {
        require(request.index.isNotBlank()) { "index must not be blank" }
        require(request.name.isNotBlank()) { "name must not be blank" }
        require(request.description.isNotBlank()) { "description must not be blank" }
        require(request.speciesName.isNotBlank()) { "speciesName must not be blank" }
        require(request.source.isNotBlank()) { "source must not be blank" }
        require(!repository.existsById(request.index)) { "A trait with index '${request.index}' already exists" }

        return repository.save(
            SpeciesTrait(
                index = request.index,
                name = request.name,
                description = request.description,
                speciesName = request.speciesName,
                url = request.url,
                source = request.source,
                updatedAt = Instant.now(),
            )
        )
    }
}
