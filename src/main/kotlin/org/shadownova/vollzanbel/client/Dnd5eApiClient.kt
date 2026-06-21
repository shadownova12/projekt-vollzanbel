package org.shadownova.vollzanbel.client

import org.shadownova.vollzanbel.dto.SpellListResponse
import org.shadownova.vollzanbel.repository.Spell
import org.shadownova.vollzanbel.dto.SpellDetailResponse
import org.shadownova.vollzanbel.dto.toEntity
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import kotlin.jvm.java

@Service
class Dnd5eApiClient(
    private val restClient: RestClient
) {

    // Return a deserialized SpellListResponse instead of a raw JSON String so callers
    // can work with a typed object (avoids trying to cast a String to the DTO).
    fun getSpellList(): SpellListResponse =
        restClient
            .get()
            .uri("https://www.dnd5eapi.co/api/2014/spells")
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .body(SpellListResponse::class.java)!!

    // Fetches the detailed spell response from the API, maps the subset of fields
    // we care about into our `Spell` entity and returns it.
    fun getSpell(path: String): Spell {
        val detail = restClient
            .get()
            .uri("https://www.dnd5eapi.co$path")
            .retrieve()
            .body(SpellDetailResponse::class.java)!!

        return detail.toEntity()
    }
}