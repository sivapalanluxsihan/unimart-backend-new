package lk.ac.kln.unimart;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Confirms the Spring context loads with the local profile. Requires the
 * DB_URL / DB_USERNAME / DB_PASSWORD / JWT_SECRET / APP_ALLOWED_ORIGINS
 * environment variables described in Guide 05 to be present, since Flyway
 * and the datasource are wired eagerly. Run `./mvnw clean test` from
 * IntelliJ's terminal (or the Maven tool window) after setting those
 * variables.
 */
@SpringBootTest
@ActiveProfiles("local")
class UniMartApplicationTests {

    @Test
    void contextLoads() {
    }
}
