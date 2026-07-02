package br.com.sol_do_amanhecer.service.impl;

import br.com.sol_do_amanhecer.model.dto.AcaoDTO;
import br.com.sol_do_amanhecer.model.dto.AcaoResponseDTO;
import br.com.sol_do_amanhecer.model.dto.ImagemAcaoDTO;
import br.com.sol_do_amanhecer.model.entity.Acao;
import br.com.sol_do_amanhecer.model.entity.ImagemAcao;
import br.com.sol_do_amanhecer.repository.AcaoRepository;
import br.com.sol_do_amanhecer.repository.ImagemAcaoRepository;
import br.com.sol_do_amanhecer.shared.enums.ETipoAcao;
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

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - AcaoServiceImpl")
class AcaoServiceImplTest {

    @Mock
    private AcaoRepository acaoRepository;

    @Mock
    private ImagemAcaoRepository imagemAcaoRepository;

    @InjectMocks
    private AcaoServiceImpl acaoService;

    private AcaoDTO acaoDTO;
    private Acao acao;
    private UUID acaoId;

    @BeforeEach
    void setUp() {
        acaoId = UUID.randomUUID();

        acaoDTO = AcaoDTO.builder()
                .uuid(acaoId)
                .nome("Ação Social")
                .descricao("Distribuição de alimentos")
                .dataAcao(LocalDate.now())
                .localAcao("Praça Central")
                .tipo(ETipoAcao.SOCIAL_ALIMENTAR)
                .build();

        acao = Acao.builder()
                .uuid(acaoId)
                .nome("Ação Social")
                .descricao("Distribuição de alimentos")
                .dataAcao(LocalDate.now())
                .localAcao("Praça Central")
                .tipo(ETipoAcao.SOCIAL_ALIMENTAR)
                .build();
    }

    @Nested
    @DisplayName("criar")
    class Criar {

        @Test
        @DisplayName("Deve criar ação com imagens")
        void deveCriarAcaoComImagens() {
            ImagemAcaoDTO imagemDTO = ImagemAcaoDTO.builder()
                    .uuidAcao(acaoId)
                    .imagem(new byte[]{1, 2, 3})
                    .build();

            when(acaoRepository.save(any(Acao.class))).thenReturn(acao);
            when(imagemAcaoRepository.save(any(ImagemAcao.class))).thenReturn(new ImagemAcao());

            AcaoDTO resultado = acaoService.criar(acaoDTO, List.of(imagemDTO));

            assertThat(resultado).isNotNull();
            verify(acaoRepository, times(1)).save(any(Acao.class));
            verify(imagemAcaoRepository, times(1)).save(any(ImagemAcao.class));
        }

        @Test
        @DisplayName("Deve criar ação sem imagens")
        void deveCriarAcaoSemImagens() {
            when(acaoRepository.save(any(Acao.class))).thenReturn(acao);

            AcaoDTO resultado = acaoService.criar(acaoDTO, Collections.emptyList());

            assertThat(resultado).isNotNull();
            verify(acaoRepository, times(1)).save(any(Acao.class));
            verify(imagemAcaoRepository, never()).save(any(ImagemAcao.class));
        }
    }

    @Nested
    @DisplayName("atualizar")
    class Atualizar {

        @Test
        @DisplayName("Deve atualizar ação existente")
        void deveAtualizarAcaoExistente() {
            when(acaoRepository.findById(acaoId)).thenReturn(Optional.of(acao));
            when(acaoRepository.save(any(Acao.class))).thenReturn(acao);

            acaoService.atualizar(acaoId, acaoDTO, Collections.emptyList());

            verify(acaoRepository, times(1)).findById(acaoId);
            verify(acaoRepository, times(1)).save(any(Acao.class));
            verify(imagemAcaoRepository, times(1)).deleteAllByAcao(acao);
        }

        @Test
        @DisplayName("Deve lançar exceção ao atualizar ação inexistente")
        void deveAtualizarAcaoInexistente() {
            when(acaoRepository.findById(acaoId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> acaoService.atualizar(acaoId, acaoDTO, Collections.emptyList()))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Ação não encontrada");
        }
    }

    @Nested
    @DisplayName("remover")
    class Remover {

        @Test
        @DisplayName("Deve remover ação e suas imagens")
        void deveRemoverAcao() {
            when(acaoRepository.findById(acaoId)).thenReturn(Optional.of(acao));

            acaoService.remover(acaoId);

            verify(acaoRepository, times(1)).findById(acaoId);
            verify(imagemAcaoRepository, times(1)).deleteAllByAcao(acao);
            verify(acaoRepository, times(1)).deleteById(acaoId);
        }

        @Test
        @DisplayName("Deve lançar exceção ao remover ação inexistente")
        void deveRemoverAcaoInexistente() {
            when(acaoRepository.findById(acaoId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> acaoService.remover(acaoId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Ação não encontrada");
        }
    }

    @Nested
    @DisplayName("buscarPorId")
    class BuscarPorId {

        @Test
        @DisplayName("Deve buscar ação por ID com imagens")
        void deveBuscarAcaoPorIdComImagens() {
            ImagemAcao imagem = new ImagemAcao();
            imagem.setImagem(new byte[]{1, 2, 3});

            when(acaoRepository.findById(acaoId)).thenReturn(Optional.of(acao));
            when(imagemAcaoRepository.findByAcao(acao)).thenReturn(List.of(imagem));

            AcaoResponseDTO resultado = acaoService.buscarPorId(acaoId);

            assertThat(resultado).isNotNull();
            assertThat(resultado.getImagemDTOList())
                    .as("A lista de imagens deve ter exatamente 1 elemento")
                    .hasSize(1);
            verify(acaoRepository, times(1)).findById(acaoId);
            verify(imagemAcaoRepository, times(1)).findByAcao(acao);
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar ação inexistente")
        void deveBuscarAcaoInexistente() {
            when(acaoRepository.findById(acaoId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> acaoService.buscarPorId(acaoId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Ação não encontrada");
        }
    }

    @Nested
    @DisplayName("buscarTodos")
    class BuscarTodos {

        @Test
        @DisplayName("Deve buscar todas as ações sem filtros")
        void deveBuscarTodasAcoesSemFiltros() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Acao> page = new PageImpl<>(List.of(acao), pageable, 1);

            when(acaoRepository.findAll(pageable)).thenReturn(page);
            when(imagemAcaoRepository.findByAcao(acao)).thenReturn(Collections.emptyList());

            Page<AcaoResponseDTO> resultado = acaoService.buscarTodos(null, null, null, pageable);

            assertThat(resultado).isNotNull();
            assertThat(resultado.getContent())
                    .as("O conteúdo paginado deve ter exatamente 1 ação")
                    .hasSize(1);
            verify(acaoRepository, times(1)).findAll(pageable);
        }

        @Test
        @DisplayName("Deve buscar ações filtradas por tipo")
        void deveBuscarAcoesFiltroTipo() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Acao> page = new PageImpl<>(List.of(acao), pageable, 1);

            when(acaoRepository.findByTipo(ETipoAcao.SOCIAL_ALIMENTAR, pageable)).thenReturn(page);
            when(imagemAcaoRepository.findByAcao(acao)).thenReturn(Collections.emptyList());

            Page<AcaoResponseDTO> resultado = acaoService.buscarTodos(ETipoAcao.SOCIAL_ALIMENTAR, null, null, pageable);

            assertThat(resultado).isNotNull();
            verify(acaoRepository, times(1)).findByTipo(ETipoAcao.SOCIAL_ALIMENTAR, pageable);
        }

        @Test
        @DisplayName("Deve buscar ações filtradas por ano e mês")
        void deveBuscarAcoesFiltroAnoMes() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Acao> page = new PageImpl<>(List.of(acao), pageable, 1);

            when(acaoRepository.findByAnoAndMes(2026, 3, pageable)).thenReturn(page);
            when(imagemAcaoRepository.findByAcao(acao)).thenReturn(Collections.emptyList());

            Page<AcaoResponseDTO> resultado = acaoService.buscarTodos(null, 2026, 3, pageable);

            assertThat(resultado).isNotNull();
            verify(acaoRepository, times(1)).findByAnoAndMes(2026, 3, pageable);
        }

        @Test
        @DisplayName("Deve buscar ações filtradas por tipo, ano e mês")
        void deveBuscarAcoesFiltroTipoAnoMes() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Acao> page = new PageImpl<>(List.of(acao), pageable, 1);

            when(acaoRepository.findByTipoAndAnoAndMes(ETipoAcao.SOCIAL_ALIMENTAR, 2026, 3, pageable)).thenReturn(page);
            when(imagemAcaoRepository.findByAcao(acao)).thenReturn(Collections.emptyList());

            Page<AcaoResponseDTO> resultado = acaoService.buscarTodos(ETipoAcao.SOCIAL_ALIMENTAR, 2026, 3, pageable);

            assertThat(resultado).isNotNull();
            verify(acaoRepository, times(1)).findByTipoAndAnoAndMes(ETipoAcao.SOCIAL_ALIMENTAR, 2026, 3, pageable);
        }

        @Test
        @DisplayName("Deve buscar ações filtradas por tipo e ano")
        void deveBuscarAcoesFiltroTipoAno() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Acao> page = new PageImpl<>(List.of(acao), pageable, 1);

            when(acaoRepository.findByTipoAndAno(ETipoAcao.SOCIAL_ALIMENTAR, 2026, pageable)).thenReturn(page);
            when(imagemAcaoRepository.findByAcao(acao)).thenReturn(Collections.emptyList());

            Page<AcaoResponseDTO> resultado = acaoService.buscarTodos(ETipoAcao.SOCIAL_ALIMENTAR, 2026, null, pageable);

            assertThat(resultado).isNotNull();
            verify(acaoRepository, times(1)).findByTipoAndAno(ETipoAcao.SOCIAL_ALIMENTAR, 2026, pageable);
        }

        @Test
        @DisplayName("Deve buscar ações filtradas apenas por ano")
        void deveBuscarAcoesFiltroAno() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Acao> page = new PageImpl<>(List.of(acao), pageable, 1);

            when(acaoRepository.findByAno(2026, pageable)).thenReturn(page);
            when(imagemAcaoRepository.findByAcao(acao)).thenReturn(Collections.emptyList());

            Page<AcaoResponseDTO> resultado = acaoService.buscarTodos(null, 2026, null, pageable);

            assertThat(resultado).isNotNull();
            verify(acaoRepository, times(1)).findByAno(2026, pageable);
        }

        @Test
        @DisplayName("Deve buscar ações filtradas apenas por mês")
        void deveBuscarAcoesFiltroMes() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Acao> page = new PageImpl<>(List.of(acao), pageable, 1);

            when(acaoRepository.findByMes(3, pageable)).thenReturn(page);
            when(imagemAcaoRepository.findByAcao(acao)).thenReturn(Collections.emptyList());

            Page<AcaoResponseDTO> resultado = acaoService.buscarTodos(null, null, 3, pageable);

            assertThat(resultado).isNotNull();
            verify(acaoRepository, times(1)).findByMes(3, pageable);
        }
    }
}
