package br.com.sol_do_amanhecer.service.impl;

import br.com.sol_do_amanhecer.exception.UsuarioException;
import br.com.sol_do_amanhecer.model.dto.PermissaoDTO;
import br.com.sol_do_amanhecer.model.dto.UsuarioDTO;
import br.com.sol_do_amanhecer.model.entity.Permissao;
import br.com.sol_do_amanhecer.model.entity.Usuario;
import br.com.sol_do_amanhecer.model.entity.Voluntario;
import br.com.sol_do_amanhecer.model.mapper.UsuarioMapper;
import br.com.sol_do_amanhecer.repository.PermissaoRepository;
import br.com.sol_do_amanhecer.repository.UsuarioRepository;
import br.com.sol_do_amanhecer.repository.VoluntarioRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private VoluntarioRepository voluntarioRepository;
    @Mock
    private PermissaoRepository permissaoRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private final UUID VOLUNTARIO_ID = UUID.randomUUID();
    private final UUID USUARIO_ID = UUID.randomUUID();
    private final UUID PERMISSAO_ID = UUID.randomUUID();

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

    @Test
    @DisplayName("Deve retornar UsuarioDTO ao buscar por ID existente")
    void buscarPorId_sucesso() {
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

        UsuarioDTO dto = new UsuarioDTO();
        dto.setUsuario("teste");
        dto.setSenha(null);
        dto.setContaExpirada(false);
        dto.setContaBloqueada(false);
        dto.setCredenciaisExpiradas(false);
        dto.setAtivo(true);
        dto.setPermissaoDTOList(null);
        dto.setUuidVoluntario(VOLUNTARIO_ID);

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

    @Test
    @DisplayName("Deve retornar lista de UsuarioDTO ao buscar todos os usuários")
    void buscarTodos_sucesso() {
        Usuario entity1 = new Usuario();
        entity1.setUsuario("user1");
        Usuario entity2 = new Usuario();
        entity2.setUsuario("user2");
        List<Usuario> listaEntity = Arrays.asList(entity1, entity2);

        UsuarioDTO dto1 = new UsuarioDTO(); dto1.setUsuario("user1");
        UsuarioDTO dto2 = new UsuarioDTO(); dto2.setUsuario("user2");

        when(usuarioRepository.findAll()).thenReturn(listaEntity);
        when(usuarioMapper.entityParaDto(entity1)).thenReturn(dto1);
        when(usuarioMapper.entityParaDto(entity2)).thenReturn(dto2);

        List<UsuarioDTO> resultado = usuarioService.buscarTodos();

        assertThat(resultado).containsExactlyInAnyOrder(dto1, dto2);
        verify(usuarioRepository).findAll();
        verify(usuarioMapper).entityParaDto(entity1);
        verify(usuarioMapper).entityParaDto(entity2);
    }

    @Test
    @DisplayName("Deve criar um usuário com sucesso")
    void criarUsuario_sucesso() {
        List<PermissaoDTO> permissaoDTOS = List.of(new PermissaoDTO(PERMISSAO_ID, "ROLE_ADMIN"));
        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setUsuario("novo");
        usuarioDTO.setSenha("senha123");
        usuarioDTO.setUuidVoluntario(VOLUNTARIO_ID);
        usuarioDTO.setPermissaoDTOList(permissaoDTOS);

        Voluntario voluntario = new Voluntario();
        voluntario.setUuid(VOLUNTARIO_ID);

        Permissao permissao = new Permissao(PERMISSAO_ID, "ROLE_ADMIN");

        Usuario usuarioEntity = new Usuario();
        usuarioEntity.setUsuario("novo");
        usuarioEntity.setVoluntario(voluntario);
        usuarioEntity.setPermissoes(List.of(permissao));

        UsuarioDTO usuarioDTOSalvo = new UsuarioDTO();
        usuarioDTOSalvo.setUsuario("novo");

        when(voluntarioRepository.findById(VOLUNTARIO_ID)).thenReturn(Optional.of(voluntario));
        when(permissaoRepository.findById(PERMISSAO_ID)).thenReturn(Optional.of(permissao));
        when(usuarioMapper.dtoParaEntity(usuarioDTO)).thenReturn(usuarioEntity);
        when(passwordEncoder.encode("senha123")).thenReturn("senhaCriptografada");
        when(usuarioRepository.save(usuarioEntity)).thenReturn(usuarioEntity);
        when(usuarioMapper.entityParaDto(usuarioEntity)).thenReturn(usuarioDTOSalvo);

        UsuarioDTO resultado = usuarioService.criar(usuarioDTO);

        assertThat(resultado).isEqualTo(usuarioDTOSalvo);
        verify(voluntarioRepository).findById(VOLUNTARIO_ID);
        verify(permissaoRepository).findById(PERMISSAO_ID);
        verify(usuarioMapper).dtoParaEntity(usuarioDTO);
        verify(passwordEncoder).encode("senha123");
        verify(usuarioRepository).save(usuarioEntity);
        verify(usuarioMapper).entityParaDto(usuarioEntity);
    }

    @Test
    @DisplayName("Deve lançar UsuarioException ao tentar criar usuário com voluntário inexistente")
    void criarUsuario_voluntarioNaoEncontrado() {
        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setUuidVoluntario(VOLUNTARIO_ID);

        when(voluntarioRepository.findById(VOLUNTARIO_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.criar(usuarioDTO))
                .isInstanceOf(UsuarioException.class)
                .hasMessageContaining("Voluntário não encontrado com ID: " + VOLUNTARIO_ID);

        verify(voluntarioRepository).findById(VOLUNTARIO_ID);
    }

    @Test
    @DisplayName("Deve lançar UsuarioException ao tentar criar usuário com permissão inexistente")
    void criarUsuario_permissaoNaoEncontrada() {
        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setUuidVoluntario(VOLUNTARIO_ID);
        usuarioDTO.setPermissaoDTOList(List.of(new PermissaoDTO(PERMISSAO_ID, "ROLE_ADMIN")));

        Voluntario voluntario = new Voluntario();
        voluntario.setUuid(VOLUNTARIO_ID);

        when(voluntarioRepository.findById(VOLUNTARIO_ID)).thenReturn(Optional.of(voluntario));
        when(permissaoRepository.findById(PERMISSAO_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.criar(usuarioDTO))
                .isInstanceOf(UsuarioException.class)
                .hasMessageContaining("Permissão não encontrada com ID: " + PERMISSAO_ID);

        verify(voluntarioRepository).findById(VOLUNTARIO_ID);
        verify(permissaoRepository).findById(PERMISSAO_ID);
    }

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
    }

    @Test
    @DisplayName("Deve remover um usuário existente com sucesso")
    void removerUsuario_sucesso() {
        Usuario userEntity = new Usuario();
        userEntity.setUuid(USUARIO_ID);

        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(userEntity));

        usuarioService.remover(USUARIO_ID);

        verify(usuarioRepository).findById(USUARIO_ID);
        verify(usuarioRepository).delete(userEntity);
    }

    @Test
    @DisplayName("Deve lançar UsuarioException ao remover usuário inexistente")
    void removerUsuario_usuarioNaoEncontrado() {
        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.remover(USUARIO_ID))
                .isInstanceOf(UsuarioException.class)
                .hasMessageContaining("Usuário não encontrado com ID: " + USUARIO_ID);

        verify(usuarioRepository).findById(USUARIO_ID);
    }
}