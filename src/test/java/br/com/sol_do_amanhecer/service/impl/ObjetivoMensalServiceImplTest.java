package br.com.sol_do_amanhecer.service.impl;

import br.com.sol_do_amanhecer.model.dto.ObjetivoMensalDTO;
import br.com.sol_do_amanhecer.model.dto.ObjetivoMensalRequestDTO;
import br.com.sol_do_amanhecer.model.entity.ObjetivoMensal;
import br.com.sol_do_amanhecer.repository.DoacaoRepository;
import br.com.sol_do_amanhecer.repository.ObjetivoMensalRepository;
import br.com.sol_do_amanhecer.repository.PrestacaoContasRepository;
import br.com.sol_do_amanhecer.shared.enums.EMes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

    @Nested
    @DisplayName("criar")
    class Criar {

        @Test
        @DisplayName("Deve criar objetivo mensal")
        void deveCriarObjetivo() {
            when(objetivoRepository.existsByMesAndAno(EMes.JANEIRO, 2026)).thenReturn(false);
            when(objetivoRepository.save(any(ObjetivoMensal.class))).thenReturn(objetivo);

            ObjetivoMensalDTO resultado = objetivoService.criar(requestDTO);

            assertThat(resultado).isNotNull();
            verify(objetivoRepository, times(1)).save(any(ObjetivoMensal.class));
        }

        @Test
        @DisplayName("Deve lançar exceção ao criar objetivo duplicado")
        void deveCriarObjetivoDuplicado() {
            when(objetivoRepository.existsByMesAndAno(EMes.JANEIRO, 2026)).thenReturn(true);

            assertThatThrownBy(() -> objetivoService.criar(requestDTO))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Já existe um objetivo cadastrado para o mês e ano informados.");
        }
    }

    @Nested
    @DisplayName("atualizar")
    class Atualizar {

        @Test
        @DisplayName("Deve atualizar objetivo existente")
        void deveAtualizarObjetivo() {
            when(objetivoRepository.findById(objetivoId)).thenReturn(Optional.of(objetivo));
            when(objetivoRepository.save(any(ObjetivoMensal.class))).thenReturn(objetivo);

            objetivoService.atualizar(objetivoId, requestDTO);

            verify(objetivoRepository, times(1)).findById(objetivoId);
            verify(objetivoRepository, times(1)).save(any(ObjetivoMensal.class));
        }

        @Test
        @DisplayName("Deve lançar exceção ao atualizar objetivo inexistente")
        void deveAtualizarObjetivoInexistente() {
            when(objetivoRepository.findById(objetivoId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> objetivoService.atualizar(objetivoId, requestDTO))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Objetivo Mensal não encontrado");
        }
    }

    @Nested
    @DisplayName("remover")
    class Remover {

        @Test
        @DisplayName("Deve remover objetivo")
        void deveRemoverObjetivo() {
            when(objetivoRepository.existsById(objetivoId)).thenReturn(true);

            objetivoService.remover(objetivoId);

            verify(objetivoRepository, times(1)).deleteById(objetivoId);
        }

        @Test
        @DisplayName("Deve lançar exceção ao remover objetivo inexistente")
        void deveRemoverObjetivoInexistente() {
            when(objetivoRepository.existsById(objetivoId)).thenReturn(false);

            assertThatThrownBy(() -> objetivoService.remover(objetivoId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Objetivo Mensal não encontrado");
        }
    }

    @Nested
    @DisplayName("buscarPorId")
    class BuscarPorId {

        @Test
        @DisplayName("Deve buscar objetivo por ID")
        void deveBuscarObjetivoPorId() {
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
        @DisplayName("Deve calcular percentual quando arrecadado e objetivo são positivos")
        void deveBuscarObjetivoPorId_ComPercentualPositivo() {
            when(objetivoRepository.findById(objetivoId)).thenReturn(Optional.of(objetivo));
            when(doacaoRepository.findTotalByPeriodo(any(), any())).thenReturn(new BigDecimal("2500.00"));
            when(prestacaoRepository.findTotalByPeriodo(any(), any())).thenReturn(new BigDecimal("100.00"));
            when(doacaoRepository.findCountByPeriodo(any(), any())).thenReturn(5L);
            when(prestacaoRepository.findCountByPeriodo(any(), any())).thenReturn(2L);

            ObjetivoMensalDTO resultado = objetivoService.buscarPorId(objetivoId);

            assertThat(resultado).isNotNull();
        }

        @Test
        @DisplayName("Deve retornar percentual zero quando objetivo de arrecadação é zero")
        void deveBuscarObjetivoPorId_ComObjetivoArrecadacaoZero() {
            objetivo.setObjetivoArrecadacao(BigDecimal.ZERO);
            when(objetivoRepository.findById(objetivoId)).thenReturn(Optional.of(objetivo));
            when(doacaoRepository.findTotalByPeriodo(any(), any())).thenReturn(new BigDecimal("2500.00"));
            when(prestacaoRepository.findTotalByPeriodo(any(), any())).thenReturn(BigDecimal.ZERO);
            when(doacaoRepository.findCountByPeriodo(any(), any())).thenReturn(3L);
            when(prestacaoRepository.findCountByPeriodo(any(), any())).thenReturn(0L);

            ObjetivoMensalDTO resultado = objetivoService.buscarPorId(objetivoId);

            assertThat(resultado).isNotNull();
        }

        @Test
        @DisplayName("Deve retornar percentual zero quando repositórios retornam nulo")
        void deveBuscarObjetivoPorId_ComRepositorioRetornandoNulos() {
            when(objetivoRepository.findById(objetivoId)).thenReturn(Optional.of(objetivo));
            when(doacaoRepository.findTotalByPeriodo(any(), any())).thenReturn(null);
            when(prestacaoRepository.findTotalByPeriodo(any(), any())).thenReturn(null);
            when(doacaoRepository.findCountByPeriodo(any(), any())).thenReturn(null);
            when(prestacaoRepository.findCountByPeriodo(any(), any())).thenReturn(null);

            ObjetivoMensalDTO resultado = objetivoService.buscarPorId(objetivoId);

            assertThat(resultado).isNotNull();
        }
    }

    @Nested
    @DisplayName("buscarTodos")
    class BuscarTodos {

        @Test
        @DisplayName("Deve buscar objetivos sem filtros")
        void deveBuscarObjetivosSemFiltros() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<ObjetivoMensal> page = new PageImpl<>(List.of(objetivo), pageable, 1);

            when(objetivoRepository.findAll(pageable)).thenReturn(page);
            when(doacaoRepository.findTotalByPeriodo(any(), any())).thenReturn(BigDecimal.ZERO);
            when(prestacaoRepository.findTotalByPeriodo(any(), any())).thenReturn(BigDecimal.ZERO);
            when(doacaoRepository.findCountByPeriodo(any(), any())).thenReturn(0L);
            when(prestacaoRepository.findCountByPeriodo(any(), any())).thenReturn(0L);

            Page<ObjetivoMensalDTO> resultado = objetivoService.buscarTodos(null, null, pageable);

            assertThat(resultado).isNotNull();
            assertThat(resultado.getContent())
                    .as("O conteúdo paginado deve ter exatamente 1 objetivo")
                    .hasSize(1);
            verify(objetivoRepository, times(1)).findAll(pageable);
        }

        @Test
        @DisplayName("Deve buscar objetivos filtrados por mês e ano")
        void deveBuscarObjetivosFiltroMesAno() {
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

        @Test
        @DisplayName("Deve buscar objetivos filtrados apenas por mês")
        void deveBuscarObjetivosFiltroMes() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<ObjetivoMensal> page = new PageImpl<>(List.of(objetivo), pageable, 1);

            when(objetivoRepository.findByMes(EMes.JANEIRO, pageable)).thenReturn(page);
            when(doacaoRepository.findTotalByPeriodo(any(), any())).thenReturn(BigDecimal.ZERO);
            when(prestacaoRepository.findTotalByPeriodo(any(), any())).thenReturn(BigDecimal.ZERO);
            when(doacaoRepository.findCountByPeriodo(any(), any())).thenReturn(0L);
            when(prestacaoRepository.findCountByPeriodo(any(), any())).thenReturn(0L);

            Page<ObjetivoMensalDTO> resultado = objetivoService.buscarTodos(EMes.JANEIRO, null, pageable);

            assertThat(resultado).isNotNull();
            verify(objetivoRepository, times(1)).findByMes(EMes.JANEIRO, pageable);
        }

        @Test
        @DisplayName("Deve buscar objetivos filtrados apenas por ano")
        void deveBuscarObjetivosFiltroAno() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<ObjetivoMensal> page = new PageImpl<>(List.of(objetivo), pageable, 1);

            when(objetivoRepository.findByAno(2026, pageable)).thenReturn(page);
            when(doacaoRepository.findTotalByPeriodo(any(), any())).thenReturn(BigDecimal.ZERO);
            when(prestacaoRepository.findTotalByPeriodo(any(), any())).thenReturn(BigDecimal.ZERO);
            when(doacaoRepository.findCountByPeriodo(any(), any())).thenReturn(0L);
            when(prestacaoRepository.findCountByPeriodo(any(), any())).thenReturn(0L);

            Page<ObjetivoMensalDTO> resultado = objetivoService.buscarTodos(null, 2026, pageable);

            assertThat(resultado).isNotNull();
            verify(objetivoRepository, times(1)).findByAno(2026, pageable);
        }
    }
}
