package com.steven.auth.services;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.steven.auth.dto.UsuarioRequest;
import com.steven.auth.dto.UsuarioResponse;
import com.steven.auth.entities.Rol;
import com.steven.auth.entities.Usuario;
import com.steven.auth.entities.EstadoRegistro;
import com.steven.auth.mapper.UsuarioMapper;
import com.steven.auth.repositories.RolRepository;
import com.steven.auth.repositories.UsuarioRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    private final RolRepository rolRepository;

    private final UsuarioMapper usuarioMapper;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public Set<UsuarioResponse> listar() {
        log.info("Listado de usuarios activos solicitado");
        return usuarioRepository.findAllByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(usuarioMapper::entityToResponse).collect(Collectors.toSet());
    }

    @Override
    public UsuarioResponse registrar(UsuarioRequest request) {
        log.info("Buscando usuario {}", request.username());
        if (usuarioRepository.existsByUsernameAndEstadoRegistro(request.username(), EstadoRegistro.ACTIVO)) {
            throw new IllegalStateException("El usuario " + request.username() + " ya está registrado y activo");
        }

        Usuario usuario = usuarioMapper.requestToEntity(request,
                passwordEncoder.encode(request.password()), obtenerRoles(request.roles()));

        usuario = usuarioRepository.saveAndFlush(usuario);
        return usuarioMapper.entityToResponse(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse obtenerPorId(Long id) {
        return usuarioMapper.entityToResponse(obtenerActivo(id));
    }

    @Override
    public UsuarioResponse actualizar(Long id, UsuarioRequest request) {
        Usuario usuario = obtenerActivo(id);
        if (usuarioRepository.existsByUsernameAndEstadoRegistroAndIdNot(
                request.username(), EstadoRegistro.ACTIVO, id)) {
            throw new IllegalStateException("El usuario " + request.username() + " ya está registrado y activo");
        }
        Set<Rol> roles = obtenerRoles(request.roles());
        usuario.setUsername(request.username());
        usuario.setRoles(roles);
        if (request.password() != null) {
            usuario.setPassword(passwordEncoder.encode(request.password()));
        }
        return usuarioMapper.entityToResponse(usuarioRepository.saveAndFlush(usuario));
    }

    @Override
    public UsuarioResponse eliminar(Long id) {
        Usuario usuario = obtenerActivo(id);
        usuario.setEstadoRegistro(EstadoRegistro.ELIMINADO);
        return usuarioMapper.entityToResponse(usuarioRepository.saveAndFlush(usuario));
    }

    private Usuario obtenerActivo(Long id) {
        if (id == null || id <= 0) throw new IllegalArgumentException("El ID debe ser positivo");
        return usuarioRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO)
                .orElseThrow(() -> new NoSuchElementException("No se encontró el usuario activo: " + id));
    }

    private Set<Rol> obtenerRoles(Set<String> nombres) {
        return nombres.stream().map(nombre -> rolRepository.findByNombre(nombre)
                .orElseThrow(() -> new IllegalArgumentException("Rol " + nombre + " no válido")))
                .collect(Collectors.toSet());
    }
}
