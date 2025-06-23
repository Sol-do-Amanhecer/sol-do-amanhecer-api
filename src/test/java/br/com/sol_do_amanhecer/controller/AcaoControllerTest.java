package br.com.sol_do_amanhecer.controller;

import br.com.sol_do_amanhecer.model.dto.*;
import br.com.sol_do_amanhecer.service.AcaoService;
import br.com.sol_do_amanhecer.shared.enums.ETipoAcao;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AcaoControllerTest {

    @Mock
    private AcaoService acaoService;

    @InjectMocks
    private AcaoController acaoController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve criar uma nova ação e retornar status 200 OK com o corpo correto")
    void testCriar() {
        AcaoDTO acaoDTO = new AcaoDTO();
        AcaoRequestDTO acaoRequestDTO = new AcaoRequestDTO();
        acaoRequestDTO.setAcaoDTO(acaoDTO);
        acaoRequestDTO.setImagemDTOList(Collections.emptyList());

        when(acaoService.criar(any(), anyList())).thenReturn(acaoDTO);

        ResponseEntity<AcaoDTO> response = acaoController.criar(acaoRequestDTO);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(acaoDTO, response.getBody());
        verify(acaoService, times(1)).criar(any(), anyList());
    }

    @Test
    @DisplayName("Deve buscar uma ação por ID e retornar status 200 OK com o corpo correto")
    void testBuscarPorId() {
        UUID id = UUID.randomUUID();
        AcaoResponseDTO retorno = new AcaoResponseDTO();

        when(acaoService.buscarPorId(id)).thenReturn(retorno);

        ResponseEntity<AcaoResponseDTO> response = acaoController.buscarPorId(id);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(retorno, response.getBody());
        verify(acaoService, times(1)).buscarPorId(id);
    }

    @Test
    @DisplayName("Deve buscar todas as ações paginadas e retornar status 200 OK")
    void testBuscarTodos() {
        Page<AcaoResponseDTO> pageMock = new PageImpl<>(List.of(new AcaoResponseDTO()));
        int page = 0;
        int size = 10;
        ETipoAcao tipo = ETipoAcao.SOCIAL_ALIMENTAR;
        Integer ano = 2024;
        Integer mes = 6;

        when(acaoService.buscarTodos(eq(tipo), eq(ano), eq(mes), any(Pageable.class)))
                .thenReturn(pageMock);

        ResponseEntity<Page<AcaoResponseDTO>> response = acaoController.buscarTodos(page, size, tipo, ano, mes);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(pageMock, response.getBody());
        verify(acaoService, times(1)).buscarTodos(eq(tipo), eq(ano), eq(mes), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve atualizar uma ação existente e retornar status 200 OK")
    void testAtualizar() {
        UUID id = UUID.randomUUID();
        AcaoDTO acaoDTO = new AcaoDTO();
        AcaoRequestDTO acaoRequestDTO = new AcaoRequestDTO();
        acaoRequestDTO.setAcaoDTO(acaoDTO);
        acaoRequestDTO.setImagemDTOList(Collections.emptyList());

        doNothing().when(acaoService).atualizar(any(UUID.class), any(), anyList());

        ResponseEntity<Void> response = acaoController.atualizar(id, acaoRequestDTO);

        assertEquals(200, response.getStatusCode().value());
        assertNull(response.getBody());
        verify(acaoService, times(1)).atualizar(eq(id), any(), anyList());
    }

    @Test
    @DisplayName("Deve deletar uma ação pelo ID e retornar status 204 No Content")
    void testDeletar() {
        UUID id = UUID.randomUUID();

        doNothing().when(acaoService).remover(id);

        ResponseEntity<Void> response = acaoController.deletar(id);

        assertEquals(204, response.getStatusCode().value());
        assertNull(response.getBody());
        verify(acaoService, times(1)).remover(id);
    }
}