package br.com.sol_do_amanhecer.service.impl;

import br.com.sol_do_amanhecer.model.dto.ObjetivoMensalDTO;
import br.com.sol_do_amanhecer.model.dto.ObjetivoMensalRequestDTO;
import br.com.sol_do_amanhecer.model.entity.ObjetivoMensal;
import br.com.sol_do_amanhecer.model.mapper.ObjetivoMensalMapper;
import br.com.sol_do_amanhecer.repository.DoacaoRepository;
import br.com.sol_do_amanhecer.repository.ObjetivoMensalRepository;
import br.com.sol_do_amanhecer.repository.PrestacaoContasRepository;
import br.com.sol_do_amanhecer.shared.enums.EMes;
import org.junit.jupiter.api.BeforeEach;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - ObjetivoMensalServiceImpl")
class ObjetivoMensalServiceImplTest {

    @Mock
    private ObjetivoMensalRepository objetivoRepository;

    @Mock
    private DoacaoRepository doacaoRepository;

    @Mock
    private PrestacaoContasRepository prestacaoRepository;

    @InjectMocks
    private ObjetivoMensalServiceImpl objetivoService;

    private ObjetivoMensalRequestDTO requestDTO;
    private ObjetivoMensal objetivo;
    private UUID objetivoId;

    @BeforeEach
    void setUp() {
        objetivoId = UUID.randomUUID();

        requestDTO = ObjetivoMensalRequestDTO.builder()
                .titulo("Objetivo Janeiro")
                .descricao("Arrecadar fundos")
                .mes(EMes.JANEIRO)
                .ano(2026)
                .objetivoArrecadacao(new BigDecimal("5000.00"))
                .build();

        objetivo = ObjetivoMensal.builder()
                .uuid(objetivoId)
                .titulo("Objetivo Janeiro")
                .descricao("Arrecadar fundos")
                .mes(EMes.JANEIRO)
                .ano(2026)
                .objetivoArrecadacao(new BigDecimal("5000.00"))
                .build();
    }

    @Test
    @DisplayName("Deve criar objetivo mensal")
    void testCriarObjetivo() {
        when(objetivoRepository.existsByMesAndAno(EMes.JANEIRO, 2026)).thenReturn(false);
        when(objetivoRepository.save(any(ObjetivoMensal.class))).thenReturn(objetivo);

        ObjetivoMensalDTO resultado = objetivoService.criar(requestDTO);

        assertThat(resultado).isNotNull();
        verify(objetivoRepository, times(1)).save(any(ObjetivoMensal.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar objetivo duplicado")
    void testCriarObjetivoDuplicado() {
        when(objetivoRepository.existsByMesAndAno(EMes.JANEIRO, 2026)).thenReturn(true);

        assertThatThrownBy(() -> objetivoService.criar(requestDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Já existe um objetivo cadastrado para o mês e ano informados.");
    }

    @Test
    @DisplayName("Deve atualizar objetivo existente")
    void testAtualizarObjetivo() {
        when(objetivoRepository.findById(objetivoId)).thenReturn(Optional.of(objetivo));
        when(objetivoRepository.save(any(ObjetivoMensal.class))).thenReturn(objetivo);

        objetivoService.atualizar(objetivoId, requestDTO);

        verify(objetivoRepository, times(1)).findById(objetivoId);
        verify(objetivoRepository, times(1)).save(any(ObjetivoMensal.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar objetivo inexistente")
    void testAtualizarObjetivoInexistente() {
        when(objetivoRepository.findById(objetivoId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> objetivoService.atualizar(objetivoId, requestDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Objetivo Mensal não encontrado");
    }

    @Test
    @DisplayName("Deve remover objetivo")
    void testRemoverObjetivo() {
        when(objetivoRepository.existsById(objetivoId)).thenReturn(true);

        objetivoService.remover(objetivoId);

        verify(objetivoRepository, times(1)).deleteById(objetivoId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao remover objetivo inexistente")
    void testRemoverObjetivoInexistente() {
        when(objetivoRepository.existsById(objetivoId)).thenReturn(false);

        assertThatThrownBy(() -> objetivoService.remover(objetivoId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Objetivo Mensal não encontrado");
    }

    @Test
    @DisplayName("Deve buscar objetivo por ID")
    void testBuscarObjetivoPorId() {
        when(objetivoRepository.findById(objetivoId)).thenReturn(Optional.of(objetivo));
        when(doacaoRepository.findTotalByPeriodo(any(), any())).thenReturn(BigDecimal.ZERO);
        when(prestacaoRepository.findTotalByPeriodo(any(), any())).thenReturn(BigDecimal.ZERO);
        when(doacaoRepository.findCountByPeriodo(any(), any())).thenReturn(0L);
        when(prestacaoRepository.findCountByPeriodo(any(), any())).thenReturn(0L);

        ObjetivoMensalDTO resultado = objetivoService.buscarPorId(objetivoId);

        assertThat(resultado).isNotNull();
        verify(objetivoRepository, times(1)).findById(objetivoId);
    }

    @Test
    @DisplayName("Deve buscar objetivos sem filtros")
    void testBuscarObjetivosSemFiltros() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ObjetivoMensal> page = new PageImpl<>(List.of(objetivo), pageable, 1);

        when(objetivoRepository.findAll(pageable)).thenReturn(page);
        when(doacaoRepository.findTotalByPeriodo(any(), any())).thenReturn(BigDecimal.ZERO);
        when(prestacaoRepository.findTotalByPeriodo(any(), any())).thenReturn(BigDecimal.ZERO);
        when(doacaoRepository.findCountByPeriodo(any(), any())).thenReturn(0L);
        when(prestacaoRepository.findCountByPeriodo(any(), any())).thenReturn(0L);

        Page<ObjetivoMensalDTO> resultado = objetivoService.buscarTodos(null, null, pageable);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        verify(objetivoRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve buscar objetivos filtrados por mês e ano")
    void testBuscarObjetivosFiltroMesAno() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ObjetivoMensal> page = new PageImpl<>(List.of(objetivo), pageable, 1);

        when(objetivoRepository.findByMesAndAno(EMes.JANEIRO, 2026, pageable)).thenReturn(page);
        when(doacaoRepository.findTotalByPeriodo(any(), any())).thenReturn(BigDecimal.ZERO);
        when(prestacaoRepository.findTotalByPeriodo(any(), any())).thenReturn(BigDecimal.ZERO);
        when(doacaoRepository.findCountByPeriodo(any(), any())).thenReturn(0L);
        when(prestacaoRepository.findCountByPeriodo(any(), any())).thenReturn(0L);

        Page<ObjetivoMensalDTO> resultado = objetivoService.buscarTodos(EMes.JANEIRO, 2026, pageable);

        assertThat(resultado).isNotNull();
        verify(objetivoRepository, times(1)).findByMesAndAno(EMes.JANEIRO, 2026, pageable);
    }
}