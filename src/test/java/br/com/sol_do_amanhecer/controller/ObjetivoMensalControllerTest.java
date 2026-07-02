package br.com.sol_do_amanhecer.controller;

import br.com.sol_do_amanhecer.model.dto.ObjetivoMensalDTO;
import br.com.sol_do_amanhecer.model.dto.ObjetivoMensalRequestDTO;
import br.com.sol_do_amanhecer.service.ObjetivoMensalService;
import br.com.sol_do_amanhecer.shared.enums.EMes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de ObjetivoMensalController")
class ObjetivoMensalControllerTest {

    @Mock
    private ObjetivoMensalService objetivoMensalService;

    @InjectMocks
    private ObjetivoMensalController objetivoMensalController;

    @Test
    @DisplayName("Deve criar um novo objetivo mensal com sucesso")
    void deveCriarObjetivoMensal() {
        ObjetivoMensalRequestDTO requestDTO = new ObjetivoMensalRequestDTO();
        ObjetivoMensalDTO responseDTO = new ObjetivoMensalDTO();

        when(objetivoMensalService.criar(requestDTO)).thenReturn(responseDTO);

        ResponseEntity<ObjetivoMensalDTO> response = objetivoMensalController.criar(requestDTO);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value(), "O status HTTP deve ser 200 OK");
        assertEquals(responseDTO, response.getBody(), "O corpo deve ser o DTO retornado pelo serviço");
        verify(objetivoMensalService, times(1)).criar(requestDTO);
    }

    @Test
    @DisplayName("Deve buscar um objetivo mensal por ID")
    void deveBuscarPorId() {
        UUID id = UUID.randomUUID();
        ObjetivoMensalDTO dto = new ObjetivoMensalDTO();

        when(objetivoMensalService.buscarPorId(id)).thenReturn(dto);

        ResponseEntity<ObjetivoMensalDTO> response = objetivoMensalController.buscarPorId(id);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value(), "O status HTTP deve ser 200 OK");
        assertEquals(dto, response.getBody(), "O corpo deve ser o DTO do objetivo buscado");
        verify(objetivoMensalService, times(1)).buscarPorId(id);
    }

    @Test
    @DisplayName("Deve buscar todos os objetivos mensais com paginação e filtros")
    void deveBuscarTodos() {
        int page = 0;
        int size = 10;
        EMes mes = EMes.JANEIRO;
        Integer ano = 2024;
        Sort sort = Sort.by("ano").descending().and(Sort.by("mes").descending());
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ObjetivoMensalDTO> objetivos = new PageImpl<>(List.of(new ObjetivoMensalDTO()), pageable, 1);

        when(objetivoMensalService.buscarTodos(mes, ano, pageable)).thenReturn(objetivos);

        ResponseEntity<Page<ObjetivoMensalDTO>> response = objetivoMensalController.buscarTodos(page, size, mes, ano);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value(), "O status HTTP deve ser 200 OK");
        assertEquals(objetivos, response.getBody(), "O corpo deve ser a página de objetivos retornada pelo serviço");
        verify(objetivoMensalService, times(1)).buscarTodos(mes, ano, pageable);
    }

    @Test
    @DisplayName("Deve atualizar um objetivo mensal existente")
    void deveAtualizar() {
        UUID id = UUID.randomUUID();
        ObjetivoMensalRequestDTO requestDTO = new ObjetivoMensalRequestDTO();

        doNothing().when(objetivoMensalService).atualizar(id, requestDTO);

        ResponseEntity<Void> response = objetivoMensalController.atualizar(id, requestDTO);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value(), "O status HTTP deve ser 200 OK");
        assertNull(response.getBody(), "O corpo da resposta deve ser nulo para atualizações");
        verify(objetivoMensalService, times(1)).atualizar(id, requestDTO);
    }

    @Test
    @DisplayName("Deve deletar um objetivo mensal existente")
    void deveDeletar() {
        UUID id = UUID.randomUUID();

        doNothing().when(objetivoMensalService).remover(id);

        ResponseEntity<Void> response = objetivoMensalController.deletar(id);

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatusCode().value(), "O status HTTP deve ser 204 No Content");
        assertNull(response.getBody(), "O corpo da resposta deve ser nulo para deleções");
        verify(objetivoMensalService, times(1)).remover(id);
    }
}
