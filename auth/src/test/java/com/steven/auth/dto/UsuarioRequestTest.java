package com.steven.auth.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.groups.Default;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioRequestTest {
    static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    static final Validator validator = factory.getValidator();
    @AfterAll static void close() { factory.close(); }

    @Test void createRequiresPasswordButUpdateMayOmitIt() {
        var request = new UsuarioRequest("pruebauser", "", Set.of("ROLE_USER"));
        assertTrue(validator.validate(request).isEmpty());
        assertFalse(validator.validate(request, Default.class, UsuarioRequest.Creacion.class).isEmpty());
    }

    @Test void updateValidatesEveryProvidedPassword() {
        for (String password : new String[]{"a", "sololetras", "12345678", "a1".repeat(11)}) {
            assertFalse(validator.validate(new UsuarioRequest("pruebauser", password, Set.of("ROLE_USER"))).isEmpty());
        }
        assertTrue(validator.validate(new UsuarioRequest("pruebauser", "Prueba123", Set.of("ROLE_USER"))).isEmpty());
    }

    @Test void usernameAndRolesRemainMandatoryOnUpdate() {
        for (String name : new String[]{"abcd", "", "x".repeat(21), "nombre malo"}) {
            assertFalse(validator.validate(new UsuarioRequest(name, null, Set.of("ROLE_USER"))).isEmpty());
        }
        assertFalse(validator.validate(new UsuarioRequest("pruebauser", null, Set.of())).isEmpty());
        assertFalse(validator.validate(new UsuarioRequest("pruebauser", null, Set.of("OTRO"))).isEmpty());
    }
}
