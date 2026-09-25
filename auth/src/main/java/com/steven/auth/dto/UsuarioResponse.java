package com.steven.auth.dto;

import java.util.Set;
import com.steven.auth.entities.EstadoRegistro;

public record UsuarioResponse(
        Long idUsuario,
        String username,
        Set<String> roles,
        EstadoRegistro estadoRegistro
) {
}
