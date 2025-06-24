package br.com.sol_do_amanhecer.controller;

import br.com.sol_do_amanhecer.model.dto.PrestacaoContasDTO;
import br.com.sol_do_amanhecer.service.PrestacaoContasService;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PrestacaoContasControllerTest {

    @Mock
    private PrestacaoContasService prestacaoContasService;

    @InjectMocks
    private PrestacaoContasController prestacaoContasController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve criar uma nova prestação de contas com sucesso")
    void testCriar() {
        PrestacaoContasDTO request = new PrestacaoContasDTO();
        PrestacaoContasDTO response = new PrestacaoContasDTO();

        when(prestacaoContasService.criar(request)).thenReturn(response);

        ResponseEntity<PrestacaoContasDTO> resp = prestacaoContasController.criar(request);

        assertEquals(200, resp.getStatusCode().value());
        assertEquals(response, resp.getBody());
        verify(prestacaoContasService, times(1)).criar(request);
    }

    @Test
    @DisplayName("Deve buscar uma prestação de contas pelo ID")
    void testBuscarPorId() {
        UUID id = UUID.randomUUID();
        PrestacaoContasDTO prestacao = new PrestacaoContasDTO();

        when(prestacaoContasService.buscarPorId(id)).thenReturn(prestacao);

        ResponseEntity<PrestacaoContasDTO> resp = prestacaoContasController.buscarPorId(id);

        assertEquals(200, resp.getStatusCode().value());
        assertEquals(prestacao, resp.getBody());
        verify(prestacaoContasService, times(1)).buscarPorId(id);
    }

    @Test
    @DisplayName("Deve buscar todas as prestações de contas com paginação e filtro")
    void testBuscarTodas() {
        int page = 0;
        int size = 2;
        Integer mes = 5;
        Integer ano = 2024;
        Page<PrestacaoContasDTO> paged = new PageImpl<>(List.of(new PrestacaoContasDTO()));

        when(prestacaoContasService.buscarTodas(mes, ano, PageRequest.of(page, size))).thenReturn(paged);

        ResponseEntity<Page<PrestacaoContasDTO>> resp = prestacaoContasController.buscarTodas(page, size, mes, ano);

        assertEquals(200, resp.getStatusCode().value());
        assertEquals(paged, resp.getBody());
        verify(prestacaoContasService, times(1)).buscarTodas(mes, ano, PageRequest.of(page, size));
    }

    @Test
    @DisplayName("Deve atualizar uma prestação de contas existente")
    void testAtualizar() {
        UUID id = UUID.randomUUID();
        PrestacaoContasDTO request = new PrestacaoContasDTO();

        doNothing().when(prestacaoContasService).atualizar(id, request);

        ResponseEntity<Void> resp = prestacaoContasController.atualizar(id, request);

        assertEquals(200, resp.getStatusCode().value());
        assertNull(resp.getBody());
        verify(prestacaoContasService, times(1)).atualizar(id, request);
    }

    @Test
    @DisplayName("Deve deletar uma prestação de contas existente")
    void testDeletar() {
        UUID id = UUID.randomUUID();

        doNothing().when(prestacaoContasService).remover(id);

        ResponseEntity<Void> resp = prestacaoContasController.deletar(id);

        assertEquals(204, resp.getStatusCode().value());
        assertNull(resp.getBody());
        verify(prestacaoContasService, times(1)).remover(id);
    }
}