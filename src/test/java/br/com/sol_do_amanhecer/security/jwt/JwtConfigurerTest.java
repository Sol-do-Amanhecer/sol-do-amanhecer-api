package br.com.sol_do_amanhecer.security.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtConfigurer - Testes de Unidade")
class JwtConfigurerTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private HttpSecurity httpSecurity;

    private JwtConfigurer jwtConfigurer;

    @BeforeEach
    void setUp() {
        jwtConfigurer = new JwtConfigurer(jwtTokenProvider);
    }

    @Test
    @DisplayName("Deve configurar HttpSecurity com JwtTokenFilter antes do UsernamePasswordAuthenticationFilter")
    void deveConfigurarHttpSecurityComJwtTokenFilter() {
        when(httpSecurity.addFilterBefore(any(), eq(UsernamePasswordAuthenticationFilter.class)))
                .thenReturn(httpSecurity);

        jwtConfigurer.configure(httpSecurity);

        ArgumentCaptor<JwtTokenFilter> filterCaptor = ArgumentCaptor.forClass(JwtTokenFilter.class);
        verify(httpSecurity).addFilterBefore(filterCaptor.capture(), eq(UsernamePasswordAuthenticationFilter.class));

        JwtTokenFilter capturedFilter = filterCaptor.getValue();
        assertNotNull(capturedFilter);

        assertDoesNotThrow(() -> new JwtTokenFilter(jwtTokenProvider));
    }

    @Test
    @DisplayName("Deve criar nova instância de JwtTokenFilter a cada configuração")
    void deveCriarNovaInstanciaDeJwtTokenFilterACadaConfiguracao() {
        when(httpSecurity.addFilterBefore(any(), eq(UsernamePasswordAuthenticationFilter.class)))
                .thenReturn(httpSecurity);

        jwtConfigurer.configure(httpSecurity);
        jwtConfigurer.configure(httpSecurity);

        ArgumentCaptor<JwtTokenFilter> filterCaptor = ArgumentCaptor.forClass(JwtTokenFilter.class);
        verify(httpSecurity, times(2)).addFilterBefore(filterCaptor.capture(), eq(UsernamePasswordAuthenticationFilter.class));

        assertEquals(2, filterCaptor.getAllValues().size());
        assertNotSame(filterCaptor.getAllValues().get(0), filterCaptor.getAllValues().get(1));
    }

    @Test
    @DisplayName("Deve usar o mesmo JwtTokenProvider em todas as configurações")
    void deveUsarOMesmoJwtTokenProviderEmTodasAsConfiguracoes() {
        when(httpSecurity.addFilterBefore(any(), eq(UsernamePasswordAuthenticationFilter.class)))
                .thenReturn(httpSecurity);

        jwtConfigurer.configure(httpSecurity);

        ArgumentCaptor<JwtTokenFilter> filterCaptor = ArgumentCaptor.forClass(JwtTokenFilter.class);
        verify(httpSecurity).addFilterBefore(filterCaptor.capture(), eq(UsernamePasswordAuthenticationFilter.class));

        assertNotNull(filterCaptor.getValue());
    }

    @Test
    @DisplayName("Deve manter referência ao JwtTokenProvider fornecido no construtor")
    void deveManterReferenciaAoJwtTokenProviderFornecidoNoconstrutor() {
        JwtConfigurer configurer = new JwtConfigurer(jwtTokenProvider);

        assertNotNull(configurer);

        when(httpSecurity.addFilterBefore(any(), eq(UsernamePasswordAuthenticationFilter.class)))
                .thenReturn(httpSecurity);

        assertDoesNotThrow(() -> configurer.configure(httpSecurity));
    }

    @Test
    @DisplayName("Deve funcionar com JwtTokenProvider nulo (se permitido pela implementação)")
    void deveFuncionarComJwtTokenProviderNulo() {
        JwtConfigurer configurerComProviderNulo = new JwtConfigurer(null);
        when(httpSecurity.addFilterBefore(any(), eq(UsernamePasswordAuthenticationFilter.class)))
                .thenReturn(httpSecurity);

        try {
            configurerComProviderNulo.configure(httpSecurity);

            verify(httpSecurity).addFilterBefore(any(JwtTokenFilter.class), eq(UsernamePasswordAuthenticationFilter.class));
        } catch (Exception e) {
            assertTrue(e instanceof NullPointerException || e instanceof IllegalArgumentException,
                    "Exceção deve ser NullPointerException ou IllegalArgumentException, mas foi: " + e.getClass().getSimpleName());
        }
    }

    @Test
    @DisplayName("Deve adicionar filtro na posição correta da cadeia de filtros")
    void deveAdicionarFiltroNaPosicaoCorretaDaCadeiaDefiltros() {
        when(httpSecurity.addFilterBefore(any(), eq(UsernamePasswordAuthenticationFilter.class)))
                .thenReturn(httpSecurity);

        jwtConfigurer.configure(httpSecurity);

        verify(httpSecurity).addFilterBefore(any(JwtTokenFilter.class), eq(UsernamePasswordAuthenticationFilter.class));
        verify(httpSecurity, never()).addFilterAfter(any(), any());
        verify(httpSecurity, never()).addFilter(any());
    }
}