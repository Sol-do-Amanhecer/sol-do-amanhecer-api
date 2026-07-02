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
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
@DisplayName("Testes do UsuarioServiceImpl")
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

    private final UUID VOLUNTARIO_ID = UUID.randomUUID();
    private final UUID USUARIO_ID = UUID.randomUUID();
    private final UUID PERMISSAO_ID = UUID.randomUUID();

    private Usuario criarUsuarioEntityCompleto() {
        Usuario entity = new Usuario();
        entity.setUuid(USUARIO_ID);
        entity.setUsuario("teste");
        entity.setContaExpirada(false);
        entity.setContaBloqueada(false);
        entity.setCredenciaisExpiradas(false);
        entity.setAtivo(true);
        entity.setPermissoes(null);
        Voluntario v = new Voluntario();
        v.setUuid(VOLUNTARIO_ID);
        entity.setVoluntario(v);
        return entity;
    }

    private UsuarioDTO criarUsuarioDTOCompleto() {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setUsuario("teste");
        dto.setSenha(null);
        dto.setContaExpirada(false);
        dto.setContaBloqueada(false);
        dto.setCredenciaisExpiradas(false);
        dto.setAtivo(true);
        dto.setPermissaoDTOList(null);
        dto.setUuidVoluntario(VOLUNTARIO_ID);
        return dto;
    }

    private Voluntario criarVoluntario() {
        Voluntario voluntario = new Voluntario();
        voluntario.setUuid(VOLUNTARIO_ID);
        return voluntario;
    }

    private UsuarioDTO criarUsuarioDTOParaCriacao(List<PermissaoDTO> permissoes) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setUsuario("novo");
        dto.setSenha("senha123");
        dto.setUuidVoluntario(VOLUNTARIO_ID);
        dto.setPermissaoDTOList(permissoes);
        return dto;
    }

    private Permissao criarPermissaoAdmin() {
        return new Permissao(PERMISSAO_ID, "ROLE_ADMIN");
    }

    private UsuarioDTO criarUsuarioDTORetorno() {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setUsuario("novo");
        return dto;
    }

    private void configurarMocksBasicosCriar(UsuarioDTO dto, Voluntario voluntario,
                                              Usuario entity, UsuarioDTO salvo) {
        when(voluntarioRepository.findById(VOLUNTARIO_ID)).thenReturn(Optional.of(voluntario));
        when(usuarioMapper.dtoParaEntity(dto)).thenReturn(entity);
        when(passwordEncoder.encode("senha123")).thenReturn("senhaCriptografada");
        when(usuarioRepository.save(entity)).thenReturn(entity);
        when(usuarioMapper.entityParaDto(entity)).thenReturn(salvo);
    }

    @Nested
    @DisplayName("loadUserByUsername")
    class LoadUserByUsername {

        @Test
        @DisplayName("Deve retornar UserDetails ao buscar usuário existente pelo login")
        void loadUserByUsername_comUsuarioExistente() {
            String usuario = "testeUser";
            Usuario userDetails = new Usuario();
            userDetails.setUsuario(usuario);

            when(usuarioRepository.findByUsuario(usuario)).thenReturn(userDetails);

            var resultado = usuarioService.loadUserByUsername(usuario);

            assertThat(resultado).isEqualTo(userDetails);
            verify(usuarioRepository).findByUsuario(usuario);
        }

        @Test
        @DisplayName("Deve lançar UsernameNotFoundException ao buscar usuário inexistente pelo login")
        void loadUserByUsername_comUsuarioInexistente() {
            String usuario = "inexistente";

            when(usuarioRepository.findByUsuario(usuario)).thenReturn(null);

            assertThatThrownBy(() -> usuarioService.loadUserByUsername(usuario))
                    .isInstanceOf(UsernameNotFoundException.class)
                    .hasMessageContaining("Usuário " + usuario + " não encontrado");
            verify(usuarioRepository).findByUsuario(usuario);
        }
    }

    @Nested
    @DisplayName("buscarPorId")
    class BuscarPorId {

        @Test
        @DisplayName("Deve retornar UsuarioDTO ao buscar por ID existente")
        void buscarPorId_sucesso() {
            Usuario entity = criarUsuarioEntityCompleto();
            UsuarioDTO dto = criarUsuarioDTOCompleto();

            when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(entity));
            when(usuarioMapper.entityParaDto(entity)).thenReturn(dto);

            UsuarioDTO resultado = usuarioService.buscarPorId(USUARIO_ID);

            assertThat(resultado).usingRecursiveComparison().isEqualTo(dto);
            verify(usuarioRepository).findById(USUARIO_ID);
            verify(usuarioMapper).entityParaDto(entity);
        }

        @Test
        @DisplayName("Deve lançar UsuarioException ao buscar por ID inexistente")
        void buscarPorId_inexistente() {
            when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> usuarioService.buscarPorId(USUARIO_ID))
                    .isInstanceOf(UsuarioException.class)
                    .hasMessageContaining("Usuário não encontrado com ID: " + USUARIO_ID);

            verify(usuarioRepository).findById(USUARIO_ID);
        }
    }

    @Nested
    @DisplayName("buscarTodos")
    class BuscarTodos {

        @Test
        @DisplayName("Deve retornar Page de UsuarioDTO ao buscar todos os usuários sem filtro")
        void buscarTodos_semFiltro_sucesso() {
            Usuario entity1 = new Usuario();
            entity1.setUsuario("user1");
            Usuario entity2 = new Usuario();
            entity2.setUsuario("user2");

            Page<Usuario> pageEntity = new PageImpl<>(Arrays.asList(entity1, entity2));
            Pageable pageable = PageRequest.of(0, 10);

            UsuarioDTO dto1 = new UsuarioDTO();
            dto1.setUsuario("user1");
            UsuarioDTO dto2 = new UsuarioDTO();
            dto2.setUsuario("user2");

            when(usuarioRepository.findAll(pageable)).thenReturn(pageEntity);
            when(usuarioMapper.entityParaDto(entity1)).thenReturn(dto1);
            when(usuarioMapper.entityParaDto(entity2)).thenReturn(dto2);

            Page<UsuarioDTO> resultado = usuarioService.buscarTodos(null, pageable);

            assertThat(resultado.getContent()).containsExactlyInAnyOrder(dto1, dto2);
            assertThat(resultado.getTotalElements())
                    .as("Total de elementos deve ser 2")
                    .isEqualTo(2);
            verify(usuarioRepository).findAll(pageable);
            verify(usuarioRepository, never()).findByAtivo(anyBoolean(), any(Pageable.class));
            verify(usuarioMapper).entityParaDto(entity1);
            verify(usuarioMapper).entityParaDto(entity2);
        }

        @Test
        @DisplayName("Deve retornar Page de UsuarioDTO ao buscar usuários ativos")
        void buscarTodos_comFiltroAtivo_sucesso() {
            Usuario entity1 = new Usuario();
            entity1.setUsuario("user1");
            entity1.setAtivo(true);

            Page<Usuario> pageEntity = new PageImpl<>(Arrays.asList(entity1));
            Pageable pageable = PageRequest.of(0, 10);

            UsuarioDTO dto1 = new UsuarioDTO();
            dto1.setUsuario("user1");
            dto1.setAtivo(true);

            when(usuarioRepository.findByAtivo(true, pageable)).thenReturn(pageEntity);
            when(usuarioMapper.entityParaDto(entity1)).thenReturn(dto1);

            Page<UsuarioDTO> resultado = usuarioService.buscarTodos(true, pageable);

            assertThat(resultado.getContent()).containsExactly(dto1);
            assertThat(resultado.getTotalElements())
                    .as("Total de elementos deve ser 1")
                    .isEqualTo(1);
            verify(usuarioRepository).findByAtivo(true, pageable);
            verify(usuarioRepository, never()).findAll(any(Pageable.class));
            verify(usuarioMapper).entityParaDto(entity1);
        }

        @Test
        @DisplayName("Deve retornar Page de UsuarioDTO ao buscar usuários inativos")
        void buscarTodos_comFiltroInativo_sucesso() {
            Usuario entity1 = new Usuario();
            entity1.setUsuario("user1");
            entity1.setAtivo(false);

            Page<Usuario> pageEntity = new PageImpl<>(Arrays.asList(entity1));
            Pageable pageable = PageRequest.of(0, 10);

            UsuarioDTO dto1 = new UsuarioDTO();
            dto1.setUsuario("user1");
            dto1.setAtivo(false);

            when(usuarioRepository.findByAtivo(false, pageable)).thenReturn(pageEntity);
            when(usuarioMapper.entityParaDto(entity1)).thenReturn(dto1);

            Page<UsuarioDTO> resultado = usuarioService.buscarTodos(false, pageable);

            assertThat(resultado.getContent()).containsExactly(dto1);
            assertThat(resultado.getTotalElements())
                    .as("Total de elementos deve ser 1")
                    .isEqualTo(1);
            verify(usuarioRepository).findByAtivo(false, pageable);
            verify(usuarioRepository, never()).findAll(any(Pageable.class));
            verify(usuarioMapper).entityParaDto(entity1);
        }

        @Test
        @DisplayName("Deve retornar Page vazia quando não há usuários")
        void buscarTodos_pageVazia() {
            Page<Usuario> pageEntity = new PageImpl<>(Collections.emptyList());
            Pageable pageable = PageRequest.of(0, 10);

            when(usuarioRepository.findAll(pageable)).thenReturn(pageEntity);

            Page<UsuarioDTO> resultado = usuarioService.buscarTodos(null, pageable);

            assertThat(resultado.getContent()).isEmpty();
            assertThat(resultado.getTotalElements())
                    .as("Total de elementos deve ser 0")
                    .isEqualTo(0);
            verify(usuarioRepository).findAll(pageable);
        }
    }

    @Nested
    @DisplayName("criar")
    class Criar {

        @Test
        @DisplayName("Deve criar um usuário com sucesso")
        void criarUsuario_sucesso() {
            // Arrange
            Permissao permissao = criarPermissaoAdmin();
            Voluntario voluntario = criarVoluntario();
            UsuarioDTO usuarioDTO = criarUsuarioDTOParaCriacao(List.of(new PermissaoDTO(PERMISSAO_ID, "ROLE_ADMIN")));
            Usuario usuarioEntity = new Usuario();
            usuarioEntity.setUsuario("novo");
            usuarioEntity.setVoluntario(voluntario);
            usuarioEntity.setPermissoes(List.of(permissao));
            UsuarioDTO usuarioDTOSalvo = criarUsuarioDTORetorno();

            configurarMocksBasicosCriar(usuarioDTO, voluntario, usuarioEntity, usuarioDTOSalvo);
            when(permissaoRepository.findById(PERMISSAO_ID)).thenReturn(Optional.of(permissao));

            // Act
            UsuarioDTO resultado = usuarioService.criar(usuarioDTO);

            // Assert
            assertThat(resultado).isEqualTo(usuarioDTOSalvo);
            verify(voluntarioRepository).findById(VOLUNTARIO_ID);
            verify(permissaoRepository).findById(PERMISSAO_ID);
            verify(usuarioMapper).dtoParaEntity(usuarioDTO);
            verify(passwordEncoder).encode("senha123");
            verify(usuarioRepository).save(usuarioEntity);
            verify(usuarioMapper).entityParaDto(usuarioEntity);
        }

        @Test
        @DisplayName("Deve criar usuário com múltiplas permissões")
        void criarUsuario_comMultiplasPermissoes_sucesso() {
            // Arrange
            UUID permissao2Id = UUID.randomUUID();
            Permissao permissao1 = criarPermissaoAdmin();
            Permissao permissao2 = new Permissao(permissao2Id, "ROLE_USER");
            Voluntario voluntario = criarVoluntario();
            UsuarioDTO usuarioDTO = criarUsuarioDTOParaCriacao(Arrays.asList(
                    new PermissaoDTO(PERMISSAO_ID, "ROLE_ADMIN"),
                    new PermissaoDTO(permissao2Id, "ROLE_USER")
            ));
            Usuario usuarioEntity = new Usuario();
            usuarioEntity.setUsuario("novo");
            UsuarioDTO usuarioDTOSalvo = criarUsuarioDTORetorno();

            configurarMocksBasicosCriar(usuarioDTO, voluntario, usuarioEntity, usuarioDTOSalvo);
            when(permissaoRepository.findById(PERMISSAO_ID)).thenReturn(Optional.of(permissao1));
            when(permissaoRepository.findById(permissao2Id)).thenReturn(Optional.of(permissao2));

            // Act
            UsuarioDTO resultado = usuarioService.criar(usuarioDTO);

            // Assert
            assertThat(resultado).isEqualTo(usuarioDTOSalvo);
            verify(permissaoRepository).findById(PERMISSAO_ID);
            verify(permissaoRepository).findById(permissao2Id);
            assertThat(usuarioEntity.getPermissoes()).containsExactlyInAnyOrder(permissao1, permissao2);
        }

        @Test
        @DisplayName("Deve lançar UsuarioException ao tentar criar usuário com voluntário inexistente")
        void criarUsuario_voluntarioNaoEncontrado() {
            UsuarioDTO usuarioDTO = new UsuarioDTO();
            usuarioDTO.setUuidVoluntario(VOLUNTARIO_ID);
            usuarioDTO.setPermissaoDTOList(Collections.emptyList());

            when(voluntarioRepository.findById(VOLUNTARIO_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> usuarioService.criar(usuarioDTO))
                    .isInstanceOf(UsuarioException.class)
                    .hasMessageContaining("Voluntário não encontrado com ID: " + VOLUNTARIO_ID);

            verify(voluntarioRepository).findById(VOLUNTARIO_ID);
            verify(permissaoRepository, never()).findById(any());
        }

        @Test
        @DisplayName("Deve lançar UsuarioException ao tentar criar usuário com permissão inexistente")
        void criarUsuario_permissaoNaoEncontrada() {
            UsuarioDTO usuarioDTO = new UsuarioDTO();
            usuarioDTO.setUuidVoluntario(VOLUNTARIO_ID);
            usuarioDTO.setPermissaoDTOList(List.of(new PermissaoDTO(PERMISSAO_ID, "ROLE_ADMIN")));

            Voluntario voluntario = criarVoluntario();

            when(voluntarioRepository.findById(VOLUNTARIO_ID)).thenReturn(Optional.of(voluntario));
            when(permissaoRepository.findById(PERMISSAO_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> usuarioService.criar(usuarioDTO))
                    .isInstanceOf(UsuarioException.class)
                    .hasMessageContaining("Permissão não encontrada com ID: " + PERMISSAO_ID);

            verify(voluntarioRepository).findById(VOLUNTARIO_ID);
            verify(permissaoRepository).findById(PERMISSAO_ID);
        }
    }

    @Nested
    @DisplayName("atualizar")
    class Atualizar {

        @Test
        @DisplayName("Deve atualizar um usuário existente com sucesso")
        void atualizarUsuario_sucesso() {
            Usuario userEntity = new Usuario();
            userEntity.setUuid(USUARIO_ID);
            userEntity.setUsuario("orig");

            UsuarioDTO userDTO = new UsuarioDTO();
            userDTO.setUsuario("novo");
            userDTO.setSenha("novaSenha");

            when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(userEntity));
            when(passwordEncoder.encode("novaSenha")).thenReturn("codificada");
            when(usuarioRepository.save(userEntity)).thenReturn(userEntity);

            usuarioService.atualizar(USUARIO_ID, userDTO);

            assertThat(userEntity.getUsuario()).isEqualTo("novo");
            assertThat(userEntity.getSenha()).isEqualTo("codificada");
            verify(usuarioRepository).findById(USUARIO_ID);
            verify(passwordEncoder).encode("novaSenha");
            verify(usuarioRepository).save(userEntity);
        }

        @Test
        @DisplayName("Deve lançar UsuarioException ao atualizar usuário inexistente")
        void atualizarUsuario_usuarioNaoEncontrado() {
            UsuarioDTO userDTO = new UsuarioDTO();
            userDTO.setUsuario("novo");

            when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> usuarioService.atualizar(USUARIO_ID, userDTO))
                    .isInstanceOf(UsuarioException.class)
                    .hasMessageContaining("Usuário não encontrado com ID: " + USUARIO_ID);

            verify(usuarioRepository).findById(USUARIO_ID);
            verify(passwordEncoder, never()).encode(any());
            verify(usuarioRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve definir flags de conta como true ao desativar usuário na atualização")
        void atualizarUsuario_comAtivoFalso_setaFlagsComoTrue() {
            Usuario userEntity = new Usuario();
            userEntity.setUuid(USUARIO_ID);
            userEntity.setContaExpirada(false);
            userEntity.setContaBloqueada(false);
            userEntity.setCredenciaisExpiradas(false);
            userEntity.setAtivo(true);

            UsuarioDTO userDTO = new UsuarioDTO();
            userDTO.setUsuario("novo");
            userDTO.setSenha("novaSenha");
            userDTO.setAtivo(false);

            when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(userEntity));
            when(passwordEncoder.encode("novaSenha")).thenReturn("codificada");
            when(usuarioRepository.save(userEntity)).thenReturn(userEntity);

            usuarioService.atualizar(USUARIO_ID, userDTO);

            assertAll("Flags de desativação devem estar todas marcadas como true",
                    () -> assertThat(userEntity.getContaExpirada()).as("conta expirada").isTrue(),
                    () -> assertThat(userEntity.getContaBloqueada()).as("conta bloqueada").isTrue(),
                    () -> assertThat(userEntity.getCredenciaisExpiradas()).as("credenciais expiradas").isTrue(),
                    () -> assertThat(userEntity.getAtivo()).as("usuário inativo").isFalse()
            );
            verify(usuarioRepository).save(userEntity);
        }

        @Test
        @DisplayName("Deve definir flags de conta como false ao ativar usuário na atualização")
        void atualizarUsuario_comAtivoVerdadeiro_setaFlagsComoFalse() {
            Usuario userEntity = new Usuario();
            userEntity.setUuid(USUARIO_ID);
            userEntity.setContaExpirada(true);
            userEntity.setContaBloqueada(true);
            userEntity.setCredenciaisExpiradas(true);
            userEntity.setAtivo(false);

            UsuarioDTO userDTO = new UsuarioDTO();
            userDTO.setUsuario("novo");
            userDTO.setSenha("novaSenha");
            userDTO.setAtivo(true);

            when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(userEntity));
            when(passwordEncoder.encode("novaSenha")).thenReturn("codificada");
            when(usuarioRepository.save(userEntity)).thenReturn(userEntity);

            usuarioService.atualizar(USUARIO_ID, userDTO);

            assertAll("Flags de ativação devem estar todas marcadas como false",
                    () -> assertThat(userEntity.getContaExpirada()).as("conta expirada").isFalse(),
                    () -> assertThat(userEntity.getContaBloqueada()).as("conta bloqueada").isFalse(),
                    () -> assertThat(userEntity.getCredenciaisExpiradas()).as("credenciais expiradas").isFalse(),
                    () -> assertThat(userEntity.getAtivo()).as("usuário ativo").isTrue()
            );
            verify(usuarioRepository).save(userEntity);
        }

        @Test
        @DisplayName("Deve definir voluntário como nulo quando UUID do voluntário é nulo na atualização")
        void atualizarUsuario_comUuidVoluntarioNulo_setaVoluntarioComoNulo() {
            Voluntario voluntarioExistente = new Voluntario();
            voluntarioExistente.setUuid(VOLUNTARIO_ID);

            Usuario userEntity = new Usuario();
            userEntity.setUuid(USUARIO_ID);
            userEntity.setVoluntario(voluntarioExistente);

            UsuarioDTO userDTO = new UsuarioDTO();
            userDTO.setUsuario("novo");
            userDTO.setSenha("novaSenha");
            userDTO.setAtivo(true);
            userDTO.setUuidVoluntario(null);

            when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(userEntity));
            when(passwordEncoder.encode("novaSenha")).thenReturn("codificada");
            when(usuarioRepository.save(userEntity)).thenReturn(userEntity);

            usuarioService.atualizar(USUARIO_ID, userDTO);

            assertThat(userEntity.getVoluntario()).isNull();
            verify(voluntarioRepository, never()).findById(any());
            verify(usuarioRepository).save(userEntity);
        }
    }

    @Nested
    @DisplayName("remover")
    class Remover {

        @Test
        @DisplayName("Deve remover um usuário existente com sucesso (soft delete)")
        void removerUsuario_sucesso() {
            Usuario userEntity = new Usuario();
            userEntity.setUuid(USUARIO_ID);
            userEntity.setContaExpirada(false);
            userEntity.setContaBloqueada(false);
            userEntity.setCredenciaisExpiradas(false);
            userEntity.setAtivo(true);

            when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(userEntity));
            when(usuarioRepository.save(userEntity)).thenReturn(userEntity);

            usuarioService.remover(USUARIO_ID);

            verify(usuarioRepository).findById(USUARIO_ID);
            assertAll("Flags de soft delete devem estar corretamente definidas",
                    () -> assertThat(userEntity.getContaExpirada()).as("conta expirada").isTrue(),
                    () -> assertThat(userEntity.getContaBloqueada()).as("conta bloqueada").isTrue(),
                    () -> assertThat(userEntity.getCredenciaisExpiradas()).as("credenciais expiradas").isTrue(),
                    () -> assertThat(userEntity.getAtivo()).as("usuário inativo").isFalse()
            );
            verify(usuarioRepository).save(userEntity);
            verify(usuarioRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Deve lançar UsuarioException ao remover usuário inexistente")
        void removerUsuario_usuarioNaoEncontrado() {
            when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> usuarioService.remover(USUARIO_ID))
                    .isInstanceOf(UsuarioException.class)
                    .hasMessageContaining("Usuário não encontrado com ID: " + USUARIO_ID);

            verify(usuarioRepository).findById(USUARIO_ID);
            verify(usuarioRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("trocarSenha")
    class TrocarSenha {

        @Test
        @DisplayName("Deve trocar a senha com sucesso")
        void trocarSenha_sucesso() {
            Usuario userEntity = new Usuario();
            userEntity.setUuid(USUARIO_ID);
            userEntity.setSenha("senhaAntiga");

            when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(userEntity));
            when(passwordEncoder.encode("novaSenha")).thenReturn("senhaCriptografada");
            when(usuarioRepository.save(userEntity)).thenReturn(userEntity);

            usuarioService.trocarSenha(USUARIO_ID, "novaSenha");

            assertThat(userEntity.getSenha()).isEqualTo("senhaCriptografada");
            verify(usuarioRepository).findById(USUARIO_ID);
            verify(passwordEncoder).encode("novaSenha");
            verify(usuarioRepository).save(userEntity);
        }

        @Test
        @DisplayName("Deve lançar UsuarioException ao trocar senha de usuário inexistente")
        void trocarSenha_usuarioNaoEncontrado() {
            when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> usuarioService.trocarSenha(USUARIO_ID, "novaSenha"))
                    .isInstanceOf(UsuarioException.class)
                    .hasMessageContaining("Usuário não encontrado com ID: " + USUARIO_ID);

            verify(usuarioRepository).findById(USUARIO_ID);
            verify(passwordEncoder, never()).encode(any());
            verify(usuarioRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("enviarEmailRedefinicaoSenhaPorUsername")
    class EnviarEmailRedefinicao {

        @Test
        @DisplayName("Deve enviar email de redefinição de senha com sucesso")
        void enviarEmailRedefinicaoSenhaPorUsername_sucesso() {
            String username = "testeUser";

            Voluntario voluntario = new Voluntario();
            voluntario.setUuid(VOLUNTARIO_ID);

            Usuario usuario = new Usuario();
            usuario.setUuid(USUARIO_ID);
            usuario.setUsuario(username);
            usuario.setVoluntario(voluntario);

            Email email = new Email();
            email.setEmail("teste@email.com");

            when(usuarioRepository.findByUsuario(username)).thenReturn(usuario);
            when(emailRepository.findFirstByVoluntarioUuid(VOLUNTARIO_ID)).thenReturn(Optional.of(email));

            usuarioService.enviarEmailRedefinicaoSenhaPorUsername(username);

            verify(usuarioRepository).findByUsuario(username);
            verify(emailRepository).findFirstByVoluntarioUuid(VOLUNTARIO_ID);
            verify(emailUtil).enviarEmail(eq("teste@email.com"), anyString(), anyString());
        }

        @Test
        @DisplayName("Deve lançar UsuarioException quando usuário não encontrado por username")
        void enviarEmailRedefinicaoSenhaPorUsername_usuarioNaoEncontrado() {
            String username = "inexistente";

            when(usuarioRepository.findByUsuario(username)).thenReturn(null);

            assertThatThrownBy(() -> usuarioService.enviarEmailRedefinicaoSenhaPorUsername(username))
                    .isInstanceOf(UsuarioException.class)
                    .hasMessageContaining("Usuário não encontrado com o username: " + username);

            verify(usuarioRepository).findByUsuario(username);
            verify(emailUtil, never()).enviarEmail(any(), any(), any());
        }

        @Test
        @DisplayName("Deve lançar UsuarioException quando voluntário associado ao usuário é nulo")
        void enviarEmailRedefinicaoSenhaPorUsername_voluntarioNulo() {
            String username = "testeUser";

            Usuario usuario = new Usuario();
            usuario.setUuid(USUARIO_ID);
            usuario.setUsuario(username);
            usuario.setVoluntario(null);

            when(usuarioRepository.findByUsuario(username)).thenReturn(usuario);

            assertThatThrownBy(() -> usuarioService.enviarEmailRedefinicaoSenhaPorUsername(username))
                    .isInstanceOf(UsuarioException.class)
                    .hasMessageContaining("Voluntário associado ao usuário não encontrado.");

            verify(usuarioRepository).findByUsuario(username);
            verify(emailUtil, never()).enviarEmail(any(), any(), any());
        }

        @Test
        @DisplayName("Deve lançar UsuarioException quando email do voluntário não é encontrado")
        void enviarEmailRedefinicaoSenhaPorUsername_emailNaoEncontrado() {
            String username = "testeUser";

            Voluntario voluntario = new Voluntario();
            voluntario.setUuid(VOLUNTARIO_ID);

            Usuario usuario = new Usuario();
            usuario.setUuid(USUARIO_ID);
            usuario.setUsuario(username);
            usuario.setVoluntario(voluntario);

            when(usuarioRepository.findByUsuario(username)).thenReturn(usuario);
            when(emailRepository.findFirstByVoluntarioUuid(VOLUNTARIO_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> usuarioService.enviarEmailRedefinicaoSenhaPorUsername(username))
                    .isInstanceOf(UsuarioException.class)
                    .hasMessageContaining("Nenhum e-mail encontrado para o voluntário associado ao usuário.");

            verify(emailUtil, never()).enviarEmail(any(), any(), any());
        }
    }
}
