package com.steven.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

import java.util.Set;

public record UsuarioRequest(
        @NotBlank(message = "El username es requerido")
        @Size(min = 5, max = 20)
        @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Formato de username inválido")
        String username,

        @NotBlank(groups = Creacion.class, message = "La contraseña es requerida")
        @Size(min = 8, max = 20)
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "La contraseña debe contener letras y números")
        String password,

        @NotNull(message = "Los roles son requeridos")
        @Size(min = 1, message = "Debe haber al menos 1 rol")
        Set<@NotBlank @Pattern(regexp = "ROLE_ADMIN|ROLE_USER", message = "Rol inválido") String> roles
) {
    public interface Creacion {}

    public UsuarioRequest {
        if (username != null) username = username.trim();
        // En PUT, omitir la contraseña o enviarla vacía conserva el hash existente.
        if ("".equals(password)) password = null;
    }
}
