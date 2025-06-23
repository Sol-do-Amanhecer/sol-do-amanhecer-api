package br.com.sol_do_amanhecer.controller;

import br.com.sol_do_amanhecer.model.dto.DoacaoDTO;
import br.com.sol_do_amanhecer.service.DoacaoService;
import br.com.sol_do_amanhecer.shared.enums.EMeioDoacao;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DoacaoControllerTest {

    @Mock
    private DoacaoService doacaoService;

    @InjectMocks
    private DoacaoController doacaoController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve criar uma nova doação com sucesso")
    void testCriar() {
        DoacaoDTO dtoEntrada = new DoacaoDTO();
        DoacaoDTO dtoSaida = new DoacaoDTO();

        when(doacaoService.criar(any(DoacaoDTO.class))).thenReturn(dtoSaida);

        ResponseEntity<DoacaoDTO> response = doacaoController.criar(dtoEntrada);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(dtoSaida, response.getBody());
        verify(doacaoService, times(1)).criar(dtoEntrada);
    }

    @Test
    @DisplayName("Deve buscar uma doação pelo ID")
    void testBuscarPorId() {
        UUID id = UUID.randomUUID();
        DoacaoDTO dtoEsperado = new DoacaoDTO();

        when(doacaoService.buscarPorId(id)).thenReturn(dtoEsperado);

        ResponseEntity<DoacaoDTO> response = doacaoController.buscarPorId(id);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(dtoEsperado, response.getBody());
        verify(doacaoService, times(1)).buscarPorId(id);
    }

    @Test
    @DisplayName("Deve buscar todas as doações paginadas e filtradas")
    void testBuscarTodas() {
        int page = 0, size = 10;
        LocalDate dataInicio = LocalDate.of(2024, 1, 1);
        LocalDate dataFim = LocalDate.of(2024, 5, 25);
        EMeioDoacao meio = EMeioDoacao.PIX;
        Page<DoacaoDTO> pageResult = new PageImpl<>(List.of(new DoacaoDTO()));

        when(doacaoService.buscarTodas(dataInicio, dataFim, meio, PageRequest.of(page, size)))
                .thenReturn(pageResult);

        ResponseEntity<Page<DoacaoDTO>> response = doacaoController.buscarTodas(page, size, dataInicio, dataFim, meio);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(pageResult, response.getBody());
        verify(doacaoService, times(1)).buscarTodas(dataInicio, dataFim, meio, PageRequest.of(page, size));
    }

    @Test
    @DisplayName("Deve atualizar uma doação existente")
    void testAtualizar() {
        UUID id = UUID.randomUUID();
        DoacaoDTO dto = new DoacaoDTO();

        doNothing().when(doacaoService).atualizar(id, dto);

        ResponseEntity<Void> response = doacaoController.atualizar(id, dto);

        assertEquals(200, response.getStatusCode().value());
        assertNull(response.getBody());
        verify(doacaoService, times(1)).atualizar(id, dto);
    }

    @Test
    @DisplayName("Deve deletar uma doação existente")
    void testDeletar() {
        UUID id = UUID.randomUUID();

        doNothing().when(doacaoService).remover(id);

        ResponseEntity<Void> response = doacaoController.deletar(id);

        assertEquals(204, response.getStatusCode().value());
        assertNull(response.getBody());
        verify(doacaoService, times(1)).remover(id);
    }
}