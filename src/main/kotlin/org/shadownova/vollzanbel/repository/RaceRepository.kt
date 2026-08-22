package org.shadownova.vollzanbel.repository

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Entity
@Table(name = "race")
data class Race(
    @Id val index: String,
    val name: String,
    val speed: Int,
    @Column(name = "ability_bonuses", columnDefinition = "TEXT") val abilityBonuses: String,
    @Column(columnDefinition = "TEXT") val age: String,
    @Column(columnDefinition = "TEXT") val alignment: String,
    val size: String,
    @Column(name = "size_description", columnDefinition = "TEXT") val sizeDescription: String,
    @Column(columnDefinition = "TEXT") val languages: String,
    @Column(name = "language_description", columnDefinition = "TEXT") val languageDescription: String,
    @Column(columnDefinition = "TEXT") val traits: String,
    @Column(columnDefinition = "TEXT") val subraces: String,
    val url: String?,
    val source: String,
    @Column(name = "updated_at") val updatedAt: Instant,
)

@Repository
interface RaceRepository : JpaRepository<Race, String> {
    fun findAllByOrderByNameAsc(): List<Race>
}
