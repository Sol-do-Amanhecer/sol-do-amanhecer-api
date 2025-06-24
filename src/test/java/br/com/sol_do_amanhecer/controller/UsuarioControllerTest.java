package br.com.sol_do_amanhecer.controller;

import br.com.sol_do_amanhecer.model.dto.PermissaoDTO;
import br.com.sol_do_amanhecer.model.dto.UsuarioDTO;
import br.com.sol_do_amanhecer.service.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de UsuarioController")
public class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    private UsuarioDTO criaUsuarioDTO() {
        return UsuarioDTO.builder()
                .usuario("usuarioTeste")
                .senha("senhaTeste")
                .contaExpirada(false)
                .contaBloqueada(false)
                .credenciaisExpiradas(false)
                .ativo(true)
                .permissaoDTOList(Collections.singletonList(new PermissaoDTO()))
                .uuidVoluntario(UUID.randomUUID())
                .build();
    }

    @Nested
    @DisplayName("GET /usuario/{id}")
    class BuscarPorId {

        @Test
        @DisplayName("Deve retornar usuário pelo ID com sucesso")
        void buscarPorId_Sucesso() {
            UUID id = UUID.randomUUID();
            UsuarioDTO dto = criaUsuarioDTO();
            when(usuarioService.buscarPorId(id)).thenReturn(dto);

            ResponseEntity<UsuarioDTO> response = usuarioController.buscarPorId(id);

            assertEquals(200, response.getStatusCode().value());
            assertEquals(dto, response.getBody());
            verify(usuarioService, times(1)).buscarPorId(id);
        }
    }

    @Nested
    @DisplayName("GET /usuarios")
    class BuscarTodos {

        @Test
        @DisplayName("Deve retornar lista paginada de usuários sem filtro")
        void buscarTodos_SemFiltro() {
            Pageable pageable = PageRequest.of(0, 10);
            UsuarioDTO dto = criaUsuarioDTO();
            Page<UsuarioDTO> page = new PageImpl<>(List.of(dto), pageable, 1);

            when(usuarioService.buscarTodos(null, pageable)).thenReturn(page);

            ResponseEntity<Page<UsuarioDTO>> response = usuarioController.buscarTodos(0, 10, null);

            assertEquals(200, response.getStatusCode().value());
            assertNotNull(response.getBody());
            assertEquals(1, response.getBody().getTotalElements());
            assertEquals(dto, response.getBody().getContent().get(0));
            verify(usuarioService, times(1)).buscarTodos(null, pageable);
        }

        @Test
        @DisplayName("Deve retornar lista paginada de usuários ativos")
        void buscarTodos_ComFiltroAtivo() {
            Pageable pageable = PageRequest.of(0, 10);
            UsuarioDTO dto = criaUsuarioDTO();
            Page<UsuarioDTO> page = new PageImpl<>(List.of(dto), pageable, 1);

            when(usuarioService.buscarTodos(true, pageable)).thenReturn(page);

            ResponseEntity<Page<UsuarioDTO>> response = usuarioController.buscarTodos(0, 10, true);

            assertEquals(200, response.getStatusCode().value());
            assertNotNull(response.getBody());
            assertEquals(1, response.getBody().getTotalElements());
            assertEquals(dto, response.getBody().getContent().get(0));
            verify(usuarioService, times(1)).buscarTodos(true, pageable);
        }

        @Test
        @DisplayName("Deve retornar lista paginada de usuários inativos")
        void buscarTodos_ComFiltroInativo() {
            Pageable pageable = PageRequest.of(0, 10);
            UsuarioDTO dto = criaUsuarioDTO();
            dto.setAtivo(false);
            Page<UsuarioDTO> page = new PageImpl<>(List.of(dto), pageable, 1);

            when(usuarioService.buscarTodos(false, pageable)).thenReturn(page);

            ResponseEntity<Page<UsuarioDTO>> response = usuarioController.buscarTodos(0, 10, false);

            assertEquals(200, response.getStatusCode().value());
            assertNotNull(response.getBody());
            assertEquals(1, response.getBody().getTotalElements());
            assertEquals(dto, response.getBody().getContent().get(0));
            verify(usuarioService, times(1)).buscarTodos(false, pageable);
        }
    }

    @Nested
    @DisplayName("POST /usuario")
    class CriarUsuario {

        @Test
        @DisplayName("Deve criar e retornar novo usuário")
        void criar_Sucesso() {
            UsuarioDTO input = criaUsuarioDTO();
            when(usuarioService.criar(input)).thenReturn(input);

            ResponseEntity<UsuarioDTO> response = usuarioController.criar(input);

            assertEquals(200, response.getStatusCode().value());
            assertEquals(input, response.getBody());
            verify(usuarioService, times(1)).criar(input);
        }
    }

    @Nested
    @DisplayName("PUT /usuario/{id}")
    class AtualizarUsuario {

        @Test
        @DisplayName("Deve atualizar usuário e retornar OK")
        void atualizar_Sucesso() {
            UUID id = UUID.randomUUID();
            UsuarioDTO input = criaUsuarioDTO();

            doNothing().when(usuarioService).atualizar(id, input);

            ResponseEntity<Void> response = usuarioController.atualizar(id, input);

            assertEquals(200, response.getStatusCode().value());
            assertNull(response.getBody());
            verify(usuarioService, times(1)).atualizar(id, input);
        }
    }

    @Nested
    @DisplayName("DELETE /usuario/{id}")
    class DeletarUsuario {

        @Test
        @DisplayName("Deve deletar usuário e retornar No Content")
        void deletar_Sucesso() {
            UUID id = UUID.randomUUID();
            doNothing().when(usuarioService).remover(id);

            ResponseEntity<Void> response = usuarioController.deletar(id);

            assertEquals(204, response.getStatusCode().value());
            assertNull(response.getBody());
            verify(usuarioService, times(1)).remover(id);
        }
    }
}