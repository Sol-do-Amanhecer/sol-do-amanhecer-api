package br.com.sol_do_amanhecer.controller;

import br.com.sol_do_amanhecer.model.dto.PermissaoDTO;
import br.com.sol_do_amanhecer.model.dto.UsuarioDTO;
import br.com.sol_do_amanhecer.model.entity.Permissao;
import br.com.sol_do_amanhecer.model.entity.Usuario;
import br.com.sol_do_amanhecer.model.entity.Voluntario;
import br.com.sol_do_amanhecer.model.entity.Endereco;
import br.com.sol_do_amanhecer.repository.PermissaoRepository;
import br.com.sol_do_amanhecer.repository.UsuarioRepository;
import br.com.sol_do_amanhecer.repository.VoluntarioRepository;
import br.com.sol_do_amanhecer.repository.EnderecoRepository;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração - UsuarioController")
class UsuarioControllerIntegrationTest {

    private static final int SINGLE_ENTITY = 1;

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

    private UsuarioDTO usuarioDTO;
    private Voluntario voluntario;
    private Permissao permissao;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
        voluntarioRepository.deleteAll();
        permissaoRepository.deleteAll();

        permissao = permissaoRepository.save(new Permissao(null, "ROLE_USER"));

        Endereco endereco = enderecoRepository.save(Endereco.builder()
                .logradouro("Rua Teste")
                .numero("123")
                .bairro("Centro")
                .cidade("TesteCity")
                .estado("TS")
                .cep("12345678")
                .build());

        voluntario = voluntarioRepository.save(Voluntario.builder()
                .nomeCompleto("Voluntário Teste")
                .dataNascimento(LocalDate.of(1990, 1, 1))
                .endereco(endereco)
                .ativo(true)
                .build());

        usuarioDTO = UsuarioDTO.builder()
                .usuario("usuarioteste")
                .senha("Senha-123")
                .contaExpirada(false)
                .contaBloqueada(false)
                .credenciaisExpiradas(false)
                .ativo(true)
                .permissaoDTOList(List.of(new PermissaoDTO(permissao.getUuid(), "ROLE_USER")))
                .uuidVoluntario(voluntario.getUuid())
                .build();
    }

    private Usuario criarUsuario(String nomeUsuario, Voluntario voluntarioAssociado) {
        return usuarioRepository.save(Usuario.builder()
                .usuario(nomeUsuario)
                .senha(passwordEncoder.encode("senha123"))
                .contaExpirada(false)
                .contaBloqueada(false)
                .credenciaisExpiradas(false)
                .ativo(true)
                .permissoes(new java.util.ArrayList<>(List.of(permissao)))
                .voluntario(voluntarioAssociado)
                .build());
    }

    @Test
    @DisplayName("Deve criar um novo usuário")
    public void devePersistirUsuarioAoCriar() throws Exception {
        mockMvc.perform(post("/sol-do-amanhecer/api/usuario/criar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuario", equalTo("usuarioteste")))
                .andExpect(jsonPath("$.ativo", equalTo(true)));

        assertEquals(SINGLE_ENTITY, usuarioRepository.findAll().size(), "O usuário deve ter sido persistido no banco");
    }

    @Test
    @DisplayName("Deve buscar usuário por ID")
    public void deveBuscarUsuarioPorId() throws Exception {
        Usuario usuarioCriado = criarUsuario("usuario1", voluntario);

        mockMvc.perform(get("/sol-do-amanhecer/api/usuario/" + usuarioCriado.getUuid()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuario", equalTo("usuario1")))
                .andExpect(jsonPath("$.ativo", equalTo(true)));
    }

    @Test
    @DisplayName("Deve listar usuários com paginação")
    public void deveListarUsuariosPaginados() throws Exception {
        criarUsuario("usuario1", voluntario);

        Endereco endereco2 = enderecoRepository.save(Endereco.builder()
                .logradouro("Rua Teste 2")
                .numero("456")
                .bairro("Bairro 2")
                .cidade("TesteCity2")
                .estado("TS")
                .cep("87654321")
                .build());

        Voluntario voluntario2 = voluntarioRepository.save(Voluntario.builder()
                .nomeCompleto("Voluntário Teste 2")
                .dataNascimento(LocalDate.of(1992, 2, 2))
                .endereco(endereco2)
                .ativo(true)
                .build());

        criarUsuario("usuario2", voluntario2);

        mockMvc.perform(get("/sol-do-amanhecer/api/usuario/")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));
    }

    @Test
    @DisplayName("Deve atualizar um usuário")
    public void deveAtualizarUsuarioExistente() throws Exception {
        Usuario usuarioCriado = criarUsuario("usuariooriginal", voluntario);

        UsuarioDTO usuarioAtualizado = UsuarioDTO.builder()
                .usuario("usuarioatualizado")
                .senha("novaSenha123")
                .contaExpirada(false)
                .contaBloqueada(false)
                .credenciaisExpiradas(false)
                .ativo(true)
                .permissaoDTOList(List.of(new PermissaoDTO(permissao.getUuid(), "ROLE_USER")))
                .uuidVoluntario(voluntario.getUuid())
                .build();

        mockMvc.perform(put("/sol-do-amanhecer/api/usuario/atualizar/" + usuarioCriado.getUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioAtualizado)))
                .andExpect(status().isOk());

        Usuario usuarioVerificado = usuarioRepository.findById(usuarioCriado.getUuid()).orElseThrow();
        assertEquals("usuarioatualizado", usuarioVerificado.getUsuario(),
                "O nome de usuário deve ter sido atualizado");
    }

    @Test
    @DisplayName("Deve deletar um usuário")
    public void deveDesativarUsuarioPorId() throws Exception {
        Usuario usuarioCriado = criarUsuario("usuariodeletar", voluntario);

        mockMvc.perform(delete("/sol-do-amanhecer/api/usuario/remover/" + usuarioCriado.getUuid()))
                .andExpect(status().isNoContent());

        Usuario usuarioVerificado = usuarioRepository.findById(usuarioCriado.getUuid()).orElseThrow();
        assertFalse(usuarioVerificado.getAtivo(), "O usuário deve estar inativo após remoção");
    }
}
