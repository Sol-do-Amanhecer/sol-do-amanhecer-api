package br.com.sol_do_amanhecer.controller;

import br.com.sol_do_amanhecer.model.dto.PermissaoDTO;
import br.com.sol_do_amanhecer.service.PermissaoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários do PermissaoController")
public class PermissaoControllerTest {

    private static final int HTTP_OK = HttpStatus.OK.value();
    private static final int HTTP_NO_CONTENT = HttpStatus.NO_CONTENT.value();

    @Mock
    private PermissaoService permissaoService;

    @InjectMocks
    private PermissaoController permissaoController;

    @Test
    @DisplayName("Deve retornar PermissaoDTO ao buscar permissão por ID existente")
    void buscarPorId_deveRetornarPermissaoDTO_QuandoExiste() {
        UUID id = UUID.randomUUID();
        PermissaoDTO dto = PermissaoDTO.builder().uuid(id).descricao("ADMIN").build();

        when(permissaoService.buscarPorId(id)).thenReturn(dto);

        ResponseEntity<PermissaoDTO> response = permissaoController.buscarPorId(id);

        assertEquals(HTTP_OK, response.getStatusCode().value(), "O status HTTP deve ser 200 OK");
        assertEquals(dto, response.getBody(), "O corpo deve conter o DTO da permissão buscada");
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

        assertEquals(HTTP_OK, response.getStatusCode().value(), "O status HTTP deve ser 200 OK");
        assertEquals(lista, response.getBody(), "O corpo deve conter a lista completa de permissões");
        verify(permissaoService).buscarTodos();
    }

    @Test
    @DisplayName("Deve retornar PermissaoDTO criada ao criar uma nova permissão")
    void criar_deveRetornarPermissaoCriada() {
        PermissaoDTO input = PermissaoDTO.builder().descricao("NOVO").build();
        PermissaoDTO output = PermissaoDTO.builder().uuid(UUID.randomUUID()).descricao("NOVO").build();

        when(permissaoService.criar(input)).thenReturn(output);

        ResponseEntity<PermissaoDTO> response = permissaoController.criar(input);

        assertEquals(HTTP_OK, response.getStatusCode().value(), "O status HTTP deve ser 200 OK");
        assertEquals(output, response.getBody(), "O corpo deve conter o DTO da permissão criada");
        verify(permissaoService).criar(input);
    }

    @Test
    @DisplayName("Deve atualizar permissão existente e retornar resposta OK")
    void atualizar_deveChamarServicoAtualizarERetornarOk() {
        UUID id = UUID.randomUUID();
        PermissaoDTO dto = PermissaoDTO.builder().uuid(id).descricao("ATUALIZADO").build();

        doNothing().when(permissaoService).atualizar(id, dto);

        ResponseEntity<Void> response = permissaoController.atualizar(id, dto);

        assertEquals(HTTP_OK, response.getStatusCode().value(), "O status HTTP deve ser 200 OK");
        assertNull(response.getBody(), "O corpo da resposta deve ser nulo para atualizações");
        verify(permissaoService).atualizar(id, dto);
    }

    @Test
    @DisplayName("Deve remover permissão ao informar ID e retornar NoContent")
    void remover_deveChamarServicoRemoverERetornarNoContent() {
        UUID id = UUID.randomUUID();

        doNothing().when(permissaoService).remover(id);

        ResponseEntity<Void> response = permissaoController.remover(id);

        assertEquals(HTTP_NO_CONTENT, response.getStatusCode().value(), "O status HTTP deve ser 204 No Content");
        assertNull(response.getBody(), "O corpo da resposta deve ser nulo para deleções");
        verify(permissaoService).remover(id);
    }
}
