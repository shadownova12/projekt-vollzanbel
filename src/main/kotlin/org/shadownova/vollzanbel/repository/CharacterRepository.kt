package org.shadownova.vollzanbel.repository

import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

data class CharacterRow(val compressedData: ByteArray?)

@Repository
class CharacterRepository(private val jdbc: JdbcTemplate) {

	fun listNames(deviceId: String): List<String> {
		val rows = jdbc.queryForList("SELECT name FROM characters WHERE device_id = ? ORDER BY name", deviceId)
		return rows.mapNotNull { it["name"]?.toString() }
	}

	fun findByDeviceIdAndName(deviceId: String, name: String): CharacterRow? {
		val sql = "SELECT data FROM characters WHERE device_id = ? AND name = ?"
		return try {
			val row = jdbc.queryForMap(sql, deviceId, name)
			val compressed = row["data"] as? ByteArray
			CharacterRow(compressed)
		} catch (_: EmptyResultDataAccessException) {
			null
		}
	}

	fun save(deviceId: String, name: String, compressed: ByteArray) {
		val sql = """
			INSERT INTO characters (device_id, name, data)
			VALUES (?, ?, ?)
			ON CONFLICT (device_id, name)
			DO UPDATE SET data = EXCLUDED.data
		""".trimIndent()

		jdbc.update(sql, deviceId, name, compressed)
	}

	fun delete(deviceId: String, name: String) {
		jdbc.update("DELETE FROM characters WHERE device_id = ? AND name = ?", deviceId, name)
	}
}
