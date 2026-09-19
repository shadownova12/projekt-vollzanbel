package org.shadownova.vollzanbel.service

import org.slf4j.LoggerFactory
import org.shadownova.vollzanbel.client.Dnd5eApiClient
import org.shadownova.vollzanbel.repository.Background
import org.shadownova.vollzanbel.repository.BackgroundRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class BackgroundSyncService(
    private val repository: BackgroundRepository,
    private val apiClient: Dnd5eApiClient,
) {
    @Transactional
    fun syncBackgrounds() {
        try {
            if (repository.count() == 0L) importAllBackgrounds()
            else log.info("Background table already contains rows; skipping initial import")
        } catch (e: Exception) {
            // Keep the compact local seed available when the remote catalog is
            // temporarily unavailable during application startup.
            log.error("Background catalog sync failed; keeping local seeds", e)
        }
        ensureFolkHero()
    }

    @Transactional
    fun forceSyncBackgrounds() {
        try {
            importAllBackgrounds()
        } catch (e: Exception) {
            log.error("Forced background catalog sync failed; keeping local seeds", e)
        }
        ensureFolkHero()
    }

    private fun importAllBackgrounds() {
        val refs = apiClient.getBackgroundList()
        val backgrounds = refs.results.mapNotNull { ref ->
            try {
                apiClient.getBackground(ref.index)
            } catch (e: Exception) {
                log.error("Failed to fetch background {} at {}", ref.index, ref.url, e)
                null
            }
        }
        repository.saveAll(backgrounds)
        log.info("Saved {} backgrounds", backgrounds.size)
    }

    private fun ensureFolkHero() {
        if (repository.existsById(FOLK_HERO_INDEX)) return
        repository.save(
            Background(
                index = FOLK_HERO_INDEX,
                name = "Folk Hero",
                description = "Proficiencies: Animal Handling, Survival\n" +
                    "Languages: None\n" +
                    "Starting equipment: A set of artisan's tools (one of your choice), a shovel, an iron pot, common clothes, and a pouch containing 10 gp.\n\n" +
                    "Feature: Rustic Hospitality\n" +
                    "You are comfortable among common folk and can usually find shelter and protection from them.\n\n" +
                    "Full background details: $FOLK_HERO_WIKI_URL",
                proficiencies = "Animal Handling, Survival",
                languages = "None",
                startingEquipment = "A set of artisan's tools (one of your choice), a shovel, an iron pot, common clothes, and a pouch containing 10 gp",
                featureName = "Rustic Hospitality",
                featureDescription = "You are comfortable among common folk and can usually find shelter and protection from them.",
                url = FOLK_HERO_WIKI_URL,
                source = "D&D 5e SRD API",
                updatedAt = Instant.now(),
            )
        )
        log.info("Added local background seed {}", FOLK_HERO_INDEX)
    }

    private val log = LoggerFactory.getLogger(BackgroundSyncService::class.java)

    private companion object {
        const val FOLK_HERO_INDEX = "folk-hero"
        const val FOLK_HERO_WIKI_URL = "https://dnd5e.wikidot.com/background:folk-hero"
    }
}
