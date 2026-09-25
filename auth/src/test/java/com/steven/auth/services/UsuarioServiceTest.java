package com.steven.auth.services;

import com.steven.auth.dto.UsuarioRequest;
import com.steven.auth.entities.EstadoRegistro;
import com.steven.auth.entities.Rol;
import com.steven.auth.entities.Usuario;
import com.steven.auth.mapper.UsuarioMapper;
import com.steven.auth.repositories.RolRepository;
import com.steven.auth.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {
    UsuarioRepository repository;
    RolRepository roles;
    BCryptPasswordEncoder encoder;
    UsuarioServiceImpl service;
    Usuario usuario;
    Rol role;

    @BeforeEach
    void setup() {
        repository = mock(UsuarioRepository.class);
        roles = mock(RolRepository.class);
        encoder = new BCryptPasswordEncoder(4);
        service = new UsuarioServiceImpl(repository, roles, new UsuarioMapper(), encoder);
        role = new Rol(1L, "ROLE_USER");
        usuario = new Usuario();
        usuario.setId(7L);
        usuario.setUsername("pruebauser");
        usuario.setPassword(encoder.encode("Prueba123"));
        usuario.setRoles(Set.of(role));
        when(repository.findByIdAndEstadoRegistro(7L, EstadoRegistro.ACTIVO)).thenReturn(Optional.of(usuario));
        when(repository.saveAndFlush(any())).thenAnswer(i -> i.getArgument(0));
        when(roles.findByNombre("ROLE_USER")).thenReturn(Optional.of(role));
    }

    @Test
    void deletionPreservesIdHashAndRolesAndNeverCallsPhysicalDelete() {
        String hash = usuario.getPassword();
        var result = service.eliminar(7L);
        assertEquals(7L, result.idUsuario());
        assertEquals(EstadoRegistro.ELIMINADO, result.estadoRegistro());
        assertEquals(hash, usuario.getPassword());
        assertEquals(Set.of(role), usuario.getRoles());
        verify(repository).findByIdAndEstadoRegistro(7L, EstadoRegistro.ACTIVO);
        verify(repository).saveAndFlush(usuario);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void listsOnlyActiveUsers() {
        when(repository.findAllByEstadoRegistro(EstadoRegistro.ACTIVO)).thenReturn(List.of(usuario));
        assertEquals(7L, service.listar().iterator().next().idUsuario());
        verify(repository).findAllByEstadoRegistro(EstadoRegistro.ACTIVO);
        verify(repository, never()).findAll();
    }

    @Test
    void obtainsActiveUserByStableId() {
        assertEquals("pruebauser", service.obtenerPorId(7L).username());
    }

    @Test
    void missingOrDeletedUserCannotBeReadUpdatedOrDeleted() {
        when(repository.findByIdAndEstadoRegistro(7L, EstadoRegistro.ACTIVO)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> service.obtenerPorId(7L));
        assertThrows(NoSuchElementException.class, () -> service.eliminar(7L));
        assertThrows(NoSuchElementException.class, () -> service.actualizar(7L, request("nuevoUser", null)));
        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void updateRenamesByIdAndPreservesPasswordWhenEmpty() {
        String hash = usuario.getPassword();
        var result = service.actualizar(7L, request("nuevoUser", ""));
        assertEquals(7L, result.idUsuario());
        assertEquals("nuevoUser", result.username());
        assertEquals(hash, usuario.getPassword());
        verify(repository).existsByUsernameAndEstadoRegistroAndIdNot("nuevoUser", EstadoRegistro.ACTIVO, 7L);
    }

    @Test
    void updatesPasswordAsBcryptAndRoles() {
        Rol admin = new Rol(2L, "ROLE_ADMIN");
        when(roles.findByNombre("ROLE_ADMIN")).thenReturn(Optional.of(admin));
        var result = service.actualizar(7L, new UsuarioRequest("pruebauser", "Nueva1234", Set.of("ROLE_ADMIN")));
        assertTrue(encoder.matches("Nueva1234", usuario.getPassword()));
        assertFalse(encoder.matches("Prueba123", usuario.getPassword()));
        assertEquals(Set.of("ROLE_ADMIN"), result.roles());
    }

    @Test
    void duplicateActiveUsernameOnUpdateLeavesUserUnchanged() {
        when(repository.existsByUsernameAndEstadoRegistroAndIdNot("ocupado", EstadoRegistro.ACTIVO, 7L)).thenReturn(true);
        assertThrows(IllegalStateException.class, () -> service.actualizar(7L, request("ocupado", null)));
        assertEquals("pruebauser", usuario.getUsername());
        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void duplicateActiveUsernameOnCreateIsConflict() {
        when(repository.existsByUsernameAndEstadoRegistro("ocupado", EstadoRegistro.ACTIVO)).thenReturn(true);
        assertThrows(IllegalStateException.class, () -> service.registrar(request("ocupado", "Prueba123")));
        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void allowsUsernameOfDeletedUserAndCreatesNewActiveIdentity() {
        when(repository.existsByUsername("pruebauser")).thenReturn(true);
        var result = service.registrar(request("pruebauser", "Prueba123"));
        assertEquals(EstadoRegistro.ACTIVO, result.estadoRegistro());
        verify(repository).existsByUsernameAndEstadoRegistro("pruebauser", EstadoRegistro.ACTIVO);
        verify(repository, never()).existsByUsername(anyString());
    }

    @Test
    void loginLookupRejectsDeletedUsers() {
        var details = new CustomUserDetails(repository);
        assertThrows(UsernameNotFoundException.class, () -> details.loadUserByUsername("eliminado"));
        verify(repository).findByUsernameAndEstadoRegistro("eliminado", EstadoRegistro.ACTIVO);
    }

    @Test
    void loginLoadsOnlyActiveIdentityAfterUsernameReuse() {
        when(repository.findByUsernameAndEstadoRegistro("pruebauser", EstadoRegistro.ACTIVO)).thenReturn(Optional.of(usuario));
        var details = new CustomUserDetails(repository).loadUserByUsername("pruebauser");
        assertEquals(usuario.getPassword(), details.getPassword());
        assertEquals("ROLE_USER", details.getAuthorities().iterator().next().getAuthority());
    }

    private UsuarioRequest request(String name, String password) {
        return new UsuarioRequest(name, password, Set.of("ROLE_USER"));
    }
}
