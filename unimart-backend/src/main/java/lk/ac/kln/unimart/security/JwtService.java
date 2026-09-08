package lk.ac.kln.unimart.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

/**
 * Mints short-lived HS256 access tokens. Password authentication happens at
 * login (in the auth feature, added in the CRUD/integration lab); this
 * service only signs the resulting claims. See Guide 03 section 7 and
 * Guide 05 section 6.
 */
@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final long accessMinutes;

    public JwtService(JwtEncoder jwtEncoder,
                       @Value("${app.security.access-minutes:15}") long accessMinutes) {
        this.jwtEncoder = jwtEncoder;
        this.accessMinutes = accessMinutes;
    }

    /**
     * @param subject typically the user id as a string
     * @param role    the user's role, added as a custom claim
     */
    public String generateAccessToken(String subject, String role) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("unimart-backend")
                .issuedAt(now)
                .expiresAt(now.plus(accessMinutes, ChronoUnit.MINUTES))
                .subject(subject)
                .claim("role", role)
                .build();

        JwsHeader header = JwsHeader.with(org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS256).build();

        Jwt jwt = jwtEncoder.encode(JwtEncoderParameters.from(header, claims));
        return jwt.getTokenValue();
    }
}
