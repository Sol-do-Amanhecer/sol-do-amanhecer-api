package br.com.sol_do_amanhecer.service.impl;

import br.com.sol_do_amanhecer.model.dto.DoacaoDTO;
import br.com.sol_do_amanhecer.model.entity.Doacao;
import br.com.sol_do_amanhecer.repository.DoacaoRepository;
import br.com.sol_do_amanhecer.shared.enums.EMeioDoacao;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DoacaoServiceImpl Tests")
class DoacaoServiceImplTest {

    @Mock
    private DoacaoRepository doacaoRepository;

    @InjectMocks
    private DoacaoServiceImpl doacaoService;

    private UUID doacaoId;
    private DoacaoDTO doacaoDTO;
    private Doacao doacao;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        doacaoId = UUID.randomUUID();
        byte[] comprovante = "comprovante-dados".getBytes();

        doacaoDTO = DoacaoDTO.builder()
                .dataDoacao(LocalDate.now())
                .nomeDoador("João Silva")
                .meioDoacao(EMeioDoacao.PIX)
                .valor(BigDecimal.valueOf(100.00))
                .comprovante(comprovante)
                .build();

        doacao = Doacao.builder()
                .uuid(doacaoId)
                .dataDoacao(LocalDate.now())
                .nomeDoador("João Silva")
                .meioDoacao(EMeioDoacao.PIX)
                .valor(BigDecimal.valueOf(100.00))
                .comprovante(comprovante)
                .build();

        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("Deve criar uma doação com sucesso")
    void deveCriarDoacaoComSucesso() {
        
        when(doacaoRepository.save(any(Doacao.class))).thenReturn(doacao);

        
        DoacaoDTO resultado = doacaoService.criar(doacaoDTO);

        
        assertNotNull(resultado);
        assertEquals(doacaoDTO.getNomeDoador(), resultado.getNomeDoador());
        assertEquals(doacaoDTO.getDataDoacao(), resultado.getDataDoacao());
        assertEquals(doacaoDTO.getMeioDoacao(), resultado.getMeioDoacao());
        assertEquals(doacaoDTO.getValor(), resultado.getValor());
        assertArrayEquals(doacaoDTO.getComprovante(), resultado.getComprovante());

        verify(doacaoRepository, times(1)).save(any(Doacao.class));
    }

    @Test
    @DisplayName("Deve criar doação com valores diferentes")
    void deveCriarDoacaoComValoresDiferentes() {
        
        DoacaoDTO doacaoDiferente = DoacaoDTO.builder()
                .dataDoacao(LocalDate.of(2023, 5, 15))
                .nomeDoador("Maria Santos")
                .meioDoacao(EMeioDoacao.CARTAO_CREDITO)
                .valor(BigDecimal.valueOf(250.50))
                .comprovante("outro-comprovante".getBytes())
                .build();

        Doacao doacaoSalva = Doacao.builder()
                .uuid(UUID.randomUUID())
                .dataDoacao(LocalDate.of(2023, 5, 15))
                .nomeDoador("Maria Santos")
                .meioDoacao(EMeioDoacao.CARTAO_CREDITO)
                .valor(BigDecimal.valueOf(250.50))
                .comprovante("outro-comprovante".getBytes())
                .build();

        when(doacaoRepository.save(any(Doacao.class))).thenReturn(doacaoSalva);

        
        DoacaoDTO resultado = doacaoService.criar(doacaoDiferente);

        
        assertNotNull(resultado);
        assertEquals("Maria Santos", resultado.getNomeDoador());
        assertEquals(EMeioDoacao.CARTAO_CREDITO, resultado.getMeioDoacao());
        assertEquals(BigDecimal.valueOf(250.50), resultado.getValor());

        verify(doacaoRepository, times(1)).save(any(Doacao.class));
    }

    @Test
    @DisplayName("Deve atualizar uma doação com sucesso")
    void deveAtualizarDoacaoComSucesso() {
        
        DoacaoDTO doacaoAtualizada = DoacaoDTO.builder()
                .dataDoacao(LocalDate.of(2023, 12, 25))
                .nomeDoador("João Silva Atualizado")
                .meioDoacao(EMeioDoacao.CARTAO_DEBITO)
                .valor(BigDecimal.valueOf(200.00))
                .comprovante("novo-comprovante".getBytes())
                .build();

        when(doacaoRepository.findById(doacaoId)).thenReturn(Optional.of(doacao));
        when(doacaoRepository.save(any(Doacao.class))).thenReturn(doacao);

        
        assertDoesNotThrow(() -> doacaoService.atualizar(doacaoId, doacaoAtualizada));

        
        verify(doacaoRepository, times(1)).findById(doacaoId);
        verify(doacaoRepository, times(1)).save(any(Doacao.class));

        assertEquals(doacaoAtualizada.getDataDoacao(), doacao.getDataDoacao());
        assertEquals(doacaoAtualizada.getNomeDoador(), doacao.getNomeDoador());
        assertEquals(doacaoAtualizada.getMeioDoacao(), doacao.getMeioDoacao());
        assertEquals(doacaoAtualizada.getValor(), doacao.getValor());
        assertArrayEquals(doacaoAtualizada.getComprovante(), doacao.getComprovante());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar doação inexistente")
    void deveLancarExcecaoAoAtualizarDoacaoInexistente() {
        
        when(doacaoRepository.findById(doacaoId)).thenReturn(Optional.empty());

         
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> doacaoService.atualizar(doacaoId, doacaoDTO));

        assertEquals("Doação não encontrada", exception.getMessage());
        verify(doacaoRepository, times(1)).findById(doacaoId);
        verify(doacaoRepository, never()).save(any(Doacao.class));
    }

    @Test
    @DisplayName("Deve remover uma doação com sucesso")
    void deveRemoverDoacaoComSucesso() {
        
        when(doacaoRepository.existsById(doacaoId)).thenReturn(true);
        doNothing().when(doacaoRepository).deleteById(doacaoId);

        
        assertDoesNotThrow(() -> doacaoService.remover(doacaoId));

        
        verify(doacaoRepository, times(1)).existsById(doacaoId);
        verify(doacaoRepository, times(1)).deleteById(doacaoId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar remover doação inexistente")
    void deveLancarExcecaoAoRemoverDoacaoInexistente() {
        
        when(doacaoRepository.existsById(doacaoId)).thenReturn(false);

         
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> doacaoService.remover(doacaoId));

        assertEquals("Doação não encontrada", exception.getMessage());
        verify(doacaoRepository, times(1)).existsById(doacaoId);
        verify(doacaoRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    @DisplayName("Deve buscar doação por ID com sucesso")
    void deveBuscarDoacaoPorIdComSucesso() {
        
        when(doacaoRepository.findById(doacaoId)).thenReturn(Optional.of(doacao));

        
        DoacaoDTO resultado = doacaoService.buscarPorId(doacaoId);

        
        assertNotNull(resultado);
        assertEquals(doacao.getNomeDoador(), resultado.getNomeDoador());
        assertEquals(doacao.getDataDoacao(), resultado.getDataDoacao());
        assertEquals(doacao.getMeioDoacao(), resultado.getMeioDoacao());
        assertEquals(doacao.getValor(), resultado.getValor());
        assertArrayEquals(doacao.getComprovante(), resultado.getComprovante());

        verify(doacaoRepository, times(1)).findById(doacaoId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar doação inexistente por ID")
    void deveLancarExcecaoAoBuscarDoacaoInexistentePorId() {
        
        when(doacaoRepository.findById(doacaoId)).thenReturn(Optional.empty());

         
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> doacaoService.buscarPorId(doacaoId));

        assertEquals("Doação não encontrada", exception.getMessage());
        verify(doacaoRepository, times(1)).findById(doacaoId);
    }

    @Test
    @DisplayName("Deve buscar todas as doações sem filtros")
    void deveBuscarTodasDoacoesSemFiltros() {
        
        Page<Doacao> pageDoacoes = new PageImpl<>(Collections.singletonList(doacao));
        when(doacaoRepository.findAll(pageable)).thenReturn(pageDoacoes);

        
        Page<DoacaoDTO> resultado = doacaoService.buscarTodas(null, null, null, pageable);

        
        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        assertEquals(doacao.getNomeDoador(), resultado.getContent().get(0).getNomeDoador());

        verify(doacaoRepository, times(1)).findAll(pageable);
        verify(doacaoRepository, never()).findByMeioDoacao(any(), any());
        verify(doacaoRepository, never()).findByAnoAndMes(any(), any(), any());
        verify(doacaoRepository, never()).findByMeioDoacaoAndAnoAndMes(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Deve buscar doações filtradas por meio de doação, ano e mês")
    void deveBuscarDoacoesFiltradas_MeioDoacaoAnoMes() {
        
        Page<Doacao> pageDoacoes = new PageImpl<>(Collections.singletonList(doacao));
        when(doacaoRepository.findByMeioDoacaoAndAnoAndMes(EMeioDoacao.PIX, 2023, 12, pageable))
                .thenReturn(pageDoacoes);

        
        Page<DoacaoDTO> resultado = doacaoService.buscarTodas(2023, 12, EMeioDoacao.PIX, pageable);

        
        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        assertEquals(doacao.getNomeDoador(), resultado.getContent().get(0).getNomeDoador());

        verify(doacaoRepository, times(1)).findByMeioDoacaoAndAnoAndMes(EMeioDoacao.PIX, 2023, 12, pageable);
        verify(doacaoRepository, never()).findAll(any(Pageable.class));
        verify(doacaoRepository, never()).findByMeioDoacao(any(), any());
        verify(doacaoRepository, never()).findByAnoAndMes(any(), any(), any());
    }

    @Test
    @DisplayName("Deve buscar doações filtradas apenas por meio de doação")
    void deveBuscarDoacoesFiltradas_MeioDoacao() {
        
        Page<Doacao> pageDoacoes = new PageImpl<>(Collections.singletonList(doacao));
        when(doacaoRepository.findByMeioDoacao(EMeioDoacao.PIX, pageable)).thenReturn(pageDoacoes);

        
        Page<DoacaoDTO> resultado = doacaoService.buscarTodas(null, null, EMeioDoacao.PIX, pageable);

        
        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        assertEquals(doacao.getNomeDoador(), resultado.getContent().get(0).getNomeDoador());

        verify(doacaoRepository, times(1)).findByMeioDoacao(EMeioDoacao.PIX, pageable);
        verify(doacaoRepository, never()).findAll(any(Pageable.class));
        verify(doacaoRepository, never()).findByAnoAndMes(any(), any(), any());
        verify(doacaoRepository, never()).findByMeioDoacaoAndAnoAndMes(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Deve buscar doações filtradas por ano e mês")
    void deveBuscarDoacoesFiltradas_AnoMes() {
        
        Page<Doacao> pageDoacoes = new PageImpl<>(Collections.singletonList(doacao));
        when(doacaoRepository.findByAnoAndMes(2023, 12, pageable)).thenReturn(pageDoacoes);

        
        Page<DoacaoDTO> resultado = doacaoService.buscarTodas(2023, 12, null, pageable);

        
        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        assertEquals(doacao.getNomeDoador(), resultado.getContent().get(0).getNomeDoador());

        verify(doacaoRepository, times(1)).findByAnoAndMes(2023, 12, pageable);
        verify(doacaoRepository, never()).findAll(any(Pageable.class));
        verify(doacaoRepository, never()).findByMeioDoacao(any(), any());
        verify(doacaoRepository, never()).findByMeioDoacaoAndAnoAndMes(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Deve buscar doações com meio de doação e apenas ano (sem mês)")
    void deveBuscarDoacoesFiltradas_MeioDoacaoApenasAno() {
        
        Page<Doacao> pageDoacoes = new PageImpl<>(Collections.singletonList(doacao));
        when(doacaoRepository.findByMeioDoacao(EMeioDoacao.PIX, pageable)).thenReturn(pageDoacoes);

        
        Page<DoacaoDTO> resultado = doacaoService.buscarTodas(2023, null, EMeioDoacao.PIX, pageable);

        
        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());

        verify(doacaoRepository, times(1)).findByMeioDoacao(EMeioDoacao.PIX, pageable);
        verify(doacaoRepository, never()).findByMeioDoacaoAndAnoAndMes(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Deve buscar doações com meio de doação e apenas mês (sem ano)")
    void deveBuscarDoacoesFiltradas_MeioDoacaoApenasMes() {
        
        Page<Doacao> pageDoacoes = new PageImpl<>(Collections.singletonList(doacao));
        when(doacaoRepository.findByMeioDoacao(EMeioDoacao.PIX, pageable)).thenReturn(pageDoacoes);

        
        Page<DoacaoDTO> resultado = doacaoService.buscarTodas(null, 12, EMeioDoacao.PIX, pageable);

        
        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());

        verify(doacaoRepository, times(1)).findByMeioDoacao(EMeioDoacao.PIX, pageable);
        verify(doacaoRepository, never()).findByAnoAndMes(any(), any(), any());
        verify(doacaoRepository, never()).findByMeioDoacaoAndAnoAndMes(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Deve buscar doações apenas com ano (sem mês e meio de doação)")
    void deveBuscarDoacoesFiltradas_ApenasAno() {
        
        Page<Doacao> pageDoacoes = new PageImpl<>(Collections.singletonList(doacao));
        when(doacaoRepository.findAll(pageable)).thenReturn(pageDoacoes);

        
        Page<DoacaoDTO> resultado = doacaoService.buscarTodas(2023, null, null, pageable);

        
        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());

        verify(doacaoRepository, times(1)).findAll(pageable);
        verify(doacaoRepository, never()).findByMeioDoacao(any(), any());
        verify(doacaoRepository, never()).findByAnoAndMes(any(), any(), any());
        verify(doacaoRepository, never()).findByMeioDoacaoAndAnoAndMes(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Deve buscar doações apenas com mês (sem ano e meio de doação)")
    void deveBuscarDoacoesFiltradas_ApenasMes() {
        
        Page<Doacao> pageDoacoes = new PageImpl<>(Collections.singletonList(doacao));
        when(doacaoRepository.findAll(pageable)).thenReturn(pageDoacoes);

        
        Page<DoacaoDTO> resultado = doacaoService.buscarTodas(null, 12, null, pageable);

        
        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());

        verify(doacaoRepository, times(1)).findAll(pageable);
        verify(doacaoRepository, never()).findByMeioDoacao(any(), any());
        verify(doacaoRepository, never()).findByAnoAndMes(any(), any(), any());
        verify(doacaoRepository, never()).findByMeioDoacaoAndAnoAndMes(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Deve buscar doações com diferentes meios de doação")
    void deveBuscarDoacoesComDiferentesMeiosDoacao() {
        
        Page<Doacao> pageDoacoes = new PageImpl<>(Collections.singletonList(doacao));
        when(doacaoRepository.findByMeioDoacao(EMeioDoacao.CARTAO_CREDITO, pageable)).thenReturn(pageDoacoes);

        
        Page<DoacaoDTO> resultado = doacaoService.buscarTodas(null, null, EMeioDoacao.CARTAO_CREDITO, pageable);

        
        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());

        verify(doacaoRepository, times(1)).findByMeioDoacao(EMeioDoacao.CARTAO_CREDITO, pageable);
    }

    @Test
    @DisplayName("Deve buscar doações com página vazia")
    void deveBuscarDoacoesComPaginaVazia() {
        
        Page<Doacao> paginaVazia = new PageImpl<>(List.of());
        when(doacaoRepository.findAll(pageable)).thenReturn(paginaVazia);

        
        Page<DoacaoDTO> resultado = doacaoService.buscarTodas(null, null, null, pageable);

        
        assertNotNull(resultado);
        assertTrue(resultado.getContent().isEmpty());
        assertEquals(0, resultado.getContent().size());

        verify(doacaoRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve atualizar doação com valores nulos")
    void deveAtualizarDoacaoComValoresNulos() {
        
        DoacaoDTO doacaoComNulos = DoacaoDTO.builder()
                .dataDoacao(null)
                .nomeDoador(null)
                .meioDoacao(null)
                .valor(null)
                .comprovante(null)
                .build();

        when(doacaoRepository.findById(doacaoId)).thenReturn(Optional.of(doacao));
        when(doacaoRepository.save(any(Doacao.class))).thenReturn(doacao);

        
        assertDoesNotThrow(() -> doacaoService.atualizar(doacaoId, doacaoComNulos));

        
        verify(doacaoRepository, times(1)).findById(doacaoId);
        verify(doacaoRepository, times(1)).save(any(Doacao.class));

        assertNull(doacao.getDataDoacao());
        assertNull(doacao.getNomeDoador());
        assertNull(doacao.getMeioDoacao());
        assertNull(doacao.getValor());
        assertNull(doacao.getComprovante());
    }
}