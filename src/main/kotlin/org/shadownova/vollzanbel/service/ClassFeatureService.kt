package org.shadownova.vollzanbel.service

import org.shadownova.vollzanbel.repository.ClassFeature
import org.shadownova.vollzanbel.repository.ClassFeatureRepository
import org.shadownova.vollzanbel.dto.CreateClassFeatureRequest
import java.time.Instant
import org.springframework.stereotype.Service

@Service
class ClassFeatureService(private val repository: ClassFeatureRepository) {
    fun getAllClassFeatures(): List<ClassFeature> = repository.findAllByOrderByClassNameAscLevelAscNameAsc()
    fun findClassFeature(index: String): ClassFeature? = repository.findById(index).orElse(null)

    fun createClassFeature(request: CreateClassFeatureRequest): ClassFeature {
        require(request.index.isNotBlank()) { "index must not be blank" }
        require(request.name.isNotBlank()) { "name must not be blank" }
        require(request.description.isNotBlank()) { "description must not be blank" }
        require(request.className.isNotBlank()) { "className must not be blank" }
        require(request.level >= 0) { "level must not be negative" }
        require(request.source.isNotBlank()) { "source must not be blank" }
        require(!repository.existsById(request.index)) { "A class feature with index '${request.index}' already exists" }

        return repository.save(
            ClassFeature(
                index = request.index,
                name = request.name,
                description = request.description,
                className = request.className,
                level = request.level,
                url = request.url,
                source = request.source,
                updatedAt = Instant.now(),
            )
        )
    }
}
