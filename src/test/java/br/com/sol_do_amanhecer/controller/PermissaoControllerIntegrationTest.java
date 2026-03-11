package br.com.sol_do_amanhecer.controller;

import br.com.sol_do_amanhecer.model.dto.PermissaoDTO;
import br.com.sol_do_amanhecer.model.entity.Permissao;
import br.com.sol_do_amanhecer.repository.PermissaoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração - PermissaoController")
class PermissaoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PermissaoRepository permissaoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        permissaoRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve criar uma permissão")
    void testCriarPermissaoIntegracao() throws Exception {
        PermissaoDTO permissaoDTO = PermissaoDTO.builder()
                .descricao("ROLE_ADMIN")
                .build();

        mockMvc.perform(post("/sol-do-amanhecer/api/permissao/criar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(permissaoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao", equalTo("ROLE_ADMIN")));

        assert permissaoRepository.findAll().size() == 1;
    }

    @Test
    @DisplayName("Deve buscar permissão por ID")
    void testBuscarPermissaoPorIdIntegracao() throws Exception {
        Permissao permissaoCriada = permissaoRepository.save(new Permissao(null, "ROLE_USER"));

        mockMvc.perform(get("/sol-do-amanhecer/api/permissao/" + permissaoCriada.getUuid()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao", equalTo("ROLE_USER")));
    }

    @Test
    @DisplayName("Deve listar todas as permissões")
    void testListarPermissoesIntegracao() throws Exception {
        permissaoRepository.save(new Permissao(null, "ROLE_ADMIN"));
        permissaoRepository.save(new Permissao(null, "ROLE_USER"));

        mockMvc.perform(get("/sol-do-amanhecer/api/permissao/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("Deve atualizar uma permissão")
    void testAtualizarPermissaoIntegracao() throws Exception {
        Permissao permissaoCriada = permissaoRepository.save(new Permissao(null, "ROLE_OLD"));

        PermissaoDTO permissaoAtualizada = PermissaoDTO.builder()
                .descricao("ROLE_NEW")
                .build();

        mockMvc.perform(put("/sol-do-amanhecer/api/permissao/atualizar/" + permissaoCriada.getUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(permissaoAtualizada)))
                .andExpect(status().isOk());

        Permissao permissaoVerificada = permissaoRepository.findById(permissaoCriada.getUuid()).orElseThrow();
        assert permissaoVerificada.getDescricao().equals("ROLE_NEW");
    }

    @Test
    @DisplayName("Deve deletar uma permissão")
    void testDeletarPermissaoIntegracao() throws Exception {
        Permissao permissaoCriada = permissaoRepository.save(new Permissao(null, "ROLE_DELETE"));

        mockMvc.perform(delete("/sol-do-amanhecer/api/permissao/remover/" + permissaoCriada.getUuid()))
                .andExpect(status().isNoContent());

        assert permissaoRepository.findById(permissaoCriada.getUuid()).isEmpty();
    }
}