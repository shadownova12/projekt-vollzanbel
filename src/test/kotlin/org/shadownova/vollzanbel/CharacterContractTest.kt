package org.shadownova.vollzanbel

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.shadownova.vollzanbel.dto.*
import org.shadownova.vollzanbel.service.*

class CharacterContractTest {
    private val mapper = jacksonObjectMapper()

    @Test fun `wire fixture survives typed storage without changing field names or values`() {
        val original = mapper.readTree(javaClass.getResourceAsStream("/character-sheet.json"))
        val sheet = mapper.treeToValue(original, CharacterSheet::class.java)
        val codec = CharacterSnapshotCodec(mapper)
        val snapshot = CharacterSnapshot(sheet, "spark_gold")
        assertEquals(snapshot, codec.decode(codec.encode(snapshot)))
        val encoded = mapper.valueToTree<JsonNode>(codec.decode(codec.encode(snapshot)).characterSheet)
        assertSubset(original, encoded)
    }

    @Test fun `malformed nested sheet is rejected`() {
        val original = mapper.readTree(javaClass.getResourceAsStream("/character-sheet.json"))
        (original as com.fasterxml.jackson.databind.node.ObjectNode).put("stats", "not stats")
        assertThrows(Exception::class.java) { mapper.treeToValue(original, CharacterSheet::class.java) }
    }

    @Test fun `future storage versions cannot silently lose fields`() {
        val sheet = mapper.readValue(javaClass.getResourceAsStream("/character-sheet.json"), CharacterSheet::class.java)
        val codec = CharacterSnapshotCodec(mapper)
        assertThrows(IllegalArgumentException::class.java) { codec.decode(codec.encode(CharacterSnapshot(sheet, version = 2))) }
    }

    private fun assertSubset(expected: JsonNode, actual: JsonNode?) {
        assertNotNull(actual)
        when {
            expected.isObject -> expected.fields().forEachRemaining { (key, value) -> assertSubset(value, actual!!.get(key)) }
            expected.isArray -> { assertEquals(expected.size(), actual!!.size()); expected.forEachIndexed { i, value -> assertSubset(value, actual[i]) } }
            else -> assertEquals(expected, actual)
        }
    }
}
