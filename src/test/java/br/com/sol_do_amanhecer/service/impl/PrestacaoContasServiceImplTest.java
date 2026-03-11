package br.com.sol_do_amanhecer.service.impl;

import br.com.sol_do_amanhecer.model.dto.PrestacaoContasDTO;
import br.com.sol_do_amanhecer.model.entity.PrestacaoContas;
import br.com.sol_do_amanhecer.model.mapper.PrestacaoContasMapper;
import br.com.sol_do_amanhecer.repository.PrestacaoContasRepository;
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

    @Test
    @DisplayName("Deve criar prestação de contas")
    void testCriarPrestacao() {
        when(prestacaoRepository.save(any(PrestacaoContas.class))).thenReturn(prestacao);

        PrestacaoContasDTO resultado = prestacaoService.criar(prestacaoDTO);

        assertThat(resultado).isNotNull();
        verify(prestacaoRepository, times(1)).save(any(PrestacaoContas.class));
    }

    @Test
    @DisplayName("Deve atualizar prestação existente")
    void testAtualizarPrestacao() {
        when(prestacaoRepository.findById(prestacaoId)).thenReturn(Optional.of(prestacao));
        when(prestacaoRepository.save(any(PrestacaoContas.class))).thenReturn(prestacao);

        prestacaoService.atualizar(prestacaoId, prestacaoDTO);

        verify(prestacaoRepository, times(1)).findById(prestacaoId);
        verify(prestacaoRepository, times(1)).save(any(PrestacaoContas.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar prestação inexistente")
    void testAtualizarPrestacaoInexistente() {
        when(prestacaoRepository.findById(prestacaoId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> prestacaoService.atualizar(prestacaoId, prestacaoDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Prestação de contas não encontrada");
    }

    @Test
    @DisplayName("Deve remover prestação")
    void testRemoverPrestacao() {
        when(prestacaoRepository.existsById(prestacaoId)).thenReturn(true);

        prestacaoService.remover(prestacaoId);

        verify(prestacaoRepository, times(1)).deleteById(prestacaoId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao remover prestação inexistente")
    void testRemoverPrestacaoInexistente() {
        when(prestacaoRepository.existsById(prestacaoId)).thenReturn(false);

        assertThatThrownBy(() -> prestacaoService.remover(prestacaoId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Prestação de contas não encontrada");
    }

    @Test
    @DisplayName("Deve buscar prestação por ID")
    void testBuscarPrestacaoPorId() {
        when(prestacaoRepository.findById(prestacaoId)).thenReturn(Optional.of(prestacao));

        PrestacaoContasDTO resultado = prestacaoService.buscarPorId(prestacaoId);

        assertThat(resultado).isNotNull();
        verify(prestacaoRepository, times(1)).findById(prestacaoId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar prestação inexistente")
    void testBuscarPrestacaoInexistente() {
        when(prestacaoRepository.findById(prestacaoId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> prestacaoService.buscarPorId(prestacaoId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Prestação de contas não encontrada");
    }

    @Test
    @DisplayName("Deve buscar prestações sem filtros")
    void testBuscarPrestacoesSemFiltros() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<PrestacaoContas> page = new PageImpl<>(List.of(prestacao), pageable, 1);

        when(prestacaoRepository.findAll(pageable)).thenReturn(page);

        Page<PrestacaoContasDTO> resultado = prestacaoService.buscarTodas(null, null, pageable);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        verify(prestacaoRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve buscar prestações filtradas por período")
    void testBuscarPrestacoesFiltroPeríodo() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<PrestacaoContas> page = new PageImpl<>(List.of(prestacao), pageable, 1);

        when(prestacaoRepository.findByMesEAno(3, 2026, pageable)).thenReturn(page);

        Page<PrestacaoContasDTO> resultado = prestacaoService.buscarTodas(3, 2026, pageable);

        assertThat(resultado).isNotNull();
        verify(prestacaoRepository, times(1)).findByMesEAno(3, 2026, pageable);
    }
}