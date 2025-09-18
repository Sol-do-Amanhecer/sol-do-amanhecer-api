package br.com.sol_do_amanhecer.service.impl;

import br.com.sol_do_amanhecer.exception.PermissaoException;
import br.com.sol_do_amanhecer.model.dto.PermissaoDTO;
import br.com.sol_do_amanhecer.model.entity.Permissao;
import br.com.sol_do_amanhecer.model.mapper.PermissaoMapper;
import br.com.sol_do_amanhecer.repository.PermissaoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class PermissaoServiceImplTest {

    @Mock
    private PermissaoRepository permissaoRepository;

    @Spy
    private PermissaoMapper permissaoMapper = PermissaoMapper.INSTANCE;

    @InjectMocks
    private PermissaoServiceImpl permissaoService;

    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    @DisplayName("Deve retornar PermissaoDTO ao buscar por ID existente")
    void buscarPorId_deveRetornarPermissaoDTO_quandoEncontrar() {
        UUID id = UUID.randomUUID();
        Permissao permissao = new Permissao(id, "ROLE_USER");

        when(permissaoRepository.findById(id)).thenReturn(Optional.of(permissao));

        PermissaoDTO resultado = permissaoService.buscarPorId(id);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getUuid()).isEqualTo(id);
        assertThat(resultado.getDescricao()).isEqualTo("ROLE_USER");

        verify(permissaoRepository).findById(id);
    }

    @Test
    @DisplayName("Deve lançar PermissaoException ao buscar por ID inexistente")
    void buscarPorId_deveLancarExcecao_quandoNaoEncontrar() {
        UUID id = UUID.randomUUID();
        when(permissaoRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> permissaoService.buscarPorId(id))
                .isInstanceOf(PermissaoException.class)
                .hasMessageContaining("Permissão não encontrada com ID: " + id);

        verify(permissaoRepository).findById(id);
    }

    @Test
    @DisplayName("Deve retornar lista de PermissaoDTO ao buscar todas permissões")
    void buscarTodos_deveRetornarListaDePermissaoDTO() {
        Permissao permissao1 = new Permissao(UUID.randomUUID(), "ROLE_USER");
        Permissao permissao2 = new Permissao(UUID.randomUUID(), "ROLE_ADMIN");
        List<Permissao> permissoes = Arrays.asList(permissao1, permissao2);

        when(permissaoRepository.findAll()).thenReturn(permissoes);

        List<PermissaoDTO> resultado = permissaoService.buscarTodos();

        assertThat(resultado)
                .extracting(PermissaoDTO::getDescricao)
                .containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");

        verify(permissaoRepository).findAll();
    }

    @Test
    @DisplayName("Deve criar e retornar PermissaoDTO corretamente")
    void criar_deveSalvarPermissaoERetornarDTO() {
        UUID id = UUID.randomUUID();
        PermissaoDTO permissaoDTO = new PermissaoDTO(null, "ROLE_USER");
        Permissao permissaoEntity = new Permissao(id, "ROLE_USER");

        doReturn(permissaoEntity).when(permissaoMapper).dtoParaEntity(any(PermissaoDTO.class));
        doReturn(new PermissaoDTO(id, "ROLE_USER")).when(permissaoMapper).entityParaDto(any(Permissao.class));

        when(permissaoRepository.save(any(Permissao.class))).thenReturn(permissaoEntity);

        PermissaoDTO resultado = permissaoService.criar(permissaoDTO);

        assertThat(resultado.getDescricao()).isEqualTo("ROLE_USER");
        verify(permissaoRepository).save(any(Permissao.class));
    }

    @Test
    @DisplayName("Deve alterar descrição ao atualizar uma permissão existente")
    void atualizar_deveAlterarDescricaoQuandoEncontrar() {
        UUID id = UUID.randomUUID();
        Permissao permissao = new Permissao(id, "OLD_ROLE");
        when(permissaoRepository.findById(id)).thenReturn(Optional.of(permissao));

        PermissaoDTO permissaoDTO = new PermissaoDTO(id, "NEW_ROLE");

        permissaoService.atualizar(id, permissaoDTO);

        assertThat(permissao.getDescricao()).isEqualTo("NEW_ROLE");
        verify(permissaoRepository).save(permissao);
    }

    @Test
    @DisplayName("Deve lançar PermissaoException ao tentar atualizar permissão inexistente")
    void atualizar_deveLancarExcecaoQuandoNaoEncontrar() {
        UUID id = UUID.randomUUID();
        when(permissaoRepository.findById(id)).thenReturn(Optional.empty());

        PermissaoDTO permissaoDTO = new PermissaoDTO(id, "ROLE_USER");

        assertThatThrownBy(() -> permissaoService.atualizar(id, permissaoDTO))
                .isInstanceOf(PermissaoException.class)
                .hasMessageContaining("Permissão não encontrada com ID: " + id);
    }

    @Test
    @DisplayName("Deve remover permissão quando encontrada por ID")
    void remover_deveRemoverPermissaoQuandoEncontrar() {
        UUID id = UUID.randomUUID();
        Permissao permissao = new Permissao(id, "ROLE_REMOVER");
        when(permissaoRepository.findById(id)).thenReturn(Optional.of(permissao));

        permissaoService.remover(id);

        verify(permissaoRepository).delete(permissao);
    }

    @Test
    @DisplayName("Deve lançar PermissaoException ao tentar remover permissão inexistente")
    void remover_deveLancarExcecaoQuandoNaoEncontrar() {
        UUID id = UUID.randomUUID();
        when(permissaoRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> permissaoService.remover(id))
                .isInstanceOf(PermissaoException.class)
                .hasMessageContaining("Permissão não encontrada com ID: " + id);
    }
}