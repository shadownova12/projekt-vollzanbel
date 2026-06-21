package org.shadownova.vollzanbel.controller

import org.shadownova.vollzanbel.service.SpellSyncService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/internal")
class InternalController(
    private val spellSyncService: SpellSyncService,
) {

    /**
     * Manually trigger a sync with the spells from the D&D API and the database.
     */
    @PostMapping("/spells/sync")
    fun syncSpells() {
        spellSyncService.forceSyncSpells()
    }
}