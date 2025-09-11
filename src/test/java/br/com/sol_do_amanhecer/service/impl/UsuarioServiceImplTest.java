package br.com.sol_do_amanhecer.service.impl;

import br.com.sol_do_amanhecer.exception.UsuarioException;
import br.com.sol_do_amanhecer.model.dto.PermissaoDTO;
import br.com.sol_do_amanhecer.model.dto.UsuarioDTO;
import br.com.sol_do_amanhecer.model.entity.Email;
import br.com.sol_do_amanhecer.model.entity.Permissao;
import br.com.sol_do_amanhecer.model.entity.Usuario;
import br.com.sol_do_amanhecer.model.entity.Voluntario;
import br.com.sol_do_amanhecer.model.mapper.UsuarioMapper;
import br.com.sol_do_amanhecer.repository.EmailRepository;
import br.com.sol_do_amanhecer.repository.PermissaoRepository;
import br.com.sol_do_amanhecer.repository.UsuarioRepository;
import br.com.sol_do_amanhecer.repository.VoluntarioRepository;
import br.com.sol_do_amanhecer.util.EmailUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioServiceImpl Tests")
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private VoluntarioRepository voluntarioRepository;
    @Mock
    private PermissaoRepository permissaoRepository;
    @Mock
    private EmailRepository emailRepository;
    @Mock
    private EmailUtil emailUtil;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private UUID usuarioId;
    private UUID voluntarioId;
    private UUID permissaoId;
    private Usuario usuario;
    private UsuarioDTO usuarioDTO;
    private Voluntario voluntario;
    private Permissao permissao;
    private Email email;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();
        voluntarioId = UUID.randomUUID();
        permissaoId = UUID.randomUUID();

        voluntario = new Voluntario();
        voluntario.setUuid(voluntarioId);
        voluntario.setNomeCompleto("João Silva");

        permissao = new Permissao();
        permissao.setUuid(permissaoId);
        permissao.setDescricao("ROLE_ADMIN");

        usuario = new Usuario();
        usuario.setUuid(usuarioId);
        usuario.setUsuario("joao.silva");
        usuario.setSenha("senha123");
        usuario.setAtivo(true);
        usuario.setContaBloqueada(false);
        usuario.setContaExpirada(false);
        usuario.setCredenciaisExpiradas(false);
        usuario.setVoluntario(voluntario);
        usuario.setPermissoes(Collections.singletonList(permissao));

        PermissaoDTO permissaoDTO = new PermissaoDTO();
        permissaoDTO.setUuid(permissaoId);
        permissaoDTO.setDescricao("ROLE_ADMIN");

        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setUsuario("joao.silva");
        usuarioDTO.setSenha("senha123");
        usuarioDTO.setAtivo(true);
        usuarioDTO.setUuidVoluntario(voluntarioId);
        usuarioDTO.setPermissaoDTOList(List.of(permissaoDTO));

        email = new Email();
        email.setUuid(UUID.randomUUID());
        email.setEmail("joao.silva@email.com");
        email.setVoluntario(voluntario);

        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("Deve retornar UserDetails quando usuário existe")
    void loadUserByUsername_UsuarioExiste() {
        
        when(usuarioRepository.findByUsuario("joao.silva")).thenReturn(usuario);

        
        UserDetails resultado = usuarioService.loadUserByUsername("joao.silva");

        
        assertNotNull(resultado);
        assertEquals(usuario, resultado);
        verify(usuarioRepository, times(1)).findByUsuario("joao.silva");
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException quando usuário não existe")
    void loadUserByUsername_UsuarioNaoExiste() {
        
        when(usuarioRepository.findByUsuario("inexistente")).thenReturn(null);

        
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> usuarioService.loadUserByUsername("inexistente"));

        assertEquals("Usuário inexistente não encontrado", exception.getMessage());
        verify(usuarioRepository, times(1)).findByUsuario("inexistente");
    }

    @Test
    @DisplayName("Deve buscar usuário por ID com sucesso")
    void buscarPorId_Sucesso() {
        
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(usuarioMapper.entityParaDto(usuario)).thenReturn(usuarioDTO);

        
        UsuarioDTO resultado = usuarioService.buscarPorId(usuarioId);

        
        assertNotNull(resultado);
        assertEquals(usuarioDTO, resultado);
        verify(usuarioRepository, times(1)).findById(usuarioId);
        verify(usuarioMapper, times(1)).entityParaDto(usuario);
    }

    @Test
    @DisplayName("Deve lançar UsuarioException quando usuário não encontrado por ID")
    void buscarPorId_UsuarioNaoEncontrado() {
        
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        
        UsuarioException exception = assertThrows(UsuarioException.class,
                () -> usuarioService.buscarPorId(usuarioId));

        assertEquals("Usuário não encontrado com ID: " + usuarioId, exception.getMessage());
        verify(usuarioRepository, times(1)).findById(usuarioId);
        verify(usuarioMapper, never()).entityParaDto(any());
    }

    @Test
    @DisplayName("Deve buscar todos os usuários sem filtro")
    void buscarTodos_SemFiltro() {
        
        Page<Usuario> pageUsuarios = new PageImpl<>(Collections.singletonList(usuario));
        when(usuarioRepository.findAll(pageable)).thenReturn(pageUsuarios);
        when(usuarioMapper.entityParaDto(usuario)).thenReturn(usuarioDTO);

        
        Page<UsuarioDTO> resultado = usuarioService.buscarTodos(null, pageable);

        
        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        assertEquals(usuarioDTO, resultado.getContent().get(0));
        verify(usuarioRepository, times(1)).findAll(pageable);
        verify(usuarioRepository, never()).findByAtivo(any(), any());
        verify(usuarioMapper, times(1)).entityParaDto(usuario);
    }

    @Test
    @DisplayName("Deve buscar usuários ativos")
    void buscarTodos_ComFiltroAtivo() {
        
        Page<Usuario> pageUsuarios = new PageImpl<>(Collections.singletonList(usuario));
        when(usuarioRepository.findByAtivo(true, pageable)).thenReturn(pageUsuarios);
        when(usuarioMapper.entityParaDto(usuario)).thenReturn(usuarioDTO);

        
        Page<UsuarioDTO> resultado = usuarioService.buscarTodos(true, pageable);

        
        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        verify(usuarioRepository, times(1)).findByAtivo(true, pageable);
        verify(usuarioRepository, never()).findAll((Example<Usuario>) any());
    }

    @Test
    @DisplayName("Deve buscar usuários inativos")
    void buscarTodos_ComFiltroInativo() {
        
        Page<Usuario> pageUsuarios = new PageImpl<>(Collections.singletonList(usuario));
        when(usuarioRepository.findByAtivo(false, pageable)).thenReturn(pageUsuarios);
        when(usuarioMapper.entityParaDto(usuario)).thenReturn(usuarioDTO);

        
        Page<UsuarioDTO> resultado = usuarioService.buscarTodos(false, pageable);

        
        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        verify(usuarioRepository, times(1)).findByAtivo(false, pageable);
        verify(usuarioRepository, never()).findAll((Example<Usuario>) any());
    }

    @Test
    @DisplayName("Deve retornar página vazia quando não há usuários")
    void buscarTodos_PaginaVazia() {
        
        Page<Usuario> paginaVazia = new PageImpl<>(Collections.emptyList());
        when(usuarioRepository.findAll(pageable)).thenReturn(paginaVazia);

        
        Page<UsuarioDTO> resultado = usuarioService.buscarTodos(null, pageable);

        
        assertNotNull(resultado);
        assertTrue(resultado.getContent().isEmpty());
        verify(usuarioRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve criar usuário com sucesso")
    void criar_Sucesso() {
        
        when(voluntarioRepository.findById(voluntarioId)).thenReturn(Optional.of(voluntario));
        when(permissaoRepository.findById(permissaoId)).thenReturn(Optional.of(permissao));
        when(usuarioMapper.dtoParaEntity(usuarioDTO)).thenReturn(usuario);
        when(passwordEncoder.encode("senha123")).thenReturn("senhaEncriptada");
        when(usuarioRepository.save(usuario)).thenReturn(usuario);
        when(usuarioMapper.entityParaDto(usuario)).thenReturn(usuarioDTO);

        
        UsuarioDTO resultado = usuarioService.criar(usuarioDTO);

        
        assertNotNull(resultado);
        assertEquals(usuarioDTO, resultado);
        assertFalse(usuario.getContaBloqueada());
        assertFalse(usuario.getContaExpirada());
        assertFalse(usuario.getCredenciaisExpiradas());
        assertEquals(voluntario, usuario.getVoluntario());
        assertEquals(Collections.singletonList(permissao), usuario.getPermissoes());
        assertEquals("senhaEncriptada", usuario.getSenha());

        verify(voluntarioRepository, times(1)).findById(voluntarioId);
        verify(permissaoRepository, times(1)).findById(permissaoId);
        verify(usuarioMapper, times(1)).dtoParaEntity(usuarioDTO);
        verify(passwordEncoder, times(1)).encode("senha123");
        verify(usuarioRepository, times(1)).save(usuario);
        verify(usuarioMapper, times(1)).entityParaDto(usuario);
    }

    @Test
    @DisplayName("Deve criar usuário com múltiplas permissões")
    void criar_ComMultiplasPermissoes() {
        UUID permissao2Id = UUID.randomUUID();
        Permissao permissao2 = new Permissao();
        permissao2.setUuid(permissao2Id);
        permissao2.setDescricao("ROLE_USER");

        PermissaoDTO permissaoDTO2 = new PermissaoDTO();
        permissaoDTO2.setUuid(permissao2Id);
        permissaoDTO2.setDescricao("ROLE_USER");

        List<PermissaoDTO> permissoesMutaveis = new ArrayList<>();
        permissoesMutaveis.addAll(usuarioDTO.getPermissaoDTOList());
        permissoesMutaveis.add(permissaoDTO2);
        usuarioDTO.setPermissaoDTOList(permissoesMutaveis);

        when(voluntarioRepository.findById(voluntarioId)).thenReturn(Optional.of(voluntario));
        when(permissaoRepository.findById(permissaoId)).thenReturn(Optional.of(permissao));
        when(permissaoRepository.findById(permissao2Id)).thenReturn(Optional.of(permissao2));
        when(usuarioMapper.dtoParaEntity(usuarioDTO)).thenReturn(usuario);
        when(passwordEncoder.encode("senha123")).thenReturn("senhaEncriptada");
        when(usuarioRepository.save(usuario)).thenReturn(usuario);
        when(usuarioMapper.entityParaDto(usuario)).thenReturn(usuarioDTO);

        UsuarioDTO resultado = usuarioService.criar(usuarioDTO);

        assertNotNull(resultado);
        assertEquals(2, usuario.getPermissoes().size());
        verify(permissaoRepository, times(1)).findById(permissaoId);
        verify(permissaoRepository, times(1)).findById(permissao2Id);
    }

    @Test
    @DisplayName("Deve lançar UsuarioException quando voluntário não encontrado")
    void criar_VoluntarioNaoEncontrado() {
        
        when(voluntarioRepository.findById(voluntarioId)).thenReturn(Optional.empty());

        
        UsuarioException exception = assertThrows(UsuarioException.class,
                () -> usuarioService.criar(usuarioDTO));

        assertEquals("Voluntário não encontrado com ID: " + voluntarioId, exception.getMessage());
        verify(voluntarioRepository, times(1)).findById(voluntarioId);
        verify(permissaoRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Deve lançar UsuarioException quando permissão não encontrada")
    void criar_PermissaoNaoEncontrada() {
        
        when(voluntarioRepository.findById(voluntarioId)).thenReturn(Optional.of(voluntario));
        when(permissaoRepository.findById(permissaoId)).thenReturn(Optional.empty());

        
        UsuarioException exception = assertThrows(UsuarioException.class,
                () -> usuarioService.criar(usuarioDTO));

        assertEquals("Permissão não encontrada com ID: " + permissaoId, exception.getMessage());
        verify(voluntarioRepository, times(1)).findById(voluntarioId);
        verify(permissaoRepository, times(1)).findById(permissaoId);
    }

    @Test
    @DisplayName("Deve atualizar usuário com sucesso - usuário ativo")
    void atualizar_UsuarioAtivo_Sucesso() {
        PermissaoDTO permissaoDTO = new PermissaoDTO();
        permissaoDTO.setUuid(permissaoId);
        permissaoDTO.setDescricao("ROLE_ADMIN");

        UsuarioDTO usuarioAtualizadoDTO = new UsuarioDTO();
        usuarioAtualizadoDTO.setUsuario("joao.atualizado");
        usuarioAtualizadoDTO.setSenha("novaSenha");
        usuarioAtualizadoDTO.setAtivo(true);
        usuarioAtualizadoDTO.setUuidVoluntario(voluntarioId);
        usuarioAtualizadoDTO.setPermissaoDTOList(List.of(permissaoDTO));

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("novaSenha")).thenReturn("novaSenhaEncriptada");
        when(voluntarioRepository.findById(voluntarioId)).thenReturn(Optional.of(voluntario));
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        assertDoesNotThrow(() -> usuarioService.atualizar(usuarioId, usuarioAtualizadoDTO));

        assertEquals("joao.atualizado", usuario.getUsuario());
        assertEquals("novaSenhaEncriptada", usuario.getSenha());
        assertTrue(usuario.getAtivo());
        assertFalse(usuario.getContaExpirada());
        assertFalse(usuario.getContaBloqueada());
        assertFalse(usuario.getCredenciaisExpiradas());
        assertEquals(voluntario, usuario.getVoluntario());

        verify(usuarioRepository, times(1)).findById(usuarioId);
        verify(passwordEncoder, times(1)).encode("novaSenha");
        verify(voluntarioRepository, times(1)).findById(voluntarioId);
        verify(usuarioRepository, times(1)).save(usuario);
        verify(permissaoRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Deve atualizar usuário com sucesso - usuário inativo")
    void atualizar_UsuarioInativo_Sucesso() {
        
        UsuarioDTO usuarioAtualizadoDTO = new UsuarioDTO();
        usuarioAtualizadoDTO.setUsuario("joao.inativo");
        usuarioAtualizadoDTO.setSenha("novaSenha");
        usuarioAtualizadoDTO.setAtivo(false);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("novaSenha")).thenReturn("novaSenhaEncriptada");
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        
        assertDoesNotThrow(() -> usuarioService.atualizar(usuarioId, usuarioAtualizadoDTO));

        
        assertEquals("joao.inativo", usuario.getUsuario());
        assertFalse(usuario.getAtivo());
        assertTrue(usuario.getContaExpirada());
        assertTrue(usuario.getContaBloqueada());
        assertTrue(usuario.getCredenciaisExpiradas());

        verify(usuarioRepository, times(1)).findById(usuarioId);
        verify(passwordEncoder, times(1)).encode("novaSenha");
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    @DisplayName("Deve atualizar usuário removendo voluntário")
    void atualizar_RemoverVoluntario() {
        
        UsuarioDTO usuarioAtualizadoDTO = new UsuarioDTO();
        usuarioAtualizadoDTO.setUsuario("joao.silva");
        usuarioAtualizadoDTO.setSenha("senha123");
        usuarioAtualizadoDTO.setAtivo(true);
        usuarioAtualizadoDTO.setUuidVoluntario(null);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("senha123")).thenReturn("senhaEncriptada");
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        
        assertDoesNotThrow(() -> usuarioService.atualizar(usuarioId, usuarioAtualizadoDTO));

        
        assertNull(usuario.getVoluntario());
        verify(usuarioRepository, times(1)).findById(usuarioId);
        verify(voluntarioRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Deve atualizar usuário com permissões nulas")
    void atualizar_PermissoesNulas() {
        
        UsuarioDTO usuarioAtualizadoDTO = new UsuarioDTO();
        usuarioAtualizadoDTO.setUsuario("joao.silva");
        usuarioAtualizadoDTO.setSenha("senha123");
        usuarioAtualizadoDTO.setAtivo(true);
        usuarioAtualizadoDTO.setPermissaoDTOList(null);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("senha123")).thenReturn("senhaEncriptada");
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        
        assertDoesNotThrow(() -> usuarioService.atualizar(usuarioId, usuarioAtualizadoDTO));

        
        verify(usuarioRepository, times(1)).findById(usuarioId);
        verify(permissaoRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Deve atualizar permissões do usuário - adicionar nova permissão")
    void atualizar_AdicionarNovaPermissao() {
        
        UUID novaPermissaoId = UUID.randomUUID();
        Permissao novaPermissao = new Permissao();
        novaPermissao.setUuid(novaPermissaoId);
        novaPermissao.setDescricao("ROLE_USER");

        PermissaoDTO permissaoExistenteDTO = new PermissaoDTO();
        permissaoExistenteDTO.setUuid(permissaoId); // UUID da permissão existente
        permissaoExistenteDTO.setDescricao("ROLE_ADMIN");

        PermissaoDTO novaPermissaoDTO = new PermissaoDTO();
        novaPermissaoDTO.setUuid(novaPermissaoId); // UUID da nova permissão
        novaPermissaoDTO.setDescricao("ROLE_USER");

        UsuarioDTO usuarioAtualizadoDTO = new UsuarioDTO();
        usuarioAtualizadoDTO.setUsuario("joao.silva");
        usuarioAtualizadoDTO.setSenha("senha123");
        usuarioAtualizadoDTO.setAtivo(true);
        usuarioAtualizadoDTO.setPermissaoDTOList(Arrays.asList(
                permissaoExistenteDTO,
                novaPermissaoDTO
        ));

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("senha123")).thenReturn("senhaEncriptada");
        when(permissaoRepository.findById(novaPermissaoId)).thenReturn(Optional.of(novaPermissao));
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        
        assertDoesNotThrow(() -> usuarioService.atualizar(usuarioId, usuarioAtualizadoDTO));

        
        assertEquals(2, usuario.getPermissoes().size());
        verify(permissaoRepository, times(1)).findById(novaPermissaoId);
    }

    @Test
    @DisplayName("Deve atualizar permissões do usuário - remover permissão existente")
    void atualizar_RemoverPermissaoExistente() {
        UUID permissao2Id = UUID.randomUUID();
        Permissao permissao2 = new Permissao();
        permissao2.setUuid(permissao2Id);
        permissao2.setDescricao("ROLE_USER");

        List<Permissao> permissoesMutaveis = new ArrayList<>(usuario.getPermissoes());
        permissoesMutaveis.add(permissao2);
        usuario.setPermissoes(permissoesMutaveis);

        PermissaoDTO permissaoDTO = new PermissaoDTO();
        permissaoDTO.setUuid(permissaoId);
        permissaoDTO.setDescricao("ROLE_ADMIN");

        UsuarioDTO usuarioAtualizadoDTO = new UsuarioDTO();
        usuarioAtualizadoDTO.setUsuario("joao.silva");
        usuarioAtualizadoDTO.setSenha("senha123");
        usuarioAtualizadoDTO.setAtivo(true);
        usuarioAtualizadoDTO.setPermissaoDTOList(List.of(permissaoDTO));

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("senha123")).thenReturn("senhaEncriptada");
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        assertDoesNotThrow(() -> usuarioService.atualizar(usuarioId, usuarioAtualizadoDTO));

        assertEquals(1, usuario.getPermissoes().size());
        assertEquals(permissaoId, usuario.getPermissoes().get(0).getUuid());
    }

    @Test
    @DisplayName("Deve atualizar usuário com permissões vazias inicialmente")
    void atualizar_PermissoesVaziasInicialmente() {
        usuario.setPermissoes(null);

        PermissaoDTO permissaoDTO = new PermissaoDTO();
        permissaoDTO.setUuid(permissaoId);
        permissaoDTO.setDescricao("ROLE_ADMIN");

        UsuarioDTO usuarioAtualizadoDTO = new UsuarioDTO();
        usuarioAtualizadoDTO.setUsuario("joao.silva");
        usuarioAtualizadoDTO.setSenha("senha123");
        usuarioAtualizadoDTO.setAtivo(true);
        usuarioAtualizadoDTO.setPermissaoDTOList(List.of(permissaoDTO)); // Usar o DTO criado corretamente

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("senha123")).thenReturn("senhaEncriptada");
        when(permissaoRepository.findById(permissaoId)).thenReturn(Optional.of(permissao));
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        assertDoesNotThrow(() -> usuarioService.atualizar(usuarioId, usuarioAtualizadoDTO));

        assertEquals(1, usuario.getPermissoes().size());
        verify(permissaoRepository, times(1)).findById(permissaoId);
    }

    @Test
    @DisplayName("Deve lançar UsuarioException quando usuário não encontrado para atualização")
    void atualizar_UsuarioNaoEncontrado() {
        
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        
        UsuarioException exception = assertThrows(UsuarioException.class,
                () -> usuarioService.atualizar(usuarioId, usuarioDTO));

        assertEquals("Usuário não encontrado com ID: " + usuarioId, exception.getMessage());
        verify(usuarioRepository, times(1)).findById(usuarioId);
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar UsuarioException quando voluntário não encontrado na atualização")
    void atualizar_VoluntarioNaoEncontrado() {
        
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("senha123")).thenReturn("senhaEncriptada");
        when(voluntarioRepository.findById(voluntarioId)).thenReturn(Optional.empty());

        
        UsuarioException exception = assertThrows(UsuarioException.class,
                () -> usuarioService.atualizar(usuarioId, usuarioDTO));

        assertEquals("Voluntário não encontrado com ID: " + voluntarioId, exception.getMessage());
        verify(voluntarioRepository, times(1)).findById(voluntarioId);
    }

    @Test
    @DisplayName("Deve lançar UsuarioException quando permissão não encontrada na atualização")
    void atualizar_PermissaoNaoEncontrada() {
        UUID permissaoInexistenteId = UUID.randomUUID();
        UsuarioDTO usuarioComPermissaoInexistente = getUsuarioDTO(permissaoInexistenteId);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("senha123")).thenReturn("senhaEncriptada");
        when(voluntarioRepository.findById(voluntarioId)).thenReturn(Optional.of(voluntario));
        when(permissaoRepository.findById(permissaoInexistenteId)).thenReturn(Optional.empty());

        UsuarioException exception = assertThrows(UsuarioException.class,
                () -> usuarioService.atualizar(usuarioId, usuarioComPermissaoInexistente));

        assertEquals("Permissão não encontrada com ID: " + permissaoInexistenteId, exception.getMessage());
        verify(voluntarioRepository, times(1)).findById(voluntarioId);
        verify(permissaoRepository, times(1)).findById(permissaoInexistenteId);
    }

    private UsuarioDTO getUsuarioDTO(UUID permissaoInexistenteId) {
        PermissaoDTO permissaoInexistenteDTO = new PermissaoDTO();
        permissaoInexistenteDTO.setUuid(permissaoInexistenteId);
        permissaoInexistenteDTO.setDescricao("ROLE_INEXISTENTE");

        UsuarioDTO usuarioComPermissaoInexistente = new UsuarioDTO();
        usuarioComPermissaoInexistente.setUsuario("joao.silva");
        usuarioComPermissaoInexistente.setSenha("senha123");
        usuarioComPermissaoInexistente.setAtivo(true);
        usuarioComPermissaoInexistente.setUuidVoluntario(voluntarioId);
        usuarioComPermissaoInexistente.setPermissaoDTOList(List.of(permissaoInexistenteDTO));
        return usuarioComPermissaoInexistente;
    }

    @Test
    @DisplayName("Deve trocar senha com sucesso")
    void trocarSenha_Sucesso() {
        
        String novaSenha = "novaSenha123";
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode(novaSenha)).thenReturn("novaSenhaEncriptada");
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        
        assertDoesNotThrow(() -> usuarioService.trocarSenha(usuarioId, novaSenha));

        
        assertEquals("novaSenhaEncriptada", usuario.getSenha());
        verify(usuarioRepository, times(1)).findById(usuarioId);
        verify(passwordEncoder, times(1)).encode(novaSenha);
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    @DisplayName("Deve lançar UsuarioException quando usuário não encontrado para trocar senha")
    void trocarSenha_UsuarioNaoEncontrado() {
        
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        
        UsuarioException exception = assertThrows(UsuarioException.class,
                () -> usuarioService.trocarSenha(usuarioId, "novaSenha"));

        assertEquals("Usuário não encontrado com ID: " + usuarioId, exception.getMessage());
        verify(usuarioRepository, times(1)).findById(usuarioId);
        verify(passwordEncoder, never()).encode(any());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve remover usuário com sucesso (desativação)")
    void remover_Sucesso() {
        
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        
        assertDoesNotThrow(() -> usuarioService.remover(usuarioId));

        
        assertTrue(usuario.getContaExpirada());
        assertTrue(usuario.getContaBloqueada());
        assertTrue(usuario.getCredenciaisExpiradas());
        assertFalse(usuario.getAtivo());

        verify(usuarioRepository, times(1)).findById(usuarioId);
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    @DisplayName("Deve lançar UsuarioException quando usuário não encontrado para remoção")
    void remover_UsuarioNaoEncontrado() {
        
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        
        UsuarioException exception = assertThrows(UsuarioException.class,
                () -> usuarioService.remover(usuarioId));

        assertEquals("Usuário não encontrado com ID: " + usuarioId, exception.getMessage());
        verify(usuarioRepository, times(1)).findById(usuarioId);
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve enviar email de redefinição de senha com sucesso")
    void enviarEmailRedefinicaoSenhaPorUsername_Sucesso() {
        
        when(usuarioRepository.findByUsuario("joao.silva")).thenReturn(usuario);
        when(emailRepository.findFirstByVoluntarioUuid(voluntarioId)).thenReturn(Optional.of(email));
        doNothing().when(emailUtil).enviarEmail(anyString(), anyString(), anyString());

        
        assertDoesNotThrow(() -> usuarioService.enviarEmailRedefinicaoSenhaPorUsername("joao.silva"));

        
        verify(usuarioRepository, times(1)).findByUsuario("joao.silva");
        verify(emailRepository, times(1)).findFirstByVoluntarioUuid(voluntarioId);
        verify(emailUtil, times(1)).enviarEmail(
                eq("joao.silva@email.com"),
                eq("Redefinição de Senha"),
                contains("https://soldoamanhecer.com.br/trocar-senha/" + usuarioId)
        );
    }

    @Test
    @DisplayName("Deve lançar UsuarioException quando usuário não encontrado por username")
    void enviarEmailRedefinicaoSenhaPorUsername_UsuarioNaoEncontrado() {
        
        when(usuarioRepository.findByUsuario("inexistente")).thenReturn(null);

        
        UsuarioException exception = assertThrows(UsuarioException.class,
                () -> usuarioService.enviarEmailRedefinicaoSenhaPorUsername("inexistente"));

        assertEquals("Usuário não encontrado com o username: inexistente", exception.getMessage());
        verify(usuarioRepository, times(1)).findByUsuario("inexistente");
        verify(emailRepository, never()).findFirstByVoluntarioUuid(any());
        verify(emailUtil, never()).enviarEmail(any(), any(), any());
    }

    @Test
    @DisplayName("Deve lançar UsuarioException quando voluntário não associado ao usuário")
    void enviarEmailRedefinicaoSenhaPorUsername_VoluntarioNaoAssociado() {
        
        usuario.setVoluntario(null);
        when(usuarioRepository.findByUsuario("joao.silva")).thenReturn(usuario);

        
        UsuarioException exception = assertThrows(UsuarioException.class,
                () -> usuarioService.enviarEmailRedefinicaoSenhaPorUsername("joao.silva"));

        assertEquals("Voluntário associado ao usuário não encontrado.", exception.getMessage());
        verify(usuarioRepository, times(1)).findByUsuario("joao.silva");
        verify(emailRepository, never()).findFirstByVoluntarioUuid(any());
        verify(emailUtil, never()).enviarEmail(any(), any(), any());
    }

    @Test
    @DisplayName("Deve lançar UsuarioException quando email não encontrado para o voluntário")
    void enviarEmailRedefinicaoSenhaPorUsername_EmailNaoEncontrado() {
        
        when(usuarioRepository.findByUsuario("joao.silva")).thenReturn(usuario);
        when(emailRepository.findFirstByVoluntarioUuid(voluntarioId)).thenReturn(Optional.empty());

        
        UsuarioException exception = assertThrows(UsuarioException.class,
                () -> usuarioService.enviarEmailRedefinicaoSenhaPorUsername("joao.silva"));

        assertEquals("Nenhum e-mail encontrado para o voluntário associado ao usuário.", exception.getMessage());
        verify(usuarioRepository, times(1)).findByUsuario("joao.silva");
        verify(emailRepository, times(1)).findFirstByVoluntarioUuid(voluntarioId);
        verify(emailUtil, never()).enviarEmail(any(), any(), any());
    }
}