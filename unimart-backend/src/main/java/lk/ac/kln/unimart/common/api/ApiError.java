package lk.ac.kln.unimart.common.api;

import java.time.Instant;
import java.util.Map;

/**
 * Predictable, Problem Details-style error payload used by the global
 * exception handler. See Guide 03, section 6.
 */
public record ApiError(
        String code,
        String message,
        String path,
        Instant timestamp,
        Map<String, String> fieldErrors) {
}
