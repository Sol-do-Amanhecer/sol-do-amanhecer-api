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
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração - VoluntarioController")
class VoluntarioControllerIntegrationTest {

    private static final int SINGLE_ENTITY = 1;

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

    private Endereco criarEndereco(String logradouro, String numero, String bairro,
                                   String cidade, String estado, String cep) {
        return enderecoRepository.save(Endereco.builder()
                .logradouro(logradouro)
                .numero(numero)
                .bairro(bairro)
                .cidade(cidade)
                .estado(estado)
                .cep(cep)
                .build());
    }

    private void salvarFormulario(Voluntario voluntario) {
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
    }

    private FormularioVoluntarioDTO buildFormularioDTO() {
        return FormularioVoluntarioDTO.builder()
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
                .build();
    }

    private VoluntarioRequestDTO criarRequestAtualizacao(String novoNome) {
        return VoluntarioRequestDTO.builder()
                .voluntarioDTO(VoluntarioDTO.builder()
                        .nomeCompleto(novoNome)
                        .dataNascimento(LocalDate.of(1990, 1, 1))
                        .enderecoDTO(enderecoDTO)
                        .ativo(true)
                        .build())
                .emailDTOList(List.of())
                .telefoneDTOList(List.of())
                .formularioDTO(buildFormularioDTO())
                .build();
    }

    @Test
    @DisplayName("Deve criar voluntário com todos os dados relacionados")
    public void devePersistirTodosDadosAoCriarVoluntario() throws Exception {
        mockMvc.perform(post("/sol-do-amanhecer/api/voluntario/criar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voluntarioRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeCompleto", equalTo("João Silva")))
                .andExpect(jsonPath("$.ativo", equalTo(false)));

        assertEquals(SINGLE_ENTITY, voluntarioRepository.findAll().size(), "O voluntário deve ter sido persistido");
        assertEquals(SINGLE_ENTITY, emailRepository.findAll().size(), "O e-mail deve ter sido persistido");
        assertEquals(SINGLE_ENTITY, telefoneRepository.findAll().size(), "O telefone deve ter sido persistido");
        assertEquals(SINGLE_ENTITY, formularioRepository.findAll().size(), "O formulário deve ter sido persistido");
    }

    @Test
    @DisplayName("Deve buscar voluntário por ID com todos os dados")
    public void deveBuscarVoluntarioPorIdComDadosRelacionados() throws Exception {
        Endereco endereco = criarEndereco("Rua Teste", "123", "Centro", "São Paulo", "SP", "01310100");

        Voluntario voluntario = voluntarioRepository.save(Voluntario.builder()
                .nomeCompleto("Maria Santos")
                .dataNascimento(LocalDate.of(1995, 5, 15))
                .endereco(endereco)
                .ativo(true)
                .build());

        emailRepository.save(Email.builder().email("maria@email.com").voluntario(voluntario).build());
        salvarFormulario(voluntario);

        mockMvc.perform(get("/sol-do-amanhecer/api/voluntario/" + voluntario.getUuid()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeCompleto", equalTo("Maria Santos")))
                .andExpect(jsonPath("$.emailDTOList", hasSize(1)))
                .andExpect(jsonPath("$.emailDTOList[0].email", equalTo("maria@email.com")));
    }

    @Test
    @DisplayName("Deve listar voluntários com paginação")
    public void deveListarVoluntariosPaginados() throws Exception {
        Endereco endereco1 = criarEndereco("Rua", "1", "Centro", "SP", "SP", "01310100");
        Endereco endereco2 = criarEndereco("Rua 2", "2", "Centro", "SP", "SP", "01310101");

        voluntarioRepository.save(Voluntario.builder()
                .nomeCompleto("Voluntário 1")
                .dataNascimento(LocalDate.now())
                .endereco(endereco1)
                .ativo(true)
                .aprovado(true)
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
    public void deveAtualizarVoluntarioExistente() throws Exception {
        Endereco endereco = criarEndereco("Rua Original", "1", "Centro", "SP", "SP", "01310100");

        Voluntario voluntario = voluntarioRepository.save(Voluntario.builder()
                .nomeCompleto("Nome Original")
                .dataNascimento(LocalDate.of(1990, 1, 1))
                .endereco(endereco)
                .ativo(true)
                .build());

        salvarFormulario(voluntario);

        VoluntarioRequestDTO requestAtualizado = criarRequestAtualizacao("Nome Atualizado");

        mockMvc.perform(put("/sol-do-amanhecer/api/voluntario/atualizar/" + voluntario.getUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestAtualizado)))
                .andExpect(status().isOk());

        Voluntario voluntarioVerificado = voluntarioRepository.findById(voluntario.getUuid()).orElseThrow();
        assertEquals("Nome Atualizado", voluntarioVerificado.getNomeCompleto(),
                "O nome do voluntário deve ter sido atualizado");
    }

    @Test
    @DisplayName("Deve deletar voluntário")
    public void deveDesativarVoluntarioPorId() throws Exception {
        Endereco endereco = criarEndereco("Rua", "1", "Centro", "SP", "SP", "01310100");

        Voluntario voluntario = voluntarioRepository.save(Voluntario.builder()
                .nomeCompleto("Para deletar")
                .dataNascimento(LocalDate.now())
                .endereco(endereco)
                .ativo(true)
                .build());

        mockMvc.perform(delete("/sol-do-amanhecer/api/voluntario/remover/" + voluntario.getUuid()))
                .andExpect(status().isNoContent());

        Voluntario voluntarioVerificado = voluntarioRepository.findById(voluntario.getUuid()).orElseThrow();
        assertFalse(voluntarioVerificado.getAtivo(), "O voluntário deve estar inativo após remoção");
    }

    @Test
    @DisplayName("Deve atualizar status de aprovação do voluntário")
    public void deveAprovarVoluntario() throws Exception {
        Endereco endereco = criarEndereco("Rua", "1", "Centro", "SP", "SP", "01310100");

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
        assertEquals(true, voluntarioVerificado.getAprovado(), "O voluntário deve estar aprovado");
        assertEquals(true, voluntarioVerificado.getAtivo(), "O voluntário deve estar ativo após aprovação");
    }

    @Test
    @DisplayName("Deve listar novos voluntários (status pendente)")
    public void deveListarVoluntariosPendentes() throws Exception {
        Endereco endereco1 = criarEndereco("Rua", "1", "Centro", "SP", "SP", "01310100");
        Endereco endereco2 = criarEndereco("Rua 2", "2", "Centro", "SP", "SP", "01310101");

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
