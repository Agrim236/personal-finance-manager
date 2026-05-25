package com.example.skye.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Removes legacy transaction columns left from the old schema
 * (category/type strings) so inserts use category_id instead.
 */
@Component
@Order(0)
@Profile("mysql")
public class SchemaMigrationRunner implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public SchemaMigrationRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        dropColumnIfExists("transactions", "category");
        dropColumnIfExists("transactions", "type");
    }

    private void dropColumnIfExists(String table, String column) {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*) FROM information_schema.columns
                WHERE table_schema = DATABASE()
                  AND table_name = ?
                  AND column_name = ?
                """,
                Integer.class,
                table,
                column);

        if (count != null && count > 0) {
            jdbcTemplate.execute("ALTER TABLE " + table + " DROP COLUMN " + column);
        }
    }
}
