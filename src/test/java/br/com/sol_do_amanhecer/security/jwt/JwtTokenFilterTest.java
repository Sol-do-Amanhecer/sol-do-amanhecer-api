package br.com.sol_do_amanhecer.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtTokenFilter - Testes de Unidade")
class JwtTokenFilterTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private FilterChain filterChain;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    private JwtTokenFilter jwtTokenFilter;

    @BeforeEach
    void setUp() {
        jwtTokenFilter = new JwtTokenFilter(jwtTokenProvider);

        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Deve processar token válido e definir autenticação no SecurityContext")
    void deveProcessarTokenValidoEDefinirAutenticacaoNoSecurityContext() throws IOException, ServletException {
        String tokenValido = "token.jwt.valido";

        when(jwtTokenProvider.resolverToken(httpServletRequest)).thenReturn(tokenValido);
        when(jwtTokenProvider.validarToken(tokenValido)).thenReturn(true);
        when(jwtTokenProvider.obterAutenticacao(tokenValido)).thenReturn(authentication);

        jwtTokenFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        verify(jwtTokenProvider).resolverToken(httpServletRequest);
        verify(jwtTokenProvider).validarToken(tokenValido);
        verify(jwtTokenProvider).obterAutenticacao(tokenValido);
        verify(securityContext).setAuthentication(authentication);
        verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
    }

    @Test
    @DisplayName("Deve continuar cadeia de filtros quando token é nulo")
    void deveContinuarCadeiaDefiltrosQuandoTokenENulo() throws IOException, ServletException {
        when(jwtTokenProvider.resolverToken(httpServletRequest)).thenReturn(null);

        jwtTokenFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        verify(jwtTokenProvider).resolverToken(httpServletRequest);
        verify(jwtTokenProvider, never()).validarToken(any());
        verify(jwtTokenProvider, never()).obterAutenticacao(any());
        verify(securityContext, never()).setAuthentication(any());
        verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
    }

    @Test
    @DisplayName("Deve continuar cadeia de filtros quando token é inválido")
    void deveContinuarCadeiaDefiltrosQuandoTokenEInvalido() throws IOException, ServletException {
        String tokenInvalido = "token.jwt.invalido";

        when(jwtTokenProvider.resolverToken(httpServletRequest)).thenReturn(tokenInvalido);
        when(jwtTokenProvider.validarToken(tokenInvalido)).thenReturn(false);

        jwtTokenFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        verify(jwtTokenProvider).resolverToken(httpServletRequest);
        verify(jwtTokenProvider).validarToken(tokenInvalido);
        verify(jwtTokenProvider, never()).obterAutenticacao(any());
        verify(securityContext, never()).setAuthentication(any());
        verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
    }

    @Test
    @DisplayName("Deve continuar cadeia de filtros quando autenticação é nula")
    void deveContinuarCadeiaDefiltrosQuandoAutenticacaoENula() throws IOException, ServletException {
        String tokenValido = "token.jwt.valido";

        when(jwtTokenProvider.resolverToken(httpServletRequest)).thenReturn(tokenValido);
        when(jwtTokenProvider.validarToken(tokenValido)).thenReturn(true);
        when(jwtTokenProvider.obterAutenticacao(tokenValido)).thenReturn(null);

        jwtTokenFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        verify(jwtTokenProvider).resolverToken(httpServletRequest);
        verify(jwtTokenProvider).validarToken(tokenValido);
        verify(jwtTokenProvider).obterAutenticacao(tokenValido);
        verify(securityContext, never()).setAuthentication(any());
        verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
    }

    @Test
    @DisplayName("Deve continuar cadeia de filtros quando token é string vazia")
    void deveContinuarCadeiaDefiltrosQuandoTokenEStringVazia() throws IOException, ServletException {
        String tokenVazio = "";
        when(jwtTokenProvider.resolverToken(httpServletRequest)).thenReturn(tokenVazio);
        when(jwtTokenProvider.validarToken(tokenVazio)).thenReturn(false); // String vazia deve ser inválida

        jwtTokenFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        verify(jwtTokenProvider).resolverToken(httpServletRequest);
        verify(jwtTokenProvider).validarToken(tokenVazio); // Agora esperamos que seja chamado
        verify(jwtTokenProvider, never()).obterAutenticacao(any()); // Não deve obter autenticação
        verify(securityContext, never()).setAuthentication(any());
        verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
    }

    @Test
    @DisplayName("Deve propagar IOException da cadeia de filtros")
    void devePropagarIOExceptionDaCadeiaDefiltros() throws IOException, ServletException {
        IOException ioException = new IOException("Erro de I/O");
        when(jwtTokenProvider.resolverToken(httpServletRequest)).thenReturn(null);
        doThrow(ioException).when(filterChain).doFilter(httpServletRequest, httpServletResponse);

        IOException exception = assertThrows(IOException.class,
                () -> jwtTokenFilter.doFilter(httpServletRequest, httpServletResponse, filterChain));

        assertEquals("Erro de I/O", exception.getMessage());
        verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
    }

    @Test
    @DisplayName("Deve propagar ServletException da cadeia de filtros")
    void devePropagarServletExceptionDaCadeiaDefiltros() throws IOException, ServletException {
        ServletException servletException = new ServletException("Erro de Servlet");
        when(jwtTokenProvider.resolverToken(httpServletRequest)).thenReturn(null);
        doThrow(servletException).when(filterChain).doFilter(httpServletRequest, httpServletResponse);

        ServletException exception = assertThrows(ServletException.class,
                () -> jwtTokenFilter.doFilter(httpServletRequest, httpServletResponse, filterChain));

        assertEquals("Erro de Servlet", exception.getMessage());
        verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
    }

    @Test
    @DisplayName("Deve tratar exceção durante resolução do token")
    void deveTratarExcecaoDuranteResolucaoDoToken() throws IOException, ServletException {
        RuntimeException runtimeException = new RuntimeException("Erro ao resolver token");
        when(jwtTokenProvider.resolverToken(httpServletRequest)).thenThrow(runtimeException);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> jwtTokenFilter.doFilter(httpServletRequest, httpServletResponse, filterChain));

        assertEquals("Erro ao resolver token", exception.getMessage());
        verify(jwtTokenProvider).resolverToken(httpServletRequest);
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    @DisplayName("Deve tratar exceção durante validação do token")
    void deveTratarExcecaoDuranteValidacaoDoToken() throws IOException, ServletException {
        String token = "token.jwt.test";
        RuntimeException runtimeException = new RuntimeException("Erro ao validar token");

        when(jwtTokenProvider.resolverToken(httpServletRequest)).thenReturn(token);
        when(jwtTokenProvider.validarToken(token)).thenThrow(runtimeException);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> jwtTokenFilter.doFilter(httpServletRequest, httpServletResponse, filterChain));

        assertEquals("Erro ao validar token", exception.getMessage());
        verify(jwtTokenProvider).resolverToken(httpServletRequest);
        verify(jwtTokenProvider).validarToken(token);
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    @DisplayName("Deve tratar exceção durante obtenção da autenticação")
    void deveTratarExcecaoDuranteObtencaoDaAutenticacao() throws IOException, ServletException {
        String token = "token.jwt.test";
        RuntimeException runtimeException = new RuntimeException("Erro ao obter autenticação");

        when(jwtTokenProvider.resolverToken(httpServletRequest)).thenReturn(token);
        when(jwtTokenProvider.validarToken(token)).thenReturn(true);
        when(jwtTokenProvider.obterAutenticacao(token)).thenThrow(runtimeException);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> jwtTokenFilter.doFilter(httpServletRequest, httpServletResponse, filterChain));

        assertEquals("Erro ao obter autenticação", exception.getMessage());
        verify(jwtTokenProvider).resolverToken(httpServletRequest);
        verify(jwtTokenProvider).validarToken(token);
        verify(jwtTokenProvider).obterAutenticacao(token);
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    @DisplayName("Deve funcionar com ServletRequest genérico (cast para HttpServletRequest)")
    void deveFuncionarComServletRequestGenerico() throws IOException, ServletException {
        HttpServletRequest servletRequest = httpServletRequest; // ServletRequest genérico
        ServletResponse servletResponse = httpServletResponse;

        when(jwtTokenProvider.resolverToken(servletRequest)).thenReturn(null);

        jwtTokenFilter.doFilter(servletRequest, servletResponse, filterChain);

        verify(jwtTokenProvider).resolverToken(servletRequest);
        verify(filterChain).doFilter(servletRequest, servletResponse);
    }
}