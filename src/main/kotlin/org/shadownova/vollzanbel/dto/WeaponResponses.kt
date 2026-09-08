package org.shadownova.vollzanbel.dto

import com.fasterxml.jackson.annotation.JsonAlias
import org.shadownova.vollzanbel.repository.Weapon
import java.time.Instant

data class WeaponCategoryResponse(
    val index: String,
    val name: String,
    val equipment: List<WeaponReference>,
)

data class WeaponReference(val index: String, val name: String, val url: String)

data class WeaponDetailResponse(
    val desc: List<String>?,
    val index: String,
    val name: String,
    @JsonAlias("weapon_category") val weaponCategory: String?,
    @JsonAlias("weapon_range") val weaponRange: String?,
    @JsonAlias("category_range") val categoryRange: String?,
    val cost: Cost?,
    val damage: Damage?,
    val range: WeaponRange?,
    val weight: Double?,
    val properties: List<NamedReference>?,
    @JsonAlias("two_handed_damage") val twoHandedDamage: Damage?,
    val url: String?,
    @JsonAlias("updated_at") val updatedAt: String?,
)

data class Cost(val quantity: Int?, val unit: String?)
data class Damage(
    @JsonAlias("damage_dice") val damageDice: String?,
    @JsonAlias("damage_type") val damageType: NamedReference?,
)
data class WeaponRange(val normal: Int?, val long: Int? = null)

fun WeaponDetailResponse.toEntity() = Weapon(
    index = index,
    name = name,
    description = desc.orEmpty().joinToString("\n\n"),
    weaponCategory = weaponCategory ?: "",
    weaponRange = weaponRange ?: "",
    categoryRange = categoryRange ?: "",
    costQuantity = cost?.quantity,
    costUnit = cost?.unit ?: "",
    damageDice = damage?.damageDice ?: "",
    damageType = damage?.damageType?.name ?: "",
    normalRange = range?.normal,
    longRange = range?.long,
    weight = weight,
    properties = properties.orEmpty().mapNotNull { it.name }.joinToString(", "),
    twoHandedDamageDice = twoHandedDamage?.damageDice ?: "",
    twoHandedDamageType = twoHandedDamage?.damageType?.name ?: "",
    isMagic = false,
    rarity = "",
    url = url,
    source = "D&D 5e SRD API",
    updatedAt = updatedAt?.let { Instant.parse(it) } ?: Instant.now(),
)

data class MagicItemListResponse(val count: Int, val results: List<MagicItemReference>)
data class MagicItemReference(val index: String, val name: String, val url: String)

data class MagicItemDetailResponse(
    val index: String,
    val name: String,
    @JsonAlias("equipment_category") val equipmentCategory: WeaponEquipmentCategory?,
    val rarity: Rarity?,
    val desc: List<String>?,
    val url: String?,
    @JsonAlias("updated_at") val updatedAt: String?,
)

data class WeaponEquipmentCategory(val index: String?, val name: String?, val url: String?)
data class Rarity(val name: String?)

fun MagicItemDetailResponse.toWeaponEntity() = Weapon(
    index = index,
    name = name,
    description = desc.orEmpty().joinToString("\n\n"),
    weaponCategory = "Magic Weapon",
    weaponRange = "",
    categoryRange = "",
    costQuantity = null,
    costUnit = "",
    damageDice = "",
    damageType = "",
    normalRange = null,
    longRange = null,
    weight = null,
    properties = "",
    twoHandedDamageDice = "",
    twoHandedDamageType = "",
    isMagic = true,
    rarity = rarity?.name ?: "",
    url = url,
    source = "D&D 5e SRD API",
    updatedAt = updatedAt?.let { Instant.parse(it) } ?: Instant.now(),
)
