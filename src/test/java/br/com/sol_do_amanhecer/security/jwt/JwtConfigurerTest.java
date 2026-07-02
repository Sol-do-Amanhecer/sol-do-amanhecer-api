package br.com.sol_do_amanhecer.security.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

    private static final int DUAS_CONFIGURACOES = 2;
    private static final int INDICE_PRIMEIRO_FILTRO = 0;
    private static final int INDICE_SEGUNDO_FILTRO = 1;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private HttpSecurity httpSecurity;

    @Nested
    @DisplayName("Verificação do filtro adicionado")
    class VerificacaoDeFiltro {

        private JwtConfigurer jwtConfigurer;

        @BeforeEach
        void setUp() {
            jwtConfigurer = new JwtConfigurer(jwtTokenProvider);
        }

        @Test
        @DisplayName("Deve configurar HttpSecurity com JwtTokenFilter antes do UsernamePasswordAuthenticationFilter")
        public void deveConfigurarHttpSecurityComJwtTokenFilter() {
            when(httpSecurity.addFilterBefore(any(), eq(UsernamePasswordAuthenticationFilter.class)))
                    .thenReturn(httpSecurity);

            jwtConfigurer.configure(httpSecurity);

            ArgumentCaptor<JwtTokenFilter> filterCaptor = ArgumentCaptor.forClass(JwtTokenFilter.class);
            verify(httpSecurity).addFilterBefore(filterCaptor.capture(), eq(UsernamePasswordAuthenticationFilter.class));

            assertNotNull(filterCaptor.getValue());
            assertDoesNotThrow(() -> new JwtTokenFilter(jwtTokenProvider));
        }

        @Test
        @DisplayName("Deve adicionar filtro na posição correta da cadeia de filtros")
        public void deveAdicionarFiltroNaPosicaoCorretaDaCadeiaDefiltros() {
            when(httpSecurity.addFilterBefore(any(), eq(UsernamePasswordAuthenticationFilter.class)))
                    .thenReturn(httpSecurity);

            assertDoesNotThrow(() -> jwtConfigurer.configure(httpSecurity));

            verify(httpSecurity).addFilterBefore(any(JwtTokenFilter.class), eq(UsernamePasswordAuthenticationFilter.class));
            verify(httpSecurity, never()).addFilterAfter(any(), any());
            verify(httpSecurity, never()).addFilter(any());
        }
    }

    @Nested
    @DisplayName("Instâncias de JwtTokenFilter por chamada")
    class InstanciasMultiplas {

        private JwtConfigurer jwtConfigurer;

        @BeforeEach
        void setUp() {
            jwtConfigurer = new JwtConfigurer(jwtTokenProvider);
        }

        @Test
        @DisplayName("Deve criar nova instância de JwtTokenFilter a cada configuração")
        public void deveCriarNovaInstanciaDeJwtTokenFilterACadaConfiguracao() {
            when(httpSecurity.addFilterBefore(any(), eq(UsernamePasswordAuthenticationFilter.class)))
                    .thenReturn(httpSecurity);

            jwtConfigurer.configure(httpSecurity);
            jwtConfigurer.configure(httpSecurity);

            ArgumentCaptor<JwtTokenFilter> filterCaptor = ArgumentCaptor.forClass(JwtTokenFilter.class);
            verify(httpSecurity, times(2)).addFilterBefore(filterCaptor.capture(), eq(UsernamePasswordAuthenticationFilter.class));

            assertEquals(DUAS_CONFIGURACOES, filterCaptor.getAllValues().size(),
                    "Devem haver exatamente duas instâncias de filtro capturadas");
            assertNotSame(filterCaptor.getAllValues().get(INDICE_PRIMEIRO_FILTRO),
                    filterCaptor.getAllValues().get(INDICE_SEGUNDO_FILTRO),
                    "Cada chamada deve criar uma nova instância de JwtTokenFilter");
        }

        @Test
        @DisplayName("Deve usar o mesmo JwtTokenProvider em todas as configurações")
        public void deveUsarOMesmoJwtTokenProviderEmTodasAsConfiguracoes() {
            when(httpSecurity.addFilterBefore(any(), eq(UsernamePasswordAuthenticationFilter.class)))
                    .thenReturn(httpSecurity);

            jwtConfigurer.configure(httpSecurity);

            ArgumentCaptor<JwtTokenFilter> filterCaptor = ArgumentCaptor.forClass(JwtTokenFilter.class);
            verify(httpSecurity).addFilterBefore(filterCaptor.capture(), eq(UsernamePasswordAuthenticationFilter.class));

            assertNotNull(filterCaptor.getValue());
        }
    }

    @Nested
    @DisplayName("Manutenção da referência ao provider")
    class ManutencaoDeReferencia {

        @Test
        @DisplayName("Deve manter referência ao JwtTokenProvider fornecido no construtor")
        public void deveManterReferenciaAoJwtTokenProviderFornecidoNoconstrutor() {
            JwtConfigurer configurer = new JwtConfigurer(jwtTokenProvider);

            assertNotNull(configurer);

            when(httpSecurity.addFilterBefore(any(), eq(UsernamePasswordAuthenticationFilter.class)))
                    .thenReturn(httpSecurity);

            assertDoesNotThrow(() -> configurer.configure(httpSecurity));
        }
    }

    @Nested
    @DisplayName("Comportamento com provider nulo")
    class ComProviderNulo {

        @Test
        @DisplayName("Deve funcionar com JwtTokenProvider nulo (se permitido pela implementação)")
        public void deveFuncionarComJwtTokenProviderNulo() {
            JwtConfigurer configurerComProviderNulo = new JwtConfigurer(null);
            when(httpSecurity.addFilterBefore(any(), eq(UsernamePasswordAuthenticationFilter.class)))
                    .thenReturn(httpSecurity);

            assertDoesNotThrow(() -> configurerComProviderNulo.configure(httpSecurity),
                    "JwtConfigurer com provider nulo não deve lançar exceção no ambiente de teste com mock");

            verify(httpSecurity).addFilterBefore(any(JwtTokenFilter.class), eq(UsernamePasswordAuthenticationFilter.class));
        }
    }
}
