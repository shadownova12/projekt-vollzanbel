package org.shadownova.vollzanbel.service

import org.shadownova.vollzanbel.client.Dnd5eApiClient
import org.shadownova.vollzanbel.repository.SpeciesTraitRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SpeciesTraitSyncService(
    private val repository: SpeciesTraitRepository,
    private val apiClient: Dnd5eApiClient,
) {
    @Transactional
    fun syncSpeciesTraits() {
        if (repository.count() == 0L) importAllSpeciesTraits()
        else log.info("Species trait table already contains rows; skipping initial import")
    }

    @Transactional
    fun forceSyncSpeciesTraits() = importAllSpeciesTraits()

    private fun importAllSpeciesTraits() {
        val refs = apiClient.getSpeciesTraitList()
        val traits = refs.results.mapNotNull { ref ->
            try { apiClient.getSpeciesTrait(ref.index) }
            catch (e: Exception) {
                log.error("Failed to fetch species trait {} at {}", ref.index, ref.url, e)
                null
            }
        }
        repository.saveAll(traits)
        log.info("Saved {} species traits", traits.size)
    }

    private val log = LoggerFactory.getLogger(SpeciesTraitSyncService::class.java)
}
