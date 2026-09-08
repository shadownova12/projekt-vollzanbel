package org.shadownova.vollzanbel.service

import org.shadownova.vollzanbel.client.Dnd5eApiClient
import org.shadownova.vollzanbel.dto.toEntity
import org.shadownova.vollzanbel.repository.EquipmentCategoryRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class EquipmentCategorySyncService(
    private val repository: EquipmentCategoryRepository,
    private val apiClient: Dnd5eApiClient,
) {
    @Transactional
    fun syncEquipmentCategories() {
        if (repository.count() == 0L) {
            repository.saveAll(apiClient.getEquipmentCategoryList().results.map { it.toEntity() })
            log.info("Saved equipment categories")
        } else {
            log.info("Equipment category table already contains rows; skipping initial import")
        }
    }

    @Transactional
    fun forceSyncEquipmentCategories() {
        repository.saveAll(apiClient.getEquipmentCategoryList().results.map { it.toEntity() })
        log.info("Saved equipment categories")
    }

    private val log = LoggerFactory.getLogger(EquipmentCategorySyncService::class.java)
}
