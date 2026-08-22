package org.shadownova.vollzanbel.controller

import org.shadownova.vollzanbel.repository.Race
import org.shadownova.vollzanbel.service.RaceService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/races")
class RaceController(private val service: RaceService) {
    @GetMapping
    fun getAll(): List<Race> = service.getAllRaces()

    @GetMapping("/{index}")
    fun getOne(@PathVariable index: String): ResponseEntity<Race> =
        service.findRace(index)?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
}
