package org.shadownova.vollzanbel.dto

import com.fasterxml.jackson.annotation.JsonAlias
import org.shadownova.vollzanbel.repository.EquipmentCategory
import java.time.Instant

data class EquipmentCategoryListResponse(
    val count: Int,
    val results: List<EquipmentCategoryReference>,
)

data class EquipmentCategoryReference(
    val index: String,
    val name: String,
    val url: String,
)

data class EquipmentCategoryDetailResponse(
    val index: String,
    val name: String,
    val url: String?,
    @JsonAlias("updated_at") val updatedAt: String?,
)

fun EquipmentCategoryReference.toEntity() = EquipmentCategory(
    index = index,
    name = name,
    url = url,
    source = "D&D 5e SRD API",
    updatedAt = Instant.now(),
)
