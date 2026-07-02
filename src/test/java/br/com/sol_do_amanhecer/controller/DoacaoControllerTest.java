package br.com.sol_do_amanhecer.controller;

import br.com.sol_do_amanhecer.model.dto.DoacaoDTO;
import br.com.sol_do_amanhecer.service.DoacaoService;
import br.com.sol_do_amanhecer.shared.enums.EMeioDoacao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de DoacaoController")
class DoacaoControllerTest {

    @Mock
    private DoacaoService doacaoService;

    @InjectMocks
    private DoacaoController doacaoController;

    @Test
    @DisplayName("Deve criar uma nova doação com sucesso")
    void deveCriar() {
        DoacaoDTO dtoEntrada = new DoacaoDTO();
        DoacaoDTO dtoSaida = new DoacaoDTO();

        when(doacaoService.criar(any(DoacaoDTO.class))).thenReturn(dtoSaida);

        ResponseEntity<DoacaoDTO> response = doacaoController.criar(dtoEntrada);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value(), "O status HTTP deve ser 200 OK");
        assertEquals(dtoSaida, response.getBody(), "O corpo deve conter o DTO da doação criada");
        verify(doacaoService, times(1)).criar(dtoEntrada);
    }

    @Test
    @DisplayName("Deve buscar uma doação pelo ID")
    void deveBuscarPorId() {
        UUID id = UUID.randomUUID();
        DoacaoDTO dtoEsperado = new DoacaoDTO();

        when(doacaoService.buscarPorId(id)).thenReturn(dtoEsperado);

        ResponseEntity<DoacaoDTO> response = doacaoController.buscarPorId(id);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value(), "O status HTTP deve ser 200 OK");
        assertEquals(dtoEsperado, response.getBody(), "O corpo deve conter o DTO da doação buscada");
        verify(doacaoService, times(1)).buscarPorId(id);
    }

    @Test
    @DisplayName("Deve buscar todas as doações paginadas e filtradas por ano, mês e meio de doação")
    void deveBuscarTodas() {
        int page = 0;
        int size = 10;
        Integer ano = 2024;
        Integer mes = 5;
        EMeioDoacao meio = EMeioDoacao.PIX;

        Sort sort = Sort.by("dataDoacao").ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<DoacaoDTO> pageResult = new PageImpl<>(List.of(new DoacaoDTO()), pageable, 1);

        when(doacaoService.buscarTodas(ano, mes, meio, pageable))
                .thenReturn(pageResult);

        ResponseEntity<Page<DoacaoDTO>> response = doacaoController.buscarTodas(page, size, ano, mes, meio);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value(), "O status HTTP deve ser 200 OK");
        assertEquals(pageResult, response.getBody(), "O corpo deve conter a página de doações retornada pelo serviço");
        verify(doacaoService, times(1)).buscarTodas(ano, mes, meio, pageable);
    }

    @Test
    @DisplayName("Deve atualizar uma doação existente")
    void deveAtualizar() {
        UUID id = UUID.randomUUID();
        DoacaoDTO dto = new DoacaoDTO();

        doNothing().when(doacaoService).atualizar(id, dto);

        ResponseEntity<Void> response = doacaoController.atualizar(id, dto);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value(), "O status HTTP deve ser 200 OK");
        assertNull(response.getBody(), "O corpo da resposta deve ser nulo para atualizações");
        verify(doacaoService, times(1)).atualizar(id, dto);
    }

    @Test
    @DisplayName("Deve deletar uma doação existente")
    void deveDeletar() {
        UUID id = UUID.randomUUID();

        doNothing().when(doacaoService).remover(id);

        ResponseEntity<Void> response = doacaoController.deletar(id);

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatusCode().value(), "O status HTTP deve ser 204 No Content");
        assertNull(response.getBody(), "O corpo da resposta deve ser nulo para deleções");
        verify(doacaoService, times(1)).remover(id);
    }
}
