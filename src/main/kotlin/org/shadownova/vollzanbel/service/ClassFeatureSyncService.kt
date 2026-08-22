package org.shadownova.vollzanbel.service

import org.shadownova.vollzanbel.client.Dnd5eApiClient
import org.shadownova.vollzanbel.repository.ClassFeatureRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.jdbc.core.JdbcTemplate

@Service
class ClassFeatureSyncService(
    private val repository: ClassFeatureRepository,
    private val apiClient: Dnd5eApiClient,
    private val jdbcTemplate: JdbcTemplate,
) {
    @Transactional
    fun syncClassFeatures() {
        migrateDescriptionFromPostgresLargeObjectIfNeeded()
        backfillApiSource()
        val hasLegacyDescriptions = repository.hasLegacyLargeObjectDescriptions()
        if (repository.count() == 0L || hasLegacyDescriptions) {
            if (hasLegacyDescriptions) log.info("Legacy large-object IDs found in class feature descriptions; re-importing text")
            importAllClassFeatures()
        }
        else log.info("Class feature table already contains rows; skipping initial import")
    }

    @Transactional
    fun forceSyncClassFeatures() {
        migrateDescriptionFromPostgresLargeObjectIfNeeded()
        backfillApiSource()
        importAllClassFeatures()
    }

    private fun importAllClassFeatures() {
        val refs = apiClient.getClassFeatureList()
        val features = refs.results.mapNotNull { ref ->
            try { apiClient.getClassFeature(ref.index) }
            catch (e: Exception) {
                log.error("Failed to fetch class feature {} at {}", ref.index, ref.url, e)
                null
            }
        }
        repository.saveAll(features)
        log.info("Saved {} class features", features.size)
    }

    /** Older versions used @Lob, which made PostgreSQL create an OID column. */
    private fun migrateDescriptionFromPostgresLargeObjectIfNeeded() {
        val dollar = '$'
        jdbcTemplate.execute(
            """
            DO ${dollar}${dollar}
            BEGIN
                IF EXISTS (
                    SELECT 1 FROM pg_attribute a
                    JOIN pg_class c ON c.oid = a.attrelid
                    WHERE c.relname = 'class_feature'
                      AND a.attname = 'description'
                      AND a.atttypid = 'oid'::regtype
                ) THEN
                    ALTER TABLE class_feature
                    ALTER COLUMN description TYPE TEXT
                    USING convert_from(lo_get(description), 'UTF8');
                END IF;
            END
            ${dollar}${dollar};
            """.trimIndent()
        )
    }

    private fun backfillApiSource() {
        jdbcTemplate.update(
            "UPDATE class_feature SET source = ? WHERE source IS NULL OR source = ''",
            API_SOURCE,
        )
    }

    private val log = LoggerFactory.getLogger(ClassFeatureSyncService::class.java)

    private companion object {
        const val API_SOURCE = "D&D 5e SRD API"
    }
}
