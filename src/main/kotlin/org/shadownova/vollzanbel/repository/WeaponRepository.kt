package org.shadownova.vollzanbel.repository

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Entity
@Table(name = "weapon")
data class Weapon(
    @Id val index: String,
    val name: String,
    @Column(columnDefinition = "TEXT") val description: String,
    @Column(name = "weapon_category") val weaponCategory: String,
    @Column(name = "weapon_range") val weaponRange: String,
    @Column(name = "category_range") val categoryRange: String,
    @Column(name = "cost_quantity") val costQuantity: Int?,
    @Column(name = "cost_unit") val costUnit: String,
    @Column(name = "damage_dice") val damageDice: String,
    @Column(name = "damage_type") val damageType: String,
    @Column(name = "normal_range") val normalRange: Int?,
    @Column(name = "long_range") val longRange: Int?,
    val weight: Double?,
    @Column(columnDefinition = "TEXT") val properties: String,
    @Column(name = "two_handed_damage_dice") val twoHandedDamageDice: String,
    @Column(name = "two_handed_damage_type") val twoHandedDamageType: String,
    @Column(name = "is_magic") val isMagic: Boolean,
    val rarity: String,
    val url: String?,
    val source: String,
    @Column(name = "updated_at") val updatedAt: Instant,
)

@Repository
interface WeaponRepository : JpaRepository<Weapon, String> {
    fun findAllByOrderByNameAsc(): List<Weapon>
}
