package org.shadownova.vollzanbel.service

import org.shadownova.vollzanbel.client.Dnd5eApiClient
import org.shadownova.vollzanbel.repository.WeaponRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.jdbc.core.JdbcTemplate

@Service
class WeaponSyncService(
    private val repository: WeaponRepository,
    private val apiClient: Dnd5eApiClient,
    private val jdbcTemplate: JdbcTemplate,
) {
    @Transactional
    fun syncWeapons() {
        backfillWeaponMetadata()
        if (repository.count() == 0L) importAllWeapons()
        else log.info("Weapon table already contains rows; skipping initial import")
    }

    @Transactional
    fun forceSyncWeapons() = importAllWeapons()

    private fun importAllWeapons() {
        val category = apiClient.getWeaponCategory()
        val standardWeapons = category.equipment.mapNotNull { ref ->
            try {
                if (ref.url.contains("/magic-items/")) apiClient.getMagicItem(ref.index)
                else apiClient.getWeapon(ref.index)
            }
            catch (e: Exception) {
                log.error("Failed to fetch weapon {} at {}", ref.index, ref.url, e)
                null
            }
        }
        val magicWeapons = apiClient.getMagicItemList().results.mapNotNull { ref ->
            try {
                apiClient.getMagicItem(ref.index)
            } catch (e: Exception) {
                log.error("Failed to fetch magic item {} at {}", ref.index, ref.url, e)
                null
            }
        }
        val weapons = (standardWeapons + magicWeapons)
            .filter { it.isMagic || it.weaponCategory.isNotBlank() }
            .distinctBy { it.index }
        repository.saveAll(weapons)
        log.info("Saved {} standard weapons and {} magic weapons", standardWeapons.size, magicWeapons.size)
    }

    private fun backfillWeaponMetadata() {
        jdbcTemplate.update("UPDATE weapon SET is_magic = false WHERE is_magic IS NULL")
        jdbcTemplate.update("UPDATE weapon SET rarity = '' WHERE rarity IS NULL")
    }

    private val log = LoggerFactory.getLogger(WeaponSyncService::class.java)
}
