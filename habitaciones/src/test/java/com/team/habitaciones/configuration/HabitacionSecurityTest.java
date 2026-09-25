package com.team.habitaciones.configuration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitConfig(HabitacionSecurityTest.Config.class)
@WebAppConfiguration
@TestPropertySource(properties = "RESERVAS_HABITACIONES_INTERNAL_TOKEN=test-only-internal-token")
class HabitacionSecurityTest {
    @Autowired WebApplicationContext context;
    MockMvc mvc;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    @ParameterizedTest
    @CsvSource({"POST,/", "PUT,/1", "PUT,/1/estado/3", "PATCH,/1", "DELETE,/1"})
    void userCannotAdministerRoomsEvenWithoutGateway(String method, String path) throws Exception {
        mvc.perform(request(HttpMethod.valueOf(method), path).header("Authorization", "Bearer USER"))
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @CsvSource({"POST,/", "PUT,/1", "PUT,/1/estado/3", "DELETE,/1"})
    void adminCanAdministerRooms(String method, String path) throws Exception {
        mvc.perform(request(HttpMethod.valueOf(method), path).header("Authorization", "Bearer ADMIN"))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @CsvSource({"USER,/", "ADMIN,/", "USER,/1", "USER,/id-habitacion/1"})
    void bothRolesCanReadRooms(String role, String path) throws Exception {
        mvc.perform(request(HttpMethod.GET, path).header("Authorization", "Bearer " + role))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @CsvSource({"USER,ocupar", "USER,liberar", "ADMIN,ocupar", "ADMIN,liberar"})
    void internalOperationsRequireServiceCredentialAsWellAsJwt(String role, String operation) throws Exception {
        String path = "/1/" + operation;
        mvc.perform(request(HttpMethod.PUT, path).header("Authorization", "Bearer " + role))
                .andExpect(status().isForbidden());
        mvc.perform(request(HttpMethod.PUT, path).header("Authorization", "Bearer " + role)
                .header("X-Reservas-Internal-Token", "wrong"))
                .andExpect(status().isForbidden());
        mvc.perform(request(HttpMethod.PUT, path).header("Authorization", "Bearer " + role)
                .header("X-Reservas-Internal-Token", "test-only-internal-token"))
                .andExpect(status().isOk());
    }

    @Test
    void serviceCredentialDoesNotReplaceUserAuthentication() throws Exception {
        mvc.perform(request(HttpMethod.PUT, "/1/ocupar")
                .header("X-Reservas-Internal-Token", "test-only-internal-token"))
                .andExpect(status().isUnauthorized());
        mvc.perform(request(HttpMethod.GET, "/")).andExpect(status().isUnauthorized());
    }

    @Configuration
    @EnableWebMvc
    @EnableWebSecurity
    @Import({SecurityConfig.class, Endpoints.class})
    static class Config {
        @Bean JwtDecoder jwtDecoder() {
            // Solo sustituye la firma; ejercita la extracción real del claim roles.
            return token -> Jwt.withTokenValue(token).header("alg", "RS256")
                    .subject("test").claim("roles", List.of("ROLE_" + token)).build();
        }
    }

    @RestController
    static class Endpoints {
        @RequestMapping({"/", "/{id}", "/{id}/estado/{estado}", "/{id}/ocupar", "/{id}/liberar", "/id-habitacion/{id}"})
        String ok() { return "ok"; }
    }
}
