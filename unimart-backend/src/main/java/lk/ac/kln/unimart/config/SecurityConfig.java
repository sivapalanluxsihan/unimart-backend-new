package lk.ac.kln.unimart.config;

import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Cross-cutting security for the monolith: stateless JWT bearer-token
 * authentication, CORS, and endpoint authorization rules.
 *
 * Public endpoints: registration, login, token refresh, public listing
 * reads and actuator health, per Guide 03 section 7. Protected endpoints:
 * listing/review write operations and user-specific actions. The JWT
 * secret/encoder/decoder wiring follows Guide 05 section 6.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecretKey jwtSecretKey(@Value("${app.security.jwt-secret}") String value) {
        byte[] decoded = Base64.getDecoder().decode(value);
        if (decoded.length < 32) {
            throw new IllegalStateException("JWT secret must be at least 32 bytes after Base64 decoding");
        }
        return new SecretKeySpec(decoded, "HmacSHA256");
    }

    @Bean
    JwtEncoder jwtEncoder(SecretKey key) {
        return NimbusJwtEncoder.withSecretKey(key).build();
    }

    @Bean
    JwtDecoder jwtDecoder(SecretKey key) {
        return NimbusJwtDecoder.withSecretKey(key).build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        // BCrypt per Guide 03 section 7 ("Passwords are hashed with BCrypt and never logged").
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // CORS preflight and OPTIONS requests
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Public: health, ping, auth endpoints, public listing/review reads.
                        // See Guide 07 Part F.
                        .requestMatchers("/api/v1/public/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/**").permitAll()
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/listings/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/reviews/**").permitAll()
                        // Everything else requires a valid bearer token.
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(401, "Unauthorized"))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                response.sendError(403, "Forbidden")));

        return http.build();
    }
}
