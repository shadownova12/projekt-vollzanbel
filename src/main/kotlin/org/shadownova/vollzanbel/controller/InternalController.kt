package org.shadownova.vollzanbel.controller

import org.shadownova.vollzanbel.service.SpellSyncService
import org.shadownova.vollzanbel.service.ClassFeatureSyncService
import org.shadownova.vollzanbel.service.SpeciesTraitSyncService
import org.shadownova.vollzanbel.dto.CreateClassFeatureRequest
import org.shadownova.vollzanbel.dto.CreateTraitRequest
import org.shadownova.vollzanbel.repository.ClassFeature
import org.shadownova.vollzanbel.repository.SpeciesTrait
import org.shadownova.vollzanbel.service.ClassFeatureService
import org.shadownova.vollzanbel.service.SpeciesTraitService
import org.shadownova.vollzanbel.dto.CreateRaceRequest
import org.shadownova.vollzanbel.repository.Race
import org.shadownova.vollzanbel.service.RaceService
import org.shadownova.vollzanbel.service.RaceSyncService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/internal")
class InternalController(
    private val spellSyncService: SpellSyncService,
    private val classFeatureSyncService: ClassFeatureSyncService,
    private val speciesTraitSyncService: SpeciesTraitSyncService,
    private val classFeatureService: ClassFeatureService,
    private val speciesTraitService: SpeciesTraitService,
    private val raceService: RaceService,
    private val raceSyncService: RaceSyncService,
) {

    /**
     * Manually trigger a sync with the spells from the D&D API and the database.
     */
    @PostMapping("/spells/sync")
    fun syncSpells() {
        spellSyncService.forceSyncSpells()
    }

    @PostMapping("/class-features/sync")
    fun syncClassFeatures() = classFeatureSyncService.forceSyncClassFeatures()

    @PostMapping("/traits/sync")
    fun syncSpeciesTraits() = speciesTraitSyncService.forceSyncSpeciesTraits()

    @PostMapping("/races/sync")
    fun syncRaces() = raceSyncService.forceSyncRaces()

    @PostMapping("/traits")
    fun createTrait(@RequestBody request: CreateTraitRequest): ResponseEntity<SpeciesTrait> =
        try {
            ResponseEntity.ok(speciesTraitService.createTrait(request))
        } catch (_: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }

    @PostMapping("/class-features")
    fun createClassFeature(@RequestBody request: CreateClassFeatureRequest): ResponseEntity<ClassFeature> =
        try {
            ResponseEntity.ok(classFeatureService.createClassFeature(request))
        } catch (_: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }

    @PostMapping("/races")
    fun createRace(@RequestBody request: CreateRaceRequest): ResponseEntity<Race> =
        try {
            ResponseEntity.ok(raceService.createRace(request))
        } catch (_: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
}
