package br.com.sol_do_amanhecer.controller;

import br.com.sol_do_amanhecer.model.dto.*;
import br.com.sol_do_amanhecer.model.entity.*;
import br.com.sol_do_amanhecer.repository.*;
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
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração - VoluntarioController")
class VoluntarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VoluntarioRepository voluntarioRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private TelefoneRepository telefoneRepository;

    @Autowired
    private FormularioVoluntarioRepository formularioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private VoluntarioRequestDTO voluntarioRequestDTO;
    private EnderecoDTO enderecoDTO;

    @BeforeEach
    void setUp() {
        voluntarioRepository.deleteAll();
        enderecoRepository.deleteAll();
        emailRepository.deleteAll();
        telefoneRepository.deleteAll();
        formularioRepository.deleteAll();

        enderecoDTO = EnderecoDTO.builder()
                .logradouro("Rua Teste")
                .numero("123")
                .bairro("Centro")
                .cidade("São Paulo")
                .estado("SP")
                .cep("01310100")
                .build();

        VoluntarioDTO voluntarioDTO = VoluntarioDTO.builder()
                .nomeCompleto("João Silva")
                .dataNascimento(LocalDate.of(1990, 1, 1))
                .enderecoDTO(enderecoDTO)
                .ativo(false)
                .build();

        EmailDTO emailDTO = EmailDTO.builder()
                .email("joao@email.com")
                .build();

        TelefoneDTO telefoneDTO = TelefoneDTO.builder()
                .ddd("11")
                .telefone("999999999")
                .build();

        FormularioVoluntarioDTO formularioDTO = FormularioVoluntarioDTO.builder()
                .comoConheceu("Internet")
                .motivoVoluntariado("Ajudar")
                .cienteTrabalhoVoluntario(true)
                .dedicacaoVoluntariado(true)
                .disponibilidadeSemana("Fins de semana")
                .compromissoDivulgar(true)
                .compromissoAcao(true)
                .desejaCamisa(false)
                .sobreMim("Sou dedicado")
                .dataResposta(LocalDateTime.now())
                .build();

        voluntarioRequestDTO = VoluntarioRequestDTO.builder()
                .voluntarioDTO(voluntarioDTO)
                .emailDTOList(List.of(emailDTO))
                .telefoneDTOList(List.of(telefoneDTO))
                .formularioDTO(formularioDTO)
                .build();
    }

    @Test
    @DisplayName("Deve criar voluntário com todos os dados relacionados")
    void testCriarVoluntarioIntegracao() throws Exception {
        mockMvc.perform(post("/sol-do-amanhecer/api/voluntario/criar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voluntarioRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeCompleto", equalTo("João Silva")))
                .andExpect(jsonPath("$.ativo", equalTo(false)));

        assert voluntarioRepository.findAll().size() == 1;
        assert emailRepository.findAll().size() == 1;
        assert telefoneRepository.findAll().size() == 1;
        assert formularioRepository.findAll().size() == 1;
    }

    @Test
    @DisplayName("Deve buscar voluntário por ID com todos os dados")
    void testBuscarVoluntarioPorIdIntegracao() throws Exception {
        Endereco endereco = enderecoRepository.save(Endereco.builder()
                .logradouro("Rua Teste")
                .numero("123")
                .bairro("Centro")
                .cidade("São Paulo")
                .estado("SP")
                .cep("01310100")
                .build());

        Voluntario voluntario = voluntarioRepository.save(Voluntario.builder()
                .nomeCompleto("Maria Santos")
                .dataNascimento(LocalDate.of(1995, 5, 15))
                .endereco(endereco)
                .ativo(true)
                .build());

        emailRepository.save(Email.builder()
                .email("maria@email.com")
                .voluntario(voluntario)
                .build());

        formularioRepository.save(FormularioVoluntario.builder()
                .voluntario(voluntario)
                .comoConheceu("Internet")
                .motivoVoluntariado("Ajudar")
                .cienteTrabalhoVoluntario(true)
                .dedicacaoVoluntariado(true)
                .disponibilidadeSemana("Fins de semana")
                .compromissoDivulgar(true)
                .compromissoAcao(true)
                .desejaCamisa(false)
                .sobreMim("Dedicado")
                .dataResposta(LocalDateTime.now())
                .build());

        mockMvc.perform(get("/sol-do-amanhecer/api/voluntario/" + voluntario.getUuid()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeCompleto", equalTo("Maria Santos")))
                .andExpect(jsonPath("$.emailDTOList", hasSize(1)))
                .andExpect(jsonPath("$.emailDTOList[0].email", equalTo("maria@email.com")));
    }

    @Test
    @DisplayName("Deve listar voluntários com paginação")
    void testListarVoluntariosComPaginacao() throws Exception {
        Endereco endereco = enderecoRepository.save(Endereco.builder()
                .logradouro("Rua")
                .numero("1")
                .bairro("Centro")
                .cidade("SP")
                .estado("SP")
                .cep("01310100")
                .build());

        voluntarioRepository.save(Voluntario.builder()
                .nomeCompleto("Voluntário 1")
                .dataNascimento(LocalDate.now())
                .endereco(endereco)
                .ativo(true)
                .aprovado(true)
                .build());

        Endereco endereco2 = enderecoRepository.save(Endereco.builder()
                .logradouro("Rua 2")
                .numero("2")
                .bairro("Centro")
                .cidade("SP")
                .estado("SP")
                .cep("01310101")
                .build());

        voluntarioRepository.save(Voluntario.builder()
                .nomeCompleto("Voluntário 2")
                .dataNascimento(LocalDate.now())
                .endereco(endereco2)
                .ativo(true)
                .aprovado(true)
                .build());

        mockMvc.perform(get("/sol-do-amanhecer/api/voluntario/")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));
    }

    @Test
    @DisplayName("Deve atualizar voluntário")
    void testAtualizarVoluntarioIntegracao() throws Exception {
        Endereco endereco = enderecoRepository.save(Endereco.builder()
                .logradouro("Rua Original")
                .numero("1")
                .bairro("Centro")
                .cidade("SP")
                .estado("SP")
                .cep("01310100")
                .build());

        Voluntario voluntario = voluntarioRepository.save(Voluntario.builder()
                .nomeCompleto("Nome Original")
                .dataNascimento(LocalDate.of(1990, 1, 1))
                .endereco(endereco)
                .ativo(true)
                .build());

        formularioRepository.save(FormularioVoluntario.builder()
                .voluntario(voluntario)
                .comoConheceu("Internet")
                .motivoVoluntariado("Ajudar")
                .cienteTrabalhoVoluntario(true)
                .dedicacaoVoluntariado(true)
                .disponibilidadeSemana("Fins de semana")
                .compromissoDivulgar(true)
                .compromissoAcao(true)
                .desejaCamisa(false)
                .sobreMim("Dedicado")
                .dataResposta(LocalDateTime.now())
                .build());

        VoluntarioDTO voluntarioAtualizado = VoluntarioDTO.builder()
                .nomeCompleto("Nome Atualizado")
                .dataNascimento(LocalDate.of(1990, 1, 1))
                .enderecoDTO(enderecoDTO)
                .ativo(true)
                .build();

        VoluntarioRequestDTO requestAtualizado = VoluntarioRequestDTO.builder()
                .voluntarioDTO(voluntarioAtualizado)
                .emailDTOList(List.of())
                .telefoneDTOList(List.of())
                .formularioDTO(FormularioVoluntarioDTO.builder()
                        .comoConheceu("Internet")
                        .motivoVoluntariado("Ajudar")
                        .cienteTrabalhoVoluntario(true)
                        .dedicacaoVoluntariado(true)
                        .disponibilidadeSemana("Fins de semana")
                        .compromissoDivulgar(true)
                        .compromissoAcao(true)
                        .desejaCamisa(false)
                        .sobreMim("Dedicado")
                        .dataResposta(LocalDateTime.now())
                        .build())
                .build();

        mockMvc.perform(put("/sol-do-amanhecer/api/voluntario/atualizar/" + voluntario.getUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestAtualizado)))
                .andExpect(status().isOk());

        Voluntario voluntarioVerificado = voluntarioRepository.findById(voluntario.getUuid()).orElseThrow();
        assert voluntarioVerificado.getNomeCompleto().equals("Nome Atualizado");
    }

    @Test
    @DisplayName("Deve deletar voluntário")
    void testDeletarVoluntarioIntegracao() throws Exception {
        Endereco endereco = enderecoRepository.save(Endereco.builder()
                .logradouro("Rua")
                .numero("1")
                .bairro("Centro")
                .cidade("SP")
                .estado("SP")
                .cep("01310100")
                .build());

        Voluntario voluntario = voluntarioRepository.save(Voluntario.builder()
                .nomeCompleto("Para deletar")
                .dataNascimento(LocalDate.now())
                .endereco(endereco)
                .ativo(true)
                .build());

        mockMvc.perform(delete("/sol-do-amanhecer/api/voluntario/remover/" + voluntario.getUuid()))
                .andExpect(status().isNoContent());

        Voluntario voluntarioVerificado = voluntarioRepository.findById(voluntario.getUuid()).orElseThrow();
        assert !voluntarioVerificado.getAtivo();
    }

    @Test
    @DisplayName("Deve atualizar status de aprovação do voluntário")
    void testAtualizarStatusAprovacaoIntegracao() throws Exception {
        Endereco endereco = enderecoRepository.save(Endereco.builder()
                .logradouro("Rua")
                .numero("1")
                .bairro("Centro")
                .cidade("SP")
                .estado("SP")
                .cep("01310100")
                .build());

        Voluntario voluntario = voluntarioRepository.save(Voluntario.builder()
                .nomeCompleto("Voluntário Pendente")
                .dataNascimento(LocalDate.now())
                .endereco(endereco)
                .ativo(false)
                .aprovado(null)
                .build());

        emailRepository.save(Email.builder()
                .email("voluntario@email.com")
                .voluntario(voluntario)
                .build());

        VoluntarioAtualizarStatusAprovacaoDTO statusDTO = VoluntarioAtualizarStatusAprovacaoDTO.builder()
                .aprovado(true)
                .build();

        mockMvc.perform(patch("/sol-do-amanhecer/api/voluntario/" + voluntario.getUuid() + "/status-aprovacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusDTO)))
                .andExpect(status().isOk());

        Voluntario voluntarioVerificado = voluntarioRepository.findById(voluntario.getUuid()).orElseThrow();
        assert voluntarioVerificado.getAprovado().equals(true);
        assert voluntarioVerificado.getAtivo().equals(true);
    }

    @Test
    @DisplayName("Deve listar novos voluntários (status pendente)")
    void testListarNovosVoluntariosIntegracao() throws Exception {
        Endereco endereco1 = enderecoRepository.save(Endereco.builder()
                .logradouro("Rua")
                .numero("1")
                .bairro("Centro")
                .cidade("SP")
                .estado("SP")
                .cep("01310100")
                .build());

        Endereco endereco2 = enderecoRepository.save(Endereco.builder()
                .logradouro("Rua 2")
                .numero("2")
                .bairro("Centro")
                .cidade("SP")
                .estado("SP")
                .cep("01310101")
                .build());

        voluntarioRepository.save(Voluntario.builder()
                .nomeCompleto("Novo Voluntário 1")
                .dataNascimento(LocalDate.now())
                .endereco(endereco1)
                .ativo(false)
                .aprovado(null)
                .build());

        voluntarioRepository.save(Voluntario.builder()
                .nomeCompleto("Novo Voluntário 2")
                .dataNascimento(LocalDate.now())
                .endereco(endereco2)
                .ativo(false)
                .aprovado(null)
                .build());

        mockMvc.perform(get("/sol-do-amanhecer/api/voluntario/novos")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));
    }
}