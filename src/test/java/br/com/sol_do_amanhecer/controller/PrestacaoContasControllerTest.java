package br.com.sol_do_amanhecer.controller;

import br.com.sol_do_amanhecer.model.dto.PrestacaoContasDTO;
import br.com.sol_do_amanhecer.service.PrestacaoContasService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de PrestacaoContasController")
class PrestacaoContasControllerTest {

    @Mock
    private PrestacaoContasService prestacaoContasService;

    @InjectMocks
    private PrestacaoContasController prestacaoContasController;

    @Test
    @DisplayName("Deve criar uma nova prestação de contas com sucesso")
    void deveCriar() {
        PrestacaoContasDTO request = new PrestacaoContasDTO();
        PrestacaoContasDTO response = new PrestacaoContasDTO();

        when(prestacaoContasService.criar(request)).thenReturn(response);

        ResponseEntity<PrestacaoContasDTO> resp = prestacaoContasController.criar(request);

        assertEquals(HttpStatus.OK.value(), resp.getStatusCode().value(), "O status HTTP deve ser 200 OK");
        assertEquals(response, resp.getBody(), "O corpo deve conter o DTO da prestação de contas criada");
        verify(prestacaoContasService, times(1)).criar(request);
    }

    @Test
    @DisplayName("Deve buscar uma prestação de contas pelo ID")
    void deveBuscarPorId() {
        UUID id = UUID.randomUUID();
        PrestacaoContasDTO prestacao = new PrestacaoContasDTO();

        when(prestacaoContasService.buscarPorId(id)).thenReturn(prestacao);

        ResponseEntity<PrestacaoContasDTO> resp = prestacaoContasController.buscarPorId(id);

        assertEquals(HttpStatus.OK.value(), resp.getStatusCode().value(), "O status HTTP deve ser 200 OK");
        assertEquals(prestacao, resp.getBody(), "O corpo deve conter o DTO da prestação de contas buscada");
        verify(prestacaoContasService, times(1)).buscarPorId(id);
    }

    @Test
    @DisplayName("Deve buscar todas as prestações de contas com paginação e filtro")
    void deveBuscarTodas() {
        int page = 0;
        int size = 2;
        Integer mes = 5;
        Integer ano = 2024;
        Sort sort = Sort.by("dataTransacao").descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<PrestacaoContasDTO> paged = new PageImpl<>(List.of(new PrestacaoContasDTO()), pageable, 1);

        when(prestacaoContasService.buscarTodas(mes, ano, pageable)).thenReturn(paged);

        ResponseEntity<Page<PrestacaoContasDTO>> resp = prestacaoContasController.buscarTodas(page, size, mes, ano);

        assertEquals(HttpStatus.OK.value(), resp.getStatusCode().value(), "O status HTTP deve ser 200 OK");
        assertEquals(paged, resp.getBody(), "O corpo deve conter a página de prestações retornada pelo serviço");
        verify(prestacaoContasService, times(1)).buscarTodas(mes, ano, pageable);
    }

    @Test
    @DisplayName("Deve atualizar uma prestação de contas existente")
    void deveAtualizar() {
        UUID id = UUID.randomUUID();
        PrestacaoContasDTO request = new PrestacaoContasDTO();

        doNothing().when(prestacaoContasService).atualizar(id, request);

        ResponseEntity<Void> resp = prestacaoContasController.atualizar(id, request);

        assertEquals(HttpStatus.OK.value(), resp.getStatusCode().value(), "O status HTTP deve ser 200 OK");
        assertNull(resp.getBody(), "O corpo da resposta deve ser nulo para atualizações");
        verify(prestacaoContasService, times(1)).atualizar(id, request);
    }

    @Test
    @DisplayName("Deve deletar uma prestação de contas existente")
    void deveDeletar() {
        UUID id = UUID.randomUUID();

        doNothing().when(prestacaoContasService).remover(id);

        ResponseEntity<Void> resp = prestacaoContasController.deletar(id);

        assertEquals(HttpStatus.NO_CONTENT.value(), resp.getStatusCode().value(), "O status HTTP deve ser 204 No Content");
        assertNull(resp.getBody(), "O corpo da resposta deve ser nulo para deleções");
        verify(prestacaoContasService, times(1)).remover(id);
    }
}
