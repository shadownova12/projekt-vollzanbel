package org.shadownova.vollzanbel.controller

import org.shadownova.vollzanbel.repository.ClassFeature
import org.shadownova.vollzanbel.service.ClassFeatureService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/class-features")
class ClassFeatureController(private val service: ClassFeatureService) {
    @GetMapping
    fun getAll(): List<ClassFeature> = service.getAllClassFeatures()

    @GetMapping("/{index}")
    fun getOne(@PathVariable index: String): ResponseEntity<ClassFeature> =
        service.findClassFeature(index)?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
}
