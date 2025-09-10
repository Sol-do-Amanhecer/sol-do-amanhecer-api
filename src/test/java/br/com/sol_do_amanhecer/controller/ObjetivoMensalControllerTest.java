package br.com.sol_do_amanhecer.controller;

import br.com.sol_do_amanhecer.model.dto.ObjetivoMensalDTO;
import br.com.sol_do_amanhecer.model.dto.ObjetivoMensalRequestDTO;
import br.com.sol_do_amanhecer.service.ObjetivoMensalService;
import br.com.sol_do_amanhecer.shared.enums.EMes;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ObjetivoMensalControllerTest {

    @Mock
    private ObjetivoMensalService objetivoMensalService;

    @InjectMocks
    private ObjetivoMensalController objetivoMensalController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve criar um novo objetivo mensal com sucesso")
    void testCriarObjetivoMensal() {
        ObjetivoMensalRequestDTO requestDTO = new ObjetivoMensalRequestDTO();
        ObjetivoMensalDTO responseDTO = new ObjetivoMensalDTO();

        when(objetivoMensalService.criar(requestDTO)).thenReturn(responseDTO);

        ResponseEntity<ObjetivoMensalDTO> response = objetivoMensalController.criar(requestDTO);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(responseDTO, response.getBody());
        verify(objetivoMensalService, times(1)).criar(requestDTO);
    }

    @Test
    @DisplayName("Deve buscar um objetivo mensal por ID")
    void testBuscarPorId() {
        UUID id = UUID.randomUUID();
        ObjetivoMensalDTO dto = new ObjetivoMensalDTO();

        when(objetivoMensalService.buscarPorId(id)).thenReturn(dto);

        ResponseEntity<ObjetivoMensalDTO> response = objetivoMensalController.buscarPorId(id);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(dto, response.getBody());
        verify(objetivoMensalService, times(1)).buscarPorId(id);
    }

    @Test
    @DisplayName("Deve buscar todos os objetivos mensais com paginação e filtros")
    void testBuscarTodos() {
        int page = 0;
        int size = 10;
        EMes mes = EMes.JANEIRO;
        Integer ano = 2024;
        Page<ObjetivoMensalDTO> objetivos = new PageImpl<>(List.of(new ObjetivoMensalDTO()));

        when(objetivoMensalService.buscarTodos(eq(mes), eq(ano), any(Pageable.class))).thenReturn(objetivos);

        ResponseEntity<Page<ObjetivoMensalDTO>> response = objetivoMensalController.buscarTodos(page, size, mes, ano);

        assertEquals(200, response.getStatusCode().value());

        Page<ObjetivoMensalDTO> responseBody = response.getBody();
        assertNotNull(responseBody, "Response body should not be null");
        assertEquals(1, responseBody.getTotalElements());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(objetivoMensalService, times(1)).buscarTodos(eq(mes), eq(ano), pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();
        assertEquals(page, capturedPageable.getPageNumber());
        assertEquals(size, capturedPageable.getPageSize());
    }

    @Test
    @DisplayName("Deve atualizar um objetivo mensal existente")
    void testAtualizar() {
        UUID id = UUID.randomUUID();
        ObjetivoMensalRequestDTO requestDTO = new ObjetivoMensalRequestDTO();

        doNothing().when(objetivoMensalService).atualizar(id, requestDTO);

        ResponseEntity<Void> response = objetivoMensalController.atualizar(id, requestDTO);

        assertEquals(200, response.getStatusCode().value());
        assertNull(response.getBody());
        verify(objetivoMensalService, times(1)).atualizar(id, requestDTO);
    }

    @Test
    @DisplayName("Deve deletar um objetivo mensal existente")
    void testDeletar() {
        UUID id = UUID.randomUUID();

        doNothing().when(objetivoMensalService).remover(id);

        ResponseEntity<Void> response = objetivoMensalController.deletar(id);

        assertEquals(204, response.getStatusCode().value());
        assertNull(response.getBody());
        verify(objetivoMensalService, times(1)).remover(id);
    }
}