package br.com.sol_do_amanhecer.controller;

import br.com.sol_do_amanhecer.model.dto.PermissaoDTO;
import br.com.sol_do_amanhecer.model.dto.TrocarSenhaDTO;
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
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de UsuarioController")
public class UsuarioControllerTest {

    private static final int ONE_ELEMENT = 1;
    private static final int FIRST_ELEMENT_INDEX = 0;

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

            assertEquals(HttpStatus.OK.value(), response.getStatusCode().value(), "O status HTTP deve ser 200 OK");
            assertEquals(dto, response.getBody(), "O corpo deve conter o DTO do usuário buscado");
            verify(usuarioService).buscarPorId(id);
        }
    }

    @Nested
    @DisplayName("GET /usuarios")
    class BuscarTodos {

        @Test
        @DisplayName("Deve retornar lista paginada de usuários sem filtro")
        void buscarTodos_SemFiltro() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by("criadoEm").ascending());
            UsuarioDTO dto = criaUsuarioDTO();
            Page<UsuarioDTO> page = new PageImpl<>(List.of(dto), pageable, ONE_ELEMENT);

            when(usuarioService.buscarTodos(null, pageable)).thenReturn(page);

            ResponseEntity<Page<UsuarioDTO>> response = usuarioController.buscarTodos(0, 10, null);

            assertEquals(HttpStatus.OK.value(), response.getStatusCode().value(), "O status HTTP deve ser 200 OK");
            assertNotNull(response.getBody(), "O corpo da resposta não deve ser nulo");
            assertEquals(ONE_ELEMENT, response.getBody().getTotalElements(), "A página deve conter exatamente um elemento");
            assertEquals(dto, response.getBody().getContent().get(FIRST_ELEMENT_INDEX), "O elemento da página deve ser o DTO esperado");
            verify(usuarioService).buscarTodos(null, pageable);
        }

        @Test
        @DisplayName("Deve retornar lista paginada de usuários ativos")
        void buscarTodos_ComFiltroAtivo() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by("criadoEm").ascending());
            UsuarioDTO dto = criaUsuarioDTO();
            Page<UsuarioDTO> page = new PageImpl<>(List.of(dto), pageable, ONE_ELEMENT);

            when(usuarioService.buscarTodos(true, pageable)).thenReturn(page);

            ResponseEntity<Page<UsuarioDTO>> response = usuarioController.buscarTodos(0, 10, true);

            assertEquals(HttpStatus.OK.value(), response.getStatusCode().value(), "O status HTTP deve ser 200 OK");
            assertNotNull(response.getBody(), "O corpo da resposta não deve ser nulo");
            assertEquals(ONE_ELEMENT, response.getBody().getTotalElements(), "A página deve conter exatamente um elemento");
            assertEquals(dto, response.getBody().getContent().get(FIRST_ELEMENT_INDEX), "O elemento da página deve ser o DTO esperado");
            verify(usuarioService).buscarTodos(true, pageable);
        }

        @Test
        @DisplayName("Deve retornar lista paginada de usuários inativos")
        void buscarTodos_ComFiltroInativo() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by("criadoEm").ascending());
            UsuarioDTO dto = criaUsuarioDTO();
            dto.setAtivo(false);
            Page<UsuarioDTO> page = new PageImpl<>(List.of(dto), pageable, ONE_ELEMENT);

            when(usuarioService.buscarTodos(false, pageable)).thenReturn(page);

            ResponseEntity<Page<UsuarioDTO>> response = usuarioController.buscarTodos(0, 10, false);

            assertEquals(HttpStatus.OK.value(), response.getStatusCode().value(), "O status HTTP deve ser 200 OK");
            assertNotNull(response.getBody(), "O corpo da resposta não deve ser nulo");
            assertEquals(ONE_ELEMENT, response.getBody().getTotalElements(), "A página deve conter exatamente um elemento");
            assertEquals(dto, response.getBody().getContent().get(FIRST_ELEMENT_INDEX), "O elemento da página deve ser o DTO esperado");
            verify(usuarioService).buscarTodos(false, pageable);
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

            assertEquals(HttpStatus.OK.value(), response.getStatusCode().value(), "O status HTTP deve ser 200 OK");
            assertEquals(input, response.getBody(), "O corpo deve conter o DTO do usuário criado");
            verify(usuarioService).criar(input);
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

            assertEquals(HttpStatus.OK.value(), response.getStatusCode().value(), "O status HTTP deve ser 200 OK");
            assertNull(response.getBody(), "O corpo da resposta deve ser nulo para atualizações");
            verify(usuarioService).atualizar(id, input);
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

            assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatusCode().value(), "O status HTTP deve ser 204 No Content");
            assertNull(response.getBody(), "O corpo da resposta deve ser nulo para deleções");
            verify(usuarioService).remover(id);
        }
    }

    @Nested
    @DisplayName("PATCH /usuario/{id}/trocar-senha")
    class TrocarSenhaUsuario {

        @Test
        @DisplayName("Deve trocar a senha com sucesso e retornar OK")
        void trocarSenha_Sucesso() {
            UUID id = UUID.randomUUID();
            TrocarSenhaDTO dto = new TrocarSenhaDTO("novaSenha123");

            doNothing().when(usuarioService).trocarSenha(id, dto.getSenha());

            ResponseEntity<?> response = usuarioController.trocarSenha(id, dto);

            assertEquals(HttpStatus.OK.value(), response.getStatusCode().value(), "O status HTTP deve ser 200 OK");
            assertNull(response.getBody(), "O corpo da resposta deve ser nulo para troca de senha bem-sucedida");
            verify(usuarioService).trocarSenha(id, dto.getSenha());
        }

        @Test
        @DisplayName("Deve retornar BAD_REQUEST quando o DTO é nulo")
        void trocarSenha_DtoNulo() {
            UUID id = UUID.randomUUID();

            ResponseEntity<?> response = usuarioController.trocarSenha(id, null);

            assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value(), "O status HTTP deve ser 400 BAD_REQUEST");
            assertEquals("Requisição inválida! A senha não pode ser nula ou vazia.", response.getBody(), "Deve retornar mensagem de validação");
            verify(usuarioService, never()).trocarSenha(any(), any());
        }

        @Test
        @DisplayName("Deve retornar BAD_REQUEST quando a senha é nula")
        void trocarSenha_SenhaNula() {
            UUID id = UUID.randomUUID();
            TrocarSenhaDTO dto = new TrocarSenhaDTO(null);

            ResponseEntity<?> response = usuarioController.trocarSenha(id, dto);

            assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value(), "O status HTTP deve ser 400 BAD_REQUEST");
            assertEquals("Requisição inválida! A senha não pode ser nula ou vazia.", response.getBody(), "Deve retornar mensagem de validação");
            verify(usuarioService, never()).trocarSenha(any(), any());
        }

        @Test
        @DisplayName("Deve retornar BAD_REQUEST quando a senha contém apenas espaços em branco")
        void trocarSenha_SenhaEmBranco() {
            UUID id = UUID.randomUUID();
            TrocarSenhaDTO dto = new TrocarSenhaDTO("   ");

            ResponseEntity<?> response = usuarioController.trocarSenha(id, dto);

            assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value(), "O status HTTP deve ser 400 BAD_REQUEST");
            assertEquals("Requisição inválida! A senha não pode ser nula ou vazia.", response.getBody(), "Deve retornar mensagem de validação");
            verify(usuarioService, never()).trocarSenha(any(), any());
        }
    }

    @Nested
    @DisplayName("POST /usuario/resetar-senha/{username}")
    class SolicitarResetSenha {

        @Test
        @DisplayName("Deve solicitar reset de senha com sucesso e retornar OK com mensagem")
        void solicitarResetSenhaPorUsername_Sucesso() {
            String username = "usuarioTeste";
            doNothing().when(usuarioService).enviarEmailRedefinicaoSenhaPorUsername(username);

            ResponseEntity<String> response = usuarioController.solicitarResetSenhaPorUsername(username);

            assertEquals(HttpStatus.OK.value(), response.getStatusCode().value(), "O status HTTP deve ser 200 OK");
            assertNotNull(response.getBody(), "O corpo da resposta não deve ser nulo");
            verify(usuarioService).enviarEmailRedefinicaoSenhaPorUsername(username);
        }
    }
}
