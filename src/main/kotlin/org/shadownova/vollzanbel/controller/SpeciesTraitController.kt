package org.shadownova.vollzanbel.controller

import org.shadownova.vollzanbel.repository.SpeciesTrait
import org.shadownova.vollzanbel.service.SpeciesTraitService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/traits")
class SpeciesTraitController(private val service: SpeciesTraitService) {
    @GetMapping
    fun getAll(): List<SpeciesTrait> = service.getAllSpeciesTraits()

    @GetMapping("/{index}")
    fun getOne(@PathVariable index: String): ResponseEntity<SpeciesTrait> =
        service.findSpeciesTrait(index)?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
}
