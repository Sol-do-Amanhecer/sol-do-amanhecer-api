package br.com.sol_do_amanhecer.controller;

import br.com.sol_do_amanhecer.model.dto.AcaoDTO;
import br.com.sol_do_amanhecer.model.dto.AcaoRequestDTO;
import br.com.sol_do_amanhecer.model.dto.ImagemAcaoDTO;
import br.com.sol_do_amanhecer.model.entity.Acao;
import br.com.sol_do_amanhecer.repository.AcaoRepository;
import br.com.sol_do_amanhecer.shared.enums.ETipoAcao;
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

import java.time.LocalDate;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração - AcaoController")
class AcaoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AcaoRepository acaoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private AcaoDTO acaoDTO;
    private AcaoRequestDTO acaoRequestDTO;

    @BeforeEach
    void setUp() {
        acaoRepository.deleteAll();

        acaoDTO = AcaoDTO.builder()
                .nome("Ação de Caridade")
                .descricao("Distribuição de alimentos")
                .dataAcao(LocalDate.now())
                .localAcao("Praça Central")
                .tipo(ETipoAcao.SOCIAL_ALIMENTAR)
                .build();

        acaoRequestDTO = AcaoRequestDTO.builder()
                .acaoDTO(acaoDTO)
                .imagemDTOList(Collections.emptyList())
                .build();
    }

    @Test
    @DisplayName("Deve criar uma ação e persistir no banco de dados")
    void testCriarAcaoIntegracao() throws Exception {
        mockMvc.perform(post("/sol-do-amanhecer/api/acao/criar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(acaoRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", equalTo("Ação de Caridade")))
                .andExpect(jsonPath("$.tipo", equalTo("SOCIAL_ALIMENTAR")));

        assert acaoRepository.findAll().size() == 1;
    }

    @Test
    @DisplayName("Deve buscar ação por ID após criação")
    void testBuscarAcaoPorIdIntegracao() throws Exception {
        Acao acaoCriada = acaoRepository.save(Acao.builder()
                .nome("Ação Teste")
                .descricao("Descrição teste")
                .dataAcao(LocalDate.now())
                .localAcao("Local teste")
                .tipo(ETipoAcao.SAUDE)
                .build());

        mockMvc.perform(get("/sol-do-amanhecer/api/acao/" + acaoCriada.getUuid()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.acaoDTO.nome", equalTo("Ação Teste")))
                .andExpect(jsonPath("$.acaoDTO.tipo", equalTo("SAUDE")));
    }

    @Test
    @DisplayName("Deve listar todas as ações com paginação")
    void testListarAcoesComPaginacao() throws Exception {
        acaoRepository.save(Acao.builder()
                .nome("Ação 1")
                .descricao("Desc 1")
                .dataAcao(LocalDate.now())
                .localAcao("Local 1")
                .tipo(ETipoAcao.SOCIAL_ALIMENTAR)
                .build());

        acaoRepository.save(Acao.builder()
                .nome("Ação 2")
                .descricao("Desc 2")
                .dataAcao(LocalDate.now())
                .localAcao("Local 2")
                .tipo(ETipoAcao.IDOSO)
                .build());

        mockMvc.perform(get("/sol-do-amanhecer/api/acao/")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", equalTo(2)));
    }

    @Test
    @DisplayName("Deve filtrar ações por tipo")
    void testFiltrarAcoesPorTipo() throws Exception {
        acaoRepository.save(Acao.builder()
                .nome("Ação Social")
                .descricao("Desc")
                .dataAcao(LocalDate.now())
                .localAcao("Local")
                .tipo(ETipoAcao.SOCIAL_ALIMENTAR)
                .build());

        acaoRepository.save(Acao.builder()
                .nome("Ação Saúde")
                .descricao("Desc")
                .dataAcao(LocalDate.now())
                .localAcao("Local")
                .tipo(ETipoAcao.SAUDE)
                .build());

        mockMvc.perform(get("/sol-do-amanhecer/api/acao/")
                        .param("page", "0")
                        .param("size", "10")
                        .param("tipo", "SOCIAL_ALIMENTAR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].acaoDTO.tipo", equalTo("SOCIAL_ALIMENTAR")));
    }

    @Test
    @DisplayName("Deve atualizar uma ação existente")
    void testAtualizarAcaoIntegracao() throws Exception {
        Acao acaoCriada = acaoRepository.save(Acao.builder()
                .nome("Ação Original")
                .descricao("Desc original")
                .dataAcao(LocalDate.now())
                .localAcao("Local original")
                .tipo(ETipoAcao.SOCIAL_ALIMENTAR)
                .build());

        AcaoDTO acaoAtualizada = AcaoDTO.builder()
                .nome("Ação Atualizada")
                .descricao("Desc atualizada")
                .dataAcao(LocalDate.now())
                .localAcao("Local atualizado")
                .tipo(ETipoAcao.SAUDE)
                .build();

        AcaoRequestDTO requestAtualizado = AcaoRequestDTO.builder()
                .acaoDTO(acaoAtualizada)
                .imagemDTOList(Collections.emptyList())
                .build();

        mockMvc.perform(put("/sol-do-amanhecer/api/acao/atualizar/" + acaoCriada.getUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestAtualizado)))
                .andExpect(status().isOk());

        Acao acaoVerificada = acaoRepository.findById(acaoCriada.getUuid()).orElseThrow();
        assert acaoVerificada.getNome().equals("Ação Atualizada");
    }

    @Test
    @DisplayName("Deve deletar uma ação existente")
    void testDeletarAcaoIntegracao() throws Exception {
        Acao acaoCriada = acaoRepository.save(Acao.builder()
                .nome("Ação para deletar")
                .descricao("Desc")
                .dataAcao(LocalDate.now())
                .localAcao("Local")
                .tipo(ETipoAcao.SOCIAL_ALIMENTAR)
                .build());

        mockMvc.perform(delete("/sol-do-amanhecer/api/acao/remover/" + acaoCriada.getUuid()))
                .andExpect(status().isNoContent());

        assert acaoRepository.findById(acaoCriada.getUuid()).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar ação inexistente")
    void testBuscarAcaoInexistenteRetorna404() throws Exception {
        mockMvc.perform(get("/sol-do-amanhecer/api/acao/00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound());
    }
}