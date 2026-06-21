package org.shadownova.vollzanbel.config

import org.shadownova.vollzanbel.service.SpellSyncService
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
// To avoid running the sync twice on startup this runner is not registered as a
// component. If you prefer ApplicationRunner instead of ApplicationReadyEvent,
// add the @Component annotation back to re-enable this class.
class StartupRunner(
    private val spellSyncService: SpellSyncService
) : ApplicationRunner {

    private val log = LoggerFactory.getLogger(StartupRunner::class.java)

    override fun run(args: ApplicationArguments) {
        log.info("Startup: checking/importing spells")
        try {
            spellSyncService.syncSpells()
            log.info("Startup: spell sync completed")
        } catch (e: Exception) {
            log.error("Startup: spell sync failed", e)
        }
    }
}


