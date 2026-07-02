package br.com.sol_do_amanhecer.controller;

import br.com.sol_do_amanhecer.model.dto.*;
import br.com.sol_do_amanhecer.service.VoluntarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de VoluntarioController")
public class VoluntarioControllerTest {

    private static final long ONE_ELEMENT = 1L;
    private static final int FIRST_ELEMENT_INDEX = 0;

    @Mock
    private VoluntarioService voluntarioService;

    @InjectMocks
    private VoluntarioController voluntarioController;

    private UUID uuid;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();
    }

    private VoluntarioRequestDTO buildVoluntarioRequestDTO() {
        EnderecoDTO enderecoDTO = EnderecoDTO.builder()
                .logradouro("Rua Teste")
                .numero("123")
                .complemento("Apto 1")
                .bairro("Centro")
                .cidade("Cidade")
                .estado("SP")
                .cep("12345678")
                .build();

        VoluntarioDTO voluntarioDTO = VoluntarioDTO.builder()
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

        return VoluntarioRequestDTO.builder()
                .voluntarioDTO(voluntarioDTO)
                .emailDTOList(List.of(emailDTO))
                .telefoneDTOList(List.of(telefoneDTO))
                .formularioDTO(formularioDTO)
                .build();
    }

    private VoluntarioResponseDTO buildVoluntarioResponseDTO() {
        EnderecoDTO enderecoDTO = EnderecoDTO.builder()
                .logradouro("Rua Teste")
                .numero("123")
                .complemento("Apto 1")
                .bairro("Centro")
                .cidade("Cidade")
                .estado("SP")
                .cep("12345678")
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

        return VoluntarioResponseDTO.builder()
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

    @Nested
    @DisplayName("POST /voluntario/criar")
    class CriarVoluntario {

        @Test
        @DisplayName("Deve criar um novo voluntário com sucesso")
        void criarComSucesso() {
            VoluntarioRequestDTO requestDTO = buildVoluntarioRequestDTO();
            VoluntarioDTO dto = VoluntarioDTO.builder().uuid(uuid).nomeCompleto("João da Silva").build();

            when(voluntarioService.criar(any(), anyList(), anyList(), any())).thenReturn(dto);

            ResponseEntity<VoluntarioDTO> response = voluntarioController.criar(requestDTO);

            assertNotNull(response, "A resposta não deve ser nula");
            assertEquals(HttpStatus.OK.value(), response.getStatusCode().value(), "O status HTTP deve ser 200 OK");
            assertEquals(dto, response.getBody(), "O corpo deve conter o DTO do voluntário criado");
            verify(voluntarioService).criar(any(), anyList(), anyList(), any());
        }
    }

    @Nested
    @DisplayName("GET /voluntario/{id}")
    class BuscarVoluntario {

        @Test
        @DisplayName("Deve buscar voluntário por ID com sucesso")
        void buscarPorIdComSucesso() {
            VoluntarioResponseDTO responseDTO = buildVoluntarioResponseDTO();

            when(voluntarioService.buscarPorId(uuid)).thenReturn(responseDTO);

            ResponseEntity<VoluntarioResponseDTO> response = voluntarioController.buscarPorId(uuid);

            assertNotNull(response, "A resposta não deve ser nula");
            assertEquals(HttpStatus.OK.value(), response.getStatusCode().value(), "O status HTTP deve ser 200 OK");
            assertEquals(responseDTO, response.getBody(), "O corpo deve conter o DTO do voluntário buscado");
            verify(voluntarioService).buscarPorId(uuid);
        }
    }

    @Nested
    @DisplayName("GET /voluntario/")
    class ListarVoluntarios {

        @Test
        @DisplayName("Deve retornar a lista paginada de voluntários com filtro ativo")
        void buscarTodosComFiltroAtivo() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by("criadoEm").ascending());
            VoluntarioResponseDTO responseDTO = buildVoluntarioResponseDTO();
            Page<VoluntarioResponseDTO> page = new PageImpl<>(List.of(responseDTO), pageable, 1);

            when(voluntarioService.buscarTodos(true, pageable)).thenReturn(page);

            ResponseEntity<Page<VoluntarioResponseDTO>> response =
                    voluntarioController.buscarTodos(0, 10, true);

            assertNotNull(response, "A resposta não deve ser nula");
            assertEquals(HttpStatus.OK.value(), response.getStatusCode().value(), "O status HTTP deve ser 200 OK");
            assertEquals(ONE_ELEMENT, Objects.requireNonNull(response.getBody()).getTotalElements(), "A página deve conter um elemento");
            assertEquals(responseDTO, response.getBody().getContent().get(FIRST_ELEMENT_INDEX), "O elemento deve ser o voluntário esperado");
            verify(voluntarioService).buscarTodos(true, pageable);
        }

        @Test
        @DisplayName("Deve retornar a lista paginada de todos os voluntários sem filtro")
        void buscarTodosSemFiltro() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by("criadoEm").ascending());
            VoluntarioResponseDTO responseDTO = buildVoluntarioResponseDTO();
            Page<VoluntarioResponseDTO> page = new PageImpl<>(List.of(responseDTO), pageable, 1);

            when(voluntarioService.buscarTodos(null, pageable)).thenReturn(page);

            ResponseEntity<Page<VoluntarioResponseDTO>> response =
                    voluntarioController.buscarTodos(0, 10, null);

            assertNotNull(response, "A resposta não deve ser nula");
            assertEquals(HttpStatus.OK.value(), response.getStatusCode().value(), "O status HTTP deve ser 200 OK");
            assertEquals(ONE_ELEMENT, Objects.requireNonNull(response.getBody()).getTotalElements(), "A página deve conter um elemento");
            assertEquals(responseDTO, response.getBody().getContent().get(FIRST_ELEMENT_INDEX), "O elemento deve ser o voluntário esperado");
            verify(voluntarioService).buscarTodos(null, pageable);
        }
    }

    @Nested
    @DisplayName("PUT /voluntario/atualizar/{id}")
    class AtualizarVoluntario {

        @Test
        @DisplayName("Deve atualizar voluntário com sucesso")
        void atualizarComSucesso() {
            VoluntarioRequestDTO requestDTO = buildVoluntarioRequestDTO();

            doNothing().when(voluntarioService).atualizar(
                    eq(uuid),
                    any(),
                    anyList(),
                    anyList(),
                    any()
            );

            ResponseEntity<Void> response = voluntarioController.atualizar(uuid, requestDTO);

            assertNotNull(response, "A resposta não deve ser nula");
            assertEquals(HttpStatus.OK.value(), response.getStatusCode().value(), "O status HTTP deve ser 200 OK");
            assertNull(response.getBody(), "O corpo da resposta deve ser nulo para atualizações");
            verify(voluntarioService).atualizar(eq(uuid), any(), anyList(), anyList(), any());
        }
    }

    @Nested
    @DisplayName("DELETE /voluntario/remover/{id}")
    class DeletarVoluntario {

        @Test
        @DisplayName("Deve deletar voluntário com sucesso")
        void deletarComSucesso() {
            doNothing().when(voluntarioService).remover(uuid);

            ResponseEntity<Void> response = voluntarioController.deletar(uuid);

            assertNotNull(response, "A resposta não deve ser nula");
            assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatusCode().value(), "O status HTTP deve ser 204 No Content");
            assertNull(response.getBody(), "O corpo da resposta deve ser nulo para deleções");
            verify(voluntarioService).remover(uuid);
        }
    }

    @Nested
    @DisplayName("PATCH /voluntario/status-aprovacao/{id}")
    class AtualizarStatusAprovacao {

        @Test
        @DisplayName("Deve atualizar status de aprovação para aprovado com sucesso")
        void atualizarStatusAprovacaoComSucesso() {
            VoluntarioAtualizarStatusAprovacaoDTO statusDTO = VoluntarioAtualizarStatusAprovacaoDTO.builder()
                    .aprovado(true)
                    .build();

            doNothing().when(voluntarioService).atualizarStatusAprovacao(uuid, true);

            ResponseEntity<Void> response = voluntarioController.atualizarStatusAprovacao(uuid, statusDTO);

            assertNotNull(response, "A resposta não deve ser nula");
            assertEquals(HttpStatus.OK.value(), response.getStatusCode().value(), "O status HTTP deve ser 200 OK");
            assertNull(response.getBody(), "O corpo da resposta deve ser nulo para atualizações de status");
            verify(voluntarioService).atualizarStatusAprovacao(uuid, true);
        }

        @Test
        @DisplayName("Deve atualizar status de aprovação para reprovado com sucesso")
        void atualizarStatusReprovacaoComSucesso() {
            VoluntarioAtualizarStatusAprovacaoDTO statusDTO = VoluntarioAtualizarStatusAprovacaoDTO.builder()
                    .aprovado(false)
                    .build();

            doNothing().when(voluntarioService).atualizarStatusAprovacao(uuid, false);

            ResponseEntity<Void> response = voluntarioController.atualizarStatusAprovacao(uuid, statusDTO);

            assertNotNull(response, "A resposta não deve ser nula");
            assertEquals(HttpStatus.OK.value(), response.getStatusCode().value(), "O status HTTP deve ser 200 OK");
            assertNull(response.getBody(), "O corpo da resposta deve ser nulo para atualizações de status");
            verify(voluntarioService).atualizarStatusAprovacao(uuid, false);
        }
    }

    @Nested
    @DisplayName("GET /voluntario/novos")
    class BuscarNovosVoluntarios {

        @Test
        @DisplayName("Deve retornar lista paginada de novos voluntários com sucesso")
        void buscarNovosVoluntariosComSucesso() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by("criadoEm").ascending());
            VoluntarioResponseDTO responseDTO = buildVoluntarioResponseDTO();
            Page<VoluntarioResponseDTO> page = new PageImpl<>(List.of(responseDTO), pageable, 1);

            when(voluntarioService.buscarNovos(pageable)).thenReturn(page);

            ResponseEntity<Page<VoluntarioResponseDTO>> response = voluntarioController.buscarNovosVoluntarios(0, 10);

            assertNotNull(response, "A resposta não deve ser nula");
            assertEquals(HttpStatus.OK.value(), response.getStatusCode().value(), "O status HTTP deve ser 200 OK");
            assertEquals(ONE_ELEMENT, Objects.requireNonNull(response.getBody()).getTotalElements(), "A página deve conter um elemento");
            assertEquals(responseDTO, response.getBody().getContent().get(FIRST_ELEMENT_INDEX), "O elemento deve ser o voluntário esperado");
            verify(voluntarioService).buscarNovos(pageable);
        }
    }
}
