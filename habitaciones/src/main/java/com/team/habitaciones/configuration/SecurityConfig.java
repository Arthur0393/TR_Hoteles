package com.team.habitaciones.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http,
            @Value("${RESERVAS_HABITACIONES_INTERNAL_TOKEN:}") String internalToken) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(exchange -> exchange
                        .requestMatchers(HttpMethod.GET, "/**").hasAnyRole("ADMIN", "USER")
                        // El JWT identifica al usuario; la credencial adicional identifica a Reservas.
                        .requestMatchers(HttpMethod.PUT, "/*/ocupar", "/*/liberar")
                            .access((authentication, context) -> {
                                String supplied = context.getRequest().getHeader("X-Reservas-Internal-Token");
                                boolean trustedService = !internalToken.isBlank() && supplied != null
                                        && MessageDigest.isEqual(internalToken.getBytes(StandardCharsets.UTF_8),
                                                supplied.getBytes(StandardCharsets.UTF_8));
                                boolean allowedRole = authentication.get().getAuthorities().stream()
                                        .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN")
                                                || role.getAuthority().equals("ROLE_USER"));
                                return new AuthorizationDecision(trustedService && allowedRole);
                            })
                        .anyRequest().hasRole("ADMIN")
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
        return http.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authorities = new JwtGrantedAuthoritiesConverter();
        authorities.setAuthoritiesClaimName("roles");
        authorities.setAuthorityPrefix("");
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authorities);
        return converter;
    }
}
