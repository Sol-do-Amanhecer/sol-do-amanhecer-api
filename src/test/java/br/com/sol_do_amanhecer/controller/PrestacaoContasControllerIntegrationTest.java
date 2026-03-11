package br.com.sol_do_amanhecer.controller;

import br.com.sol_do_amanhecer.model.dto.PrestacaoContasDTO;
import br.com.sol_do_amanhecer.model.entity.PrestacaoContas;
import br.com.sol_do_amanhecer.repository.PrestacaoContasRepository;
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
@DisplayName("Testes de Integração - PrestacaoContasController")
class PrestacaoContasControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PrestacaoContasRepository prestacaoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private PrestacaoContasDTO prestacaoDTO;

    @BeforeEach
    void setUp() {
        prestacaoRepository.deleteAll();

        prestacaoDTO = PrestacaoContasDTO.builder()
                .dataTransacao(LocalDate.now())
                .descricaoGasto("Compra de alimentos")
                .destinoGasto("Ação social")
                .valorPago(new BigDecimal("500.00"))
                .estabelecimento("Supermercado XYZ")
                .notaFiscal("NF123456")
                .build();
    }

    @Test
    @DisplayName("Deve criar uma prestação de contas")
    void testCriarPrestacaoIntegracao() throws Exception {
        mockMvc.perform(post("/sol-do-amanhecer/api/prestacao/criar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(prestacaoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricaoGasto", equalTo("Compra de alimentos")))
                .andExpect(jsonPath("$.valorPago", equalTo(500.00)));

        assert prestacaoRepository.findAll().size() == 1;
    }

    @Test
    @DisplayName("Deve buscar prestação por ID")
    void testBuscarPrestacaoPorIdIntegracao() throws Exception {
        PrestacaoContas prestacao = prestacaoRepository.save(PrestacaoContas.builder()
                .dataTransacao(LocalDate.now())
                .descricaoGasto("Gasto teste")
                .destinoGasto("Destino teste")
                .valorPago(new BigDecimal("250.00"))
                .estabelecimento("Estabelecimento teste")
                .build());

        mockMvc.perform(get("/sol-do-amanhecer/api/prestacao/" + prestacao.getUuid()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricaoGasto", equalTo("Gasto teste")))
                .andExpect(jsonPath("$.valorPago", equalTo(250.00)));
    }

    @Test
    @DisplayName("Deve listar prestações com paginação")
    void testListarPrestacoeIntegracao() throws Exception {
        prestacaoRepository.save(PrestacaoContas.builder()
                .dataTransacao(LocalDate.now())
                .descricaoGasto("Gasto 1")
                .destinoGasto("Destino 1")
                .valorPago(new BigDecimal("100.00"))
                .estabelecimento("Estabelecimento 1")
                .build());

        prestacaoRepository.save(PrestacaoContas.builder()
                .dataTransacao(LocalDate.now())
                .descricaoGasto("Gasto 2")
                .destinoGasto("Destino 2")
                .valorPago(new BigDecimal("200.00"))
                .estabelecimento("Estabelecimento 2")
                .build());

        mockMvc.perform(get("/sol-do-amanhecer/api/prestacao/")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));
    }

    @Test
    @DisplayName("Deve atualizar uma prestação")
    void testAtualizarPrestacaoIntegracao() throws Exception {
        PrestacaoContas prestacao = prestacaoRepository.save(PrestacaoContas.builder()
                .dataTransacao(LocalDate.now())
                .descricaoGasto("Gasto original")
                .destinoGasto("Destino original")
                .valorPago(new BigDecimal("100.00"))
                .estabelecimento("Estabelecimento original")
                .build());

        PrestacaoContasDTO prestacaoAtualizada = PrestacaoContasDTO.builder()
                .dataTransacao(LocalDate.now())
                .descricaoGasto("Gasto atualizado")
                .destinoGasto("Destino atualizado")
                .valorPago(new BigDecimal("500.00"))
                .estabelecimento("Estabelecimento atualizado")
                .build();

        mockMvc.perform(put("/sol-do-amanhecer/api/prestacao/atualizar/" + prestacao.getUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(prestacaoAtualizada)))
                .andExpect(status().isOk());

        PrestacaoContas prestacaoVerificada = prestacaoRepository.findById(prestacao.getUuid()).orElseThrow();
        assert prestacaoVerificada.getDescricaoGasto().equals("Gasto atualizado");
    }

    @Test
    @DisplayName("Deve deletar uma prestação")
    void testDeletarPrestacaoIntegracao() throws Exception {
        PrestacaoContas prestacao = prestacaoRepository.save(PrestacaoContas.builder()
                .dataTransacao(LocalDate.now())
                .descricaoGasto("Gasto para deletar")
                .destinoGasto("Destino")
                .valorPago(new BigDecimal("100.00"))
                .estabelecimento("Estabelecimento")
                .build());

        mockMvc.perform(delete("/sol-do-amanhecer/api/prestacao/remover/" + prestacao.getUuid()))
                .andExpect(status().isNoContent());

        assert prestacaoRepository.findById(prestacao.getUuid()).isEmpty();
    }
}