package br.com.sol_do_amanhecer.config;

import br.com.sol_do_amanhecer.security.jwt.JwtConfigurer;
import br.com.sol_do_amanhecer.security.jwt.JwtTokenProvider;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        authorizeHttpRequests -> authorizeHttpRequests
                                .requestMatchers(
                                        "/sol-do-amanhecer/api/acao/",
                                        "/sol-do-amanhecer/api/doacao/",
                                        "/sol-do-amanhecer/api/voluntario/criar",
                                        "/sol-do-amanhecer/api/usuario/trocar-senha/",
                                        "/sol-do-amanhecer/api/usuario/resetar-senha/",
//                                        "/sol-do-amanhecer/api/usuario/**", //Só para teste localhost
//                                        "/sol-do-amanhecer/api/permissao/**", //Só para teste localhost
//                                        "/sol-do-amanhecer/api/voluntario/**", //Só para teste localhost
                                        "/sol-do-amanhecer/api/prestacao/",
                                        "/sol-do-amanhecer/api/objetivo/",
                                        "/sol-do-amanhecer/api/autenticacao/**",
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**"
                                ).permitAll()
                                .requestMatchers(
                                        "/sol-do-amanhecer/api/acao/**",
                                        "/sol-do-amanhecer/api/doacao/**",
                                        "/sol-do-amanhecer/api/voluntario/**",
                                        "/sol-do-amanhecer/api/usuario/**",
                                        "/sol-do-amanhecer/api/prestacao/**",
                                        "/sol-do-amanhecer/api/objetivo/**"
                                ).authenticated()
                                .requestMatchers(
                                        "/sol-do-amanhecer/api/permissao/**"
                                ).denyAll()
                                .anyRequest().authenticated()
                )
                .cors(Customizer.withDefaults())
                .apply(new JwtConfigurer(jwtTokenProvider));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
