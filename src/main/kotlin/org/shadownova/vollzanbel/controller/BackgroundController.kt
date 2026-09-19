package org.shadownova.vollzanbel.controller

import org.shadownova.vollzanbel.repository.Background
import org.shadownova.vollzanbel.service.BackgroundService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/backgrounds")
class BackgroundController(private val service: BackgroundService) {
    @GetMapping
    fun getAll(): List<Background> = service.getAllBackgrounds()

    @GetMapping("/{index}")
    fun getOne(@PathVariable index: String): ResponseEntity<Background> =
        service.findBackground(index)?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
}
