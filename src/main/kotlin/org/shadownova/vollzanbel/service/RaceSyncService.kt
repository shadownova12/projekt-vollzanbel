package org.shadownova.vollzanbel.service

import org.shadownova.vollzanbel.client.Dnd5eApiClient
import org.shadownova.vollzanbel.repository.RaceRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RaceSyncService(
    private val repository: RaceRepository,
    private val apiClient: Dnd5eApiClient,
) {
    @Transactional
    fun syncRaces() {
        if (repository.count() == 0L) importAllRaces()
        else log.info("Race table already contains rows; skipping initial import")
    }

    @Transactional
    fun forceSyncRaces() = importAllRaces()

    private fun importAllRaces() {
        val refs = apiClient.getRaceList()
        val races = refs.results.mapNotNull { ref ->
            try { apiClient.getRace(ref.index) }
            catch (e: Exception) {
                log.error("Failed to fetch race {} at {}", ref.index, ref.url, e)
                null
            }
        }
        repository.saveAll(races)
        log.info("Saved {} races", races.size)
    }

    private val log = LoggerFactory.getLogger(RaceSyncService::class.java)
}
