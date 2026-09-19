package org.shadownova.vollzanbel.client

import org.shadownova.vollzanbel.dto.SpellListResponse
import org.shadownova.vollzanbel.dto.ClassFeatureDetailResponse
import org.shadownova.vollzanbel.dto.ClassFeatureListResponse
import org.shadownova.vollzanbel.dto.SpeciesTraitDetailResponse
import org.shadownova.vollzanbel.dto.SpeciesTraitListResponse
import org.shadownova.vollzanbel.dto.RaceDetailResponse
import org.shadownova.vollzanbel.dto.RaceListResponse
import org.shadownova.vollzanbel.dto.BackgroundDetailResponse
import org.shadownova.vollzanbel.dto.BackgroundListResponse
import org.shadownova.vollzanbel.dto.toEntity
import org.shadownova.vollzanbel.dto.WeaponCategoryResponse
import org.shadownova.vollzanbel.dto.WeaponDetailResponse
import org.shadownova.vollzanbel.dto.EquipmentCategoryListResponse
import org.shadownova.vollzanbel.dto.MagicItemDetailResponse
import org.shadownova.vollzanbel.dto.MagicItemListResponse
import org.shadownova.vollzanbel.dto.toWeaponEntity
import org.shadownova.vollzanbel.repository.Spell
import org.shadownova.vollzanbel.dto.SpellDetailResponse
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import kotlin.jvm.java

data class ApiReference(val index: String, val name: String, val url: String)
data class ApiReferenceList(val count: Int = 0, val results: List<ApiReference> = emptyList())

@Service
class Dnd5eApiClient(
    private val restClient: RestClient
) {
    fun getReferenceDetail(resource: String, index: String): String = restClient.get()
        .uri("https://www.dnd5eapi.co/api/2014/$resource/$index")
        .accept(MediaType.APPLICATION_JSON).retrieve().body(String::class.java)!!

    /** Fetch a compact reference list from one of the 2014 catalog endpoints. */
    fun getReferenceList(resource: String): List<ApiReference> = restClient
        .get().uri("https://www.dnd5eapi.co/api/2014/$resource")
        .accept(MediaType.APPLICATION_JSON).retrieve()
        .body(ApiReferenceList::class.java)?.results.orEmpty()

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

    fun getClassFeatureList(): ClassFeatureListResponse = restClient
        .get().uri("https://www.dnd5eapi.co/api/2014/features")
        .accept(MediaType.APPLICATION_JSON).retrieve()
        .body(ClassFeatureListResponse::class.java)!!

    fun getClassFeature(index: String) = restClient
        .get().uri("https://www.dnd5eapi.co/api/2014/features/$index")
        .accept(MediaType.APPLICATION_JSON).retrieve()
        .body(ClassFeatureDetailResponse::class.java)!!.toEntity()

    fun getSpeciesTraitList(): SpeciesTraitListResponse = restClient
        .get().uri("https://www.dnd5eapi.co/api/2014/traits")
        .accept(MediaType.APPLICATION_JSON).retrieve()
        .body(SpeciesTraitListResponse::class.java)!!

    fun getSpeciesTrait(index: String) = restClient
        .get().uri("https://www.dnd5eapi.co/api/2014/traits/$index")
        .accept(MediaType.APPLICATION_JSON).retrieve()
        .body(SpeciesTraitDetailResponse::class.java)!!.toEntity()

    fun getRaceList(): RaceListResponse = restClient
        .get().uri("https://www.dnd5eapi.co/api/2014/races")
        .accept(MediaType.APPLICATION_JSON).retrieve()
        .body(RaceListResponse::class.java)!!

    fun getRace(index: String) = restClient
        .get().uri("https://www.dnd5eapi.co/api/2014/races/$index")
        .accept(MediaType.APPLICATION_JSON).retrieve()
        .body(RaceDetailResponse::class.java)!!.toEntity()

    fun getBackgroundList(): BackgroundListResponse = restClient
        .get().uri("https://www.dnd5eapi.co/api/2014/backgrounds")
        .accept(MediaType.APPLICATION_JSON).retrieve()
        .body(BackgroundListResponse::class.java)!!

    fun getBackground(index: String) = restClient
        .get().uri("https://www.dnd5eapi.co/api/2014/backgrounds/$index")
        .accept(MediaType.APPLICATION_JSON).retrieve()
        .body(BackgroundDetailResponse::class.java)!!.toEntity()

    fun getWeaponCategory(): WeaponCategoryResponse = restClient
        .get().uri("https://www.dnd5eapi.co/api/2014/equipment-categories/weapon")
        .accept(MediaType.APPLICATION_JSON).retrieve()
        .body(WeaponCategoryResponse::class.java)!!

    fun getWeapon(index: String) = restClient
        .get().uri("https://www.dnd5eapi.co/api/2014/equipment/$index")
        .accept(MediaType.APPLICATION_JSON).retrieve()
        .body(WeaponDetailResponse::class.java)!!.toEntity()

    fun getMagicItemList(): MagicItemListResponse = restClient
        .get().uri("https://www.dnd5eapi.co/api/2014/magic-items")
        .accept(MediaType.APPLICATION_JSON).retrieve()
        .body(MagicItemListResponse::class.java)!!

    fun getMagicItem(index: String) = restClient
        .get().uri("https://www.dnd5eapi.co/api/2014/magic-items/$index")
        .accept(MediaType.APPLICATION_JSON).retrieve()
        .body(MagicItemDetailResponse::class.java)!!.let { detail ->
            detail.takeIf { it.equipmentCategory?.index == "weapon" }?.toWeaponEntity()
        }

    fun getEquipmentCategoryList(): EquipmentCategoryListResponse = restClient
        .get().uri("https://www.dnd5eapi.co/api/2014/equipment-categories")
        .accept(MediaType.APPLICATION_JSON).retrieve()
        .body(EquipmentCategoryListResponse::class.java)!!
}
