package br.com.sol_do_amanhecer.controller;

import br.com.sol_do_amanhecer.model.dto.ObjetivoMensalDTO;
import br.com.sol_do_amanhecer.model.dto.ObjetivoMensalRequestDTO;
import br.com.sol_do_amanhecer.model.entity.ObjetivoMensal;
import br.com.sol_do_amanhecer.repository.ObjetivoMensalRepository;
import br.com.sol_do_amanhecer.shared.enums.EMes;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração - ObjetivoMensalController")
class ObjetivoMensalControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjetivoMensalRepository objetivoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objetivoRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve criar um objetivo mensal")
    void testCriarObjetivoIntegracao() throws Exception {
        ObjetivoMensalRequestDTO requestDTO = ObjetivoMensalRequestDTO.builder()
                .titulo("Objetivo Janeiro")
                .descricao("Arrecadar fundos para ações sociais")
                .mes(EMes.JANEIRO)
                .ano(2026)
                .objetivoArrecadacao(new BigDecimal("5000.00"))
                .build();

        mockMvc.perform(post("/sol-do-amanhecer/api/objetivo/criar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo", equalTo("Objetivo Janeiro")))
                .andExpect(jsonPath("$.mes", equalTo("JANEIRO")))
                .andExpect(jsonPath("$.ano", equalTo(2026)));

        assert objetivoRepository.findAll().size() == 1;
    }

    @Test
    @DisplayName("Deve buscar objetivo por ID")
    void testBuscarObjetivoPorIdIntegracao() throws Exception {
        ObjetivoMensal objetivo = objetivoRepository.save(ObjetivoMensal.builder()
                .titulo("Objetivo Teste")
                .descricao("Descrição teste")
                .mes(EMes.FEVEREIRO)
                .ano(2026)
                .objetivoArrecadacao(new BigDecimal("3000.00"))
                .build());

        mockMvc.perform(get("/sol-do-amanhecer/api/objetivo/" + objetivo.getUuid()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo", equalTo("Objetivo Teste")))
                .andExpect(jsonPath("$.mes", equalTo("FEVEREIRO")));
    }

    @Test
    @DisplayName("Deve listar objetivos com paginação")
    void testListarObjetivosIntegracao() throws Exception {
        objetivoRepository.save(ObjetivoMensal.builder()
                .titulo("Objetivo 1")
                .descricao("Desc 1")
                .mes(EMes.JANEIRO)
                .ano(2026)
                .objetivoArrecadacao(new BigDecimal("1000.00"))
                .build());

        objetivoRepository.save(ObjetivoMensal.builder()
                .titulo("Objetivo 2")
                .descricao("Desc 2")
                .mes(EMes.FEVEREIRO)
                .ano(2026)
                .objetivoArrecadacao(new BigDecimal("2000.00"))
                .build());

        mockMvc.perform(get("/sol-do-amanhecer/api/objetivo/")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));
    }

    @Test
    @DisplayName("Deve atualizar um objetivo")
    void testAtualizarObjetivoIntegracao() throws Exception {
        ObjetivoMensal objetivo = objetivoRepository.save(ObjetivoMensal.builder()
                .titulo("Objetivo Original")
                .descricao("Desc original")
                .mes(EMes.MARCO)
                .ano(2026)
                .objetivoArrecadacao(new BigDecimal("1000.00"))
                .build());

        ObjetivoMensalRequestDTO requestAtualizado = ObjetivoMensalRequestDTO.builder()
                .titulo("Objetivo Atualizado")
                .descricao("Desc atualizada")
                .mes(EMes.MARCO)
                .ano(2026)
                .objetivoArrecadacao(new BigDecimal("5000.00"))
                .build();

        mockMvc.perform(put("/sol-do-amanhecer/api/objetivo/atualizar/" + objetivo.getUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestAtualizado)))
                .andExpect(status().isOk());

        ObjetivoMensal objetivoVerificado = objetivoRepository.findById(objetivo.getUuid()).orElseThrow();
        assert objetivoVerificado.getTitulo().equals("Objetivo Atualizado");
    }

    @Test
    @DisplayName("Deve deletar um objetivo")
    void testDeletarObjetivoIntegracao() throws Exception {
        ObjetivoMensal objetivo = objetivoRepository.save(ObjetivoMensal.builder()
                .titulo("Objetivo para deletar")
                .descricao("Desc")
                .mes(EMes.ABRIL)
                .ano(2026)
                .objetivoArrecadacao(new BigDecimal("1000.00"))
                .build());

        mockMvc.perform(delete("/sol-do-amanhecer/api/objetivo/remover/" + objetivo.getUuid()))
                .andExpect(status().isNoContent());

        assert objetivoRepository.findById(objetivo.getUuid()).isEmpty();
    }
}