package org.shadownova.vollzanbel.dto

import com.fasterxml.jackson.annotation.JsonAlias
import org.shadownova.vollzanbel.repository.ClassFeature
import java.time.Instant

data class ClassFeatureListResponse(val count: Int, val results: List<ClassFeatureReference>)
data class ClassFeatureReference(val index: String, val name: String, val url: String)

data class ClassFeatureDetailResponse(
    val index: String,
    val name: String,
    val desc: List<String>?,
    val level: Int?,
    val `class`: FeatureClass?,
    val url: String?,
    @JsonAlias("updated_at") val updatedAt: String?,
)

data class FeatureClass(val index: String?, val name: String?, val url: String?)

fun ClassFeatureDetailResponse.toEntity() = ClassFeature(
    index = index,
    name = name,
    description = desc?.joinToString("\n\n") ?: "",
    className = `class`?.name ?: "Unknown",
    level = level ?: 0,
    url = url ?: "",
    source = "D&D 5e SRD API",
    updatedAt = updatedAt?.let { Instant.parse(it) } ?: Instant.now(),
)
