package br.com.sol_do_amanhecer.service.impl;

import br.com.sol_do_amanhecer.model.dto.PrestacaoContasDTO;
import br.com.sol_do_amanhecer.model.entity.PrestacaoContas;
import br.com.sol_do_amanhecer.repository.PrestacaoContasRepository;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - PrestacaoContasServiceImpl")
class PrestacaoContasServiceImplTest {

    @Mock
    private PrestacaoContasRepository prestacaoRepository;

    @InjectMocks
    private PrestacaoContasServiceImpl prestacaoService;

    private PrestacaoContasDTO prestacaoDTO;
    private PrestacaoContas prestacao;
    private UUID prestacaoId;

    @BeforeEach
    void setUp() {
        prestacaoId = UUID.randomUUID();

        prestacaoDTO = PrestacaoContasDTO.builder()
                .uuid(prestacaoId)
                .dataTransacao(LocalDate.now())
                .descricaoGasto("Compra de alimentos")
                .destinoGasto("Ação social")
                .valorPago(new BigDecimal("500.00"))
                .estabelecimento("Supermercado XYZ")
                .notaFiscal("NF123456")
                .build();

        prestacao = PrestacaoContas.builder()
                .uuid(prestacaoId)
                .dataTransacao(LocalDate.now())
                .descricaoGasto("Compra de alimentos")
                .destinoGasto("Ação social")
                .valorPago(new BigDecimal("500.00"))
                .estabelecimento("Supermercado XYZ")
                .notaFiscal("NF123456")
                .build();
    }

    @Nested
    @DisplayName("criar")
    class Criar {

        @Test
        @DisplayName("Deve criar prestação de contas")
        void deveCriarPrestacao() {
            when(prestacaoRepository.save(any(PrestacaoContas.class))).thenReturn(prestacao);

            PrestacaoContasDTO resultado = prestacaoService.criar(prestacaoDTO);

            assertThat(resultado).isNotNull();
            verify(prestacaoRepository, times(1)).save(any(PrestacaoContas.class));
        }
    }

    @Nested
    @DisplayName("atualizar")
    class Atualizar {

        @Test
        @DisplayName("Deve atualizar prestação existente")
        void deveAtualizarPrestacao() {
            when(prestacaoRepository.findById(prestacaoId)).thenReturn(Optional.of(prestacao));
            when(prestacaoRepository.save(any(PrestacaoContas.class))).thenReturn(prestacao);

            prestacaoService.atualizar(prestacaoId, prestacaoDTO);

            verify(prestacaoRepository, times(1)).findById(prestacaoId);
            verify(prestacaoRepository, times(1)).save(any(PrestacaoContas.class));
        }

        @Test
        @DisplayName("Deve lançar exceção ao atualizar prestação inexistente")
        void deveAtualizarPrestacaoInexistente() {
            when(prestacaoRepository.findById(prestacaoId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> prestacaoService.atualizar(prestacaoId, prestacaoDTO))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Prestação de contas não encontrada");
        }
    }

    @Nested
    @DisplayName("remover")
    class Remover {

        @Test
        @DisplayName("Deve remover prestação")
        void deveRemoverPrestacao() {
            when(prestacaoRepository.existsById(prestacaoId)).thenReturn(true);

            prestacaoService.remover(prestacaoId);

            verify(prestacaoRepository, times(1)).deleteById(prestacaoId);
        }

        @Test
        @DisplayName("Deve lançar exceção ao remover prestação inexistente")
        void deveRemoverPrestacaoInexistente() {
            when(prestacaoRepository.existsById(prestacaoId)).thenReturn(false);

            assertThatThrownBy(() -> prestacaoService.remover(prestacaoId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Prestação de contas não encontrada");
        }
    }

    @Nested
    @DisplayName("buscarPorId")
    class BuscarPorId {

        @Test
        @DisplayName("Deve buscar prestação por ID")
        void deveBuscarPrestacaoPorId() {
            when(prestacaoRepository.findById(prestacaoId)).thenReturn(Optional.of(prestacao));

            PrestacaoContasDTO resultado = prestacaoService.buscarPorId(prestacaoId);

            assertThat(resultado).isNotNull();
            verify(prestacaoRepository, times(1)).findById(prestacaoId);
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar prestação inexistente")
        void deveBuscarPrestacaoInexistente() {
            when(prestacaoRepository.findById(prestacaoId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> prestacaoService.buscarPorId(prestacaoId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Prestação de contas não encontrada");
        }
    }

    @Nested
    @DisplayName("buscarTodas")
    class BuscarTodas {

        @Test
        @DisplayName("Deve buscar prestações sem filtros")
        void deveBuscarPrestacoesSemFiltros() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<PrestacaoContas> page = new PageImpl<>(List.of(prestacao), pageable, 1);

            when(prestacaoRepository.findAll(pageable)).thenReturn(page);

            Page<PrestacaoContasDTO> resultado = prestacaoService.buscarTodas(null, null, pageable);

            assertThat(resultado).isNotNull();
            assertThat(resultado.getContent()).as("O conteúdo paginado deve ter exatamente 1 prestação").hasSize(1);
            verify(prestacaoRepository, times(1)).findAll(pageable);
        }

        @Test
        @DisplayName("Deve buscar prestações filtradas por período")
        void deveBuscarPrestacoesFiltroPeríodo() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<PrestacaoContas> page = new PageImpl<>(List.of(prestacao), pageable, 1);

            when(prestacaoRepository.findByMesEAno(3, 2026, pageable)).thenReturn(page);

            Page<PrestacaoContasDTO> resultado = prestacaoService.buscarTodas(3, 2026, pageable);

            assertThat(resultado).isNotNull();
            verify(prestacaoRepository, times(1)).findByMesEAno(3, 2026, pageable);
        }

        @Test
        @DisplayName("Deve buscar prestações filtradas apenas por mês")
        void deveBuscarPrestacoesFiltroMes() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<PrestacaoContas> page = new PageImpl<>(List.of(prestacao), pageable, 1);

            when(prestacaoRepository.findByMes(3, pageable)).thenReturn(page);

            Page<PrestacaoContasDTO> resultado = prestacaoService.buscarTodas(3, null, pageable);

            assertThat(resultado).isNotNull();
            verify(prestacaoRepository, times(1)).findByMes(3, pageable);
        }

        @Test
        @DisplayName("Deve buscar prestações filtradas apenas por ano")
        void deveBuscarPrestacoesFiltroAno() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<PrestacaoContas> page = new PageImpl<>(List.of(prestacao), pageable, 1);

            when(prestacaoRepository.findByAno(2026, pageable)).thenReturn(page);

            Page<PrestacaoContasDTO> resultado = prestacaoService.buscarTodas(null, 2026, pageable);

            assertThat(resultado).isNotNull();
            verify(prestacaoRepository, times(1)).findByAno(2026, pageable);
        }
    }
}
