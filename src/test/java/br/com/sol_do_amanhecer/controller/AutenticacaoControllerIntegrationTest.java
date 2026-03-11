package br.com.sol_do_amanhecer.controller;

import br.com.sol_do_amanhecer.model.entity.Permissao;
import br.com.sol_do_amanhecer.model.entity.Usuario;
import br.com.sol_do_amanhecer.model.entity.Voluntario;
import br.com.sol_do_amanhecer.model.entity.Endereco;
import br.com.sol_do_amanhecer.repository.PermissaoRepository;
import br.com.sol_do_amanhecer.repository.UsuarioRepository;
import br.com.sol_do_amanhecer.repository.VoluntarioRepository;
import br.com.sol_do_amanhecer.repository.EnderecoRepository;
import br.com.sol_do_amanhecer.security.LoginDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração - AutenticacaoController")
class AutenticacaoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private VoluntarioRepository voluntarioRepository;

    @Autowired
    private PermissaoRepository permissaoRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private Usuario usuarioTeste;
    private LoginDTO loginDTO;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
        voluntarioRepository.deleteAll();
        permissaoRepository.deleteAll();

        Permissao permissao = permissaoRepository.save(new Permissao(null, "ROLE_USER"));

        Endereco endereco = enderecoRepository.save(Endereco.builder()
                .logradouro("Rua Login")
                .numero("10")
                .bairro("Centro")
                .cidade("Cidade Login")
                .estado("TS")
                .cep("00000000")
                .build());

        Voluntario voluntario = voluntarioRepository.save(Voluntario.builder()
                .nomeCompleto("Usuário Teste")
                .dataNascimento(LocalDate.of(1990, 1, 1))
                .endereco(endereco)
                .ativo(true)
                .build());

        usuarioTeste = usuarioRepository.save(Usuario.builder()
                .usuario("usuarioteste")
                .senha(passwordEncoder.encode("senha123"))
                .contaExpirada(false)
                .contaBloqueada(false)
                .credenciaisExpiradas(false)
                .ativo(true)
                .permissoes(new java.util.ArrayList<>(java.util.List.of(permissao)))
                .voluntario(voluntario)
                .build());

        loginDTO = new LoginDTO("usuarioteste", "senha123");
    }

    @Test
    @DisplayName("Deve fazer login com credenciais válidas")
    void testLoginComCredenciaisValidas() throws Exception {
        mockMvc.perform(post("/sol-do-amanhecer/api/autenticacao/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuario", equalTo("usuarioteste")))
                .andExpect(jsonPath("$.authenticated", equalTo(true)))
                .andExpect(jsonPath("$.accessToken", notNullValue()))
                .andExpect(jsonPath("$.refreshToken", notNullValue()));
    }

    @Test
    @DisplayName("Deve retornar 401 com credenciais inválidas")
    void testLoginComCredenciaisInvalidas() throws Exception {
        LoginDTO loginInvalido = new LoginDTO("usuarioteste", "senhaerrada");

        mockMvc.perform(post("/sol-do-amanhecer/api/autenticacao/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginInvalido)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve retornar 400 com usuário nulo")
    void testLoginComUsuarioNulo() throws Exception {
        LoginDTO loginNulo = new LoginDTO(null, "senha123");

        mockMvc.perform(post("/sol-do-amanhecer/api/autenticacao/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginNulo)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 400 com senha nula")
    void testLoginComSenhaNula() throws Exception {
        LoginDTO loginNulo = new LoginDTO("usuarioteste", null);

        mockMvc.perform(post("/sol-do-amanhecer/api/autenticacao/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginNulo)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 401 com usuário inexistente")
    void testLoginComUsuarioInexistente() throws Exception {
        LoginDTO loginInexistente = new LoginDTO("usuarioinexistente", "senha123");

        mockMvc.perform(post("/sol-do-amanhecer/api/autenticacao/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginInexistente)))
                .andExpect(status().isUnauthorized());
    }
}