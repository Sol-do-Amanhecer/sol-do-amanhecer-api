package br.com.sol_do_amanhecer.controller;

import br.com.sol_do_amanhecer.model.dto.AcaoDTO;
import br.com.sol_do_amanhecer.model.dto.AcaoRequestDTO;
import br.com.sol_do_amanhecer.model.dto.AcaoResponseDTO;
import br.com.sol_do_amanhecer.service.AcaoService;
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
@Tag(name = "Ação", description = "Endpoints para Gerenciar as Ações")
public class AcaoController implements Serializable {

    private final Logger LOGGER = LoggerFactory.getLogger(AcaoController.class);
    private final AcaoService acaoService;

    public AcaoController(AcaoService acaoService) {
        this.acaoService = acaoService;
    }

    @PostMapping(value = CRIAR_ACAO)
    @Operation(summary = "Criar uma nova ação", description = "Adiciona uma nova ação com imagens associadas")
    public ResponseEntity<AcaoDTO> criar(@RequestBody AcaoRequestDTO acaoRequestDTO) {
        LOGGER.debug("Requisição para criar ação");
        AcaoDTO createdAcaoDTO = this.acaoService.criar(
                acaoRequestDTO.getAcaoDTO(),
                acaoRequestDTO.getImagemDTOList()
        );
        return ResponseEntity.ok().body(createdAcaoDTO);
    }

    @GetMapping(value = ACAO_POR_ID)
    @Operation(summary = "Buscar ação por ID", description = "Busca uma ação pelo ID")
    public ResponseEntity<AcaoResponseDTO> buscarPorId(@PathVariable(value = "id") UUID id) {
        LOGGER.debug("Requisição para buscar ação por ID");
        AcaoResponseDTO acaoResponseDTO = this.acaoService.buscarPorId(id);
        return ResponseEntity.ok().body(acaoResponseDTO);
    }

    @GetMapping(value = TODAS_ACOES)
    @Operation(summary = "Buscar todas as ações", description = "Retorna uma lista de todas as ações")
    public ResponseEntity<List<AcaoResponseDTO>> buscarTodos() {
        LOGGER.debug("Requisição para buscar todas as ações");
        List<AcaoResponseDTO> acoes = this.acaoService.buscarTodos();
        return ResponseEntity.ok().body(acoes);
    }

    @PutMapping(value = ATUALIZAR_ACAO)
    @Operation(summary = "Atualizar uma ação", description = "Atualiza uma ação existente com imagens associadas")
    public ResponseEntity<Void> atualizar(
            @PathVariable(value = "id") UUID id,
            @RequestBody AcaoRequestDTO acaoRequestDTO) {
        LOGGER.debug("Requisição para atualizar ação");
        this.acaoService.atualizar(
                id,
                acaoRequestDTO.getAcaoDTO(),
                acaoRequestDTO.getImagemDTOList()
        );
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(APAGAR_ACAO)
    @Operation(summary = "Excluir uma ação", description = "Remove uma ação pelo ID")
    public ResponseEntity<Void> deletar(@PathVariable(value = "id") UUID id) {
        LOGGER.debug("Requisição para deletar ação");
        this.acaoService.remover(id);
        return ResponseEntity.noContent().build();
    }
}