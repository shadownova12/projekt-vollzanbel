package org.shadownova.vollzanbel.schedule

import org.shadownova.vollzanbel.service.SpellSyncService
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
// Runs the sync when the application is fully ready. To use ApplicationRunner
// instead, remove this class or delete the @Component annotation.
class StartupInitializer(
    private val spellSyncService: SpellSyncService
) {

    @EventListener(ApplicationReadyEvent::class)
    fun initialize() {
        // method remains available but this class is not a bean so the event listener
        // won't be wired. Re-enable by adding @Component above the class.
        spellSyncService.syncSpells()
    }
}