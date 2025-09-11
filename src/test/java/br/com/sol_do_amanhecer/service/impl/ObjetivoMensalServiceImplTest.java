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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ObjetivoMensalServiceImpl Tests")
class ObjetivoMensalServiceImplTest {

    @Mock
    private ObjetivoMensalRepository objetivoMensalRepository;

    @Mock
    private DoacaoRepository doacaoRepository;

    @Mock
    private PrestacaoContasRepository prestacaoContasRepository;

    @InjectMocks
    private ObjetivoMensalServiceImpl objetivoMensalService;

    private UUID objetivoId;
    private ObjetivoMensalRequestDTO requestDTO;
    private ObjetivoMensal objetivoMensal;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        objetivoId = UUID.randomUUID();

        requestDTO = ObjetivoMensalRequestDTO.builder()
                .titulo("Objetivo Dezembro 2023")
                .descricao("Meta de arrecadação para dezembro")
                .mes(EMes.DEZEMBRO)
                .ano(2023)
                .objetivoArrecadacao(BigDecimal.valueOf(10000.00))
                .build();

        objetivoMensal = ObjetivoMensal.builder()
                .uuid(objetivoId)
                .titulo("Objetivo Dezembro 2023")
                .descricao("Meta de arrecadação para dezembro")
                .mes(EMes.DEZEMBRO)
                .ano(2023)
                .objetivoArrecadacao(BigDecimal.valueOf(10000.00))
                .build();

        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("Deve criar um objetivo mensal com sucesso")
    void deveCriarObjetivoMensalComSucesso() {
        
        when(objetivoMensalRepository.existsByMesAndAno(EMes.DEZEMBRO, 2023)).thenReturn(false);
        when(objetivoMensalRepository.save(any(ObjetivoMensal.class))).thenReturn(objetivoMensal);

        
        ObjetivoMensalDTO resultado = objetivoMensalService.criar(requestDTO);

        
        assertNotNull(resultado);
        assertEquals(requestDTO.getTitulo(), resultado.getTitulo());
        assertEquals(requestDTO.getDescricao(), resultado.getDescricao());
        assertEquals(requestDTO.getMes(), resultado.getMes());
        assertEquals(requestDTO.getAno(), resultado.getAno());
        assertEquals(requestDTO.getObjetivoArrecadacao(), resultado.getObjetivoArrecadacao());

        verify(objetivoMensalRepository, times(1)).existsByMesAndAno(EMes.DEZEMBRO, 2023);
        verify(objetivoMensalRepository, times(1)).save(any(ObjetivoMensal.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar objetivo para mês/ano já existente")
    void deveLancarExcecaoAoCriarObjetivoJaExistente() {
        
        when(objetivoMensalRepository.existsByMesAndAno(EMes.DEZEMBRO, 2023)).thenReturn(true);

        
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> objetivoMensalService.criar(requestDTO));

        assertEquals("Já existe um objetivo cadastrado para o mês e ano informados.", exception.getMessage());
        verify(objetivoMensalRepository, times(1)).existsByMesAndAno(EMes.DEZEMBRO, 2023);
        verify(objetivoMensalRepository, never()).save(any(ObjetivoMensal.class));
    }

    @Test
    @DisplayName("Deve criar objetivos para diferentes meses e anos")
    void deveCriarObjetivosParaDiferentesMesesAnos() {
        
        ObjetivoMensalRequestDTO requestJaneiro = ObjetivoMensalRequestDTO.builder()
                .titulo("Objetivo Janeiro 2024")
                .descricao("Meta de janeiro")
                .mes(EMes.JANEIRO)
                .ano(2024)
                .objetivoArrecadacao(BigDecimal.valueOf(5000.00))
                .build();

        ObjetivoMensal objetivoJaneiro = ObjetivoMensal.builder()
                .uuid(UUID.randomUUID())
                .titulo("Objetivo Janeiro 2024")
                .descricao("Meta de janeiro")
                .mes(EMes.JANEIRO)
                .ano(2024)
                .objetivoArrecadacao(BigDecimal.valueOf(5000.00))
                .build();

        when(objetivoMensalRepository.existsByMesAndAno(EMes.JANEIRO, 2024)).thenReturn(false);
        when(objetivoMensalRepository.save(any(ObjetivoMensal.class))).thenReturn(objetivoJaneiro);

        
        ObjetivoMensalDTO resultado = objetivoMensalService.criar(requestJaneiro);

        
        assertNotNull(resultado);
        assertEquals(EMes.JANEIRO, resultado.getMes());
        assertEquals(2024, resultado.getAno());

        verify(objetivoMensalRepository, times(1)).existsByMesAndAno(EMes.JANEIRO, 2024);
        verify(objetivoMensalRepository, times(1)).save(any(ObjetivoMensal.class));
    }

    @Test
    @DisplayName("Deve atualizar um objetivo mensal com sucesso")
    void deveAtualizarObjetivoMensalComSucesso() {
        
        ObjetivoMensalRequestDTO requestAtualizado = ObjetivoMensalRequestDTO.builder()
                .titulo("Objetivo Atualizado")
                .descricao("Descrição atualizada")
                .mes(EMes.JANEIRO)
                .ano(2024)
                .objetivoArrecadacao(BigDecimal.valueOf(15000.00))
                .build();

        when(objetivoMensalRepository.findById(objetivoId)).thenReturn(Optional.of(objetivoMensal));
        when(objetivoMensalRepository.save(any(ObjetivoMensal.class))).thenReturn(objetivoMensal);

        
        assertDoesNotThrow(() -> objetivoMensalService.atualizar(objetivoId, requestAtualizado));

        
        verify(objetivoMensalRepository, times(1)).findById(objetivoId);
        verify(objetivoMensalRepository, times(1)).save(any(ObjetivoMensal.class));

        assertEquals(requestAtualizado.getTitulo(), objetivoMensal.getTitulo());
        assertEquals(requestAtualizado.getDescricao(), objetivoMensal.getDescricao());
        assertEquals(requestAtualizado.getMes(), objetivoMensal.getMes());
        assertEquals(requestAtualizado.getAno(), objetivoMensal.getAno());
        assertEquals(requestAtualizado.getObjetivoArrecadacao(), objetivoMensal.getObjetivoArrecadacao());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar objetivo inexistente")
    void deveLancarExcecaoAoAtualizarObjetivoInexistente() {
        
        when(objetivoMensalRepository.findById(objetivoId)).thenReturn(Optional.empty());

        
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> objetivoMensalService.atualizar(objetivoId, requestDTO));

        assertEquals("Objetivo Mensal não encontrado", exception.getMessage());
        verify(objetivoMensalRepository, times(1)).findById(objetivoId);
        verify(objetivoMensalRepository, never()).save(any(ObjetivoMensal.class));
    }

    @Test
    @DisplayName("Deve buscar objetivo por ID com cálculos dinâmicos")
    void deveBuscarObjetivoPorIdComCalculosDinamicos() {
        
        when(objetivoMensalRepository.findById(objetivoId)).thenReturn(Optional.of(objetivoMensal));
        when(doacaoRepository.findTotalByPeriodo(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(BigDecimal.valueOf(5000.00));
        when(prestacaoContasRepository.findTotalByPeriodo(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(BigDecimal.valueOf(2000.00));
        when(doacaoRepository.findCountByPeriodo(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(10L);
        when(prestacaoContasRepository.findCountByPeriodo(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(5L);

        
        ObjetivoMensalDTO resultado = objetivoMensalService.buscarPorId(objetivoId);


        BigDecimal porcentagemEsperada = BigDecimal.valueOf(50.00).setScale(2, RoundingMode.HALF_UP);
        BigDecimal porcentagemAtual = resultado.getPercentualProgresso().setScale(2, RoundingMode.HALF_UP);

        assertNotNull(resultado);
        assertEquals(objetivoMensal.getTitulo(), resultado.getTitulo());
        assertEquals(BigDecimal.valueOf(5000.00), resultado.getArrecadado());
        assertEquals(BigDecimal.valueOf(2000.00), resultado.getGasto());
        assertEquals(porcentagemEsperada, porcentagemAtual);
        assertEquals(10, resultado.getQuantidadeDoacao());
        assertEquals(5, resultado.getQuantidadePrestacaoConta());

        verify(objetivoMensalRepository, times(1)).findById(objetivoId);
        verify(doacaoRepository, times(1)).findTotalByPeriodo(any(LocalDate.class), any(LocalDate.class));
        verify(prestacaoContasRepository, times(1)).findTotalByPeriodo(any(LocalDate.class), any(LocalDate.class));
        verify(doacaoRepository, times(1)).findCountByPeriodo(any(LocalDate.class), any(LocalDate.class));
        verify(prestacaoContasRepository, times(1)).findCountByPeriodo(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar objetivo inexistente por ID")
    void deveLancarExcecaoAoBuscarObjetivoInexistentePorId() {
        
        when(objetivoMensalRepository.findById(objetivoId)).thenReturn(Optional.empty());

        
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> objetivoMensalService.buscarPorId(objetivoId));

        assertEquals("Objetivo Mensal não encontrado", exception.getMessage());
        verify(objetivoMensalRepository, times(1)).findById(objetivoId);
    }

    @Test
    @DisplayName("Deve calcular valores dinâmicos com valores nulos do banco")
    void deveCalcularValoresDinamicosComValoresNulos() {
        
        when(objetivoMensalRepository.findById(objetivoId)).thenReturn(Optional.of(objetivoMensal));
        when(doacaoRepository.findTotalByPeriodo(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(null);
        when(prestacaoContasRepository.findTotalByPeriodo(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(null);
        when(doacaoRepository.findCountByPeriodo(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(null);
        when(prestacaoContasRepository.findCountByPeriodo(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(null);

        
        ObjetivoMensalDTO resultado = objetivoMensalService.buscarPorId(objetivoId);

        
        assertNotNull(resultado);
        assertEquals(BigDecimal.ZERO, resultado.getArrecadado());
        assertEquals(BigDecimal.ZERO, resultado.getGasto());
        assertEquals(BigDecimal.ZERO, resultado.getPercentualProgresso());
        assertEquals(0, resultado.getQuantidadeDoacao());
        assertEquals(0, resultado.getQuantidadePrestacaoConta());
    }

    @Test
    @DisplayName("Deve calcular percentual de progresso zero quando arrecadado é zero")
    void deveCalcularPercentualProgressoZeroQuandoArrecadadoZero() {
        
        when(objetivoMensalRepository.findById(objetivoId)).thenReturn(Optional.of(objetivoMensal));
        when(doacaoRepository.findTotalByPeriodo(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(BigDecimal.ZERO);
        when(prestacaoContasRepository.findTotalByPeriodo(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(BigDecimal.valueOf(1000.00));
        when(doacaoRepository.findCountByPeriodo(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(0L);
        when(prestacaoContasRepository.findCountByPeriodo(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(3L);

        
        ObjetivoMensalDTO resultado = objetivoMensalService.buscarPorId(objetivoId);

        
        assertNotNull(resultado);
        assertEquals(BigDecimal.ZERO, resultado.getArrecadado());
        assertEquals(BigDecimal.ZERO, resultado.getPercentualProgresso());
    }

    @Test
    @DisplayName("Deve calcular percentual de progresso zero quando objetivo é zero")
    void deveCalcularPercentualProgressoZeroQuandoObjetivoZero() {
        
        objetivoMensal.setObjetivoArrecadacao(BigDecimal.ZERO);
        when(objetivoMensalRepository.findById(objetivoId)).thenReturn(Optional.of(objetivoMensal));
        when(doacaoRepository.findTotalByPeriodo(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(BigDecimal.valueOf(5000.00));
        when(prestacaoContasRepository.findTotalByPeriodo(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(BigDecimal.valueOf(1000.00));
        when(doacaoRepository.findCountByPeriodo(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(10L);
        when(prestacaoContasRepository.findCountByPeriodo(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(3L);

        
        ObjetivoMensalDTO resultado = objetivoMensalService.buscarPorId(objetivoId);

        
        assertNotNull(resultado);
        assertEquals(BigDecimal.valueOf(5000.00), resultado.getArrecadado());
        assertEquals(BigDecimal.ZERO, resultado.getPercentualProgresso());
    }

    @Test
    @DisplayName("Deve buscar todos os objetivos sem filtros")
    void deveBuscarTodosObjetivosSemFiltros() {
        
        Page<ObjetivoMensal> pageObjetivos = new PageImpl<>(Collections.singletonList(objetivoMensal));
        when(objetivoMensalRepository.findAll(pageable)).thenReturn(pageObjetivos);
        configurarMocksCalculosDinamicos();

        
        Page<ObjetivoMensalDTO> resultado = objetivoMensalService.buscarTodos(null, null, pageable);

        
        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());

        verify(objetivoMensalRepository, times(1)).findAll(pageable);
        verify(objetivoMensalRepository, never()).findByMes(any(), any());
        verify(objetivoMensalRepository, never()).findByAno(any(), any());
        verify(objetivoMensalRepository, never()).findByMesAndAno(any(), any(), any());
    }

    @Test
    @DisplayName("Deve buscar objetivos filtrados por mês e ano")
    void deveBuscarObjetivosFiltradosMesAno() {
        
        Page<ObjetivoMensal> pageObjetivos = new PageImpl<>(Collections.singletonList(objetivoMensal));
        when(objetivoMensalRepository.findByMesAndAno(EMes.DEZEMBRO, 2023, pageable)).thenReturn(pageObjetivos);
        configurarMocksCalculosDinamicos();

        
        Page<ObjetivoMensalDTO> resultado = objetivoMensalService.buscarTodos(EMes.DEZEMBRO, 2023, pageable);

        
        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());

        verify(objetivoMensalRepository, times(1)).findByMesAndAno(EMes.DEZEMBRO, 2023, pageable);
        verify(objetivoMensalRepository, never()).findAll((Example<ObjetivoMensal>) any());
        verify(objetivoMensalRepository, never()).findByMes(any(), any());
        verify(objetivoMensalRepository, never()).findByAno(any(), any());
    }

    @Test
    @DisplayName("Deve buscar objetivos filtrados apenas por mês")
    void deveBuscarObjetivosFiltradosApenasMes() {
        
        Page<ObjetivoMensal> pageObjetivos = new PageImpl<>(Collections.singletonList(objetivoMensal));
        when(objetivoMensalRepository.findByMes(EMes.DEZEMBRO, pageable)).thenReturn(pageObjetivos);
        configurarMocksCalculosDinamicos();

        
        Page<ObjetivoMensalDTO> resultado = objetivoMensalService.buscarTodos(EMes.DEZEMBRO, null, pageable);

        
        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());

        verify(objetivoMensalRepository, times(1)).findByMes(EMes.DEZEMBRO, pageable);
        verify(objetivoMensalRepository, never()).findAll((Example<ObjetivoMensal>) any());
        verify(objetivoMensalRepository, never()).findByAno(any(), any());
        verify(objetivoMensalRepository, never()).findByMesAndAno(any(), any(), any());
    }

    @Test
    @DisplayName("Deve buscar objetivos filtrados apenas por ano")
    void deveBuscarObjetivosFiltradosApenasAno() {
        
        Page<ObjetivoMensal> pageObjetivos = new PageImpl<>(Collections.singletonList(objetivoMensal));
        when(objetivoMensalRepository.findByAno(2023, pageable)).thenReturn(pageObjetivos);
        configurarMocksCalculosDinamicos();

        
        Page<ObjetivoMensalDTO> resultado = objetivoMensalService.buscarTodos(null, 2023, pageable);

        
        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());

        verify(objetivoMensalRepository, times(1)).findByAno(2023, pageable);
        verify(objetivoMensalRepository, never()).findAll((Example<ObjetivoMensal>) any());
        verify(objetivoMensalRepository, never()).findByMes(any(), any());
        verify(objetivoMensalRepository, never()).findByMesAndAno(any(), any(), any());
    }

    @Test
    @DisplayName("Deve buscar objetivos com página vazia")
    void deveBuscarObjetivosComPaginaVazia() {
        
        Page<ObjetivoMensal> paginaVazia = new PageImpl<>(Collections.emptyList());
        when(objetivoMensalRepository.findAll(pageable)).thenReturn(paginaVazia);

        
        Page<ObjetivoMensalDTO> resultado = objetivoMensalService.buscarTodos(null, null, pageable);

        
        assertNotNull(resultado);
        assertTrue(resultado.getContent().isEmpty());
        assertEquals(0, resultado.getTotalElements());

        verify(objetivoMensalRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve remover um objetivo mensal com sucesso")
    void deveRemoverObjetivoMensalComSucesso() {
        
        when(objetivoMensalRepository.existsById(objetivoId)).thenReturn(true);
        doNothing().when(objetivoMensalRepository).deleteById(objetivoId);

        
        assertDoesNotThrow(() -> objetivoMensalService.remover(objetivoId));

        
        verify(objetivoMensalRepository, times(1)).existsById(objetivoId);
        verify(objetivoMensalRepository, times(1)).deleteById(objetivoId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar remover objetivo inexistente")
    void deveLancarExcecaoAoRemoverObjetivoInexistente() {
        
        when(objetivoMensalRepository.existsById(objetivoId)).thenReturn(false);

        
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> objetivoMensalService.remover(objetivoId));

        assertEquals("Objetivo Mensal não encontrado", exception.getMessage());
        verify(objetivoMensalRepository, times(1)).existsById(objetivoId);
        verify(objetivoMensalRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    @DisplayName("Deve testar cálculo com diferentes meses")
    void deveTestarCalculoComDiferentesMeses() {
        ObjetivoMensal objetivoFevereiro = ObjetivoMensal.builder()
                .uuid(UUID.randomUUID())
                .titulo("Objetivo Fevereiro")
                .mes(EMes.FEVEREIRO)
                .ano(2023)
                .objetivoArrecadacao(BigDecimal.valueOf(8000.00))
                .build();

        when(objetivoMensalRepository.findById(any(UUID.class))).thenReturn(Optional.of(objetivoFevereiro));
        configurarMocksCalculosDinamicos();

        
        ObjetivoMensalDTO resultado = objetivoMensalService.buscarPorId(UUID.randomUUID());

        
        assertNotNull(resultado);
        assertEquals(EMes.FEVEREIRO, resultado.getMes());

        verify(doacaoRepository, times(1)).findTotalByPeriodo(
                eq(LocalDate.of(2023, 2, 1)),
                eq(LocalDate.of(2023, 2, 28))
        );
    }

    @Test
    @DisplayName("Deve testar cálculo com ano bissexto")
    void deveTestarCalculoComAnoBissexto() {
        ObjetivoMensal objetivoFevBissexto = ObjetivoMensal.builder()
                .uuid(UUID.randomUUID())
                .titulo("Objetivo Fevereiro Bissexto")
                .mes(EMes.FEVEREIRO)
                .ano(2024)
                .objetivoArrecadacao(BigDecimal.valueOf(8000.00))
                .build();

        when(objetivoMensalRepository.findById(any(UUID.class))).thenReturn(Optional.of(objetivoFevBissexto));
        configurarMocksCalculosDinamicos();

        
        ObjetivoMensalDTO resultado = objetivoMensalService.buscarPorId(UUID.randomUUID());

        
        assertNotNull(resultado);

        verify(doacaoRepository, times(1)).findTotalByPeriodo(
                eq(LocalDate.of(2024, 2, 1)),
                eq(LocalDate.of(2024, 2, 29))
        );
    }

    @Test
    @DisplayName("Deve testar cálculo de percentual com valores altos")
    void deveTestarCalculoPercentualComValoresAltos() {
        
        when(objetivoMensalRepository.findById(objetivoId)).thenReturn(Optional.of(objetivoMensal));
        when(doacaoRepository.findTotalByPeriodo(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(BigDecimal.valueOf(15000.00));
        when(prestacaoContasRepository.findTotalByPeriodo(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(BigDecimal.valueOf(3000.00));
        when(doacaoRepository.findCountByPeriodo(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(50L);
        when(prestacaoContasRepository.findCountByPeriodo(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(15L);
        
        ObjetivoMensalDTO resultado = objetivoMensalService.buscarPorId(objetivoId);

        BigDecimal porcentagemEsperada = BigDecimal.valueOf(150.00).setScale(2, RoundingMode.HALF_UP);
        BigDecimal porcentagemAtual = resultado.getPercentualProgresso().setScale(2, RoundingMode.HALF_UP);

        assertNotNull(resultado);
        assertEquals(BigDecimal.valueOf(15000.00), resultado.getArrecadado());
        assertEquals(porcentagemEsperada, porcentagemAtual);
        assertEquals(50, resultado.getQuantidadeDoacao());
        assertEquals(15, resultado.getQuantidadePrestacaoConta());
    }

    @Test
    @DisplayName("Deve buscar objetivos com múltiplos registros e aplicar cálculos")
    void deveBuscarObjetivosComMultiplosRegistros() {
        
        ObjetivoMensal objetivo2 = ObjetivoMensal.builder()
                .uuid(UUID.randomUUID())
                .titulo("Objetivo Janeiro")
                .mes(EMes.JANEIRO)
                .ano(2023)
                .objetivoArrecadacao(BigDecimal.valueOf(8000.00))
                .build();

        Page<ObjetivoMensal> pageObjetivos = new PageImpl<>(Arrays.asList(objetivoMensal, objetivo2));
        when(objetivoMensalRepository.findAll(pageable)).thenReturn(pageObjetivos);
        configurarMocksCalculosDinamicos();

        
        Page<ObjetivoMensalDTO> resultado = objetivoMensalService.buscarTodos(null, null, pageable);

        
        assertNotNull(resultado);
        assertEquals(2, resultado.getContent().size());

        verify(doacaoRepository, times(2)).findTotalByPeriodo(any(LocalDate.class), any(LocalDate.class));
        verify(prestacaoContasRepository, times(2)).findTotalByPeriodo(any(LocalDate.class), any(LocalDate.class));
    }

    private void configurarMocksCalculosDinamicos() {
        when(doacaoRepository.findTotalByPeriodo(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(BigDecimal.valueOf(5000.00));
        when(prestacaoContasRepository.findTotalByPeriodo(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(BigDecimal.valueOf(2000.00));
        when(doacaoRepository.findCountByPeriodo(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(10L);
        when(prestacaoContasRepository.findCountByPeriodo(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(5L);
    }
}