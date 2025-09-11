package br.com.sol_do_amanhecer.service.impl;

import br.com.sol_do_amanhecer.model.dto.PrestacaoContasDTO;
import br.com.sol_do_amanhecer.model.entity.PrestacaoContas;
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
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PrestacaoContasServiceImpl Tests")
class PrestacaoContasServiceImplTest {

    @Mock
    private PrestacaoContasRepository prestacaoContasRepository;

    @InjectMocks
    private PrestacaoContasServiceImpl prestacaoContasService;

    private UUID prestacaoId;
    private PrestacaoContasDTO prestacaoContasDTO;
    private PrestacaoContas prestacaoContas;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        prestacaoId = UUID.randomUUID();
        byte[] notaFiscalBytes = "nota-fiscal-dados".getBytes();
        byte[] comprovanteBytes = "comprovante-dados".getBytes();

        prestacaoContasDTO = PrestacaoContasDTO.builder()
                .dataTransacao(LocalDate.of(2023, 12, 15))
                .descricaoGasto("Compra de materiais escolares")
                .destinoGasto("Educação")
                .valorPago(BigDecimal.valueOf(500.00))
                .estabelecimento("Papelaria ABC")
                .notaFiscal(new String (notaFiscalBytes, StandardCharsets.UTF_8))
                .comprovante(comprovanteBytes)
                .build();

        prestacaoContas = PrestacaoContas.builder()
                .uuid(prestacaoId)
                .dataTransacao(LocalDate.of(2023, 12, 15))
                .descricaoGasto("Compra de materiais escolares")
                .destinoGasto("Educação")
                .valorPago(BigDecimal.valueOf(500.00))
                .estabelecimento("Papelaria ABC")
                .notaFiscal(new String(notaFiscalBytes, StandardCharsets.UTF_8))
                .comprovante(comprovanteBytes)
                .build();

        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("Deve criar uma prestação de contas com sucesso")
    void deveCriarPrestacaoContasComSucesso() {
        
        when(prestacaoContasRepository.save(any(PrestacaoContas.class))).thenReturn(prestacaoContas);

        
        PrestacaoContasDTO resultado = prestacaoContasService.criar(prestacaoContasDTO);

        
        assertNotNull(resultado);
        assertEquals(prestacaoContasDTO.getDataTransacao(), resultado.getDataTransacao());
        assertEquals(prestacaoContasDTO.getDescricaoGasto(), resultado.getDescricaoGasto());
        assertEquals(prestacaoContasDTO.getDestinoGasto(), resultado.getDestinoGasto());
        assertEquals(prestacaoContasDTO.getValorPago(), resultado.getValorPago());
        assertEquals(prestacaoContasDTO.getEstabelecimento(), resultado.getEstabelecimento());
        assertEquals(prestacaoContasDTO.getNotaFiscal(), resultado.getNotaFiscal());
        assertArrayEquals(prestacaoContasDTO.getComprovante(), resultado.getComprovante());

        verify(prestacaoContasRepository, times(1)).save(any(PrestacaoContas.class));
    }

    @Test
    @DisplayName("Deve criar prestação de contas com valores nulos opcionais")
    void deveCriarPrestacaoContasComValoresNulos() {
        
        PrestacaoContasDTO prestacaoSemAnexos = PrestacaoContasDTO.builder()
                .dataTransacao(LocalDate.of(2023, 12, 20))
                .descricaoGasto("Gasto sem anexos")
                .destinoGasto("Administração")
                .valorPago(BigDecimal.valueOf(100.00))
                .estabelecimento("Loja XYZ")
                .notaFiscal(null)
                .comprovante(null)
                .build();

        PrestacaoContas prestacaoSalva = PrestacaoContas.builder()
                .uuid(UUID.randomUUID())
                .dataTransacao(LocalDate.of(2023, 12, 20))
                .descricaoGasto("Gasto sem anexos")
                .destinoGasto("Administração")
                .valorPago(BigDecimal.valueOf(100.00))
                .estabelecimento("Loja XYZ")
                .notaFiscal(null)
                .comprovante(null)
                .build();

        when(prestacaoContasRepository.save(any(PrestacaoContas.class))).thenReturn(prestacaoSalva);

        
        PrestacaoContasDTO resultado = prestacaoContasService.criar(prestacaoSemAnexos);

        
        assertNotNull(resultado);
        assertEquals(prestacaoSemAnexos.getDescricaoGasto(), resultado.getDescricaoGasto());
        assertNull(resultado.getNotaFiscal());
        assertNull(resultado.getComprovante());

        verify(prestacaoContasRepository, times(1)).save(any(PrestacaoContas.class));
    }

    @Test
    @DisplayName("Deve atualizar uma prestação de contas com sucesso")
    void deveAtualizarPrestacaoContasComSucesso() {
        
        when(prestacaoContasRepository.findById(prestacaoId)).thenReturn(Optional.of(prestacaoContas));
        when(prestacaoContasRepository.save(any(PrestacaoContas.class))).thenReturn(prestacaoContas);

        
        assertDoesNotThrow(() -> prestacaoContasService.atualizar(prestacaoId, prestacaoContasDTO));

        
        verify(prestacaoContasRepository, times(1)).findById(prestacaoId);
        verify(prestacaoContasRepository, times(1)).save(any(PrestacaoContas.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar prestação inexistente")
    void deveLancarExcecaoAoAtualizarPrestacaoInexistente() {
        
        when(prestacaoContasRepository.findById(prestacaoId)).thenReturn(Optional.empty());

         
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> prestacaoContasService.atualizar(prestacaoId, prestacaoContasDTO));

        assertEquals("Prestação de contas não encontrada", exception.getMessage());
        verify(prestacaoContasRepository, times(1)).findById(prestacaoId);
        verify(prestacaoContasRepository, never()).save(any(PrestacaoContas.class));
    }

    @Test
    @DisplayName("Deve atualizar todos os campos da prestação de contas")
    void deveAtualizarTodosCamposPrestacaoContas() {
        
        PrestacaoContasDTO prestacaoAtualizada = PrestacaoContasDTO.builder()
                .dataTransacao(LocalDate.of(2024, 1, 10))
                .descricaoGasto("Gasto atualizado")
                .destinoGasto("Saúde")
                .valorPago(BigDecimal.valueOf(750.00))
                .estabelecimento("Farmácia DEF")
                .notaFiscal("nova-nota-fiscal")
                .comprovante("novo-comprovante".getBytes())
                .build();

        when(prestacaoContasRepository.findById(prestacaoId)).thenReturn(Optional.of(prestacaoContas));
        when(prestacaoContasRepository.save(any(PrestacaoContas.class))).thenReturn(prestacaoContas);

        
        assertDoesNotThrow(() -> prestacaoContasService.atualizar(prestacaoId, prestacaoAtualizada));

        
        verify(prestacaoContasRepository, times(1)).findById(prestacaoId);
        verify(prestacaoContasRepository, times(1)).save(any(PrestacaoContas.class));

        assertEquals(prestacaoAtualizada.getDataTransacao(), prestacaoContas.getDataTransacao());
        assertEquals(prestacaoAtualizada.getDescricaoGasto(), prestacaoContas.getDescricaoGasto());
        assertEquals(prestacaoAtualizada.getDestinoGasto(), prestacaoContas.getDestinoGasto());
        assertEquals(prestacaoAtualizada.getValorPago(), prestacaoContas.getValorPago());
        assertEquals(prestacaoAtualizada.getEstabelecimento(), prestacaoContas.getEstabelecimento());
        assertEquals(prestacaoAtualizada.getNotaFiscal(), prestacaoContas.getNotaFiscal());
        assertArrayEquals(prestacaoAtualizada.getComprovante(), prestacaoContas.getComprovante());
    }

    @Test
    @DisplayName("Deve atualizar prestação com valores nulos")
    void deveAtualizarPrestacaoComValoresNulos() {
        
        PrestacaoContasDTO prestacaoComNulos = PrestacaoContasDTO.builder()
                .dataTransacao(null)
                .descricaoGasto(null)
                .destinoGasto(null)
                .valorPago(null)
                .estabelecimento(null)
                .notaFiscal(null)
                .comprovante(null)
                .build();

        when(prestacaoContasRepository.findById(prestacaoId)).thenReturn(Optional.of(prestacaoContas));
        when(prestacaoContasRepository.save(any(PrestacaoContas.class))).thenReturn(prestacaoContas);

        
        assertDoesNotThrow(() -> prestacaoContasService.atualizar(prestacaoId, prestacaoComNulos));

        
        verify(prestacaoContasRepository, times(1)).findById(prestacaoId);
        verify(prestacaoContasRepository, times(1)).save(any(PrestacaoContas.class));

        assertNull(prestacaoContas.getDataTransacao());
        assertNull(prestacaoContas.getDescricaoGasto());
        assertNull(prestacaoContas.getDestinoGasto());
        assertNull(prestacaoContas.getValorPago());
        assertNull(prestacaoContas.getEstabelecimento());
        assertNull(prestacaoContas.getNotaFiscal());
        assertNull(prestacaoContas.getComprovante());
    }

    @Test
    @DisplayName("Deve remover uma prestação de contas com sucesso")
    void deveRemoverPrestacaoContasComSucesso() {
        
        when(prestacaoContasRepository.existsById(prestacaoId)).thenReturn(true);
        doNothing().when(prestacaoContasRepository).deleteById(prestacaoId);

        
        assertDoesNotThrow(() -> prestacaoContasService.remover(prestacaoId));

        
        verify(prestacaoContasRepository, times(1)).existsById(prestacaoId);
        verify(prestacaoContasRepository, times(1)).deleteById(prestacaoId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar remover prestação inexistente")
    void deveLancarExcecaoAoRemoverPrestacaoInexistente() {
        
        when(prestacaoContasRepository.existsById(prestacaoId)).thenReturn(false);

        
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> prestacaoContasService.remover(prestacaoId));

        assertEquals("Prestação de contas não encontrada", exception.getMessage());
        verify(prestacaoContasRepository, times(1)).existsById(prestacaoId);
        verify(prestacaoContasRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    @DisplayName("Deve buscar prestação de contas por ID com sucesso")
    void deveBuscarPrestacaoContasPorIdComSucesso() {
        
        when(prestacaoContasRepository.findById(prestacaoId)).thenReturn(Optional.of(prestacaoContas));

        
        PrestacaoContasDTO resultado = prestacaoContasService.buscarPorId(prestacaoId);

        
        assertNotNull(resultado);
        assertEquals(prestacaoContas.getDataTransacao(), resultado.getDataTransacao());
        assertEquals(prestacaoContas.getDescricaoGasto(), resultado.getDescricaoGasto());
        assertEquals(prestacaoContas.getDestinoGasto(), resultado.getDestinoGasto());
        assertEquals(prestacaoContas.getValorPago(), resultado.getValorPago());
        assertEquals(prestacaoContas.getEstabelecimento(), resultado.getEstabelecimento());
        assertEquals(prestacaoContas.getNotaFiscal(), resultado.getNotaFiscal());
        assertArrayEquals(prestacaoContas.getComprovante(), resultado.getComprovante());

        verify(prestacaoContasRepository, times(1)).findById(prestacaoId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar prestação inexistente por ID")
    void deveLancarExcecaoAoBuscarPrestacaoInexistentePorId() {
        
        when(prestacaoContasRepository.findById(prestacaoId)).thenReturn(Optional.empty());

        
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> prestacaoContasService.buscarPorId(prestacaoId));

        assertEquals("Prestação de contas não encontrada", exception.getMessage());
        verify(prestacaoContasRepository, times(1)).findById(prestacaoId);
    }

    @Test
    @DisplayName("Deve buscar todas as prestações sem filtros")
    void deveBuscarTodasPrestacoesSemFiltros() {
        
        Page<PrestacaoContas> pagePrestacoes = new PageImpl<>(Collections.singletonList(prestacaoContas));
        when(prestacaoContasRepository.findAll(pageable)).thenReturn(pagePrestacoes);

        
        Page<PrestacaoContasDTO> resultado = prestacaoContasService.buscarTodas(null, null, pageable);

        
        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        assertEquals(prestacaoContas.getDescricaoGasto(), resultado.getContent().get(0).getDescricaoGasto());

        verify(prestacaoContasRepository, times(1)).findAll(pageable);
        verify(prestacaoContasRepository, never()).findByMes(any(), any());
        verify(prestacaoContasRepository, never()).findByAno(any(), any());
        verify(prestacaoContasRepository, never()).findByMesEAno(any(), any(), any());
    }

    @Test
    @DisplayName("Deve buscar prestações filtradas por mês e ano")
    void deveBuscarPrestacoesFiltradas_MesAno() {
        
        Page<PrestacaoContas> pagePrestacoes = new PageImpl<>(Collections.singletonList(prestacaoContas));
        when(prestacaoContasRepository.findByMesEAno(12, 2023, pageable)).thenReturn(pagePrestacoes);

        
        Page<PrestacaoContasDTO> resultado = prestacaoContasService.buscarTodas(12, 2023, pageable);

        
        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        assertEquals(prestacaoContas.getDescricaoGasto(), resultado.getContent().get(0).getDescricaoGasto());

        verify(prestacaoContasRepository, times(1)).findByMesEAno(12, 2023, pageable);
        verify(prestacaoContasRepository, never()).findAll(any(Pageable.class));
        verify(prestacaoContasRepository, never()).findByMes(any(), any());
        verify(prestacaoContasRepository, never()).findByAno(any(), any());
    }

    @Test
    @DisplayName("Deve buscar prestações filtradas apenas por mês")
    void deveBuscarPrestacoesFiltradas_ApenasMes() {
        
        Page<PrestacaoContas> pagePrestacoes = new PageImpl<>(Collections.singletonList(prestacaoContas));
        when(prestacaoContasRepository.findByMes(12, pageable)).thenReturn(pagePrestacoes);

        
        Page<PrestacaoContasDTO> resultado = prestacaoContasService.buscarTodas(12, null, pageable);

        
        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        assertEquals(prestacaoContas.getDescricaoGasto(), resultado.getContent().get(0).getDescricaoGasto());

        verify(prestacaoContasRepository, times(1)).findByMes(12, pageable);
        verify(prestacaoContasRepository, never()).findAll(any(Pageable.class));
        verify(prestacaoContasRepository, never()).findByAno(any(), any());
        verify(prestacaoContasRepository, never()).findByMesEAno(any(), any(), any());
    }

    @Test
    @DisplayName("Deve buscar prestações filtradas apenas por ano")
    void deveBuscarPrestacoesFiltradas_ApenasAno() {
        
        Page<PrestacaoContas> pagePrestacoes = new PageImpl<>(Collections.singletonList(prestacaoContas));
        when(prestacaoContasRepository.findByAno(2023, pageable)).thenReturn(pagePrestacoes);

        
        Page<PrestacaoContasDTO> resultado = prestacaoContasService.buscarTodas(null, 2023, pageable);

        
        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        assertEquals(prestacaoContas.getDescricaoGasto(), resultado.getContent().get(0).getDescricaoGasto());

        verify(prestacaoContasRepository, times(1)).findByAno(2023, pageable);
        verify(prestacaoContasRepository, never()).findAll(any(Pageable.class));
        verify(prestacaoContasRepository, never()).findByMes(any(), any());
        verify(prestacaoContasRepository, never()).findByMesEAno(any(), any(), any());
    }

    @Test
    @DisplayName("Deve buscar prestações com página vazia")
    void deveBuscarPrestacoesComPaginaVazia() {
        
        Page<PrestacaoContas> paginaVazia = new PageImpl<>(Collections.emptyList());
        when(prestacaoContasRepository.findAll(pageable)).thenReturn(paginaVazia);

        
        Page<PrestacaoContasDTO> resultado = prestacaoContasService.buscarTodas(null, null, pageable);

        
        assertNotNull(resultado);
        assertTrue(resultado.getContent().isEmpty());
        assertEquals(0, resultado.getTotalElements());

        verify(prestacaoContasRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve buscar prestações com múltiplos registros")
    void deveBuscarPrestacoesComMultiplosRegistros() {
        
        PrestacaoContas prestacao2 = PrestacaoContas.builder()
                .uuid(UUID.randomUUID())
                .dataTransacao(LocalDate.of(2023, 12, 20))
                .descricaoGasto("Outro gasto")
                .destinoGasto("Saúde")
                .valorPago(BigDecimal.valueOf(300.00))
                .estabelecimento("Farmácia XYZ")
                .notaFiscal(null)
                .comprovante(null)
                .build();

        Page<PrestacaoContas> pagePrestacoes = new PageImpl<>(Arrays.asList(prestacaoContas, prestacao2));
        when(prestacaoContasRepository.findByMes(12, pageable)).thenReturn(pagePrestacoes);

        
        Page<PrestacaoContasDTO> resultado = prestacaoContasService.buscarTodas(12, null, pageable);

        
        assertNotNull(resultado);
        assertEquals(2, resultado.getContent().size());

        verify(prestacaoContasRepository, times(1)).findByMes(12, pageable);
    }

    @Test
    @DisplayName("Deve testar diferentes valores de mês e ano")
    void deveTestarDiferentesValoresMesAno() {
        
        Page<PrestacaoContas> pagePrestacoes = new PageImpl<>(Collections.singletonList(prestacaoContas));

        when(prestacaoContasRepository.findByMes(1, pageable)).thenReturn(pagePrestacoes);
        when(prestacaoContasRepository.findByMes(6, pageable)).thenReturn(pagePrestacoes);
        when(prestacaoContasRepository.findByMes(12, pageable)).thenReturn(pagePrestacoes);

        when(prestacaoContasRepository.findByAno(2022, pageable)).thenReturn(pagePrestacoes);
        when(prestacaoContasRepository.findByAno(2023, pageable)).thenReturn(pagePrestacoes);
        when(prestacaoContasRepository.findByAno(2024, pageable)).thenReturn(pagePrestacoes);

        
        Page<PrestacaoContasDTO> resultadoJan = prestacaoContasService.buscarTodas(1, null, pageable);
        Page<PrestacaoContasDTO> resultadoJun = prestacaoContasService.buscarTodas(6, null, pageable);
        Page<PrestacaoContasDTO> resultadoDez = prestacaoContasService.buscarTodas(12, null, pageable);

        Page<PrestacaoContasDTO> resultado2022 = prestacaoContasService.buscarTodas(null, 2022, pageable);
        Page<PrestacaoContasDTO> resultado2023 = prestacaoContasService.buscarTodas(null, 2023, pageable);
        Page<PrestacaoContasDTO> resultado2024 = prestacaoContasService.buscarTodas(null, 2024, pageable);

        assertNotNull(resultadoJan);
        assertNotNull(resultadoJun);
        assertNotNull(resultadoDez);
        assertNotNull(resultado2022);
        assertNotNull(resultado2023);
        assertNotNull(resultado2024);

        verify(prestacaoContasRepository, times(1)).findByMes(1, pageable);
        verify(prestacaoContasRepository, times(1)).findByMes(6, pageable);
        verify(prestacaoContasRepository, times(1)).findByMes(12, pageable);
        verify(prestacaoContasRepository, times(1)).findByAno(2022, pageable);
        verify(prestacaoContasRepository, times(1)).findByAno(2023, pageable);
        verify(prestacaoContasRepository, times(1)).findByAno(2024, pageable);
    }

    @Test
    @DisplayName("Deve testar combinações específicas de mês e ano")
    void deveTestarCombinacoesEspecificasMesAno() {
        
        Page<PrestacaoContas> pagePrestacoes = new PageImpl<>(Collections.singletonList(prestacaoContas));

        when(prestacaoContasRepository.findByMesEAno(1, 2023, pageable)).thenReturn(pagePrestacoes);
        when(prestacaoContasRepository.findByMesEAno(6, 2024, pageable)).thenReturn(pagePrestacoes);
        when(prestacaoContasRepository.findByMesEAno(12, 2022, pageable)).thenReturn(pagePrestacoes);

        
        Page<PrestacaoContasDTO> resultado1 = prestacaoContasService.buscarTodas(1, 2023, pageable);
        Page<PrestacaoContasDTO> resultado2 = prestacaoContasService.buscarTodas(6, 2024, pageable);
        Page<PrestacaoContasDTO> resultado3 = prestacaoContasService.buscarTodas(12, 2022, pageable);

        
        assertNotNull(resultado1);
        assertNotNull(resultado2);
        assertNotNull(resultado3);

        verify(prestacaoContasRepository, times(1)).findByMesEAno(1, 2023, pageable);
        verify(prestacaoContasRepository, times(1)).findByMesEAno(6, 2024, pageable);
        verify(prestacaoContasRepository, times(1)).findByMesEAno(12, 2022, pageable);
    }

    @Test
    @DisplayName("Deve testar prestação com diferentes tipos de dados")
    void deveTestarPrestacaoComDiferentesTiposDados() {
        
        PrestacaoContasDTO prestacaoVariada = PrestacaoContasDTO.builder()
                .dataTransacao(LocalDate.of(2024, 2, 29))
                .descricaoGasto("Descrição com caracteres especiais: áéíóú çñ")
                .destinoGasto("Destino com números 123")
                .valorPago(BigDecimal.valueOf(999.99))
                .estabelecimento("Estabelecimento & Cia Ltda.")
                .notaFiscal("bytes-nota-fiscal-especial")
                .comprovante("bytes-comprovante-especial".getBytes())
                .build();

        PrestacaoContas prestacaoSalva = PrestacaoContas.builder()
                .uuid(UUID.randomUUID())
                .dataTransacao(LocalDate.of(2024, 2, 29))
                .descricaoGasto("Descrição com caracteres especiais: áéíóú çñ")
                .destinoGasto("Destino com números 123")
                .valorPago(BigDecimal.valueOf(999.99))
                .estabelecimento("Estabelecimento & Cia Ltda.")
                .notaFiscal("bytes-nota-fiscal-especial")
                .comprovante("bytes-comprovante-especial".getBytes())
                .build();

        when(prestacaoContasRepository.save(any(PrestacaoContas.class))).thenReturn(prestacaoSalva);

        
        PrestacaoContasDTO resultado = prestacaoContasService.criar(prestacaoVariada);

        
        assertNotNull(resultado);
        assertEquals(prestacaoVariada.getDataTransacao(), resultado.getDataTransacao());
        assertEquals(prestacaoVariada.getDescricaoGasto(), resultado.getDescricaoGasto());
        assertEquals(prestacaoVariada.getDestinoGasto(), resultado.getDestinoGasto());
        assertEquals(prestacaoVariada.getValorPago(), resultado.getValorPago());
        assertEquals(prestacaoVariada.getEstabelecimento(), resultado.getEstabelecimento());
        assertEquals(prestacaoVariada.getNotaFiscal(), resultado.getNotaFiscal());
        assertArrayEquals(prestacaoVariada.getComprovante(), resultado.getComprovante());

        verify(prestacaoContasRepository, times(1)).save(any(PrestacaoContas.class));
    }
}