package com.steven.auth;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.steven.auth.dto.LoginRequest;
import com.steven.auth.services.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import static org.junit.jupiter.api.Assertions.*;

// Requiere DbAuth iniciado; usa los usuarios de demostración del entorno local.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthApplicationTests {
    @LocalServerPort
    int port;

    @Autowired
    AuthService authService;

    @Autowired
    JwtDecoder jwtDecoder;

    @Test
    void loginProducesSignedTokenWithGatewayRoles() throws Exception {
        var token = authService.autenticar(new LoginRequest("admin", "admin")).token();
        var jwt = jwtDecoder.decode(token);
        assertEquals("admin", jwt.getSubject());
        assertTrue(jwt.getClaimAsStringList("roles").contains("ROLE_ADMIN"));
        assertEquals(200, get("/oauth2/jwks", null));
    }

    @Test
    void invalidCredentialsReturnUnauthorized() throws Exception {
        for (String username : new String[]{"admin", "missing-user"}) {
            var request = HttpRequest.newBuilder(URI.create(base() + "/api/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            "{\"username\":\"" + username + "\",\"password\":\"wrong\"}"))
                    .build();
            var client = HttpClient.newHttpClient();
            assertEquals(401, client.send(request, HttpResponse.BodyHandlers.discarding()).statusCode());
        }
    }

    @Test
    void onlyAdminCanListUsers() throws Exception {
        assertEquals(401, get("/admin/usuarios", null));
        var user = authService.autenticar(new LoginRequest("usuario", "usuario")).token();
        assertEquals(403, get("/admin/usuarios", user));
        var admin = authService.autenticar(new LoginRequest("admin", "admin")).token();
        assertEquals(200, get("/admin/usuarios", admin));
    }

    private String base() {
        return "http://localhost:" + port;
    }

    private int get(String path, String token) throws Exception {
        var request = HttpRequest.newBuilder(URI.create(base() + path));
        if (token != null) request.header("Authorization", "Bearer " + token);
        var client = HttpClient.newHttpClient();
        return client.send(request.GET().build(), HttpResponse.BodyHandlers.discarding()).statusCode();
    }
}
