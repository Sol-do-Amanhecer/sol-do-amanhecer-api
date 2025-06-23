package br.com.sol_do_amanhecer.controller;

import br.com.sol_do_amanhecer.model.dto.*;
import br.com.sol_do_amanhecer.service.VoluntarioService;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.http.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VoluntarioControllerTest {

    @Mock
    private VoluntarioService voluntarioService;

    @InjectMocks
    private VoluntarioController voluntarioController;

    private VoluntarioDTO voluntarioDTO;
    private VoluntarioResponseDTO voluntarioResponseDTO;
    private VoluntarioRequestDTO voluntarioRequestDTO;
    private final UUID VOLUNTARIO_ID = UUID.randomUUID();
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        EnderecoDTO enderecoDTO = EnderecoDTO.builder()
                .logradouro("Rua Exemplo")
                .numero("123")
                .cidade("Cidade")
                .estado("Estado")
                .cep("00000-000")
                .build();

        voluntarioDTO = VoluntarioDTO.builder()
                .uuid(VOLUNTARIO_ID)
                .nomeCompleto("João da Silva")
                .dataNascimento(LocalDate.of(1990,1,1))
                .enderecoDTO(enderecoDTO)
                .ativo(true)
                .build();

        voluntarioResponseDTO = VoluntarioResponseDTO.builder()
                .uuid(VOLUNTARIO_ID)
                .nomeCompleto("João da Silva")
                .dataNascimento(LocalDate.of(1990,1,1))
                .enderecoDTO(enderecoDTO)
                .ativo(true)
                .emailDTOList(Collections.emptyList())
                .telefoneDTOList(Collections.emptyList())
                .formularioDTO(null)
                .build();

        voluntarioRequestDTO = VoluntarioRequestDTO.builder()
                .voluntarioDTO(voluntarioDTO)
                .emailDTOList(Collections.emptyList())
                .telefoneDTOList(Collections.emptyList())
                .formularioDTO(null)
                .build();

        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("Deve criar voluntário e retornar VoluntarioDTO com status 200")
    void testCriarVoluntario() {
        when(voluntarioService.criar(any(), anyList(), anyList(), any())).thenReturn(voluntarioDTO);

        ResponseEntity<VoluntarioDTO> response = voluntarioController.criar(voluntarioRequestDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(voluntarioDTO, response.getBody());
        verify(voluntarioService).criar(
                eq(voluntarioDTO),
                eq(Collections.emptyList()),
                eq(Collections.emptyList()),
                eq(null)
        );
    }

    @Test
    @DisplayName("Deve buscar voluntário por ID e retornar VoluntarioResponseDTO com status 200")
    void testBuscarPorId() {
        when(voluntarioService.buscarPorId(VOLUNTARIO_ID)).thenReturn(voluntarioResponseDTO);

        ResponseEntity<VoluntarioResponseDTO> response = voluntarioController.buscarPorId(VOLUNTARIO_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(voluntarioResponseDTO, response.getBody());
        verify(voluntarioService).buscarPorId(VOLUNTARIO_ID);
    }

    @Test
    @DisplayName("Deve buscar todos voluntários paginados com filtro ativo")
    void testBuscarTodos() {
        Boolean ativo = true;
        Page<VoluntarioResponseDTO> page = new PageImpl<>(List.of(voluntarioResponseDTO));
        when(voluntarioService.buscarTodos(ativo, pageable)).thenReturn(page);

        ResponseEntity<Page<VoluntarioResponseDTO>> response =
                voluntarioController.buscarTodos(0, 10, ativo);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(page, response.getBody());
        verify(voluntarioService).buscarTodos(ativo, pageable);
    }

    @Test
    @DisplayName("Deve atualizar voluntário e retornar status 200")
    void testAtualizar() {
        doNothing().when(voluntarioService).atualizar(
                eq(VOLUNTARIO_ID),
                eq(voluntarioDTO),
                eq(Collections.emptyList()),
                eq(Collections.emptyList()),
                eq(null)
        );

        ResponseEntity<Void> response =
                voluntarioController.atualizar(VOLUNTARIO_ID, voluntarioRequestDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(voluntarioService).atualizar(
                eq(VOLUNTARIO_ID),
                eq(voluntarioDTO),
                eq(Collections.emptyList()),
                eq(Collections.emptyList()),
                eq(null)
        );
    }

    @Test
    @DisplayName("Deve deletar voluntário e retornar status 204")
    void testDeletar() {
        doNothing().when(voluntarioService).remover(VOLUNTARIO_ID);

        ResponseEntity<Void> response = voluntarioController.deletar(VOLUNTARIO_ID);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(voluntarioService).remover(VOLUNTARIO_ID);
    }
}