package org.shadownova.vollzanbel.repository

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant

/**
 * The compact, app-facing portion of a 2014 D&D background.
 *
 * The large random tables (defining events, ideals, bonds, flaws, and
 * personality traits) intentionally stay on the linked reference page rather
 * than being copied into the database.
 */
@Entity
@Table(name = "background")
data class Background(
    @Id val index: String,
    val name: String,
    @Column(columnDefinition = "TEXT") val description: String,
    @Column(name = "proficiencies", columnDefinition = "TEXT") val proficiencies: String,
    @Column(name = "languages", columnDefinition = "TEXT") val languages: String,
    @Column(name = "starting_equipment", columnDefinition = "TEXT") val startingEquipment: String,
    @Column(name = "feature_name") val featureName: String,
    @Column(name = "feature_description", columnDefinition = "TEXT") val featureDescription: String,
    /** User-facing full-details link, currently the matching Wikidot page. */
    val url: String?,
    val source: String,
    @Column(name = "updated_at") val updatedAt: Instant,
)

@Repository
interface BackgroundRepository : JpaRepository<Background, String> {
    fun findAllByOrderByNameAsc(): List<Background>
}
