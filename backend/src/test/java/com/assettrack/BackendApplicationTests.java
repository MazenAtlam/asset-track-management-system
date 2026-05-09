package com.assettrack;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration smoke test — verifies the full Spring application context
 * loads successfully against the real PostgreSQL database configured in
 * src/main/resources/application.properties.
 *
 * <p>Requires a running PostgreSQL instance at the URL defined in
 * application.properties (jdbc:postgresql://localhost:5432/assettrack-db).
 * Hibernate will auto-create/update the schema on startup (ddl-auto=update).
 */
@SpringBootTest
class BackendApplicationTests {

    /**
     * Confirms the Spring context starts without errors.
     * All beans (JPA repositories, security config, scheduled jobs, etc.)
     * must wire up successfully against the real database.
     */
    @Test
    void contextLoads() {
        // If the context fails to start (bad config, missing DB, etc.)
        // Spring will throw and this test will fail with a clear error message.
    }
}
