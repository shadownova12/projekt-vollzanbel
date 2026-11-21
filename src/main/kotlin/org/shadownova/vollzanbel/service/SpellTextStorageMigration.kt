package org.shadownova.vollzanbel.service

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

/** Converts spell text columns left as PostgreSQL large-object references by older @Lob mappings. */
@Component
class SpellTextStorageMigration(private val jdbcTemplate: JdbcTemplate) {
    @Volatile
    private var migrationChecked = false

    @Synchronized
    fun migrateLegacyLargeObjectsIfNeeded() {
        if (migrationChecked) return

        val databaseName = jdbcTemplate.dataSource?.connection?.use { it.metaData.databaseProductName }
        if (databaseName != "PostgreSQL") {
            migrationChecked = true
            return
        }

        val dollar = '$'
        jdbcTemplate.execute(
            """
            DO ${dollar}${dollar}
            DECLARE
                description_type regtype;
                higher_level_type regtype;
            BEGIN
                SELECT a.atttypid::regtype INTO description_type
                FROM pg_attribute a
                JOIN pg_class c ON c.oid = a.attrelid
                JOIN pg_namespace n ON n.oid = c.relnamespace
                WHERE n.nspname = current_schema()
                  AND c.relname = 'spell'
                  AND a.attname = 'description'
                  AND NOT a.attisdropped;

                SELECT a.atttypid::regtype INTO higher_level_type
                FROM pg_attribute a
                JOIN pg_class c ON c.oid = a.attrelid
                JOIN pg_namespace n ON n.oid = c.relnamespace
                WHERE n.nspname = current_schema()
                  AND c.relname = 'spell'
                  AND a.attname = 'higher_level'
                  AND NOT a.attisdropped;

                IF description_type = 'oid'::regtype THEN
                    ALTER TABLE spell ALTER COLUMN description TYPE TEXT
                    USING convert_from(lo_get(description), 'UTF8');
                ELSIF description_type = 'text'::regtype THEN
                    UPDATE spell s
                    SET description = convert_from(lo_get(s.description::oid), 'UTF8')
                    WHERE s.description ~ '^[0-9]+${'$'}'
                      AND EXISTS (
                          SELECT 1 FROM pg_largeobject_metadata m
                          WHERE m.oid = s.description::oid
                      );
                END IF;

                IF higher_level_type = 'oid'::regtype THEN
                    ALTER TABLE spell ALTER COLUMN higher_level TYPE TEXT
                    USING convert_from(lo_get(higher_level), 'UTF8');
                ELSIF higher_level_type = 'text'::regtype THEN
                    UPDATE spell s
                    SET higher_level = convert_from(lo_get(s.higher_level::oid), 'UTF8')
                    WHERE s.higher_level ~ '^[0-9]+${'$'}'
                      AND EXISTS (
                          SELECT 1 FROM pg_largeobject_metadata m
                          WHERE m.oid = s.higher_level::oid
                      );
                END IF;
            END
            ${dollar}${dollar};
            """.trimIndent()
        )
        migrationChecked = true
    }
}
