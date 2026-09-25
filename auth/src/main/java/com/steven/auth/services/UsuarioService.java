package com.steven.auth.services;

import com.steven.auth.dto.UsuarioRequest;
import com.steven.auth.dto.UsuarioResponse;

import java.util.Set;

public interface UsuarioService {

    Set<UsuarioResponse> listar();

    UsuarioResponse registrar(UsuarioRequest request);

    UsuarioResponse obtenerPorId(Long id);

    UsuarioResponse actualizar(Long id, UsuarioRequest request);

    UsuarioResponse eliminar(Long id);
}
