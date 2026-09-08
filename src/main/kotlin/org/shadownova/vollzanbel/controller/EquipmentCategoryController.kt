package org.shadownova.vollzanbel.controller

import org.shadownova.vollzanbel.repository.EquipmentCategory
import org.shadownova.vollzanbel.service.EquipmentCategoryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/equipment-categories")
class EquipmentCategoryController(private val service: EquipmentCategoryService) {
    @GetMapping
    fun getAll(): List<EquipmentCategory> = service.getAllEquipmentCategories()

    @GetMapping("/{index}")
    fun getOne(@PathVariable index: String): ResponseEntity<EquipmentCategory> =
        service.findEquipmentCategory(index)?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
}
