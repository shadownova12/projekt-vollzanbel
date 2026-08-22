package org.shadownova.vollzanbel.repository

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.Instant

@Entity
@Table(name = "class_feature")
data class ClassFeature(
    @Id val index: String,
    val name: String,
    @Column(name = "description", columnDefinition = "TEXT") val description: String,
    @Column(name = "class_name") val className: String,
    val level: Int,
    val url: String?,
    val source: String,
    @Column(name = "updated_at") val updatedAt: Instant,
)

@Repository
interface ClassFeatureRepository : JpaRepository<ClassFeature, String> {
    fun findAllByOrderByClassNameAscLevelAscNameAsc(): List<ClassFeature>

    @Query(
        value = "SELECT EXISTS (SELECT 1 FROM class_feature WHERE description ~ '^[0-9]+${'$'}')",
        nativeQuery = true,
    )
    fun hasLegacyLargeObjectDescriptions(): Boolean
}
