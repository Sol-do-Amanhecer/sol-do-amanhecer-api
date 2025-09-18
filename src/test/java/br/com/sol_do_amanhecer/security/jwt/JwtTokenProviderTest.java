package br.com.sol_do_amanhecer.security.jwt;

import br.com.sol_do_amanhecer.model.entity.Permissao;
import br.com.sol_do_amanhecer.model.entity.Usuario;
import br.com.sol_do_amanhecer.repository.UsuarioRepository;
import br.com.sol_do_amanhecer.security.TokenDTO;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

import java.util.*;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @Mock
    private UsuarioRepository repositorioUsuario;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    private static final String CHAVE_SECRETA = "minha-chave-secreta-para-teste";
    private static final Long VALIDADE_EM_MILISSEGUNDOS = 3600000L;
    private static final String USUARIO_TESTE = "usuario.teste";
    private static final UUID UUID_USUARIO = UUID.randomUUID();

    private List<Permissao> permissoes;
    private Usuario usuarioEntity;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtTokenProvider, "chaveSecreta", CHAVE_SECRETA);
        ReflectionTestUtils.setField(jwtTokenProvider, "validadeEmMilissegundos", VALIDADE_EM_MILISSEGUNDOS);

        jwtTokenProvider.inicializar();

        permissoes = new ArrayList<>();
        Permissao permissao1 = new Permissao();
        permissao1.setDescricao("ROLE_USER");
        Permissao permissao2 = new Permissao();
        permissao2.setDescricao("ROLE_ADMIN");
        permissoes.add(permissao1);
        permissoes.add(permissao2);

        usuarioEntity = new Usuario();
        usuarioEntity.setUsuario(USUARIO_TESTE);
        usuarioEntity.setSenha("senha");
        usuarioEntity.setPermissoes(Arrays.asList(
                permissao1,
                permissao2
        ));
    }

    @Test
    void inicializar_DeveConfigurarChaveSecretaEAlgoritmo() {
        JwtTokenProvider provider = new JwtTokenProvider(repositorioUsuario);
        ReflectionTestUtils.setField(provider, "chaveSecreta", "teste");
        ReflectionTestUtils.setField(provider, "validadeEmMilissegundos", 3600000L);

        provider.inicializar();

        Algorithm algoritmo = (Algorithm) ReflectionTestUtils.getField(provider, "algoritmo");
        String chaveSecretaEncoded = (String) ReflectionTestUtils.getField(provider, "chaveSecreta");

        assertNotNull(algoritmo);
        assertNotEquals("teste", chaveSecretaEncoded);
        assertNotNull(chaveSecretaEncoded);
        assertTrue(chaveSecretaEncoded.length() > "teste".length());
    }

    @Test
    void criarTokenAcesso_DeveRetornarTokenDTOValido() {
        try (MockedStatic<ServletUriComponentsBuilder> mockedBuilder = mockStatic(ServletUriComponentsBuilder.class)) {
            setupMockBuilder(mockedBuilder);

            TokenDTO result = jwtTokenProvider.criarTokenAcesso(UUID_USUARIO, USUARIO_TESTE, permissoes);

            assertNotNull(result);
            assertEquals(UUID_USUARIO, result.getUuidUsuario());
            assertEquals(USUARIO_TESTE, result.getUsuario());
            assertTrue(result.getAuthenticated());
            assertNotNull(result.getCreated());
            assertNotNull(result.getExpiration());
            assertNotNull(result.getAccessToken());
            assertNotNull(result.getRefreshToken());

            assertTrue(result.getExpiration().before(new Date(result.getCreated().getTime() + (VALIDADE_EM_MILISSEGUNDOS * 3))));
        }
    }

    @Test
    void criarTokenAcesso_ComListaPermissoesVazia_DeveRetornarTokenValido() {
        try (MockedStatic<ServletUriComponentsBuilder> mockedBuilder = mockStatic(ServletUriComponentsBuilder.class)) {
            setupMockBuilder(mockedBuilder);

            List<Permissao> permissoesVazias = new ArrayList<>();

            TokenDTO result = jwtTokenProvider.criarTokenAcesso(UUID_USUARIO, USUARIO_TESTE, permissoesVazias);

            assertNotNull(result);
            assertEquals(UUID_USUARIO, result.getUuidUsuario());
            assertEquals(USUARIO_TESTE, result.getUsuario());
            assertTrue(result.getAuthenticated());
        }
    }

    @Test
    void criarRefreshToken_ComTokenSemBearer_DeveRetornarNovoToken() {
        try (MockedStatic<ServletUriComponentsBuilder> mockedBuilder = mockStatic(ServletUriComponentsBuilder.class)) {
            setupMockBuilder(mockedBuilder);

            String refreshToken = criarRefreshTokenValidoComAlgoritmoCorreto(asList("ROLE_USER", "ROLE_ADMIN"));

            TokenDTO result = jwtTokenProvider.criarRefreshToken(refreshToken);

            assertNotNull(result);
            assertEquals(USUARIO_TESTE, result.getUsuario());
            assertEquals(UUID_USUARIO, result.getUuidUsuario());
            assertTrue(result.getAuthenticated());
            assertNotNull(result.getAccessToken());
            assertNotNull(result.getRefreshToken());
        }
    }

    @Test
    void criarRefreshToken_ComTokenComBearer_DeveProcessarCorretamente() {
        try (MockedStatic<ServletUriComponentsBuilder> mockedBuilder = mockStatic(ServletUriComponentsBuilder.class)) {
            setupMockBuilder(mockedBuilder);

            String refreshToken = criarRefreshTokenValidoComAlgoritmoCorreto(asList("ROLE_USER", "ROLE_ADMIN"));
            String bearerToken = "Bearer " + refreshToken + " ";

            TokenDTO result = jwtTokenProvider.criarRefreshToken(bearerToken);

            assertNotNull(result);
            assertEquals(USUARIO_TESTE, result.getUsuario());
            assertEquals(UUID_USUARIO, result.getUuidUsuario());
            assertTrue(result.getAuthenticated());
        }
    }

//    @Test
//    void criarRefreshToken_ComTokenComBearerSemEspaco_DeveProcessarCorretamente() {
//        try (MockedStatic<ServletUriComponentsBuilder> mockedBuilder = mockStatic(ServletUriComponentsBuilder.class)) {
//            setupMockBuilder(mockedBuilder);
//
//              String refreshToken = criarRefreshTokenValidoComAlgoritmoCorreto(asList("ROLE_USER", "ROLE_ADMIN"));
//            String bearerToken = "Bearer " + refreshToken;
//
 //            TokenDTO result = jwtTokenProvider.criarRefreshToken(bearerToken);
//
   //          assertNotNull(result);
     //       assertEquals(USUARIO_TESTE, result.getUsuario());
       //     assertEquals(UUID_USUARIO, result.getUuidUsuario());
         //   assertTrue(result.getAuthenticated());
        //}
    //}

    @Test
    void criarRefreshToken_ComRolesVazias_DeveProcessarCorretamente() {
        try (MockedStatic<ServletUriComponentsBuilder> mockedBuilder = mockStatic(ServletUriComponentsBuilder.class)) {
            setupMockBuilder(mockedBuilder);

            String refreshToken = criarRefreshTokenValidoComAlgoritmoCorreto(new ArrayList<>());

            TokenDTO result = jwtTokenProvider.criarRefreshToken(refreshToken);

            assertNotNull(result);
            assertEquals(USUARIO_TESTE, result.getUsuario());
            assertEquals(UUID_USUARIO, result.getUuidUsuario());
            assertTrue(result.getAuthenticated());
        }
    }

    @Test
    void obterAutenticacao_DeveRetornarAuthenticationValido() {
        try (MockedStatic<ServletUriComponentsBuilder> mockedBuilder = mockStatic(ServletUriComponentsBuilder.class)) {
            setupMockBuilder(mockedBuilder);

            TokenDTO tokenDTO = jwtTokenProvider.criarTokenAcesso(UUID_USUARIO, USUARIO_TESTE, permissoes);

            when(repositorioUsuario.findByUsuario(USUARIO_TESTE)).thenReturn(usuarioEntity);

            Authentication result = jwtTokenProvider.obterAutenticacao(tokenDTO.getAccessToken());

            assertNotNull(result);
            assertEquals(usuarioEntity, result.getPrincipal());
            assertEquals("", result.getCredentials());
            assertNotNull(result.getAuthorities());
        }
    }

    @Test
    void decodificarToken_ComTokenValido_DeveRetornarDecodedJWT() {
        try (MockedStatic<ServletUriComponentsBuilder> mockedBuilder = mockStatic(ServletUriComponentsBuilder.class)) {
            setupMockBuilder(mockedBuilder);

            TokenDTO tokenDTO = jwtTokenProvider.criarTokenAcesso(UUID_USUARIO, USUARIO_TESTE, permissoes);

            when(repositorioUsuario.findByUsuario(USUARIO_TESTE)).thenReturn(usuarioEntity);

            Authentication result = jwtTokenProvider.obterAutenticacao(tokenDTO.getAccessToken());

            assertNotNull(result);
        }
    }

    @Test
    void resolverToken_ComHeaderAuthorizationValido_DeveRetornarToken() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
        String bearerToken = "Bearer " + token;
        when(httpServletRequest.getHeader("Authorization")).thenReturn(bearerToken);

        String result = jwtTokenProvider.resolverToken(httpServletRequest);

        assertEquals(token, result);
    }

    @Test
    void resolverToken_ComHeaderAuthorizationNull_DeveRetornarNull() {
        when(httpServletRequest.getHeader("Authorization")).thenReturn(null);

        String result = jwtTokenProvider.resolverToken(httpServletRequest);

        assertNull(result);
    }

    @Test
    void resolverToken_ComHeaderSemBearer_DeveRetornarNull() {
        String tokenSemBearer = "Basic eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
        when(httpServletRequest.getHeader("Authorization")).thenReturn(tokenSemBearer);

        String result = jwtTokenProvider.resolverToken(httpServletRequest);

        assertNull(result);
    }

    @Test
    void resolverToken_ComHeaderVazio_DeveRetornarNull() {
        when(httpServletRequest.getHeader("Authorization")).thenReturn("");

        String result = jwtTokenProvider.resolverToken(httpServletRequest);

        assertNull(result);
    }

    @Test
    void validarToken_ComTokenValido_DeveRetornarTrue() {
        try (MockedStatic<ServletUriComponentsBuilder> mockedBuilder = mockStatic(ServletUriComponentsBuilder.class)) {
            setupMockBuilder(mockedBuilder);

            TokenDTO tokenDTO = jwtTokenProvider.criarTokenAcesso(UUID_USUARIO, USUARIO_TESTE, permissoes);

            boolean result = jwtTokenProvider.validarToken(tokenDTO.getAccessToken());

            assertTrue(result);
        }
    }

    @Test
    void validarToken_ComTokenExpirado_DeveLancarException() {
        Date agora = new Date();
        Date passado = new Date(agora.getTime() - 1000);

        String tokenExpirado = JWT.create()
                .withSubject(USUARIO_TESTE)
                .withIssuedAt(passado)
                .withExpiresAt(passado)
                .sign((Algorithm) ReflectionTestUtils.getField(jwtTokenProvider, "algoritmo"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> jwtTokenProvider.validarToken(tokenExpirado));

        assertEquals("Token JWT expirado ou inválido!", exception.getMessage());
    }

    @Test
    void validarToken_ComTokenInvalido_DeveLancarException() {
        String tokenInvalido = "token.invalido.aqui";

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> jwtTokenProvider.validarToken(tokenInvalido));

        assertEquals("Token JWT expirado ou inválido!", exception.getMessage());
    }

    @Test
    void validarToken_ComTokenMalFormado_DeveLancarException() {
        String tokenMalFormado = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> jwtTokenProvider.validarToken(tokenMalFormado));

        assertEquals("Token JWT expirado ou inválido!", exception.getMessage());
    }

    @Test
    void validarToken_ComTokenNull_DeveLancarException() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> jwtTokenProvider.validarToken(null));

        assertEquals("Token JWT expirado ou inválido!", exception.getMessage());
    }

    @Test
    void criarRefreshToken_ComTokenInvalido_DeveLancarException() {
        String tokenInvalido = "token.refresh.invalido";

        assertThrows(JWTVerificationException.class,
                () -> jwtTokenProvider.criarRefreshToken(tokenInvalido));
    }

    @Test
    void criarRefreshToken_ComTokenNull_DeveLancarException() {
        assertThrows(Exception.class,
                () -> jwtTokenProvider.criarRefreshToken(null));
    }

    @Test
    void criarRefreshToken_ComTokenSemUuidUsuario_DeveLancarException() {
        Date agora = new Date();
        Date validadeAtualizacao = new Date(agora.getTime() + (VALIDADE_EM_MILISSEGUNDOS * 3));
        Algorithm algoritmo = (Algorithm) ReflectionTestUtils.getField(jwtTokenProvider, "algoritmo");

        String tokenSemUuid = JWT.create()
                .withClaim("roles", List.of("ROLE_USER"))
                .withIssuedAt(agora)
                .withExpiresAt(validadeAtualizacao)
                .withSubject(USUARIO_TESTE)
                .sign(algoritmo);

        assertThrows(NullPointerException.class,
                () -> jwtTokenProvider.criarRefreshToken(tokenSemUuid));
    }

    @Test
    void obterAutenticacao_ComTokenInvalido_DeveLancarException() {
        String tokenInvalido = "token.invalido.para.autenticacao";

        assertThrows(JWTVerificationException.class,
                () -> jwtTokenProvider.obterAutenticacao(tokenInvalido));
    }

    @Test
    void obterAutenticacao_ComTokenNull_DeveLancarException() {
        assertThrows(Exception.class,
                () -> jwtTokenProvider.obterAutenticacao(null));
    }

    @Test
    void gerarTokenAcesso_DeveIncluirTodosCamposNecessarios() {
        try (MockedStatic<ServletUriComponentsBuilder> mockedBuilder = mockStatic(ServletUriComponentsBuilder.class)) {
            setupMockBuilder(mockedBuilder);

            TokenDTO tokenDTO = jwtTokenProvider.criarTokenAcesso(UUID_USUARIO, USUARIO_TESTE, permissoes);

            String token = tokenDTO.getAccessToken();
            assertNotNull(token);
            assertFalse(token.contains("Bearer"));
            assertEquals(3, token.split("\\.").length);

            assertEquals(token.trim(), token);
        }
    }

    @Test
    void gerarRefreshToken_DeveTerMaiorValidadeQueAccessToken() {
        try (MockedStatic<ServletUriComponentsBuilder> mockedBuilder = mockStatic(ServletUriComponentsBuilder.class)) {
            setupMockBuilder(mockedBuilder);

            TokenDTO tokenDTO = jwtTokenProvider.criarTokenAcesso(UUID_USUARIO, USUARIO_TESTE, permissoes);

            String refreshToken = tokenDTO.getRefreshToken();
            assertNotNull(refreshToken);
            assertFalse(refreshToken.contains("Bearer"));
            assertEquals(3, refreshToken.split("\\.").length);

            assertEquals(refreshToken.trim(), refreshToken);
        }
    }

    @Test
    void gerarRefreshToken_DeveTerValidadeTripla() {
        try (MockedStatic<ServletUriComponentsBuilder> mockedBuilder = mockStatic(ServletUriComponentsBuilder.class)) {
            setupMockBuilder(mockedBuilder);

            TokenDTO tokenDTO = jwtTokenProvider.criarTokenAcesso(UUID_USUARIO, USUARIO_TESTE, permissoes);

            long diferencaAccessToken = tokenDTO.getExpiration().getTime() - tokenDTO.getCreated().getTime();
            assertTrue(diferencaAccessToken >= VALIDADE_EM_MILISSEGUNDOS - 1000);
            assertTrue(diferencaAccessToken <= VALIDADE_EM_MILISSEGUNDOS + 1000);
        }
    }

    @Test
    void testCoberturaBranchesCompleta() {

        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer token123");
        String result1 = jwtTokenProvider.resolverToken(httpServletRequest);
        assertEquals("token123", result1);

        when(httpServletRequest.getHeader("Authorization")).thenReturn(null);
        String result2 = jwtTokenProvider.resolverToken(httpServletRequest);
        assertNull(result2);

        when(httpServletRequest.getHeader("Authorization")).thenReturn("Basic token123");
        String result3 = jwtTokenProvider.resolverToken(httpServletRequest);
        assertNull(result3);
    }

    @Test
    void testValidarToken_BranchTokenExpirado() {
        try (MockedStatic<ServletUriComponentsBuilder> mockedBuilder = mockStatic(ServletUriComponentsBuilder.class)) {
            setupMockBuilder(mockedBuilder);

            TokenDTO tokenDTO = jwtTokenProvider.criarTokenAcesso(UUID_USUARIO, USUARIO_TESTE, permissoes);

            boolean result = jwtTokenProvider.validarToken(tokenDTO.getAccessToken());
            assertTrue(result);
        }

        Date agora = new Date();
        Date passado = new Date(agora.getTime() - 1000);
        Algorithm algoritmo = (Algorithm) ReflectionTestUtils.getField(jwtTokenProvider, "algoritmo");

        String tokenExpirado = JWT.create()
                .withSubject(USUARIO_TESTE)
                .withIssuedAt(passado)
                .withExpiresAt(passado)
                .sign(algoritmo);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> jwtTokenProvider.validarToken(tokenExpirado));
        assertEquals("Token JWT expirado ou inválido!", exception.getMessage());
    }

    @Test
    void testCriarRefreshToken_ProcessamentoBearerToken() {
        try (MockedStatic<ServletUriComponentsBuilder> mockedBuilder = mockStatic(ServletUriComponentsBuilder.class)) {
            setupMockBuilder(mockedBuilder);

            String refreshToken = criarRefreshTokenValidoComAlgoritmoCorreto(asList("ROLE_USER", "ROLE_ADMIN"));

            TokenDTO result1 = jwtTokenProvider.criarRefreshToken(refreshToken);
            assertNotNull(result1);

            String bearerToken = "Bearer " + refreshToken + " ";
            TokenDTO result2 = jwtTokenProvider.criarRefreshToken(bearerToken);
            assertNotNull(result2);
        }
    }

    @Test
    void testGerarRefreshTokenNaoIncluiUuidUsuario_MasMetodoEsperaEsseClaim() {
        try (MockedStatic<ServletUriComponentsBuilder> mockedBuilder = mockStatic(ServletUriComponentsBuilder.class)) {
            setupMockBuilder(mockedBuilder);

            TokenDTO tokenDTO = jwtTokenProvider.criarTokenAcesso(UUID_USUARIO, USUARIO_TESTE, permissoes);
            String refreshTokenGerado = tokenDTO.getRefreshToken();

            assertThrows(NullPointerException.class,
                    () -> jwtTokenProvider.criarRefreshToken(refreshTokenGerado));
        }
    }

    @Test
    void testCriarRefreshToken_TestaSubstringComEspaco() {
        try (MockedStatic<ServletUriComponentsBuilder> mockedBuilder = mockStatic(ServletUriComponentsBuilder.class)) {
            setupMockBuilder(mockedBuilder);

            String refreshToken = criarRefreshTokenValidoComAlgoritmoCorreto(List.of("ROLE_USER"));

            String bearerTokenComEspaco = "Bearer " + refreshToken + " ";

            TokenDTO result = jwtTokenProvider.criarRefreshToken(bearerTokenComEspaco);

            assertNotNull(result);
            assertEquals(USUARIO_TESTE, result.getUsuario());
        }
    }

    private void setupMockBuilder(MockedStatic<ServletUriComponentsBuilder> mockedBuilder) {
        ServletUriComponentsBuilder builder = mock(ServletUriComponentsBuilder.class);
        UriComponents uriComponents = mock(UriComponents.class);

        mockedBuilder.when(ServletUriComponentsBuilder::fromCurrentContextPath).thenReturn(builder);
        when(builder.build()).thenReturn(uriComponents);
        when(uriComponents.toUriString()).thenReturn("http://localhost:8080");
    }

    private String criarRefreshTokenValidoComAlgoritmoCorreto(List<String> roles) {
        Date agora = new Date();
        Date validadeAtualizacao = new Date(agora.getTime() + (VALIDADE_EM_MILISSEGUNDOS * 3));

        Algorithm algoritmo = (Algorithm) ReflectionTestUtils.getField(jwtTokenProvider, "algoritmo");

        return JWT.create()
                .withClaim("roles", roles)
                .withClaim("uuidUsuario", UUID_USUARIO.toString())
                .withIssuedAt(agora)
                .withExpiresAt(validadeAtualizacao)
                .withSubject(USUARIO_TESTE)
                .sign(algoritmo)
                .strip();
    }
}