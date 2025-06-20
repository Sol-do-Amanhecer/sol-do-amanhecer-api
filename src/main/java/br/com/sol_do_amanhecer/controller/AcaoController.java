package br.com.sol_do_amanhecer.controller;

import br.com.sol_do_amanhecer.model.dto.AcaoDTO;
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
@Tag(name = "Acao", description = "Endpoints para Gerenciar as Ações")
public class AcaoController implements Serializable {
    private final Logger LOGGER = LoggerFactory.getLogger(AcaoController.class);
    private final AcaoService acaoService;

    public AcaoController(AcaoService acaoService) {
        this.acaoService = acaoService;
    }

    @GetMapping(ACAO_POR_ID)
    @Operation(summary = "Buscar ação por ID", description = "Busca uma ação pelo ID")
    public ResponseEntity<AcaoDTO> buscarPorId(@PathVariable(value = "id") UUID id) {
        LOGGER.debug("Requisição para buscar ação por ID");
        AcaoDTO acaoDTO = acaoService.buscarPorId(id);
        if (acaoDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(acaoDTO);
    }

    @GetMapping(TODAS_ACOES)
    @Operation(summary = "Buscar todas as ações", description = "Retorna uma lista de todas as ações")
    public ResponseEntity<List<AcaoDTO>> buscarTodos() {
        LOGGER.debug("Requisição para buscar todas as ações");
        List<AcaoDTO> acoes = acaoService.buscarTodos();
        return ResponseEntity.ok().body(acoes);
    }

    @PostMapping(CRIAR_ACAO)
    @Operation(summary = "Criar uma nova ação", description = "Adiciona uma nova ação")
    public ResponseEntity<AcaoDTO> criar(@RequestBody AcaoDTO acaoDTO) {
        LOGGER.debug("Requisição para cria ação");
        AcaoDTO criada = acaoService.criar(acaoDTO);
        return ResponseEntity.ok().body(criada);
    }

    @PutMapping(ATUALIZAR_ACAO)
    @Operation(summary = "Atualizar uma ação", description = "Atualiza uma ação existente")
    public ResponseEntity<Void> atualizar(@PathVariable(value = "id") UUID id, @RequestBody AcaoDTO acaoDTO) {
        LOGGER.debug("Requisição para atualizar ação");
        acaoService.atualizar(id, acaoDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(APAGAR_ACAO)
    @Operation(summary = "Excluir uma ação", description = "Remove uma ação pelo ID")
    public ResponseEntity<Void> remover(@PathVariable(value = "id") UUID id) {
        LOGGER.debug("Requisição para deletar ação");
        AcaoDTO acaoDTO = acaoService.buscarPorId(id);
        if (acaoDTO == null) {
            return ResponseEntity.notFound().build();
        }
        acaoService.remover(acaoDTO);
        return ResponseEntity.noContent().build();
    }

}
