package org.shadownova.vollzanbel.controller

import org.shadownova.vollzanbel.client.ApiReference
import org.shadownova.vollzanbel.client.Dnd5eApiClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/** Read-only proxy for the identity catalogs used by the character sheet. */
@RestController
@RequestMapping("/catalog")
class ReferenceCatalogController(private val apiClient: Dnd5eApiClient) {
    @GetMapping("/{resource}/{index}", produces = ["application/json"])
    fun detail(@PathVariable resource: String, @PathVariable index: String): ResponseEntity<String> =
        if (resource in setOf("classes", "races", "subraces", "subclasses") && index.matches(Regex("[a-z0-9-]+")))
            ResponseEntity.ok(apiClient.getReferenceDetail(resource, index))
        else ResponseEntity.notFound().build()
    private val resources = setOf("languages", "classes", "backgrounds", "subclasses", "subraces")

    @GetMapping("/{resource}")
    fun list(@PathVariable resource: String): ResponseEntity<List<ApiReference>> =
        if (resource in resources) ResponseEntity.ok(apiClient.getReferenceList(resource))
        else ResponseEntity.notFound().build()
}
