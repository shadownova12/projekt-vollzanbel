package org.shadownova.vollzanbel.service

import org.shadownova.vollzanbel.repository.EquipmentCategory
import org.shadownova.vollzanbel.repository.EquipmentCategoryRepository
import org.springframework.stereotype.Service

@Service
class EquipmentCategoryService(private val repository: EquipmentCategoryRepository) {
    fun getAllEquipmentCategories(): List<EquipmentCategory> = repository.findAllByOrderByNameAsc()
    fun findEquipmentCategory(index: String): EquipmentCategory? = repository.findById(index).orElse(null)
}
