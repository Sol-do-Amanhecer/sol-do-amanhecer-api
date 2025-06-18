package br.com.sol_do_amanhecer.controller;

import br.com.sol_do_amanhecer.model.dto.PermissaoDTO;
import br.com.sol_do_amanhecer.service.PermissaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("Testes unitários do PermissaoController")
class PermissaoControllerTest {

    @Mock
    private PermissaoService permissaoService;

    @InjectMocks
    private PermissaoController permissaoController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve retornar PermissaoDTO ao buscar permissão por ID existente")
    void buscarPorId_deveRetornarPermissaoDTO_QuandoExiste() {
        UUID id = UUID.randomUUID();
        PermissaoDTO dto = PermissaoDTO.builder().uuid(id).descricao("ADMIN").build();

        when(permissaoService.buscarPorId(id)).thenReturn(dto);

        ResponseEntity<PermissaoDTO> response = permissaoController.buscarPorId(id);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(dto);
        verify(permissaoService).buscarPorId(id);
    }

    @Test
    @DisplayName("Deve retornar lista de permissões ao buscar todas as permissões")
    void buscarTodos_deveRetornarListaPermissoes() {
        List<PermissaoDTO> lista = Arrays.asList(
                PermissaoDTO.builder().uuid(UUID.randomUUID()).descricao("ADMIN").build(),
                PermissaoDTO.builder().uuid(UUID.randomUUID()).descricao("USER").build()
        );

        when(permissaoService.buscarTodos()).thenReturn(lista);

        ResponseEntity<List<PermissaoDTO>> response = permissaoController.buscarTodos();

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(lista);
        verify(permissaoService).buscarTodos();
    }

    @Test
    @DisplayName("Deve retornar PermissaoDTO criada ao criar uma nova permissão")
    void criar_deveRetornarPermissaoCriada() {
        PermissaoDTO input = PermissaoDTO.builder().descricao("NOVO").build();
        PermissaoDTO output = PermissaoDTO.builder().uuid(UUID.randomUUID()).descricao("NOVO").build();

        when(permissaoService.criar(input)).thenReturn(output);

        ResponseEntity<PermissaoDTO> response = permissaoController.criar(input);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(output);
        verify(permissaoService).criar(input);
    }

    @Test
    @DisplayName("Deve atualizar permissão existente e retornar resposta OK")
    void atualizar_deveChamarServicoAtualizarERetornarOk() {
        UUID id = UUID.randomUUID();
        PermissaoDTO dto = PermissaoDTO.builder().uuid(id).descricao("ATUALIZADO").build();

        doNothing().when(permissaoService).atualizar(id, dto);

        ResponseEntity<Void> response = permissaoController.atualizar(id, dto);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNull();
        verify(permissaoService).atualizar(id, dto);
    }

    @Test
    @DisplayName("Deve remover permissão ao informar ID e retornar NoContent")
    void remover_deveChamarServicoRemoverERetornarNoContent() {
        UUID id = UUID.randomUUID();

        doNothing().when(permissaoService).remover(id);

        ResponseEntity<Void> response = permissaoController.remover(id);

        assertThat(response.getStatusCode().value()).isEqualTo(204);
        assertThat(response.getBody()).isNull();
        verify(permissaoService).remover(id);
    }
}