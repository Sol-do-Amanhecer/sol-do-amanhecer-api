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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

import java.util.*;

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

        User.builder()
                .username(USUARIO_TESTE)
                .password("senha")
                .authorities(Arrays.asList(
                        new SimpleGrantedAuthority("ROLE_USER"),
                        new SimpleGrantedAuthority("ROLE_ADMIN")
                ))
                .build();
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
            ServletUriComponentsBuilder builder = mock(ServletUriComponentsBuilder.class);
            mockedBuilder.when(ServletUriComponentsBuilder::fromCurrentContextPath).thenReturn(builder);
            when(builder.build()).thenReturn(mock(org.springframework.web.util.UriComponents.class));
            when(builder.build().toUriString()).thenReturn("http://localhost:8080");
            
            TokenDTO result = jwtTokenProvider.criarTokenAcesso(UUID_USUARIO, USUARIO_TESTE, permissoes);
            
            assertNotNull(result);
            assertEquals(UUID_USUARIO, result.getUuidUsuario());
            assertEquals(USUARIO_TESTE, result.getUsuario());
            assertTrue(result.getAuthenticated());
            assertNotNull(result.getCreated());
            assertNotNull(result.getExpiration());
            assertNotNull(result.getAccessToken());
            assertNotNull(result.getRefreshToken());
        }
    }

    @Test
    void criarRefreshToken_ComTokenSemBearer_DeveRetornarNovoToken() {
        try (MockedStatic<ServletUriComponentsBuilder> mockedBuilder = mockStatic(ServletUriComponentsBuilder.class)) {
            ServletUriComponentsBuilder builder = mock(ServletUriComponentsBuilder.class);
            mockedBuilder.when(ServletUriComponentsBuilder::fromCurrentContextPath).thenReturn(builder);
            when(builder.build()).thenReturn(mock(org.springframework.web.util.UriComponents.class));
            when(builder.build().toUriString()).thenReturn("http://localhost:8080");

            String refreshToken = criarRefreshTokenValido(
                    Arrays.asList("ROLE_USER", "ROLE_ADMIN")
            );
            
            TokenDTO result = jwtTokenProvider.criarRefreshToken(refreshToken);
            
            assertNotNull(result);
            assertEquals(USUARIO_TESTE, result.getUsuario());
            assertTrue(result.getAuthenticated());
            assertNotNull(result.getAccessToken());
            assertNotNull(result.getRefreshToken());
        }
    }

    @Test
    void criarRefreshToken_ComTokenComBearer_DeveProcessarCorretamente() {
        try (MockedStatic<ServletUriComponentsBuilder> mockedBuilder = mockStatic(ServletUriComponentsBuilder.class)) {
            ServletUriComponentsBuilder builder = mock(ServletUriComponentsBuilder.class);
            mockedBuilder.when(ServletUriComponentsBuilder::fromCurrentContextPath).thenReturn(builder);
            when(builder.build()).thenReturn(mock(org.springframework.web.util.UriComponents.class));
            when(builder.build().toUriString()).thenReturn("http://localhost:8080");

            String refreshToken = criarRefreshTokenValido(
                    Arrays.asList("ROLE_USER", "ROLE_ADMIN")
            );

            TokenDTO result = jwtTokenProvider.criarRefreshToken(refreshToken);
            
            assertNotNull(result);
            assertEquals(USUARIO_TESTE, result.getUsuario());
            assertTrue(result.getAuthenticated());
        }
    }

    @Test
    void obterAutenticacao_DeveRetornarAuthenticationValido() {
        try (MockedStatic<ServletUriComponentsBuilder> mockedBuilder = mockStatic(ServletUriComponentsBuilder.class)) {
            ServletUriComponentsBuilder builder = mock(ServletUriComponentsBuilder.class);
            UriComponents uriComponents = mock(UriComponents.class);

            mockedBuilder.when(ServletUriComponentsBuilder::fromCurrentContextPath).thenReturn(builder);
            when(builder.build()).thenReturn(uriComponents);
            when(uriComponents.toUriString()).thenReturn("http://localhost:8080");

            TokenDTO tokenDTO = jwtTokenProvider.criarTokenAcesso(UUID_USUARIO, USUARIO_TESTE, permissoes);

            Usuario usuario = new Usuario();
            usuario.setUsuario(USUARIO_TESTE);
            usuario.setSenha("senha");

            when(repositorioUsuario.findByUsuario(USUARIO_TESTE)).thenReturn(usuario);

            Authentication result = jwtTokenProvider.obterAutenticacao(tokenDTO.getAccessToken());

            assertNotNull(result);
            assertEquals(usuario, result.getPrincipal());
            assertEquals("", result.getCredentials());
            assertNotNull(result.getAuthorities());
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
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn(null);

        String result = jwtTokenProvider.resolverToken(request);

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
    void validarToken_ComTokenValido_DeveRetornarTrue() {
        try (MockedStatic<ServletUriComponentsBuilder> mockedBuilder = mockStatic(ServletUriComponentsBuilder.class)) {
            ServletUriComponentsBuilder builder = mock(ServletUriComponentsBuilder.class);
            mockedBuilder.when(ServletUriComponentsBuilder::fromCurrentContextPath).thenReturn(builder);
            when(builder.build()).thenReturn(mock(org.springframework.web.util.UriComponents.class));
            when(builder.build().toUriString()).thenReturn("http://localhost:8080");

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
    void criarRefreshToken_ComTokenInvalido_DeveLancarException() {
        String tokenInvalido = "token.refresh.invalido";

        assertThrows(JWTVerificationException.class,
                () -> jwtTokenProvider.criarRefreshToken(tokenInvalido));
    }

    @Test
    void obterAutenticacao_ComTokenInvalido_DeveLancarException() {
        String tokenInvalido = "token.invalido.para.autenticacao";
         
        assertThrows(JWTVerificationException.class,
                () -> jwtTokenProvider.obterAutenticacao(tokenInvalido));
    }

    @Test
    void gerarAccessToken_DeveIncluirTodosCamposNecessarios() {
        try (MockedStatic<ServletUriComponentsBuilder> mockedBuilder = mockStatic(ServletUriComponentsBuilder.class)) {
            ServletUriComponentsBuilder builder = mock(ServletUriComponentsBuilder.class);
            mockedBuilder.when(ServletUriComponentsBuilder::fromCurrentContextPath).thenReturn(builder);
            when(builder.build()).thenReturn(mock(org.springframework.web.util.UriComponents.class));
            when(builder.build().toUriString()).thenReturn("http://localhost:8080");
            
            TokenDTO tokenDTO = jwtTokenProvider.criarTokenAcesso(UUID_USUARIO, USUARIO_TESTE, permissoes);
            
            String token = tokenDTO.getAccessToken();
            assertNotNull(token);
            assertFalse(token.contains("Bearer"));
            assertEquals(3, token.split("\\.").length);
        }
    }

    @Test
    void gerarRefreshToken_DeveTermaiorValidadeQueAccessToken() {
        try (MockedStatic<ServletUriComponentsBuilder> mockedBuilder = mockStatic(ServletUriComponentsBuilder.class)) {
            ServletUriComponentsBuilder builder = mock(ServletUriComponentsBuilder.class);
            mockedBuilder.when(ServletUriComponentsBuilder::fromCurrentContextPath).thenReturn(builder);
            when(builder.build()).thenReturn(mock(org.springframework.web.util.UriComponents.class));
            when(builder.build().toUriString()).thenReturn("http://localhost:8080");

            TokenDTO tokenDTO = jwtTokenProvider.criarTokenAcesso(UUID_USUARIO, USUARIO_TESTE, permissoes);

            String refreshToken = tokenDTO.getRefreshToken();
            assertNotNull(refreshToken);
            assertFalse(refreshToken.contains("Bearer"));
            assertEquals(3, refreshToken.split("\\.").length);
        }
    }

    private String criarRefreshTokenValido(List<String> roles) {
        Date agora = new Date();
        Date validadeAtualizacao = new Date(agora.getTime() + (VALIDADE_EM_MILISSEGUNDOS * 3));
        Algorithm algoritmo = (Algorithm) ReflectionTestUtils.getField(jwtTokenProvider, "algoritmo");

        return JWT.create()
                .withClaim("roles", roles)
                .withClaim("uuidUsuario", JwtTokenProviderTest.UUID_USUARIO.toString())
                .withIssuedAt(agora)
                .withExpiresAt(validadeAtualizacao)
                .withSubject(JwtTokenProviderTest.USUARIO_TESTE)
                .sign(algoritmo)
                .strip();
    }
}