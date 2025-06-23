package br.com.sol_do_amanhecer.controller;

import br.com.sol_do_amanhecer.model.dto.UsuarioDTO;
import br.com.sol_do_amanhecer.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve buscar um usuário por ID e retornar o DTO correto")
    void buscarPorId_DeveRetornarUsuarioDTO() {
        UUID id = UUID.randomUUID();
        UsuarioDTO usuarioDTO = new UsuarioDTO();

        when(usuarioService.buscarPorId(id)).thenReturn(usuarioDTO);

        ResponseEntity<UsuarioDTO> response = usuarioController.buscarPorId(id);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(usuarioDTO, response.getBody());
        verify(usuarioService, times(1)).buscarPorId(id);
    }

    @Test
    @DisplayName("Deve buscar todos os usuários com paginação")
    void buscarTodos_DeveRetornarPaginaDeUsuarios() {
        int page = 0, size = 2;
        Boolean ativo = true;
        List<UsuarioDTO> usuarios = Arrays.asList(new UsuarioDTO(), new UsuarioDTO());
        Pageable pageable = PageRequest.of(page, size);
        Page<UsuarioDTO> usuariosPage = new PageImpl<>(usuarios, pageable, usuarios.size());

        when(usuarioService.buscarTodos(ativo, pageable)).thenReturn(usuariosPage);

        ResponseEntity<Page<UsuarioDTO>> response = usuarioController.buscarTodos(page, size, ativo);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(2, response.getBody().getContent().size());
        verify(usuarioService, times(1)).buscarTodos(ativo, pageable);
    }

    @Test
    @DisplayName("Deve criar um novo usuário e retornar o DTO criado")
    void criar_DeveRetornarUsuarioCriado() {
        UsuarioDTO usuarioDTO = new UsuarioDTO();
        UsuarioDTO usuarioCriado = new UsuarioDTO();

        when(usuarioService.criar(usuarioDTO)).thenReturn(usuarioCriado);

        ResponseEntity<UsuarioDTO> response = usuarioController.criar(usuarioDTO);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(usuarioCriado, response.getBody());
        verify(usuarioService, times(1)).criar(usuarioDTO);
    }

    @Test
    @DisplayName("Deve atualizar um usuário existente")
    void atualizar_DeveAtualizarUsuario() {
        UUID id = UUID.randomUUID();
        UsuarioDTO usuarioDTO = new UsuarioDTO();

        doNothing().when(usuarioService).atualizar(id, usuarioDTO);

        ResponseEntity<Void> response = usuarioController.atualizar(id, usuarioDTO);

        assertEquals(200, response.getStatusCode().value());
        verify(usuarioService, times(1)).atualizar(id, usuarioDTO);
    }

    @Test
    @DisplayName("Deve deletar um usuário pelo ID")
    void deletar_DeveRemoverUsuario() {
        UUID id = UUID.randomUUID();

        doNothing().when(usuarioService).remover(id);

        ResponseEntity<Void> response = usuarioController.deletar(id);

        assertEquals(204, response.getStatusCode().value());
        verify(usuarioService, times(1)).remover(id);
    }
}