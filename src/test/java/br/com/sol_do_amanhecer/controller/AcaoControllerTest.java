package br.com.sol_do_amanhecer.controller;

import br.com.sol_do_amanhecer.model.dto.*;
import br.com.sol_do_amanhecer.service.AcaoService;
import br.com.sol_do_amanhecer.shared.enums.ETipoAcao;
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
@DisplayName("Testes de AcaoController")
class AcaoControllerTest {

    @Mock
    private AcaoService acaoService;

    @InjectMocks
    private AcaoController acaoController;

    @Test
    @DisplayName("Deve criar uma nova ação e retornar status 200 OK com o corpo correto")
    void deveCriar() {
        AcaoDTO acaoDTO = new AcaoDTO();
        AcaoRequestDTO acaoRequestDTO = new AcaoRequestDTO();
        acaoRequestDTO.setAcaoDTO(acaoDTO);
        acaoRequestDTO.setImagemDTOList(Collections.emptyList());

        when(acaoService.criar(any(), anyList())).thenReturn(acaoDTO);

        ResponseEntity<AcaoDTO> response = acaoController.criar(acaoRequestDTO);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value(), "O status HTTP deve ser 200 OK");
        assertEquals(acaoDTO, response.getBody(), "O corpo deve conter o DTO da ação criada");
        verify(acaoService, times(1)).criar(any(), anyList());
    }

    @Test
    @DisplayName("Deve buscar por ID retornando 200, 404 por nulo e 404 por exceção")
    void deveBuscarPorId() {
        UUID id = UUID.randomUUID();
        AcaoResponseDTO retorno = new AcaoResponseDTO();
        when(acaoService.buscarPorId(id)).thenReturn(retorno);
        ResponseEntity<AcaoResponseDTO> responseOk = acaoController.buscarPorId(id);
        assertEquals(HttpStatus.OK.value(), responseOk.getStatusCode().value(), "Deve retornar 200 OK quando ação encontrada");
        assertEquals(retorno, responseOk.getBody(), "O corpo deve conter o DTO da ação buscada");

        UUID idNulo = UUID.randomUUID();
        when(acaoService.buscarPorId(idNulo)).thenReturn(null);
        ResponseEntity<AcaoResponseDTO> responseNulo = acaoController.buscarPorId(idNulo);
        assertEquals(HttpStatus.NOT_FOUND.value(), responseNulo.getStatusCode().value(), "Deve retornar 404 quando serviço retorna nulo");

        UUID idExcecao = UUID.randomUUID();
        when(acaoService.buscarPorId(idExcecao)).thenThrow(new RuntimeException("Ação não encontrada"));
        ResponseEntity<AcaoResponseDTO> responseExcecao = acaoController.buscarPorId(idExcecao);
        assertEquals(HttpStatus.NOT_FOUND.value(), responseExcecao.getStatusCode().value(), "Deve retornar 404 quando serviço lança exceção");
    }

    @Test
    @DisplayName("Deve buscar todas as ações paginadas e retornar status 200 OK")
    void deveBuscarTodos() {
        Page<AcaoResponseDTO> pageMock = new PageImpl<>(List.of(new AcaoResponseDTO()));
        int page = 0;
        int size = 10;
        ETipoAcao tipo = ETipoAcao.SOCIAL_ALIMENTAR;
        Integer ano = 2024;
        Integer mes = 6;

        when(acaoService.buscarTodos(eq(tipo), eq(ano), eq(mes), any(Pageable.class)))
                .thenReturn(pageMock);

        ResponseEntity<Page<AcaoResponseDTO>> response = acaoController.buscarTodos(page, size, tipo, ano, mes);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value(), "O status HTTP deve ser 200 OK");
        assertEquals(pageMock, response.getBody(), "O corpo deve conter a página de ações retornada pelo serviço");
        verify(acaoService, times(1)).buscarTodos(eq(tipo), eq(ano), eq(mes), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve atualizar uma ação existente e retornar status 200 OK")
    void deveAtualizar() {
        UUID id = UUID.randomUUID();
        AcaoDTO acaoDTO = new AcaoDTO();
        AcaoRequestDTO acaoRequestDTO = new AcaoRequestDTO();
        acaoRequestDTO.setAcaoDTO(acaoDTO);
        acaoRequestDTO.setImagemDTOList(Collections.emptyList());

        doNothing().when(acaoService).atualizar(any(UUID.class), any(), anyList());

        ResponseEntity<Void> response = acaoController.atualizar(id, acaoRequestDTO);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value(), "O status HTTP deve ser 200 OK");
        assertNull(response.getBody(), "O corpo da resposta deve ser nulo para atualizações");
        verify(acaoService, times(1)).atualizar(eq(id), any(), anyList());
    }

    @Test
    @DisplayName("Deve deletar uma ação pelo ID e retornar status 204 No Content")
    void deveDeletar() {
        UUID id = UUID.randomUUID();

        doNothing().when(acaoService).remover(id);

        ResponseEntity<Void> response = acaoController.deletar(id);

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatusCode().value(), "O status HTTP deve ser 204 No Content");
        assertNull(response.getBody(), "O corpo da resposta deve ser nulo para deleções");
        verify(acaoService, times(1)).remover(id);
    }
}
