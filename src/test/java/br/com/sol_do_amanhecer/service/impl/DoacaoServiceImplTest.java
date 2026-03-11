package br.com.sol_do_amanhecer.service.impl;

import br.com.sol_do_amanhecer.model.dto.DoacaoDTO;
import br.com.sol_do_amanhecer.model.entity.Doacao;
import br.com.sol_do_amanhecer.model.mapper.DoacaoMapper;
import br.com.sol_do_amanhecer.repository.DoacaoRepository;
import br.com.sol_do_amanhecer.shared.enums.EMeioDoacao;
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
@DisplayName("Testes Unitários - DoacaoServiceImpl")
class DoacaoServiceImplTest {

    @Mock
    private DoacaoRepository doacaoRepository;

    @InjectMocks
    private DoacaoServiceImpl doacaoService;

    private DoacaoDTO doacaoDTO;
    private Doacao doacao;
    private UUID doacaoId;

    @BeforeEach
    void setUp() {
        doacaoId = UUID.randomUUID();

        doacaoDTO = DoacaoDTO.builder()
                .uuid(doacaoId)
                .dataDoacao(LocalDate.now())
                .nomeDoador("João Silva")
                .meioDoacao(EMeioDoacao.PIX)
                .valor(new BigDecimal("100.00"))
                .build();

        doacao = Doacao.builder()
                .uuid(doacaoId)
                .dataDoacao(LocalDate.now())
                .nomeDoador("João Silva")
                .meioDoacao(EMeioDoacao.PIX)
                .valor(new BigDecimal("100.00"))
                .build();
    }

    @Nested
    @DisplayName("Criar Doação")
    class CriarDoacao {
        @Test
        @DisplayName("Deve criar doação com sucesso")
        void testCriarDoacao() {
            when(doacaoRepository.save(any(Doacao.class))).thenReturn(doacao);

            DoacaoDTO resultado = doacaoService.criar(doacaoDTO);

            assertThat(resultado).isNotNull();
            verify(doacaoRepository, times(1)).save(any(Doacao.class));
        }

        @Test
        @DisplayName("Deve criar doação com diferentes meios")
        void testCriarDoacaoComDiferentesMeios() {
            for (EMeioDoacao meio : EMeioDoacao.values()) {
                DoacaoDTO dto = DoacaoDTO.builder()
                        .dataDoacao(LocalDate.now())
                        .nomeDoador("Doador")
                        .meioDoacao(meio)
                        .valor(new BigDecimal("100.00"))
                        .build();

                Doacao doacaoComMeio = Doacao.builder()
                        .uuid(UUID.randomUUID())
                        .dataDoacao(LocalDate.now())
                        .nomeDoador("Doador")
                        .meioDoacao(meio)
                        .valor(new BigDecimal("100.00"))
                        .build();

                when(doacaoRepository.save(any(Doacao.class))).thenReturn(doacaoComMeio);

                DoacaoDTO resultado = doacaoService.criar(dto);

                assertThat(resultado).isNotNull();
            }
        }
    }

    @Nested
    @DisplayName("Atualizar Doação")
    class AtualizarDoacao {
        @Test
        @DisplayName("Deve atualizar doação existente")
        void testAtualizarDoacao() {
            when(doacaoRepository.findById(doacaoId)).thenReturn(Optional.of(doacao));
            when(doacaoRepository.save(any(Doacao.class))).thenReturn(doacao);

            doacaoService.atualizar(doacaoId, doacaoDTO);

            verify(doacaoRepository, times(1)).findById(doacaoId);
            verify(doacaoRepository, times(1)).save(any(Doacao.class));
        }

        @Test
        @DisplayName("Deve lançar exceção ao atualizar doação inexistente")
        void testAtualizarDoacaoInexistente() {
            when(doacaoRepository.findById(doacaoId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> doacaoService.atualizar(doacaoId, doacaoDTO))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Doação não encontrada");
        }
    }

    @Nested
    @DisplayName("Remover Doação")
    class RemoverDoacao {
        @Test
        @DisplayName("Deve remover doação")
        void testRemoverDoacao() {
            when(doacaoRepository.existsById(doacaoId)).thenReturn(true);

            doacaoService.remover(doacaoId);

            verify(doacaoRepository, times(1)).deleteById(doacaoId);
        }

        @Test
        @DisplayName("Deve lançar exceção ao remover doação inexistente")
        void testRemoverDoacaoInexistente() {
            when(doacaoRepository.existsById(doacaoId)).thenReturn(false);

            assertThatThrownBy(() -> doacaoService.remover(doacaoId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Doação não encontrada");
        }
    }

    @Nested
    @DisplayName("Buscar Doação")
    class BuscarDoacao {
        @Test
        @DisplayName("Deve buscar doação por ID")
        void testBuscarDoacaoPorId() {
            when(doacaoRepository.findById(doacaoId)).thenReturn(Optional.of(doacao));

            DoacaoDTO resultado = doacaoService.buscarPorId(doacaoId);

            assertThat(resultado).isNotNull();
            verify(doacaoRepository, times(1)).findById(doacaoId);
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar doação inexistente")
        void testBuscarDoacaoInexistente() {
            when(doacaoRepository.findById(doacaoId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> doacaoService.buscarPorId(doacaoId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Doação não encontrada");
        }
    }

    @Nested
    @DisplayName("Listar Doações")
    class ListarDoacoes {
        @Test
        @DisplayName("Deve buscar doações sem filtros")
        void testBuscarDoacoesSemFiltros() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Doacao> page = new PageImpl<>(List.of(doacao), pageable, 1);

            when(doacaoRepository.findAll(pageable)).thenReturn(page);

            Page<DoacaoDTO> resultado = doacaoService.buscarTodas(null, null, null, pageable);

            assertThat(resultado).isNotNull();
            assertThat(resultado.getContent()).hasSize(1);
            verify(doacaoRepository, times(1)).findAll(pageable);
        }

        @Test
        @DisplayName("Deve buscar doações filtradas por meio")
        void testBuscarDoacoesFiltroMeio() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Doacao> page = new PageImpl<>(List.of(doacao), pageable, 1);

            when(doacaoRepository.findByMeioDoacao(EMeioDoacao.PIX, pageable)).thenReturn(page);

            Page<DoacaoDTO> resultado = doacaoService.buscarTodas(null, null, EMeioDoacao.PIX, pageable);

            assertThat(resultado).isNotNull();
            verify(doacaoRepository, times(1)).findByMeioDoacao(EMeioDoacao.PIX, pageable);
        }

        @Test
        @DisplayName("Deve buscar doações filtradas por período")
        void testBuscarDoacoesFiltroPeríodo() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Doacao> page = new PageImpl<>(List.of(doacao), pageable, 1);

            when(doacaoRepository.findByAnoAndMes(2026, 3, pageable)).thenReturn(page);

            Page<DoacaoDTO> resultado = doacaoService.buscarTodas(2026, 3, null, pageable);

            assertThat(resultado).isNotNull();
            verify(doacaoRepository, times(1)).findByAnoAndMes(2026, 3, pageable);
        }

        @Test
        @DisplayName("Deve buscar doações com múltiplos filtros")
        void testBuscarDoacoesComMultiplosFiltros() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Doacao> page = new PageImpl<>(List.of(doacao), pageable, 1);

            when(doacaoRepository.findByMeioDoacaoAndAnoAndMes(EMeioDoacao.PIX, 2026, 3, pageable))
                    .thenReturn(page);

            Page<DoacaoDTO> resultado = doacaoService.buscarTodas(2026, 3, EMeioDoacao.PIX, pageable);

            assertThat(resultado).isNotNull();
            verify(doacaoRepository, times(1))
                    .findByMeioDoacaoAndAnoAndMes(EMeioDoacao.PIX, 2026, 3, pageable);
        }
    }
}