package br.com.sol_do_amanhecer.controller;

import br.com.sol_do_amanhecer.model.dto.DoacaoDTO;
import br.com.sol_do_amanhecer.model.entity.Doacao;
import br.com.sol_do_amanhecer.repository.DoacaoRepository;
import br.com.sol_do_amanhecer.shared.enums.EMeioDoacao;
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

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração - DoacaoController")
class DoacaoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DoacaoRepository doacaoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private DoacaoDTO doacaoDTO;

    @BeforeEach
    void setUp() {
        doacaoRepository.deleteAll();

        doacaoDTO = DoacaoDTO.builder()
                .dataDoacao(LocalDate.now())
                .nomeDoador("João Silva")
                .meioDoacao(EMeioDoacao.PIX)
                .valor(new BigDecimal("100.00"))
                .build();
    }

    @Test
    @DisplayName("Deve criar uma doação e persistir no banco")
    void testCriarDoacaoIntegracao() throws Exception {
        mockMvc.perform(post("/sol-do-amanhecer/api/doacao/criar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(doacaoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeDoador", equalTo("João Silva")))
                .andExpect(jsonPath("$.meioDoacao", equalTo("PIX")))
                .andExpect(jsonPath("$.valor", equalTo(100.00)));

        assert doacaoRepository.findAll().size() == 1;
    }

    @Test
    @DisplayName("Deve buscar doação por ID")
    void testBuscarDoacaoPorIdIntegracao() throws Exception {
        Doacao doacaoCriada = doacaoRepository.save(Doacao.builder()
                .dataDoacao(LocalDate.now())
                .nomeDoador("Maria Santos")
                .meioDoacao(EMeioDoacao.TRANSFERENCIA)
                .valor(new BigDecimal("250.00"))
                .build());

        mockMvc.perform(get("/sol-do-amanhecer/api/doacao/" + doacaoCriada.getUuid()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeDoador", equalTo("Maria Santos")))
                .andExpect(jsonPath("$.valor", equalTo(250.00)));
    }

    @Test
    @DisplayName("Deve listar doações com paginação")
    void testListarDoacoesComPaginacao() throws Exception {
        doacaoRepository.save(Doacao.builder()
                .dataDoacao(LocalDate.now())
                .nomeDoador("Doador 1")
                .meioDoacao(EMeioDoacao.PIX)
                .valor(new BigDecimal("100.00"))
                .build());

        doacaoRepository.save(Doacao.builder()
                .dataDoacao(LocalDate.now())
                .nomeDoador("Doador 2")
                .meioDoacao(EMeioDoacao.BOLETO)
                .valor(new BigDecimal("200.00"))
                .build());

        mockMvc.perform(get("/sol-do-amanhecer/api/doacao/")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", equalTo(2)));
    }

    @Test
    @DisplayName("Deve filtrar doações por meio de doação")
    void testFiltrarDoacoesPorMeio() throws Exception {
        doacaoRepository.save(Doacao.builder()
                .dataDoacao(LocalDate.now())
                .nomeDoador("Doador PIX")
                .meioDoacao(EMeioDoacao.PIX)
                .valor(new BigDecimal("100.00"))
                .build());

        doacaoRepository.save(Doacao.builder()
                .dataDoacao(LocalDate.now())
                .nomeDoador("Doador Boleto")
                .meioDoacao(EMeioDoacao.BOLETO)
                .valor(new BigDecimal("200.00"))
                .build());

        mockMvc.perform(get("/sol-do-amanhecer/api/doacao/")
                        .param("page", "0")
                        .param("size", "10")
                        .param("meioDoacao", "PIX"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].meioDoacao", equalTo("PIX")));
    }

    @Test
    @DisplayName("Deve atualizar uma doação")
    void testAtualizarDoacaoIntegracao() throws Exception {
        Doacao doacaoCriada = doacaoRepository.save(Doacao.builder()
                .dataDoacao(LocalDate.now())
                .nomeDoador("Doador Original")
                .meioDoacao(EMeioDoacao.PIX)
                .valor(new BigDecimal("100.00"))
                .build());

        DoacaoDTO doacaoAtualizada = DoacaoDTO.builder()
                .dataDoacao(LocalDate.now())
                .nomeDoador("Doador Atualizado")
                .meioDoacao(EMeioDoacao.TRANSFERENCIA)
                .valor(new BigDecimal("500.00"))
                .build();

        mockMvc.perform(put("/sol-do-amanhecer/api/doacao/atualizar/" + doacaoCriada.getUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(doacaoAtualizada)))
                .andExpect(status().isOk());

        Doacao doacaoVerificada = doacaoRepository.findById(doacaoCriada.getUuid()).orElseThrow();
        assert doacaoVerificada.getNomeDoador().equals("Doador Atualizado");
    }

    @Test
    @DisplayName("Deve deletar uma doação")
    void testDeletarDoacaoIntegracao() throws Exception {
        Doacao doacaoCriada = doacaoRepository.save(Doacao.builder()
                .dataDoacao(LocalDate.now())
                .nomeDoador("Doador para deletar")
                .meioDoacao(EMeioDoacao.PIX)
                .valor(new BigDecimal("100.00"))
                .build());

        mockMvc.perform(delete("/sol-do-amanhecer/api/doacao/remover/" + doacaoCriada.getUuid()))
                .andExpect(status().isNoContent());

        assert doacaoRepository.findById(doacaoCriada.getUuid()).isEmpty();
    }
}