package br.com.sol_do_amanhecer.security.jwt;

import br.com.sol_do_amanhecer.model.entity.Permissao;
import br.com.sol_do_amanhecer.model.entity.Usuario;
import br.com.sol_do_amanhecer.repository.UsuarioRepository;
import br.com.sol_do_amanhecer.security.TokenDTO;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

import java.util.*;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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
    private static final int JWT_PARTS_COUNT = 3;
    private static final long TOLERANCIA_MILISSEGUNDOS = 1000L;

    private List<Permissao> permissoes;
    private MockedStatic<ServletUriComponentsBuilder> mockedBuilderStatic;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtTokenProvider, "chaveSecreta", CHAVE_SECRETA);
        ReflectionTestUtils.setField(jwtTokenProvider, "validadeEmMilissegundos", VALIDADE_EM_MILISSEGUNDOS);

        jwtTokenProvider.inicializar();

        Permissao permissao1 = new Permissao();
        permissao1.setDescricao("ROLE_USER");
        Permissao permissao2 = new Permissao();
        permissao2.setDescricao("ROLE_ADMIN");

        permissoes = new ArrayList<>();
        permissoes.add(permissao1);
        permissoes.add(permissao2);

        mockedBuilderStatic = mockStatic(ServletUriComponentsBuilder.class);
        configurarMockBuilder(mockedBuilderStatic);
    }

    @AfterEach
    void tearDown() {
        mockedBuilderStatic.close();
    }

    private Usuario criarUsuarioEntity() {
        Usuario usuario = new Usuario();
        usuario.setUsuario(USUARIO_TESTE);
        usuario.setSenha("senha");
        usuario.setPermissoes(new ArrayList<>(permissoes));
        return usuario;
    }

    private void configurarMockBuilder(MockedStatic<ServletUriComponentsBuilder> mockedBuilder) {
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

    private String criarAccessTokenValido() {
        Date agora = new Date();
        Date validade = new Date(agora.getTime() + VALIDADE_EM_MILISSEGUNDOS);

        Algorithm algoritmo = (Algorithm) ReflectionTestUtils.getField(jwtTokenProvider, "algoritmo");

        List<String> roles = permissoes.stream()
                .map(Permissao::getDescricao)
                .toList();

        return JWT.create()
                .withClaim("roles", roles)
                .withIssuedAt(agora)
                .withExpiresAt(validade)
                .withSubject(USUARIO_TESTE)
                .sign(algoritmo)
                .strip();
    }

    @Nested
    @DisplayName("inicializar")
    class Inicializar {

        @Test
        void inicializar_DeveConfigurarChaveSecretaEAlgoritmo() {
            JwtTokenProvider provider = new JwtTokenProvider(repositorioUsuario);
            ReflectionTestUtils.setField(provider, "chaveSecreta", "teste");
            ReflectionTestUtils.setField(provider, "validadeEmMilissegundos", 3600000L);

            provider.inicializar();

            Algorithm algoritmo = (Algorithm) ReflectionTestUtils.getField(provider, "algoritmo");
            String chaveSecretaEncoded = (String) ReflectionTestUtils.getField(provider, "chaveSecreta");

            assertNotNull(algoritmo, "O algoritmo deve ser inicializado após chamar inicializar()");
            assertNotEquals("teste", chaveSecretaEncoded, "A chave secreta deve ser codificada, não armazenada como texto plano");
            assertNotNull(chaveSecretaEncoded, "A chave secreta codificada não deve ser nula");
            assertTrue(chaveSecretaEncoded.length() > "teste".length(), "A chave codificada deve ser maior que o valor original");
        }
    }

    @Nested
    @DisplayName("criarTokenAcesso")
    class CriarTokenAcesso {

        @Test
        void criarTokenAcesso_DeveRetornarTokenDTOValido() {
            TokenDTO result = jwtTokenProvider.criarTokenAcesso(UUID_USUARIO, USUARIO_TESTE, permissoes);

            assertNotNull(result, "O TokenDTO não deve ser nulo");
            assertEquals(UUID_USUARIO, result.getUuidUsuario(), "O UUID do usuário deve corresponder ao informado");
            assertEquals(USUARIO_TESTE, result.getUsuario(), "O nome de usuário deve corresponder ao informado");
            assertTrue(result.getAuthenticated(), "O token deve estar marcado como autenticado");
            assertNotNull(result.getCreated(), "A data de criação não deve ser nula");
            assertNotNull(result.getExpiration(), "A data de expiração não deve ser nula");
            assertNotNull(result.getAccessToken(), "O access token não deve ser nulo");
            assertNotNull(result.getRefreshToken(), "O refresh token não deve ser nulo");

            assertTrue(result.getExpiration().before(new Date(result.getCreated().getTime() + (VALIDADE_EM_MILISSEGUNDOS * 3))),
                    "A data de expiração deve estar dentro do período máximo de validade");
        }

        @Test
        void criarTokenAcesso_ComListaPermissoesVazia_DeveRetornarTokenValido() {
            List<Permissao> permissoesVazias = new ArrayList<>();

            TokenDTO result = jwtTokenProvider.criarTokenAcesso(UUID_USUARIO, USUARIO_TESTE, permissoesVazias);

            assertNotNull(result, "O TokenDTO não deve ser nulo mesmo com lista de permissões vazia");
            assertEquals(UUID_USUARIO, result.getUuidUsuario(), "O UUID do usuário deve corresponder mesmo sem permissões");
            assertEquals(USUARIO_TESTE, result.getUsuario(), "O nome de usuário deve corresponder mesmo sem permissões");
            assertTrue(result.getAuthenticated(), "O token deve estar autenticado mesmo sem permissões");
        }

        @Test
        void gerarTokenAcesso_DeveIncluirTodosCamposNecessarios() {
            TokenDTO tokenDTO = jwtTokenProvider.criarTokenAcesso(UUID_USUARIO, USUARIO_TESTE, permissoes);

            String token = tokenDTO.getAccessToken();
            assertNotNull(token, "O access token não deve ser nulo");
            assertFalse(token.contains("Bearer"), "O access token não deve conter o prefixo 'Bearer'");
            assertEquals(JWT_PARTS_COUNT, token.split("\\.").length, "O JWT deve ter exatamente 3 partes separadas por ponto");
            assertEquals(token.trim(), token, "O token não deve conter espaços nas extremidades");
        }

        @Test
        void gerarRefreshToken_DeveTerMaiorValidadeQueAccessToken() {
            TokenDTO tokenDTO = jwtTokenProvider.criarTokenAcesso(UUID_USUARIO, USUARIO_TESTE, permissoes);

            String refreshToken = tokenDTO.getRefreshToken();
            assertNotNull(refreshToken, "O refresh token não deve ser nulo");
            assertFalse(refreshToken.contains("Bearer"), "O refresh token não deve conter o prefixo 'Bearer'");
            assertEquals(JWT_PARTS_COUNT, refreshToken.split("\\.").length, "O refresh token JWT deve ter exatamente 3 partes separadas por ponto");
            assertEquals(refreshToken.trim(), refreshToken, "O refresh token não deve conter espaços nas extremidades");
        }

        @Test
        void gerarRefreshToken_DeveTerValidadeTripla() {
            TokenDTO tokenDTO = jwtTokenProvider.criarTokenAcesso(UUID_USUARIO, USUARIO_TESTE, permissoes);

            long diferencaAccessToken = tokenDTO.getExpiration().getTime() - tokenDTO.getCreated().getTime();
            assertTrue(diferencaAccessToken >= VALIDADE_EM_MILISSEGUNDOS - TOLERANCIA_MILISSEGUNDOS,
                    "A diferença de tempo deve ser pelo menos a validade menos a tolerância de " + TOLERANCIA_MILISSEGUNDOS + "ms");
            assertTrue(diferencaAccessToken <= VALIDADE_EM_MILISSEGUNDOS + TOLERANCIA_MILISSEGUNDOS,
                    "A diferença de tempo não deve exceder a validade mais a tolerância de " + TOLERANCIA_MILISSEGUNDOS + "ms");
        }
    }

    @Nested
    @DisplayName("criarRefreshToken")
    class CriarRefreshToken {

        @Test
        void criarRefreshToken_ComTokenSemBearer_DeveRetornarNovoToken() {
            String refreshToken = criarRefreshTokenValidoComAlgoritmoCorreto(asList("ROLE_USER", "ROLE_ADMIN"));

            TokenDTO result = jwtTokenProvider.criarRefreshToken(refreshToken);

            assertNotNull(result, "O novo TokenDTO não deve ser nulo");
            assertEquals(USUARIO_TESTE, result.getUsuario(), "O nome de usuário deve ser mantido no novo token");
            assertEquals(UUID_USUARIO, result.getUuidUsuario(), "O UUID do usuário deve ser mantido no novo token");
            assertTrue(result.getAuthenticated(), "O novo token deve estar autenticado");
            assertNotNull(result.getAccessToken(), "O access token do novo token não deve ser nulo");
            assertNotNull(result.getRefreshToken(), "O refresh token renovado não deve ser nulo");
        }

        @Test
        void criarRefreshToken_ComTokenComBearer_DeveProcessarCorretamente() {
            String refreshToken = criarRefreshTokenValidoComAlgoritmoCorreto(asList("ROLE_USER", "ROLE_ADMIN"));
            String bearerToken = "Bearer " + refreshToken + " ";

            TokenDTO result = jwtTokenProvider.criarRefreshToken(bearerToken);

            assertNotNull(result, "O novo TokenDTO não deve ser nulo quando Bearer está presente");
            assertEquals(USUARIO_TESTE, result.getUsuario(), "O usuário deve ser extraído corretamente do token com Bearer");
            assertEquals(UUID_USUARIO, result.getUuidUsuario(), "O UUID deve ser extraído corretamente do token com Bearer");
            assertTrue(result.getAuthenticated(), "O token processado com Bearer deve estar autenticado");
        }

//    @Test
//    void criarRefreshToken_ComTokenComBearerSemEspaco_DeveProcessarCorretamente() {
//        String refreshToken = criarRefreshTokenValidoComAlgoritmoCorreto(asList("ROLE_USER", "ROLE_ADMIN"));
//        String bearerToken = "Bearer " + refreshToken;
//
//        TokenDTO result = jwtTokenProvider.criarRefreshToken(bearerToken);
//
//        assertNotNull(result);
//        assertEquals(USUARIO_TESTE, result.getUsuario());
//        assertEquals(UUID_USUARIO, result.getUuidUsuario());
//        assertTrue(result.getAuthenticated());
//    }

        @Test
        void criarRefreshToken_ComRolesVazias_DeveProcessarCorretamente() {
            String refreshToken = criarRefreshTokenValidoComAlgoritmoCorreto(new ArrayList<>());

            TokenDTO result = jwtTokenProvider.criarRefreshToken(refreshToken);

            assertNotNull(result, "O TokenDTO não deve ser nulo com roles vazias");
            assertEquals(USUARIO_TESTE, result.getUsuario(), "O usuário deve ser mantido mesmo com roles vazias");
            assertEquals(UUID_USUARIO, result.getUuidUsuario(), "O UUID deve ser mantido mesmo com roles vazias");
            assertTrue(result.getAuthenticated(), "O token deve estar autenticado mesmo com roles vazias");
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
        void deveCriarRefreshToken_ProcessamentoBearerToken() {
            String refreshToken = criarRefreshTokenValidoComAlgoritmoCorreto(asList("ROLE_USER", "ROLE_ADMIN"));

            TokenDTO result1 = jwtTokenProvider.criarRefreshToken(refreshToken);
            assertNotNull(result1, "Deve processar refresh token sem prefixo Bearer");

            String bearerToken = "Bearer " + refreshToken + " ";
            TokenDTO result2 = jwtTokenProvider.criarRefreshToken(bearerToken);
            assertNotNull(result2, "Deve processar refresh token com prefixo Bearer e espaço extra");
        }

        @Test
        void deveGerarRefreshTokenSemUuid_DeveLancarNullPointerException() {
            Date agora = new Date();
            Date validade = new Date(agora.getTime() + (VALIDADE_EM_MILISSEGUNDOS * 3));
            Algorithm algoritmo = (Algorithm) ReflectionTestUtils.getField(jwtTokenProvider, "algoritmo");

            String tokenSemUuid = JWT.create()
                    .withClaim("roles", asList("ROLE_USER", "ROLE_ADMIN"))
                    .withIssuedAt(agora)
                    .withExpiresAt(validade)
                    .withSubject(USUARIO_TESTE)
                    .sign(algoritmo);

            assertThrows(NullPointerException.class,
                    () -> jwtTokenProvider.criarRefreshToken(tokenSemUuid));
        }

        @Test
        void deveCriarRefreshToken_SubstringComEspaco() {
            String refreshToken = criarRefreshTokenValidoComAlgoritmoCorreto(List.of("ROLE_USER"));
            String bearerTokenComEspaco = "Bearer " + refreshToken + " ";

            TokenDTO result = jwtTokenProvider.criarRefreshToken(bearerTokenComEspaco);

            assertNotNull(result, "O resultado não deve ser nulo ao processar Bearer com espaço");
            assertEquals(USUARIO_TESTE, result.getUsuario(), "O usuário deve ser extraído corretamente do token com espaço");
        }
    }

    @Nested
    @DisplayName("resolverToken")
    class ResolverToken {

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
        void deveCobrirBranchesCompletos() {
            when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer token123");
            String result1 = jwtTokenProvider.resolverToken(httpServletRequest);
            assertEquals("token123", result1, "Deve extrair o token sem o prefixo 'Bearer '");

            when(httpServletRequest.getHeader("Authorization")).thenReturn(null);
            String result2 = jwtTokenProvider.resolverToken(httpServletRequest);
            assertNull(result2, "Header nulo deve retornar null");

            when(httpServletRequest.getHeader("Authorization")).thenReturn("Basic token123");
            String result3 = jwtTokenProvider.resolverToken(httpServletRequest);
            assertNull(result3, "Header sem prefixo 'Bearer' deve retornar null");
        }
    }

    @Nested
    @DisplayName("validarToken")
    class ValidarToken {

        @Test
        void validarToken_ComTokenValido_DeveRetornarTrue() {
            String token = criarAccessTokenValido();

            boolean result = jwtTokenProvider.validarToken(token);

            assertTrue(result, "Token com assinatura e prazo válidos deve ser aceito pela validação");
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
        void deveValidarToken_BranchTokenExpirado() {
            String tokenValido = criarAccessTokenValido();
            boolean result = jwtTokenProvider.validarToken(tokenValido);
            assertTrue(result, "Token com prazo e assinatura válidos deve ser aceito");

            Date agora = new Date();
            Date passado = new Date(agora.getTime() - TOLERANCIA_MILISSEGUNDOS);
            Algorithm algoritmo = (Algorithm) ReflectionTestUtils.getField(jwtTokenProvider, "algoritmo");

            String tokenExpirado = JWT.create()
                    .withSubject(USUARIO_TESTE)
                    .withIssuedAt(passado)
                    .withExpiresAt(passado)
                    .sign(algoritmo);

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> jwtTokenProvider.validarToken(tokenExpirado));
            assertEquals("Token JWT expirado ou inválido!", exception.getMessage(),
                    "Deve lançar exceção com mensagem específica para token expirado");
        }
    }

    @Nested
    @DisplayName("obterAutenticacao")
    class ObterAutenticacao {

        @Test
        void obterAutenticacao_DeveRetornarAuthenticationValido() {
            String accessToken = criarAccessTokenValido();
            Usuario usuarioEntity = criarUsuarioEntity();
            when(repositorioUsuario.findByUsuario(USUARIO_TESTE)).thenReturn(usuarioEntity);

            Authentication result = jwtTokenProvider.obterAutenticacao(accessToken);

            assertNotNull(result, "A autenticação não deve ser nula para token válido");
            assertEquals(usuarioEntity, result.getPrincipal(), "O principal deve ser o usuário encontrado no repositório");
            assertEquals("", result.getCredentials(), "As credenciais devem ser vazias no padrão JWT");
            assertNotNull(result.getAuthorities(), "As authorities não devem ser nulas");
        }

        @Test
        void decodificarToken_ComTokenValido_DeveRetornarDecodedJWT() {
            String accessToken = criarAccessTokenValido();
            when(repositorioUsuario.findByUsuario(USUARIO_TESTE)).thenReturn(criarUsuarioEntity());

            Authentication result = jwtTokenProvider.obterAutenticacao(accessToken);

            assertNotNull(result, "A autenticação obtida a partir de um token válido não deve ser nula");
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
    }
}
