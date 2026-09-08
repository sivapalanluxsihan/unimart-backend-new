package lk.ac.kln.unimart.common.api;

import java.time.Instant;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Unauthenticated smoke-test endpoint. A 200 response here proves the web
 * layer is running before database/security configuration is exercised.
 * See Guide 03, section 5.
 */
@RestController
@RequestMapping("/api/v1/public")
public class PublicController {

    @GetMapping("/ping")
    public Map<String, Object> ping() {
        return Map.of(
                "service", "unimart-backend",
                "status", "UP",
                "time", Instant.now());
    }
}
