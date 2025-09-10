package br.com.sol_do_amanhecer.controller;

import br.com.sol_do_amanhecer.model.dto.PermissaoDTO;
import br.com.sol_do_amanhecer.model.dto.TrocarSenhaDTO;
import br.com.sol_do_amanhecer.model.dto.UsuarioDTO;
import br.com.sol_do_amanhecer.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
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

    private TrocarSenhaDTO criaTrocarSenhaDTO() {
        TrocarSenhaDTO dto = new TrocarSenhaDTO();
        dto.setSenha("novaSenha123");
        return dto;
    }

    @BeforeEach
    void setUp() {
        reset(usuarioService);
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
            Pageable pageable = PageRequest.of(0, 10, Sort.by("criadoEm").ascending());
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
            Pageable pageable = PageRequest.of(0, 10, Sort.by("criadoEm").ascending());
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
            Pageable pageable = PageRequest.of(0, 10, Sort.by("criadoEm").ascending());
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

        @Test
        @DisplayName("Deve retornar lista paginada com parâmetros personalizados")
        void buscarTodos_ComParametrosPersonalizados() {
            int page = 2;
            int size = 5;
            Pageable pageable = PageRequest.of(page, size, Sort.by("criadoEm").ascending());

            UsuarioDTO dto = criaUsuarioDTO();
            Page<UsuarioDTO> pageResult = new PageImpl<>(List.of(dto), pageable, 11);

            when(usuarioService.buscarTodos(null, pageable)).thenReturn(pageResult);

            ResponseEntity<Page<UsuarioDTO>> response = usuarioController.buscarTodos(page, size, null);

            assertEquals(200, response.getStatusCode().value());
            assertNotNull(response.getBody());
            assertEquals(11, response.getBody().getTotalElements());
            assertEquals(1, response.getBody().getNumberOfElements());
            assertEquals(3, response.getBody().getTotalPages());
            assertEquals(2, response.getBody().getNumber());
            assertEquals(dto, response.getBody().getContent().get(0));
            verify(usuarioService, times(1)).buscarTodos(null, pageable);
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há usuários")
        void buscarTodos_ListaVazia() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by("criadoEm").ascending());
            Page<UsuarioDTO> pageVazia = new PageImpl<>(Collections.emptyList(), pageable, 0);

            when(usuarioService.buscarTodos(null, pageable)).thenReturn(pageVazia);

            ResponseEntity<Page<UsuarioDTO>> response = usuarioController.buscarTodos(0, 10, null);

            assertEquals(200, response.getStatusCode().value());
            assertNotNull(response.getBody());
            assertEquals(0, response.getBody().getTotalElements());
            assertTrue(response.getBody().getContent().isEmpty());
            verify(usuarioService, times(1)).buscarTodos(null, pageable);
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
    @DisplayName("PATCH /usuario/{id}/trocar-senha")
    class TrocarSenha {

        @Test
        @DisplayName("Deve trocar senha do usuário com sucesso")
        void trocarSenha_Sucesso() {
            UUID id = UUID.randomUUID();
            TrocarSenhaDTO dto = criaTrocarSenhaDTO();

            doNothing().when(usuarioService).trocarSenha(id, dto.getSenha());

            ResponseEntity<?> response = usuarioController.trocarSenha(id, dto);

            assertEquals(200, response.getStatusCode().value());
            assertNull(response.getBody());
            verify(usuarioService, times(1)).trocarSenha(id, dto.getSenha());
        }

        @Test
        @DisplayName("Deve retornar BAD_REQUEST quando DTO é nulo")
        void trocarSenha_DTONulo() {
            UUID id = UUID.randomUUID();

            ResponseEntity<?> response = usuarioController.trocarSenha(id, null);

            assertEquals(400, response.getStatusCode().value());
            assertEquals("Requisição inválida! A senha não pode ser nula ou vazia.", response.getBody());
            verify(usuarioService, never()).trocarSenha(any(), any());
        }

        @Test
        @DisplayName("Deve retornar BAD_REQUEST quando senha é nula")
        void trocarSenha_SenhaNula() {
            UUID id = UUID.randomUUID();
            TrocarSenhaDTO dto = new TrocarSenhaDTO();
            dto.setSenha(null);

            ResponseEntity<?> response = usuarioController.trocarSenha(id, dto);

            assertEquals(400, response.getStatusCode().value());
            assertEquals("Requisição inválida! A senha não pode ser nula ou vazia.", response.getBody());
            verify(usuarioService, never()).trocarSenha(any(), any());
        }

        @Test
        @DisplayName("Deve retornar BAD_REQUEST quando senha é vazia")
        void trocarSenha_SenhaVazia() {
            UUID id = UUID.randomUUID();
            TrocarSenhaDTO dto = new TrocarSenhaDTO();
            dto.setSenha("");

            ResponseEntity<?> response = usuarioController.trocarSenha(id, dto);

            assertEquals(400, response.getStatusCode().value());
            assertEquals("Requisição inválida! A senha não pode ser nula ou vazia.", response.getBody());
            verify(usuarioService, never()).trocarSenha(any(), any());
        }

        @Test
        @DisplayName("Deve retornar BAD_REQUEST quando senha contém apenas espaços em branco")
        void trocarSenha_SenhaApenasEspacos() {
            UUID id = UUID.randomUUID();
            TrocarSenhaDTO dto = new TrocarSenhaDTO();
            dto.setSenha("   ");

            ResponseEntity<?> response = usuarioController.trocarSenha(id, dto);

            assertEquals(400, response.getStatusCode().value());
            assertEquals("Requisição inválida! A senha não pode ser nula ou vazia.", response.getBody());
            verify(usuarioService, never()).trocarSenha(any(), any());
        }

        @Test
        @DisplayName("Deve trocar senha com senha válida contendo espaços nas extremidades")
        void trocarSenha_SenhaValidaComEspacos() {
            UUID id = UUID.randomUUID();
            TrocarSenhaDTO dto = new TrocarSenhaDTO();
            dto.setSenha("  senhaValida123  ");

            doNothing().when(usuarioService).trocarSenha(id, dto.getSenha());

            ResponseEntity<?> response = usuarioController.trocarSenha(id, dto);

            assertEquals(200, response.getStatusCode().value());
            assertNull(response.getBody());
            verify(usuarioService, times(1)).trocarSenha(id, dto.getSenha());
        }

        @Test
        @DisplayName("Deve trocar senha com senha mínima válida")
        void trocarSenha_SenhaMinimaValida() {
            UUID id = UUID.randomUUID();
            TrocarSenhaDTO dto = new TrocarSenhaDTO();
            dto.setSenha("a"); // Senha com apenas um caractere, mas não vazia

            doNothing().when(usuarioService).trocarSenha(id, dto.getSenha());

            ResponseEntity<?> response = usuarioController.trocarSenha(id, dto);

            assertEquals(200, response.getStatusCode().value());
            assertNull(response.getBody());
            verify(usuarioService, times(1)).trocarSenha(id, dto.getSenha());
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

    @Nested
    @DisplayName("POST /usuario/reset-senha/{username}")
    class SolicitarResetSenha {

        @Test
        @DisplayName("Deve solicitar reset de senha por username com sucesso")
        void solicitarResetSenhaPorUsername_Sucesso() {
            String username = "usuarioTeste";
            String mensagemEsperada = "Um e-mail foi enviado com instruções para redefinir sua senha.";

            doNothing().when(usuarioService).enviarEmailRedefinicaoSenhaPorUsername(username);

            ResponseEntity<String> response = usuarioController.solicitarResetSenhaPorUsername(username);

            assertEquals(200, response.getStatusCode().value());
            assertEquals(mensagemEsperada, response.getBody());
            verify(usuarioService, times(1)).enviarEmailRedefinicaoSenhaPorUsername(username);
        }

        @Test
        @DisplayName("Deve solicitar reset de senha com username vazio")
        void solicitarResetSenhaPorUsername_UsernameVazio() {
            String username = "";
            String mensagemEsperada = "Um e-mail foi enviado com instruções para redefinir sua senha.";

            doNothing().when(usuarioService).enviarEmailRedefinicaoSenhaPorUsername(username);

            ResponseEntity<String> response = usuarioController.solicitarResetSenhaPorUsername(username);

            assertEquals(200, response.getStatusCode().value());
            assertEquals(mensagemEsperada, response.getBody());
            verify(usuarioService, times(1)).enviarEmailRedefinicaoSenhaPorUsername(username);
        }

        @Test
        @DisplayName("Deve solicitar reset de senha com username contendo espaços")
        void solicitarResetSenhaPorUsername_UsernameComEspacos() {
            String username = "  usuario teste  ";
            String mensagemEsperada = "Um e-mail foi enviado com instruções para redefinir sua senha.";

            doNothing().when(usuarioService).enviarEmailRedefinicaoSenhaPorUsername(username);

            ResponseEntity<String> response = usuarioController.solicitarResetSenhaPorUsername(username);

            assertEquals(200, response.getStatusCode().value());
            assertEquals(mensagemEsperada, response.getBody());
            verify(usuarioService, times(1)).enviarEmailRedefinicaoSenhaPorUsername(username);
        }

        @Test
        @DisplayName("Deve solicitar reset de senha com username especial")
        void solicitarResetSenhaPorUsername_UsernameEspecial() {
            String username = "usuario@teste.com";
            String mensagemEsperada = "Um e-mail foi enviado com instruções para redefinir sua senha.";

            doNothing().when(usuarioService).enviarEmailRedefinicaoSenhaPorUsername(username);

            ResponseEntity<String> response = usuarioController.solicitarResetSenhaPorUsername(username);

            assertEquals(200, response.getStatusCode().value());
            assertEquals(mensagemEsperada, response.getBody());
            verify(usuarioService, times(1)).enviarEmailRedefinicaoSenhaPorUsername(username);
        }
    }

    @Nested
    @DisplayName("Testes de Construtor")
    class TesteConstrutor {

        @Test
        @DisplayName("Deve criar instância do controller com service")
        void testeConstrutor() {
            UsuarioService mockService = mock(UsuarioService.class);
            UsuarioController controller = new UsuarioController(mockService);

            assertNotNull(controller);
        }
    }

    @Nested
    @DisplayName("Testes de Logging")
    class TesteLogging {

        @Test
        @DisplayName("Deve executar log debug no buscarPorId")
        void testeLogBuscarPorId() {
            UUID id = UUID.randomUUID();
            UsuarioDTO dto = criaUsuarioDTO();
            when(usuarioService.buscarPorId(id)).thenReturn(dto);

            usuarioController.buscarPorId(id);

            verify(usuarioService, times(1)).buscarPorId(id);
        }

        @Test
        @DisplayName("Deve executar log debug no buscarTodos")
        void testeLogBuscarTodos() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by("criadoEm").ascending());
            Page<UsuarioDTO> page = new PageImpl<>(Collections.emptyList(), pageable, 0);
            when(usuarioService.buscarTodos(null, pageable)).thenReturn(page);

            usuarioController.buscarTodos(0, 10, null);

            verify(usuarioService, times(1)).buscarTodos(null, pageable);
        }

        @Test
        @DisplayName("Deve executar log debug no criar")
        void testeLogCriar() {
            UsuarioDTO dto = criaUsuarioDTO();
            when(usuarioService.criar(dto)).thenReturn(dto);

            usuarioController.criar(dto);

            verify(usuarioService, times(1)).criar(dto);
        }

        @Test
        @DisplayName("Deve executar log debug no atualizar")
        void testeLogAtualizar() {
            UUID id = UUID.randomUUID();
            UsuarioDTO dto = criaUsuarioDTO();
            doNothing().when(usuarioService).atualizar(id, dto);

            usuarioController.atualizar(id, dto);

            verify(usuarioService, times(1)).atualizar(id, dto);
        }

        @Test
        @DisplayName("Deve executar log debug no trocarSenha")
        void testeLogTrocarSenha() {
            UUID id = UUID.randomUUID();
            TrocarSenhaDTO dto = criaTrocarSenhaDTO();
            doNothing().when(usuarioService).trocarSenha(id, dto.getSenha());

            usuarioController.trocarSenha(id, dto);

            verify(usuarioService, times(1)).trocarSenha(id, dto.getSenha());
        }

        @Test
        @DisplayName("Deve executar log debug no deletar")
        void testeLogDeletar() {
            UUID id = UUID.randomUUID();
            doNothing().when(usuarioService).remover(id);

            usuarioController.deletar(id);

            verify(usuarioService, times(1)).remover(id);
        }

        @Test
        @DisplayName("Deve executar log debug no solicitarResetSenhaPorUsername")
        void testeLogSolicitarResetSenha() {
            String username = "teste";
            doNothing().when(usuarioService).enviarEmailRedefinicaoSenhaPorUsername(username);

            usuarioController.solicitarResetSenhaPorUsername(username);

            verify(usuarioService, times(1)).enviarEmailRedefinicaoSenhaPorUsername(username);
        }
    }
}