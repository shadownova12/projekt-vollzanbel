package org.shadownova.vollzanbel.repository

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Lob
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.Instant

@Entity
@Table(name = "spell")
data class Spell(
    @Id
    val index: String,

    val name: String,
    val level: Int,
    val school: String,

    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    val description: String,

    @Lob
    @Column(name = "higher_level", columnDefinition = "TEXT")
    val higherLevel: String,

    @Column(name = "range", columnDefinition = "TEXT")
    val range: String,

    @Column(columnDefinition = "TEXT")
    // store components as a simple comma-separated string (e.g. "V,S,M")
    val components: String,

    @Column(columnDefinition = "TEXT")
    val material: String,

    val ritual: Boolean,
    val duration: String,
    val concentration: Boolean,

    @Column(name = "casting_time", columnDefinition = "TEXT")
    val castingTime: String,
    val url: String,

    @Column(name = "updated_at")
    val updatedAt: Instant
)

data class SpellSummary(
    val index: String?,
    val name: String?,
    val level: Int?,
    val url: String?,
    val school: String?
)

@Repository
interface SpellRepository : JpaRepository<Spell, String> {

    // Use a JPQL constructor expression to map selected columns directly into the
    // Kotlin data class `SpellSummary`. The fully-qualified class name must be
    // used in the `new` expression.
    @Query("SELECT new org.shadownova.vollzanbel.repository.SpellSummary(s.index, s.name, s.level, s.url, s.school) FROM Spell s ORDER BY s.name")
    fun getAllSpellSummaries(): List<SpellSummary>

    fun findByLevelAndSchool(
        level: Int,
        school: String
    ): List<Spell>

    fun findByNameContainingIgnoreCase(
        name: String
    ): List<Spell>
}