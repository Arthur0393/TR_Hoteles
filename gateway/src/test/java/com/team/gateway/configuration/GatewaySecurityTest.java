package com.team.gateway.configuration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.config.EnableWebFlux;
import reactor.core.publisher.Mono;

import java.util.List;

@SpringJUnitConfig(GatewaySecurityTest.Config.class)
class GatewaySecurityTest {
    @Autowired ApplicationContext context;
    WebTestClient client;

    @BeforeEach
    void setup() { client = WebTestClient.bindToApplicationContext(context).build(); }

    @ParameterizedTest
    @CsvSource({"POST,/api/habitaciones", "POST,/api/habitaciones/", "PUT,/api/habitaciones/1",
            "PUT,/api/habitaciones/1/estado/3", "PATCH,/api/habitaciones/1", "DELETE,/api/habitaciones/1"})
    void userCannotAdministerRooms(String method, String path) {
        client.method(HttpMethod.valueOf(method)).uri(path).header("Authorization", "Bearer USER")
                .exchange().expectStatus().isForbidden();
    }

    @ParameterizedTest
    @CsvSource({"POST,/api/habitaciones", "PUT,/api/habitaciones/1",
            "PUT,/api/habitaciones/1/estado/3", "DELETE,/api/habitaciones/1"})
    void adminCanAdministerRooms(String method, String path) {
        client.method(HttpMethod.valueOf(method)).uri(path).header("Authorization", "Bearer ADMIN")
                .exchange().expectStatus().isOk();
    }

    @ParameterizedTest
    @CsvSource({"USER,ocupar", "USER,liberar", "ADMIN,ocupar", "ADMIN,liberar"})
    void noPublicAccessToInternalOperationsEvenWithSpoofedHeader(String role, String operation) {
        client.put().uri("/api/habitaciones/1/" + operation).header("Authorization", "Bearer " + role)
                .header("X-Reservas-Internal-Token", "test-only-internal-token")
                .exchange().expectStatus().isForbidden();
    }

    @ParameterizedTest
    @CsvSource({"GET,/api/habitaciones", "GET,/api/habitaciones/1", "POST,/api/reservas",
            "PUT,/api/reservas/1", "PATCH,/api/reservas/1/estado/2", "PATCH,/api/reservas/1/estado/3",
            "PATCH,/api/reservas/1/estado/4", "POST,/api/huespedes"})
    void userRetainsReceptionOperations(String method, String path) {
        client.method(HttpMethod.valueOf(method)).uri(path).header("Authorization", "Bearer USER")
                .exchange().expectStatus().isOk();
    }

    @Test
    void unauthenticatedRequestsAreRejected() {
        client.get().uri("/api/habitaciones").exchange().expectStatus().isUnauthorized();
        client.post().uri("/api/habitaciones").exchange().expectStatus().isUnauthorized();
    }

    @Configuration
    @EnableWebFlux
    @EnableWebFluxSecurity
    @Import({SecurityConfig.class, Endpoints.class})
    static class Config {
        @Bean ReactiveJwtDecoder jwtDecoder() {
            return token -> Mono.just(Jwt.withTokenValue(token).header("alg", "RS256")
                    .subject("test").claim("roles", List.of("ROLE_" + token)).build());
        }
    }

    @RestController
    static class Endpoints {
        @RequestMapping("/**") String ok() { return "ok"; }
    }
}
