package br.com.sol_do_amanhecer.service.impl;

import br.com.sol_do_amanhecer.model.entity.Permissao;
import br.com.sol_do_amanhecer.model.entity.Usuario;
import br.com.sol_do_amanhecer.repository.UsuarioRepository;
import br.com.sol_do_amanhecer.security.LoginDTO;
import br.com.sol_do_amanhecer.security.TokenDTO;
import br.com.sol_do_amanhecer.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do AutenticacaoServiceImpl")
public class AutenticacaoServiceImplTest {
    public static final int HORA_EM_MILISSEGUNDO = 3600000;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AutenticacaoServiceImpl autenticacaoService;

    @Test
    @DisplayName("Deve retornar TokenDTO ao autenticar com credenciais válidas")
    void entrarComCredenciaisValidas() {
        UUID usuarioUuid = UUID.randomUUID();
        LoginDTO loginDTO = new LoginDTO("usuarioValido", "senhaCorreta");

        Usuario usuarioEntity = new Usuario();
        usuarioEntity.setUuid(usuarioUuid);
        usuarioEntity.setUsuario("usuarioValido");
        usuarioEntity.setSenha("senhaCodificada");

        List<Permissao> permissoes = Collections.singletonList(new Permissao(UUID.randomUUID(), "ROLE_USER"));
        usuarioEntity.setPermissoes(permissoes);

        TokenDTO tokenDTOEsperado = new TokenDTO(usuarioUuid,
                "usuarioValido", true, new Date(), new Date(System.currentTimeMillis() + HORA_EM_MILISSEGUNDO),
                "accessTokenGerado", "refreshTokenGerado"
        );

        when(usuarioRepository.findByUsuario("usuarioValido")).thenReturn(usuarioEntity);
        when(passwordEncoder.matches("senhaCorreta", usuarioEntity.getSenha())).thenReturn(true);
        when(jwtTokenProvider.criarTokenAcesso(usuarioUuid, "usuarioValido", permissoes))
                .thenReturn(tokenDTOEsperado);

        TokenDTO resultado = autenticacaoService.entrar(loginDTO);

        assertNotNull(resultado, "O TokenDTO não deve ser nulo.");
        assertEquals(tokenDTOEsperado, resultado, "O TokenDTO retornado deve ser o esperado.");

        verify(usuarioRepository, times(1)).findByUsuario("usuarioValido");
        verify(passwordEncoder, times(1)).matches("senhaCorreta", usuarioEntity.getSenha());
        verify(jwtTokenProvider, times(1)).criarTokenAcesso(usuarioUuid, "usuarioValido", permissoes);
        verifyNoMoreInteractions(usuarioRepository, passwordEncoder, jwtTokenProvider);
    }

    @Test
    @DisplayName("Deve retornar TokenDTO ao autenticar usuário sem permissões")
    void entrarComUsuarioSemPermissoes() {
        UUID usuarioUuid = UUID.randomUUID();
        LoginDTO loginDTO = new LoginDTO("usuarioSemPermissoes", "senhaCorreta");

        Usuario usuarioEntity = new Usuario();
        usuarioEntity.setUuid(usuarioUuid);
        usuarioEntity.setUsuario("usuarioSemPermissoes");
        usuarioEntity.setSenha("senhaCodificada");
        usuarioEntity.setPermissoes(Collections.emptyList());

        TokenDTO tokenDTOEsperado = new TokenDTO( usuarioUuid,
                "usuarioSemPermissoes", true, new Date(), new Date(System.currentTimeMillis() + HORA_EM_MILISSEGUNDO),
                "accessTokenGerado", "refreshTokenGerado"
        );

        when(usuarioRepository.findByUsuario("usuarioSemPermissoes")).thenReturn(usuarioEntity);
        when(passwordEncoder.matches("senhaCorreta", usuarioEntity.getSenha())).thenReturn(true);
        when(jwtTokenProvider.criarTokenAcesso(usuarioUuid, "usuarioSemPermissoes", Collections.emptyList()))
                .thenReturn(tokenDTOEsperado);

        TokenDTO resultado = autenticacaoService.entrar(loginDTO);

        assertNotNull(resultado, "O TokenDTO não deve ser nulo.");
        assertEquals(tokenDTOEsperado, resultado, "O TokenDTO retornado deve ser o esperado.");

        verify(usuarioRepository, times(1)).findByUsuario("usuarioSemPermissoes");
        verify(passwordEncoder, times(1)).matches("senhaCorreta", usuarioEntity.getSenha());
        verify(jwtTokenProvider, times(1)).criarTokenAcesso(usuarioUuid, "usuarioSemPermissoes", Collections.emptyList());
    }

    @Test
    @DisplayName("Deve retornar TokenDTO ao autenticar usuário com múltiplas permissões")
    void entrarComUsuarioComMultiplasPermissoes() {
        UUID usuarioUuid = UUID.randomUUID();
        LoginDTO loginDTO = new LoginDTO("usuarioAdmin", "senhaCorreta");

        Usuario usuarioEntity = new Usuario();
        usuarioEntity.setUuid(usuarioUuid);
        usuarioEntity.setUsuario("usuarioAdmin");
        usuarioEntity.setSenha("senhaCodificada");

        List<Permissao> permissoes = List.of(
                new Permissao(UUID.randomUUID(), "ROLE_USER"),
                new Permissao(UUID.randomUUID(), "ROLE_ADMIN"),
                new Permissao(UUID.randomUUID(), "ROLE_MANAGER")
        );
        usuarioEntity.setPermissoes(permissoes);

        TokenDTO tokenDTOEsperado = new TokenDTO(usuarioUuid,
                "usuarioAdmin", true, new Date(), new Date(System.currentTimeMillis() + HORA_EM_MILISSEGUNDO),
                "accessTokenGerado", "refreshTokenGerado"
        );

        when(usuarioRepository.findByUsuario("usuarioAdmin")).thenReturn(usuarioEntity);
        when(passwordEncoder.matches("senhaCorreta", usuarioEntity.getSenha())).thenReturn(true);
        when(jwtTokenProvider.criarTokenAcesso(usuarioUuid, "usuarioAdmin", permissoes))
                .thenReturn(tokenDTOEsperado);

        TokenDTO resultado = autenticacaoService.entrar(loginDTO);

        assertNotNull(resultado, "O TokenDTO não deve ser nulo.");
        assertEquals(tokenDTOEsperado, resultado, "O TokenDTO retornado deve ser o esperado.");

        verify(usuarioRepository, times(1)).findByUsuario("usuarioAdmin");
        verify(passwordEncoder, times(1)).matches("senhaCorreta", usuarioEntity.getSenha());
        verify(jwtTokenProvider, times(1)).criarTokenAcesso(usuarioUuid, "usuarioAdmin", permissoes);
    }

    @Test
    @DisplayName("Deve lançar BadCredentialsException para senha inválida")
    void entrarComSenhaInvalida() {
        UUID usuarioUuid = UUID.randomUUID();
        LoginDTO loginDTO = new LoginDTO("usuarioValido", "senhaIncorreta");

        Usuario usuarioEntity = new Usuario();
        usuarioEntity.setUuid(usuarioUuid);
        usuarioEntity.setUsuario("usuarioValido");
        usuarioEntity.setSenha("senhaCodificada");

        when(usuarioRepository.findByUsuario("usuarioValido")).thenReturn(usuarioEntity);
        when(passwordEncoder.matches("senhaIncorreta", usuarioEntity.getSenha())).thenReturn(false);

        BadCredentialsException thrown = assertThrows(BadCredentialsException.class,
                () -> autenticacaoService.entrar(loginDTO),
                "Deve lançar BadCredentialsException para senha inválida.");
        assertEquals("Usuário/senha inválidos!", thrown.getMessage());

        verify(usuarioRepository, times(1)).findByUsuario("usuarioValido");
        verify(passwordEncoder, times(1)).matches("senhaIncorreta", usuarioEntity.getSenha());
        verify(jwtTokenProvider, never()).criarTokenAcesso(any(), any(), any());
        verifyNoMoreInteractions(usuarioRepository, passwordEncoder, jwtTokenProvider);
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException para usuário inexistente")
    void entrarComUsuarioInexistente() {
        LoginDTO loginDTO = new LoginDTO("usuarioInexistente", "senha");

        when(usuarioRepository.findByUsuario("usuarioInexistente")).thenReturn(null);

        UsernameNotFoundException thrown = assertThrows(UsernameNotFoundException.class,
                () -> autenticacaoService.entrar(loginDTO),
                "Deve lançar UsernameNotFoundException para usuário inexistente.");
        assertEquals("Usuário: usuarioInexistente não encontrado", thrown.getMessage());

        verify(usuarioRepository, times(1)).findByUsuario("usuarioInexistente");
        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtTokenProvider, never()).criarTokenAcesso(any(), any(), any());
        verifyNoMoreInteractions(usuarioRepository, passwordEncoder, jwtTokenProvider);
    }

    @Test
    @DisplayName("Deve lançar BadCredentialsException quando JwtTokenProvider lança exceção")
    void entrarComErroNoJwtProvider() {
        UUID usuarioUuid = UUID.randomUUID();
        LoginDTO loginDTO = new LoginDTO("usuarioValido", "senhaCorreta");

        Usuario usuarioEntity = new Usuario();
        usuarioEntity.setUuid(usuarioUuid);
        usuarioEntity.setUsuario("usuarioValido");
        usuarioEntity.setSenha("senhaCodificada");
        usuarioEntity.setPermissoes(Collections.emptyList());

        when(usuarioRepository.findByUsuario("usuarioValido")).thenReturn(usuarioEntity);
        when(passwordEncoder.matches("senhaCorreta", usuarioEntity.getSenha())).thenReturn(true);
        when(jwtTokenProvider.criarTokenAcesso(usuarioUuid, "usuarioValido", Collections.emptyList()))
                .thenThrow(new BadCredentialsException("Erro ao gerar token"));

        BadCredentialsException thrown = assertThrows(BadCredentialsException.class,
                () -> autenticacaoService.entrar(loginDTO),
                "Deve propagar BadCredentialsException do JwtTokenProvider.");
        assertEquals("Erro ao gerar token", thrown.getMessage());

        verify(usuarioRepository, times(1)).findByUsuario("usuarioValido");
        verify(passwordEncoder, times(1)).matches("senhaCorreta", usuarioEntity.getSenha());
        verify(jwtTokenProvider, times(1)).criarTokenAcesso(usuarioUuid, "usuarioValido", Collections.emptyList());
    }

    @Test
    @DisplayName("Deve retornar TokenDTO ao atualizar token com refreshToken válido")
    void refreshTokenComSucesso() {
        String usuario = "usuarioValido";
        String refreshToken = "refreshTokenValido";

        Usuario usuarioEntity = new Usuario();
        usuarioEntity.setUsuario(usuario);

        TokenDTO tokenDTOEsperado = new TokenDTO( usuarioEntity.getUuid(),
                usuario, true, new Date(), new Date(System.currentTimeMillis() + HORA_EM_MILISSEGUNDO),
                "novoAccessToken", "novoRefreshToken"
        );

        when(usuarioRepository.findByUsuario(usuario)).thenReturn(usuarioEntity);
        when(jwtTokenProvider.criarRefreshToken(refreshToken)).thenReturn(tokenDTOEsperado);

        TokenDTO resultado = autenticacaoService.refreshToken(usuario, refreshToken);

        assertNotNull(resultado, "O TokenDTO não deve ser nulo.");
        assertEquals(tokenDTOEsperado, resultado, "O TokenDTO retornado deve ser o esperado.");

        verify(usuarioRepository, times(1)).findByUsuario(usuario);
        verify(jwtTokenProvider, times(1)).criarRefreshToken(refreshToken);
        verifyNoMoreInteractions(usuarioRepository, jwtTokenProvider, passwordEncoder);
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException ao atualizar token com usuário inexistente")
    void refreshTokenComUsuarioInexistente() {
        String usuario = "usuarioInexistente";
        String refreshToken = "refreshToken";

        when(usuarioRepository.findByUsuario(usuario)).thenReturn(null);

        UsernameNotFoundException thrown = assertThrows(UsernameNotFoundException.class,
                () -> autenticacaoService.refreshToken(usuario, refreshToken),
                "Deve lançar UsernameNotFoundException para usuário inexistente ao tentar refresh.");
        assertEquals("Usuário: usuarioInexistente não encontrado", thrown.getMessage());

        verify(usuarioRepository, times(1)).findByUsuario(usuario);
        verify(jwtTokenProvider, never()).criarRefreshToken(any());
        verifyNoMoreInteractions(usuarioRepository, jwtTokenProvider, passwordEncoder);
    }

    @Test
    @DisplayName("Deve lançar BadCredentialsException ao tentar refresh com refreshToken inválido ou expirado")
    void refreshTokenComTokenInvalido() {
        String usuario = "usuarioValido";
        String refreshToken = "refreshTokenInvalido";

        Usuario usuarioEntity = new Usuario();
        usuarioEntity.setUsuario(usuario);

        when(usuarioRepository.findByUsuario(usuario)).thenReturn(usuarioEntity);
        when(jwtTokenProvider.criarRefreshToken(refreshToken))
                .thenThrow(new BadCredentialsException("Invalid refresh token supplied!"));

        BadCredentialsException thrown = assertThrows(BadCredentialsException.class,
                () -> autenticacaoService.refreshToken(usuario, refreshToken),
                "Deve lançar BadCredentialsException para refresh token inválido.");
        assertEquals("Invalid refresh token supplied!", thrown.getMessage());

        verify(usuarioRepository, times(1)).findByUsuario(usuario);
        verify(jwtTokenProvider, times(1)).criarRefreshToken(refreshToken);
        verifyNoMoreInteractions(usuarioRepository, jwtTokenProvider, passwordEncoder);
    }

    @Test
    @DisplayName("Deve tratar corretamente quando usuário tem permissões nulas")
    void entrarComPermissoesNulas() {
        UUID usuarioUuid = UUID.randomUUID();
        LoginDTO loginDTO = new LoginDTO("usuarioComPermissoesNulas", "senhaCorreta");

        Usuario usuarioEntity = new Usuario();
        usuarioEntity.setUuid(usuarioUuid);
        usuarioEntity.setUsuario("usuarioComPermissoesNulas");
        usuarioEntity.setSenha("senhaCodificada");
        usuarioEntity.setPermissoes(null);

        TokenDTO tokenDTOEsperado = new TokenDTO(
                usuarioUuid, "usuarioComPermissoesNulas", true, new Date(), new Date(System.currentTimeMillis() + HORA_EM_MILISSEGUNDO),
                "accessTokenGerado", "refreshTokenGerado"
        );

        when(usuarioRepository.findByUsuario("usuarioComPermissoesNulas")).thenReturn(usuarioEntity);
        when(passwordEncoder.matches("senhaCorreta", usuarioEntity.getSenha())).thenReturn(true);
        when(jwtTokenProvider.criarTokenAcesso(usuarioUuid, "usuarioComPermissoesNulas", null))
                .thenReturn(tokenDTOEsperado);

        TokenDTO resultado = autenticacaoService.entrar(loginDTO);

        assertNotNull(resultado, "O TokenDTO não deve ser nulo.");
        assertEquals(tokenDTOEsperado, resultado, "O TokenDTO retornado deve ser o esperado.");

        verify(usuarioRepository, times(1)).findByUsuario("usuarioComPermissoesNulas");
        verify(passwordEncoder, times(1)).matches("senhaCorreta", usuarioEntity.getSenha());
        verify(jwtTokenProvider, times(1)).criarTokenAcesso(usuarioUuid, "usuarioComPermissoesNulas", null);
    }

    @Test
    @DisplayName("Deve tratar corretamente refresh token com token vazio")
    void refreshTokenComTokenVazio() {
        String usuario = "usuarioValido";
        String refreshToken = "";

        Usuario usuarioEntity = new Usuario();
        usuarioEntity.setUsuario(usuario);

        when(usuarioRepository.findByUsuario(usuario)).thenReturn(usuarioEntity);
        when(jwtTokenProvider.criarRefreshToken(refreshToken))
                .thenThrow(new BadCredentialsException("Empty refresh token!"));

        BadCredentialsException thrown = assertThrows(BadCredentialsException.class,
                () -> autenticacaoService.refreshToken(usuario, refreshToken),
                "Deve lançar BadCredentialsException para refresh token vazio.");
        assertEquals("Empty refresh token!", thrown.getMessage());

        verify(usuarioRepository, times(1)).findByUsuario(usuario);
        verify(jwtTokenProvider, times(1)).criarRefreshToken(refreshToken);
    }

    @Test
    @DisplayName("Deve tratar corretamente refresh token com token nulo")
    void refreshTokenComTokenNulo() {
        String usuario = "usuarioValido";
        String refreshToken = "teste-token";

        Usuario usuarioEntity = new Usuario();
        usuarioEntity.setUsuario(usuario);

        when(usuarioRepository.findByUsuario(usuario)).thenReturn(usuarioEntity);
        when(jwtTokenProvider.criarRefreshToken(refreshToken))
                .thenThrow(new BadCredentialsException("Null refresh token!"));

        BadCredentialsException thrown = assertThrows(BadCredentialsException.class,
                () -> autenticacaoService.refreshToken(usuario, refreshToken),
                "Deve lançar BadCredentialsException para refresh token nulo.");
        assertEquals("Null refresh token!", thrown.getMessage());

        verify(usuarioRepository, times(1)).findByUsuario(usuario);
        verify(jwtTokenProvider, times(1)).criarRefreshToken(refreshToken);
    }
}