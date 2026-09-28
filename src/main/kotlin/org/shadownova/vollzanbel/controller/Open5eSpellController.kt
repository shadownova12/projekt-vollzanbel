package org.shadownova.vollzanbel.controller

import org.shadownova.vollzanbel.client.Open5eApiClient
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import tools.jackson.databind.JsonNode

/** Narrow proxy so the browser can use Open5e without exposing API access to each client. */
@RestController
@RequestMapping("/open5e/spells")
class Open5eSpellController(private val open5eApiClient: Open5eApiClient) {
    @GetMapping
    fun search(
        @RequestParam(defaultValue = "") search: String,
        @RequestParam(defaultValue = "1") page: Int,
    ): JsonNode = open5eApiClient.searchSpells(search, page)

    @GetMapping("/{key}")
    fun get(@PathVariable key: String): JsonNode = try {
        open5eApiClient.getSpell(key)
    } catch (error: IllegalArgumentException) {
        throw ResponseStatusException(HttpStatus.BAD_REQUEST, error.message)
    }
}
