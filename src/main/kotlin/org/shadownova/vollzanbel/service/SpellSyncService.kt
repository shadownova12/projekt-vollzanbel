package org.shadownova.vollzanbel.service

import org.shadownova.vollzanbel.client.Dnd5eApiClient
import org.shadownova.vollzanbel.repository.SpellRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SpellSyncService(
    private val spellRepository: SpellRepository,
    private val dnd5eApiClient: Dnd5eApiClient
) {

    @Transactional
    fun syncSpells() {
        val existing = spellRepository.count()
        if (existing == 0L) {
            importAllSpells()
        } else {
            log.info("Spell table already contains {} rows — skipping initial import (update check TODO)", existing)
        }
    }

    @Transactional
    fun forceSyncSpells() {
        log.info("Forcing spell sync")
        importAllSpells()
    }

    private fun importAllSpells() {
        // getSpellList now returns a typed SpellListResponse
        val spellRefs = dnd5eApiClient.getSpellList()
        log.info("Found {} spell references from API; fetching details...", spellRefs.results.size)

        val spells = spellRefs.results.mapIndexedNotNull { idx, ref ->
            try {
                dnd5eApiClient.getSpell(ref.url)
            } catch (e: Exception) {
                log.error("Failed to fetch spell detail for {} at {} (index {})", ref.index, ref.url, idx, e)
                null
            }
        }

        log.info("Saving {} spells to the database", spells.size)
        spellRepository.saveAll(spells)
        log.info("Saved spells")
    }

    private val log = LoggerFactory.getLogger(SpellSyncService::class.java)
}