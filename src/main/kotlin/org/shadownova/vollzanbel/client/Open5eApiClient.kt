package org.shadownova.vollzanbel.client

import tools.jackson.databind.JsonNode
import tools.jackson.databind.node.ObjectNode
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.util.UriComponentsBuilder

/** Read-only Open5e v2 client used by the character spell browser. */
@Service
class Open5eApiClient(private val restClient: RestClient) {
    fun searchSpells(search: String, page: Int): JsonNode {
        val uri = UriComponentsBuilder.fromUriString("$BASE_URL/spells/")
            .queryParam("limit", PAGE_SIZE)
            .queryParam("page", page.coerceAtLeast(1))
            .queryParam("ordering", "name")
            .apply { if (search.isNotBlank()) queryParam("name__icontains", search.trim()) }
            .build().encode().toUri()
        return restClient.get().uri(uri).accept(MediaType.APPLICATION_JSON)
            .retrieve().body(JsonNode::class.java)
            ?: error("Open5e returned an empty spell page")
    }

    fun getSpell(key: String): JsonNode {
        requireValidKey(key)
        val uri = UriComponentsBuilder.fromUriString("$BASE_URL/spells/$key/")
            .build().encode().toUri()
        val spell = restClient.get().uri(uri).accept(MediaType.APPLICATION_JSON)
            .retrieve().body(JsonNode::class.java)
            ?: error("Open5e returned an empty spell")

        val sourceKey = spell.path("document").path("key").asText("")
        if (sourceKey.isBlank()) return spell

        return try {
            val documentUri = UriComponentsBuilder.fromUriString("$BASE_URL/documents/")
                .queryParam("key", sourceKey)
                .queryParam("limit", 1)
                .build().encode().toUri()
            val documents = restClient.get().uri(documentUri).accept(MediaType.APPLICATION_JSON)
                .retrieve().body(JsonNode::class.java)
            val document = documents?.path("results")?.firstOrNull() ?: return spell
            val enriched = (spell as? ObjectNode)?.deepCopy() ?: return spell
            enriched.set("document", document)
            enriched
        } catch (error: Exception) {
            log.warn("Could not load Open5e source metadata for spell {} (document {})", key, sourceKey, error)
            spell
        }
    }

    private fun requireValidKey(key: String) {
        require(key.matches(KEY_PATTERN)) { "Invalid Open5e spell key" }
    }

    private companion object {
        const val BASE_URL = "https://api.open5e.com/v2"
        const val PAGE_SIZE = 40
        val KEY_PATTERN = Regex("[A-Za-z0-9_-]+")
        val log = LoggerFactory.getLogger(Open5eApiClient::class.java)
    }
}
