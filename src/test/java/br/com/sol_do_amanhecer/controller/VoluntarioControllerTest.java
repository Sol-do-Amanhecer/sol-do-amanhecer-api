package br.com.sol_do_amanhecer.controller;

import br.com.sol_do_amanhecer.model.dto.*;
import br.com.sol_do_amanhecer.service.VoluntarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class VoluntarioControllerTest {

    @Mock
    private VoluntarioService voluntarioService;

    @InjectMocks
    private VoluntarioController voluntarioController;

    private VoluntarioDTO voluntarioDTO;
    private VoluntarioRequestDTO voluntarioRequestDTO;
    private VoluntarioResponseDTO voluntarioResponseDTO;
    private UUID uuid;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        uuid = UUID.randomUUID();

        EnderecoDTO enderecoDTO = EnderecoDTO.builder()
                .logradouro("Rua Teste")
                .numero("123")
                .complemento("Apto 1")
                .bairro("Centro")
                .cidade("Cidade")
                .estado("SP")
                .cep("12345678")
                .build();

        voluntarioDTO = VoluntarioDTO.builder()
                .uuid(uuid)
                .nomeCompleto("João da Silva")
                .dataNascimento(LocalDate.of(1990, 1, 1))
                .enderecoDTO(enderecoDTO)
                .ativo(true)
                .build();

        EmailDTO emailDTO = EmailDTO.builder()
                .uuidVoluntario(uuid)
                .email("joao@email.com")
                .build();

        TelefoneDTO telefoneDTO = TelefoneDTO.builder()
                .uuidVoluntario(uuid)
                .ddd("11")
                .telefone("912345678")
                .build();

        FormularioVoluntarioDTO formularioDTO = FormularioVoluntarioDTO.builder()
                .uuidVoluntario(uuid)
                .comoConheceu("Internet")
                .motivoVoluntariado("Ajudar")
                .cienteTrabalhoVoluntario(true)
                .dedicacaoVoluntariado("Semanal")
                .disponibilidadeSemana("Todos os dias")
                .compromissoDivulgar(true)
                .compromissoAcao(true)
                .desejaCamisa(false)
                .sobreMim("Gosto de voluntariado")
                .dataResposta(LocalDateTime.now())
                .build();

        voluntarioRequestDTO = VoluntarioRequestDTO.builder()
                .voluntarioDTO(voluntarioDTO)
                .emailDTOList(List.of(emailDTO))
                .telefoneDTOList(List.of(telefoneDTO))
                .formularioDTO(formularioDTO)
                .build();

        voluntarioResponseDTO = VoluntarioResponseDTO.builder()
                .uuid(uuid)
                .nomeCompleto("João da Silva")
                .dataNascimento(LocalDate.of(1990, 1, 1))
                .enderecoDTO(enderecoDTO)
                .ativo(true)
                .emailDTOList(List.of(emailDTO))
                .telefoneDTOList(List.of(telefoneDTO))
                .formularioDTO(formularioDTO)
                .build();
    }

    @Test
    @DisplayName("Deve criar um novo voluntário com sucesso")
    void criarComSucesso() {
        when(voluntarioService.criar(any(), anyList(), anyList(), any())).thenReturn(voluntarioDTO);

        ResponseEntity<VoluntarioDTO> response = voluntarioController.criar(voluntarioRequestDTO);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(voluntarioDTO, response.getBody());
        verify(voluntarioService).criar(any(), anyList(), anyList(), any());
    }

    @Test
    @DisplayName("Deve buscar voluntário por ID com sucesso")
    void buscarPorIdComSucesso() {
        when(voluntarioService.buscarPorId(uuid)).thenReturn(voluntarioResponseDTO);

        ResponseEntity<VoluntarioResponseDTO> response = voluntarioController.buscarPorId(uuid);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(voluntarioResponseDTO, response.getBody());
        verify(voluntarioService).buscarPorId(uuid);
    }

    @Test
    @DisplayName("Deve retornar a lista de todos os voluntários")
    void buscarTodosComSucesso() {
        when(voluntarioService.buscarTodos()).thenReturn(List.of(voluntarioResponseDTO));

        ResponseEntity<List<VoluntarioResponseDTO>> response = voluntarioController.buscarTodos();

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
        assertEquals(voluntarioResponseDTO, response.getBody().get(0));
        verify(voluntarioService).buscarTodos();
    }

    @Test
    @DisplayName("Deve atualizar voluntário com sucesso")
    void atualizarComSucesso() {
        doNothing().when(voluntarioService).atualizar(
                eq(uuid),
                any(),
                anyList(),
                anyList(),
                any()
        );

        ResponseEntity<Void> response = voluntarioController.atualizar(uuid, voluntarioRequestDTO);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNull(response.getBody());
        verify(voluntarioService).atualizar(eq(uuid), any(), anyList(), anyList(), any());
    }

    @Test
    @DisplayName("Deve deletar voluntário com sucesso")
    void deletarComSucesso() {
        doNothing().when(voluntarioService).remover(uuid);

        ResponseEntity<Void> response = voluntarioController.deletar(uuid);

        assertNotNull(response);
        assertEquals(204, response.getStatusCode().value());
        assertNull(response.getBody());
        verify(voluntarioService).remover(uuid);
    }
}