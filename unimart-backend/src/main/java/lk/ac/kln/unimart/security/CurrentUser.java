package lk.ac.kln.unimart.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Small helper for services/controllers to read who is making the current
 * request. The JWT subject ("sub") claim is the caller's university email —
 * this matches Guide 07, which uses {@code authentication.getName()}
 * directly as the email for ownership checks (e.g. ListingService,
 * ReviewService).
 */
@Component
public class CurrentUser {

    /** @return the authenticated caller's university email, or null on public endpoints. */
    public String emailOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            return null;
        }
        return jwt.getSubject();
    }

    public String emailOrThrow() {
        String email = emailOrNull();
        if (email == null) {
            throw new IllegalStateException("No authenticated user in the current security context");
        }
        return email;
    }

    public String roleOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            return null;
        }
        return jwt.getClaimAsString("role");
    }
}
