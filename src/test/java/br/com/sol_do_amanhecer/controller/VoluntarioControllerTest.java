package br.com.sol_do_amanhecer.controller;

import br.com.sol_do_amanhecer.model.dto.*;
import br.com.sol_do_amanhecer.service.VoluntarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
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
    private VoluntarioAtualizarStatusAprovacaoDTO statusAprovacaoDTO;
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
                .dedicacaoVoluntariado(true)
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

        statusAprovacaoDTO = VoluntarioAtualizarStatusAprovacaoDTO.builder()
                .aprovado(true)
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
        verify(voluntarioService).criar(
                voluntarioRequestDTO.getVoluntarioDTO(),
                voluntarioRequestDTO.getEmailDTOList(),
                voluntarioRequestDTO.getTelefoneDTOList(),
                voluntarioRequestDTO.getFormularioDTO()
        );
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
    @DisplayName("Deve retornar a lista paginada de voluntários com filtro ativo true")
    void buscarTodosComFiltroAtivoTrue() {
        Page<VoluntarioResponseDTO> page = new PageImpl<>(List.of(voluntarioResponseDTO));
        when(voluntarioService.buscarTodos(eq(true), any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<VoluntarioResponseDTO>> response =
                voluntarioController.buscarTodos(0, 10, true);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());

        Page<VoluntarioResponseDTO> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(1, responseBody.getTotalElements());
        assertEquals(voluntarioResponseDTO, responseBody.getContent().get(0));

        verify(voluntarioService).buscarTodos(eq(true), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve retornar a lista paginada de voluntários com filtro ativo false")
    void buscarTodosComFiltroAtivoFalse() {
        Page<VoluntarioResponseDTO> page = new PageImpl<>(List.of(voluntarioResponseDTO));
        when(voluntarioService.buscarTodos(eq(false), any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<VoluntarioResponseDTO>> response =
                voluntarioController.buscarTodos(0, 10, false);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());

        Page<VoluntarioResponseDTO> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(1, responseBody.getTotalElements());
        assertEquals(voluntarioResponseDTO, responseBody.getContent().get(0));

        verify(voluntarioService).buscarTodos(eq(false), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve retornar a lista paginada de todos os voluntários sem filtro")
    void buscarTodosSemFiltro() {
        // Criar o Pageable exatamente como o controller faz
        Pageable pageableEsperado = PageRequest.of(0, 10, Sort.by("criadoEm").ascending());
        Page<VoluntarioResponseDTO> page = new PageImpl<>(List.of(voluntarioResponseDTO), pageableEsperado, 1);

        // Mock mais específico
        when(voluntarioService.buscarTodos(isNull(), eq(pageableEsperado))).thenReturn(page);

        ResponseEntity<Page<VoluntarioResponseDTO>> response =
                voluntarioController.buscarTodos(0, 10, null);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());

        Page<VoluntarioResponseDTO> responseBody = response.getBody();
        assertNotNull(responseBody, "Response body should not be null");
        assertEquals(1, responseBody.getTotalElements());
        assertEquals(1, responseBody.getContent().size());
        assertEquals(voluntarioResponseDTO, responseBody.getContent().get(0));

        verify(voluntarioService).buscarTodos(isNull(), eq(pageableEsperado));
    }

    @Test
    @DisplayName("Deve buscar todos os voluntários com parâmetros de paginação customizados")
    void buscarTodosComPaginacaoCustomizada() {
        Pageable pageableEsperado = PageRequest.of(2, 5, Sort.by("criadoEm").ascending());

        Page<VoluntarioResponseDTO> page = new PageImpl<>(
                List.of(voluntarioResponseDTO),
                pageableEsperado,
                11
        );

        when(voluntarioService.buscarTodos(true, pageableEsperado)).thenReturn(page);

        ResponseEntity<Page<VoluntarioResponseDTO>> response =
                voluntarioController.buscarTodos(2, 5, true);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(11, Objects.requireNonNull(response.getBody()).getTotalElements());
        assertEquals(1, response.getBody().getContent().size());
        verify(voluntarioService).buscarTodos(true, pageableEsperado);
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
        verify(voluntarioService).atualizar(
                uuid,
                voluntarioRequestDTO.getVoluntarioDTO(),
                voluntarioRequestDTO.getEmailDTOList(),
                voluntarioRequestDTO.getTelefoneDTOList(),
                voluntarioRequestDTO.getFormularioDTO()
        );
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

    @Test
    @DisplayName("Deve atualizar status de aprovação para aprovado com sucesso")
    void atualizarStatusAprovacaoParaAprovadoComSucesso() {
        doNothing().when(voluntarioService).atualizarStatusAprovacao(uuid, true);

        ResponseEntity<Void> response = voluntarioController.atualizarStatusAprovacao(uuid, statusAprovacaoDTO);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNull(response.getBody());
        verify(voluntarioService).atualizarStatusAprovacao(uuid, true);
    }

    @Test
    @DisplayName("Deve atualizar status de aprovação para rejeitado com sucesso")
    void atualizarStatusAprovacaoParaRejeitadoComSucesso() {
        VoluntarioAtualizarStatusAprovacaoDTO statusRejeitadoDTO = VoluntarioAtualizarStatusAprovacaoDTO.builder()
                .aprovado(false)
                .build();

        doNothing().when(voluntarioService).atualizarStatusAprovacao(uuid, false);

        ResponseEntity<Void> response = voluntarioController.atualizarStatusAprovacao(uuid, statusRejeitadoDTO);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNull(response.getBody());
        verify(voluntarioService).atualizarStatusAprovacao(uuid, false);
    }

    @Test
    @DisplayName("Deve buscar novos voluntários com sucesso")
    void buscarNovosVoluntariosComSucesso() {
        Pageable pageableEsperado = PageRequest.of(0, 10, Sort.by("criadoEm").ascending());
        Page<VoluntarioResponseDTO> page = new PageImpl<>(List.of(voluntarioResponseDTO), pageableEsperado, 1);

        when(voluntarioService.buscarNovos(eq(pageableEsperado))).thenReturn(page);

        ResponseEntity<Page<VoluntarioResponseDTO>> response =
                voluntarioController.buscarNovosVoluntarios(0, 10);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());

        Page<VoluntarioResponseDTO> responseBody = response.getBody();
        assertNotNull(responseBody, "Response body should not be null");
        assertEquals(1, responseBody.getTotalElements());
        assertEquals(1, responseBody.getContent().size());
        assertEquals(voluntarioResponseDTO, responseBody.getContent().get(0));

        verify(voluntarioService).buscarNovos(eq(pageableEsperado));
    }

    @Test
    @DisplayName("Deve buscar novos voluntários com paginação customizada")
    void buscarNovosVoluntariosComPaginacaoCustomizada() {
        Page<VoluntarioResponseDTO> page = new PageImpl<>(List.of(voluntarioResponseDTO));
        when(voluntarioService.buscarNovos(any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<VoluntarioResponseDTO>> response =
                voluntarioController.buscarNovosVoluntarios(1, 5);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());

        Page<VoluntarioResponseDTO> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(1, responseBody.getTotalElements());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(voluntarioService).buscarNovos(pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();
        assertEquals(1, capturedPageable.getPageNumber());
        assertEquals(5, capturedPageable.getPageSize());
    }

    @Test
    @DisplayName("Deve buscar novos voluntários com lista vazia")
    void buscarNovosVoluntariosListaVazia() {
        Page<VoluntarioResponseDTO> pageVazia = new PageImpl<>(Collections.emptyList());
        when(voluntarioService.buscarNovos(any(Pageable.class))).thenReturn(pageVazia);

        ResponseEntity<Page<VoluntarioResponseDTO>> response =
                voluntarioController.buscarNovosVoluntarios(0, 10);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());

        Page<VoluntarioResponseDTO> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(0, responseBody.getTotalElements());
        assertTrue(responseBody.getContent().isEmpty());

        verify(voluntarioService).buscarNovos(any(Pageable.class));
    }
}