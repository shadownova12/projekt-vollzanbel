package org.shadownova.vollzanbel.repository

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Entity
@Table(name = "species_trait")
data class SpeciesTrait(
    @Id val index: String,
    val name: String,
    @Column(name = "description", columnDefinition = "TEXT") val description: String,
    @Column(name = "species_name") val speciesName: String,
    val url: String?,
    val source: String,
    @Column(name = "updated_at") val updatedAt: Instant,
)

@Repository
interface SpeciesTraitRepository : JpaRepository<SpeciesTrait, String> {
    fun findAllByOrderBySpeciesNameAscNameAsc(): List<SpeciesTrait>
}
