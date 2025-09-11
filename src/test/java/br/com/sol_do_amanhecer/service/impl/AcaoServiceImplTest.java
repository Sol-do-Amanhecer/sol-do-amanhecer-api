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
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AcaoServiceImpl Tests")
class AcaoServiceImplTest {

    @Mock
    private AcaoRepository acaoRepository;

    @Mock
    private ImagemAcaoRepository imagemAcaoRepository;

    @InjectMocks
    private AcaoServiceImpl acaoService;

    private UUID acaoId;
    private AcaoDTO acaoDTO;
    private Acao acao;
    private ImagemAcaoDTO imagemAcaoDTO;
    private ImagemAcao imagemAcao;
    private List<ImagemAcaoDTO> imagemDTOs;
    private List<ImagemAcao> imagensAcao;
    private Pageable pageable;
    private byte[] imagemBytes;

    @BeforeEach
    void setUp() {
        acaoId = UUID.randomUUID();
        UUID imagemId = UUID.randomUUID();

        imagemBytes = "dados-da-imagem-em-bytes".getBytes();

        acaoDTO = AcaoDTO.builder()
                .nome("Ação Teste")
                .descricao("Descrição da ação teste")
                .dataAcao(LocalDate.now())
                .localAcao("Local Teste")
                .tipo(ETipoAcao.SOCIAL_ALIMENTAR)
                .build();

        acao = Acao.builder()
                .uuid(acaoId)
                .nome("Ação Teste")
                .descricao("Descrição da ação teste")
                .dataAcao(LocalDate.now())
                .localAcao("Local Teste")
                .tipo(ETipoAcao.SOCIAL_ALIMENTAR)
                .build();

        imagemAcaoDTO = ImagemAcaoDTO.builder()
                .uuidAcao(acaoId)
                .imagem(imagemBytes)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        imagemAcao = ImagemAcao.builder()
                .uuid(imagemId)
                .imagem(imagemBytes)
                .acao(acao)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        imagemDTOs = Collections.singletonList(imagemAcaoDTO);
        imagensAcao = Collections.singletonList(imagemAcao);
        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("Deve criar uma ação com imagens com sucesso")
    void deveCriarAcaoComImagensComSucesso() {
        
        when(acaoRepository.save(any(Acao.class))).thenReturn(acao);
        when(imagemAcaoRepository.save(any(ImagemAcao.class))).thenReturn(imagemAcao);
        
        AcaoDTO resultado = acaoService.criar(acaoDTO, imagemDTOs);
        
        assertNotNull(resultado);
        assertEquals(acaoDTO.getNome(), resultado.getNome());
        assertEquals(acaoDTO.getDescricao(), resultado.getDescricao());
        assertEquals(acaoDTO.getLocalAcao(), resultado.getLocalAcao());
        assertEquals(acaoDTO.getTipo(), resultado.getTipo());

        verify(acaoRepository, times(1)).save(any(Acao.class));
        verify(imagemAcaoRepository, times(1)).save(any(ImagemAcao.class));
    }

    @Test
    @DisplayName("Deve criar uma ação sem imagens com sucesso")
    void deveCriarAcaoSemImagensComSucesso() {
        
        when(acaoRepository.save(any(Acao.class))).thenReturn(acao);
        
        AcaoDTO resultado = acaoService.criar(acaoDTO, List.of());

        assertNotNull(resultado);
        assertEquals(acaoDTO.getNome(), resultado.getNome());

        verify(acaoRepository, times(1)).save(any(Acao.class));
        verify(imagemAcaoRepository, never()).save(any(ImagemAcao.class));
    }

    @Test
    @DisplayName("Deve criar ação com múltiplas imagens")
    void deveCriarAcaoComMultiplasImagens() {
        
        ImagemAcaoDTO imagemAcaoDTO2 = ImagemAcaoDTO.builder()
                .uuidAcao(acaoId)
                .imagem("segunda-imagem-bytes".getBytes())
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        List<ImagemAcaoDTO> multiplasImagens = Arrays.asList(imagemAcaoDTO, imagemAcaoDTO2);

        when(acaoRepository.save(any(Acao.class))).thenReturn(acao);
        when(imagemAcaoRepository.save(any(ImagemAcao.class))).thenReturn(imagemAcao);

        AcaoDTO resultado = acaoService.criar(acaoDTO, multiplasImagens);
        
        assertNotNull(resultado);
        assertEquals(acaoDTO.getNome(), resultado.getNome());

        verify(acaoRepository, times(1)).save(any(Acao.class));
        verify(imagemAcaoRepository, times(2)).save(any(ImagemAcao.class));
    }

    @Test
    @DisplayName("Deve atualizar uma ação com sucesso")
    void deveAtualizarAcaoComSucesso() {
        
        when(acaoRepository.findById(acaoId)).thenReturn(Optional.of(acao));
        when(acaoRepository.save(any(Acao.class))).thenReturn(acao);
        doNothing().when(imagemAcaoRepository).deleteAllByAcao(any(Acao.class));
        when(imagemAcaoRepository.save(any(ImagemAcao.class))).thenReturn(imagemAcao);

        assertDoesNotThrow(() -> acaoService.atualizar(acaoId, acaoDTO, imagemDTOs));
        
        verify(acaoRepository, times(1)).findById(acaoId);
        verify(acaoRepository, times(1)).save(any(Acao.class));
        verify(imagemAcaoRepository, times(1)).deleteAllByAcao(any(Acao.class));
        verify(imagemAcaoRepository, times(1)).save(any(ImagemAcao.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar ação inexistente")
    void deveLancarExcecaoAoAtualizarAcaoInexistente() {
        
        when(acaoRepository.findById(acaoId)).thenReturn(Optional.empty());

         
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> acaoService.atualizar(acaoId, acaoDTO, imagemDTOs));

        assertEquals("Ação não encontrada", exception.getMessage());
        verify(acaoRepository, times(1)).findById(acaoId);
        verify(acaoRepository, never()).save(any(Acao.class));
        verify(imagemAcaoRepository, never()).deleteAllByAcao(any(Acao.class));
    }

    @Test
    @DisplayName("Deve remover uma ação com sucesso")
    void deveRemoverAcaoComSucesso() {
        
        when(acaoRepository.findById(acaoId)).thenReturn(Optional.of(acao));
        doNothing().when(imagemAcaoRepository).deleteAllByAcao(any(Acao.class));
        doNothing().when(acaoRepository).deleteById(acaoId);

        assertDoesNotThrow(() -> acaoService.remover(acaoId));
        
        verify(acaoRepository, times(1)).findById(acaoId);
        verify(imagemAcaoRepository, times(1)).deleteAllByAcao(acao);
        verify(acaoRepository, times(1)).deleteById(acaoId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar remover ação inexistente")
    void deveLancarExcecaoAoRemoverAcaoInexistente() {
        
        when(acaoRepository.findById(acaoId)).thenReturn(Optional.empty());
         
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> acaoService.remover(acaoId));

        assertEquals("Ação não encontrada", exception.getMessage());
        verify(acaoRepository, times(1)).findById(acaoId);
        verify(imagemAcaoRepository, never()).deleteAllByAcao(any(Acao.class));
        verify(acaoRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    @DisplayName("Deve buscar ação por ID com sucesso")
    void deveBuscarAcaoPorIdComSucesso() {
        
        when(acaoRepository.findById(acaoId)).thenReturn(Optional.of(acao));
        when(imagemAcaoRepository.findByAcao(acao)).thenReturn(imagensAcao);
        
        AcaoResponseDTO resultado = acaoService.buscarPorId(acaoId);
        
        assertNotNull(resultado);
        assertNotNull(resultado.getAcaoDTO());
        assertNotNull(resultado.getImagemDTOList());
        assertEquals(1, resultado.getImagemDTOList().size());

        ImagemAcaoDTO imagemResultado = resultado.getImagemDTOList().get(0);
        assertNotNull(imagemResultado.getImagem());
        assertArrayEquals(imagemBytes, imagemResultado.getImagem());

        verify(acaoRepository, times(1)).findById(acaoId);
        verify(imagemAcaoRepository, times(1)).findByAcao(acao);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar ação inexistente por ID")
    void deveLancarExcecaoAoBuscarAcaoInexistentePorId() {
        
        when(acaoRepository.findById(acaoId)).thenReturn(Optional.empty());
         
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> acaoService.buscarPorId(acaoId));

        assertEquals("Ação não encontrada", exception.getMessage());
        verify(acaoRepository, times(1)).findById(acaoId);
        verify(imagemAcaoRepository, never()).findByAcao(any(Acao.class));
    }

    @Test
    @DisplayName("Deve buscar todas as ações sem filtros")
    void deveBuscarTodasAcoesSemFiltros() {
        
        Page<Acao> pageAcoes = new PageImpl<>(Collections.singletonList(acao));
        when(acaoRepository.findAll(pageable)).thenReturn(pageAcoes);
        when(imagemAcaoRepository.findByAcao(acao)).thenReturn(imagensAcao);
        
        Page<AcaoResponseDTO> resultado = acaoService.buscarTodos(null, null, null, pageable);
        
        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());

        AcaoResponseDTO acaoResponse = resultado.getContent().get(0);
        assertNotNull(acaoResponse.getAcaoDTO());
        assertNotNull(acaoResponse.getImagemDTOList());
        assertEquals(1, acaoResponse.getImagemDTOList().size());

        verify(acaoRepository, times(1)).findAll(pageable);
        verify(imagemAcaoRepository, times(1)).findByAcao(acao);
    }

    @Test
    @DisplayName("Deve buscar ações filtradas por tipo, ano e mês")
    void deveBuscarAcoesFiltradas_TipoAnoMes() {
        
        Page<Acao> pageAcoes = new PageImpl<>(Collections.singletonList(acao));
        when(acaoRepository.findByTipoAndAnoAndMes(ETipoAcao.SOCIAL_ALIMENTAR, 2023, 12, pageable))
                .thenReturn(pageAcoes);
        when(imagemAcaoRepository.findByAcao(acao)).thenReturn(imagensAcao);

        Page<AcaoResponseDTO> resultado = acaoService.buscarTodos(ETipoAcao.SOCIAL_ALIMENTAR, 2023, 12, pageable);
        
        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        verify(acaoRepository, times(1)).findByTipoAndAnoAndMes(ETipoAcao.SOCIAL_ALIMENTAR, 2023, 12, pageable);
    }

    @Test
    @DisplayName("Deve buscar ações filtradas por ano e mês")
    void deveBuscarAcoesFiltradas_AnoMes() {
        
        Page<Acao> pageAcoes = new PageImpl<>(Collections.singletonList(acao));
        when(acaoRepository.findByAnoAndMes(2023, 12, pageable)).thenReturn(pageAcoes);
        when(imagemAcaoRepository.findByAcao(acao)).thenReturn(imagensAcao);
        
        Page<AcaoResponseDTO> resultado = acaoService.buscarTodos(null, 2023, 12, pageable);
        
        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        verify(acaoRepository, times(1)).findByAnoAndMes(2023, 12, pageable);
    }

    @Test
    @DisplayName("Deve buscar ações filtradas por tipo e ano")
    void deveBuscarAcoesFiltradas_TipoAno() {
        
        Page<Acao> pageAcoes = new PageImpl<>(Collections.singletonList(acao));
        when(acaoRepository.findByTipoAndAno(ETipoAcao.SOCIAL_ALIMENTAR, 2023, pageable)).thenReturn(pageAcoes);
        when(imagemAcaoRepository.findByAcao(acao)).thenReturn(imagensAcao);

        Page<AcaoResponseDTO> resultado = acaoService.buscarTodos(ETipoAcao.SOCIAL_ALIMENTAR, 2023, null, pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        verify(acaoRepository, times(1)).findByTipoAndAno(ETipoAcao.SOCIAL_ALIMENTAR, 2023, pageable);
    }

    @Test
    @DisplayName("Deve buscar ações filtradas por tipo")
    void deveBuscarAcoesFiltradas_Tipo() {
        
        Page<Acao> pageAcoes = new PageImpl<>(Collections.singletonList(acao));
        when(acaoRepository.findByTipo(ETipoAcao.SOCIAL_ALIMENTAR, pageable)).thenReturn(pageAcoes);
        when(imagemAcaoRepository.findByAcao(acao)).thenReturn(imagensAcao);

        Page<AcaoResponseDTO> resultado = acaoService.buscarTodos(ETipoAcao.SOCIAL_ALIMENTAR, null, null, pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        verify(acaoRepository, times(1)).findByTipo(ETipoAcao.SOCIAL_ALIMENTAR, pageable);
    }

    @Test
    @DisplayName("Deve buscar ações filtradas por ano")
    void deveBuscarAcoesFiltradas_Ano() {
        
        Page<Acao> pageAcoes = new PageImpl<>(Collections.singletonList(acao));
        when(acaoRepository.findByAno(2023, pageable)).thenReturn(pageAcoes);
        when(imagemAcaoRepository.findByAcao(acao)).thenReturn(imagensAcao);

        Page<AcaoResponseDTO> resultado = acaoService.buscarTodos(null, 2023, null, pageable);
        
        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        verify(acaoRepository, times(1)).findByAno(2023, pageable);
    }

    @Test
    @DisplayName("Deve buscar ações filtradas por mês")
    void deveBuscarAcoesFiltradas_Mes() {
        
        Page<Acao> pageAcoes = new PageImpl<>(Collections.singletonList(acao));
        when(acaoRepository.findByMes(12, pageable)).thenReturn(pageAcoes);
        when(imagemAcaoRepository.findByAcao(acao)).thenReturn(imagensAcao);
        
        Page<AcaoResponseDTO> resultado = acaoService.buscarTodos(null, null, 12, pageable);
        
        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        verify(acaoRepository, times(1)).findByMes(12, pageable);
    }

    @Test
    @DisplayName("Deve buscar ações com lista de imagens vazia")
    void deveBuscarAcoesComListaImagensVazia() {
        
        Page<Acao> pageAcoes = new PageImpl<>(Collections.singletonList(acao));
        when(acaoRepository.findAll(pageable)).thenReturn(pageAcoes);
        when(imagemAcaoRepository.findByAcao(acao)).thenReturn(Collections.emptyList());

        Page<AcaoResponseDTO> resultado = acaoService.buscarTodos(null, null, null, pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        assertTrue(resultado.getContent().get(0).getImagemDTOList().isEmpty());
        verify(acaoRepository, times(1)).findAll(pageable);
        verify(imagemAcaoRepository, times(1)).findByAcao(acao);
    }

    @Test
    @DisplayName("Deve atualizar ação sem imagens")
    void deveAtualizarAcaoSemImagens() {
        
        when(acaoRepository.findById(acaoId)).thenReturn(Optional.of(acao));
        when(acaoRepository.save(any(Acao.class))).thenReturn(acao);
        doNothing().when(imagemAcaoRepository).deleteAllByAcao(any(Acao.class));

        assertDoesNotThrow(() -> acaoService.atualizar(acaoId, acaoDTO, Collections.emptyList()));
        
        verify(acaoRepository, times(1)).findById(acaoId);
        verify(acaoRepository, times(1)).save(any(Acao.class));
        verify(imagemAcaoRepository, times(1)).deleteAllByAcao(any(Acao.class));
        verify(imagemAcaoRepository, never()).save(any(ImagemAcao.class));
    }

    @Test
    @DisplayName("Deve atualizar ação com múltiplas imagens")
    void deveAtualizarAcaoComMultiplasImagens() {
        
        ImagemAcaoDTO imagemAcaoDTO2 = ImagemAcaoDTO.builder()
                .uuidAcao(acaoId)
                .imagem("segunda-imagem-bytes".getBytes())
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        List<ImagemAcaoDTO> multiplasImagens = Arrays.asList(imagemAcaoDTO, imagemAcaoDTO2);

        when(acaoRepository.findById(acaoId)).thenReturn(Optional.of(acao));
        when(acaoRepository.save(any(Acao.class))).thenReturn(acao);
        doNothing().when(imagemAcaoRepository).deleteAllByAcao(any(Acao.class));
        when(imagemAcaoRepository.save(any(ImagemAcao.class))).thenReturn(imagemAcao);
        
        assertDoesNotThrow(() -> acaoService.atualizar(acaoId, acaoDTO, multiplasImagens));
        
        verify(acaoRepository, times(1)).findById(acaoId);
        verify(acaoRepository, times(1)).save(any(Acao.class));
        verify(imagemAcaoRepository, times(1)).deleteAllByAcao(any(Acao.class));
        verify(imagemAcaoRepository, times(2)).save(any(ImagemAcao.class));
    }

    @Test
    @DisplayName("Deve verificar mapeamento correto dos dados da imagem")
    void deveVerificarMapeamentoCorretoDadosImagem() {
        
        byte[] imagemEspecifica = "dados-especificos-da-imagem".getBytes();
        ImagemAcaoDTO imagemEspecifica_DTO = ImagemAcaoDTO.builder()
                .uuidAcao(acaoId)
                .imagem(imagemEspecifica)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        when(acaoRepository.save(any(Acao.class))).thenReturn(acao);
        when(imagemAcaoRepository.save(any(ImagemAcao.class))).thenReturn(imagemAcao);

        AcaoDTO resultado = acaoService.criar(acaoDTO, Collections.singletonList(imagemEspecifica_DTO));
        
        assertNotNull(resultado);
        verify(acaoRepository, times(1)).save(any(Acao.class));
        verify(imagemAcaoRepository, times(1)).save(any(ImagemAcao.class));
    }

    @Test
    @DisplayName("Deve lidar com imagens de tamanhos diferentes")
    void deveLidarComImagensTamanhosDiferentes() {
        
        byte[] imagemPequena = "pequena".getBytes();
        byte[] imagemGrande = "esta-e-uma-imagem-muito-maior-com-mais-dados-simulados".getBytes();

        ImagemAcaoDTO imagemPequenaDTO = ImagemAcaoDTO.builder()
                .uuidAcao(acaoId)
                .imagem(imagemPequena)
                .build();

        ImagemAcaoDTO imagemGrandeDTO = ImagemAcaoDTO.builder()
                .uuidAcao(acaoId)
                .imagem(imagemGrande)
                .build();

        List<ImagemAcaoDTO> imagensDiferentes = Arrays.asList(imagemPequenaDTO, imagemGrandeDTO);

        when(acaoRepository.save(any(Acao.class))).thenReturn(acao);
        when(imagemAcaoRepository.save(any(ImagemAcao.class))).thenReturn(imagemAcao);
        
        AcaoDTO resultado = acaoService.criar(acaoDTO, imagensDiferentes);
        
        assertNotNull(resultado);
        verify(acaoRepository, times(1)).save(any(Acao.class));
        verify(imagemAcaoRepository, times(2)).save(any(ImagemAcao.class));
    }
}