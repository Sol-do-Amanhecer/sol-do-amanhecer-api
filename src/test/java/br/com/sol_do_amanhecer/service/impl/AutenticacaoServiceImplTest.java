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
        LoginDTO loginDTO = new LoginDTO("usuarioValido", "senhaCorreta");
        Usuario usuarioEntity = new Usuario();
        usuarioEntity.setUsuario("usuarioValido");
        usuarioEntity.setSenha("senhaCodificada");

        List<Permissao> permissoes = Collections.singletonList(new Permissao(UUID.randomUUID(), "ROLE_USER"));
        usuarioEntity.setPermissoes(permissoes);

        TokenDTO tokenDTOEsperado = new TokenDTO(
                "usuarioValido", true, new Date(), new Date(System.currentTimeMillis() + HORA_EM_MILISSEGUNDO),
                "accessTokenGerado", "refreshTokenGerado"
        );

        when(usuarioRepository.findByUsuario("usuarioValido")).thenReturn(usuarioEntity);
        when(passwordEncoder.matches("senhaCorreta", usuarioEntity.getSenha())).thenReturn(true);
        when(jwtTokenProvider.criarTokenAcesso("usuarioValido", permissoes))
                .thenReturn(tokenDTOEsperado);

        TokenDTO resultado = autenticacaoService.entrar(loginDTO);

        assertNotNull(resultado, "O TokenDTO não deve ser nulo.");
        assertEquals(tokenDTOEsperado, resultado, "O TokenDTO retornado deve ser o esperado.");

        verify(usuarioRepository, times(1)).findByUsuario("usuarioValido");
        verify(passwordEncoder, times(1)).matches("senhaCorreta", usuarioEntity.getSenha());
        verify(jwtTokenProvider, times(1)).criarTokenAcesso("usuarioValido", permissoes);
        verifyNoMoreInteractions(usuarioRepository, passwordEncoder, jwtTokenProvider);
    }

    @Test
    @DisplayName("Deve lançar BadCredentialsException para senha inválida")
    void entrarComSenhaInvalida() {
        LoginDTO loginDTO = new LoginDTO("usuarioValido", "senhaIncorreta");
        Usuario usuarioEntity = new Usuario();
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
        verifyNoMoreInteractions(usuarioRepository, passwordEncoder, jwtTokenProvider);
    }

    @Test
    @DisplayName("Deve retornar TokenDTO ao atualizar token com refreshToken válido")
    void refreshTokenComSucesso() {
        String usuario = "usuarioValido";
        String refreshToken = "refreshTokenValido";

        Usuario usuarioEntity = new Usuario();
        usuarioEntity.setUsuario(usuario);

        TokenDTO tokenDTOEsperado = new TokenDTO(
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

}
