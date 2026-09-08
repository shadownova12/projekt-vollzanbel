package org.shadownova.vollzanbel.repository

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Entity
@Table(name = "equipment_category")
data class EquipmentCategory(
    @Id val index: String,
    val name: String,
    val url: String?,
    val source: String,
    @Column(name = "updated_at") val updatedAt: Instant,
)

@Repository
interface EquipmentCategoryRepository : JpaRepository<EquipmentCategory, String> {
    fun findAllByOrderByNameAsc(): List<EquipmentCategory>
}
