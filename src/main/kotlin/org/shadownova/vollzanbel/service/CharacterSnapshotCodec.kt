package org.shadownova.vollzanbel.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.shadownova.vollzanbel.dto.CharacterSheet
import org.springframework.stereotype.Component
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

/** Storage format is private to the API; HTTP always exchanges typed JSON. */
data class CharacterSnapshot(
    val characterSheet: CharacterSheet,
    val avatarId: String = "spark_violet",
    val version: Int = 1,
)

@Component
class CharacterSnapshotCodec(private val mapper: ObjectMapper) {
    fun encode(snapshot: CharacterSnapshot): ByteArray = ByteArrayOutputStream().use { output ->
        GZIPOutputStream(output).use { it.write(mapper.writeValueAsBytes(snapshot)) }
        output.toByteArray()
    }

    fun decode(bytes: ByteArray): CharacterSnapshot {
        val json = GZIPInputStream(ByteArrayInputStream(bytes)).use { mapper.readTree(it) }
        // Before version 1 the compressed JSON contained the sheet directly.
        if (!json.has("characterSheet")) {
            return CharacterSnapshot(mapper.treeToValue(json, CharacterSheet::class.java))
        }
        val snapshot = mapper.treeToValue(json, CharacterSnapshot::class.java)
        require(snapshot.version == 1) { "Unsupported character snapshot version: ${snapshot.version}" }
        return snapshot
    }
}
