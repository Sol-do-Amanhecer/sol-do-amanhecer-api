package br.com.sol_do_amanhecer.controller;

import br.com.sol_do_amanhecer.model.dto.VoluntarioDTO;
import br.com.sol_do_amanhecer.model.dto.VoluntarioRequestDTO;
import br.com.sol_do_amanhecer.model.dto.VoluntarioResponseDTO;
import br.com.sol_do_amanhecer.service.VoluntarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import static br.com.sol_do_amanhecer.shared.constant.PathsConstants.*;

@RestController
@RequestMapping(BASE_URL)
@Tag(name = "Voluntário", description = "Endpoints para Gerenciar os Voluntários")
public class VoluntarioController implements Serializable {
    private final Logger LOGGER = LoggerFactory.getLogger(VoluntarioController.class);
    private final VoluntarioService voluntarioService;

    public VoluntarioController(VoluntarioService voluntarioService) {
        this.voluntarioService = voluntarioService;
    }

    @PostMapping(value = CRIAR_VOLUNTARIO)
    @Operation(summary = "Criar um novo voluntário", description = "Adiciona um novo voluntário")
    public ResponseEntity<VoluntarioDTO> criar(@RequestBody VoluntarioRequestDTO voluntarioRequestDTO) {
        LOGGER.debug("Requisição para criar voluntário");
        VoluntarioDTO createdVoluntarioDTO = this.voluntarioService.criar(
                voluntarioRequestDTO.getVoluntarioDTO(),
                voluntarioRequestDTO.getEmailDTOList(),
                voluntarioRequestDTO.getTelefoneDTOList(),
                voluntarioRequestDTO.getFormularioDTO()
        );
        return ResponseEntity.ok().body(createdVoluntarioDTO);
    }

    @GetMapping(value = VOLUNTARIO_POR_ID)
    @Operation(summary = "Buscar voluntário por ID", description = "Busca um voluntário pelo ID")
    public ResponseEntity<VoluntarioResponseDTO> buscarPorId(@PathVariable(value = "id") UUID id) {
        LOGGER.debug("Requisição para buscar voluntário por ID");
        VoluntarioResponseDTO voluntarioDTO = this.voluntarioService.buscarPorId(id);
        return ResponseEntity.ok().body(voluntarioDTO);
    }

    @GetMapping(value = TODOS_VOLUNTARIOS)
    @Operation(summary = "Buscar todos os voluntários", description = "Retorna uma lista de todos os voluntários")
    public ResponseEntity<List<VoluntarioResponseDTO>> buscarTodos() {
        LOGGER.debug("Requisição para buscar todos os voluntários");
        List<VoluntarioResponseDTO> voluntarios = this.voluntarioService.buscarTodos();
        return ResponseEntity.ok().body(voluntarios);
    }

    @PutMapping(value = ATUALIZAR_VOLUNTARIO)
    @Operation(summary = "Atualizar um voluntário", description = "Atualiza um voluntário existente")
    public ResponseEntity<Void> atualizar(
            @PathVariable(value = "id") UUID id,
            @RequestBody VoluntarioRequestDTO voluntarioRequestDTO) {
        LOGGER.debug("Requisição para atualizar voluntário");
        this.voluntarioService.atualizar(
                id,
                voluntarioRequestDTO.getVoluntarioDTO(),
                voluntarioRequestDTO.getEmailDTOList(),
                voluntarioRequestDTO.getTelefoneDTOList(),
                voluntarioRequestDTO.getFormularioDTO()
        );
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(APAGAR_VOLUNTARIO)
    @Operation(summary = "Excluir um voluntário", description = "Remove um voluntário pelo ID")
    public ResponseEntity<Void> deletar(@PathVariable(value = "id") UUID id) {
        LOGGER.debug("Requisição para deletar voluntário");
        this.voluntarioService.remover(id);
        return ResponseEntity.noContent().build();
    }
}