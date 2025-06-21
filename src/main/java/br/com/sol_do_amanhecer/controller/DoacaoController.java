package br.com.sol_do_amanhecer.controller;

import br.com.sol_do_amanhecer.model.dto.DoacaoDTO;
import br.com.sol_do_amanhecer.service.DoacaoService;
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
@Tag(name = "Doações", description = "Endpoints para Gerenciar Doações")
public class DoacaoController implements Serializable {

    private final Logger LOGGER = LoggerFactory.getLogger(DoacaoController.class);
    private final DoacaoService doacaoService;

    public DoacaoController(DoacaoService doacaoService) {
        this.doacaoService = doacaoService;
    }

    @PostMapping(value = CRIAR_DOACAO)
    @Operation(summary = "Criar uma nova doação", description = "Adiciona uma nova doação")
    public ResponseEntity<DoacaoDTO> criar(@RequestBody DoacaoDTO doacaoDTO) {
        LOGGER.debug("Requisição para criar uma nova doação");
        DoacaoDTO novaDoacao = doacaoService.criar(doacaoDTO);
        return ResponseEntity.ok().body(novaDoacao);
    }

    @GetMapping(value = DOACAO_POR_ID)
    @Operation(summary = "Buscar doação por ID", description = "Busca uma doação pelo ID")
    public ResponseEntity<DoacaoDTO> buscarPorId(@PathVariable(value = "id") UUID id) {
        LOGGER.debug("Requisição para buscar doação por ID");
        DoacaoDTO doacao = doacaoService.buscarPorId(id);
        return ResponseEntity.ok().body(doacao);
    }

    @GetMapping(value = TODAS_DOACOES)
    @Operation(summary = "Buscar todas as doações", description = "Retorna uma lista de todas as doações")
    public ResponseEntity<List<DoacaoDTO>> buscarTodas() {
        LOGGER.debug("Requisição para buscar todas as doações");
        List<DoacaoDTO> doacoes = doacaoService.buscarTodas();
        return ResponseEntity.ok().body(doacoes);
    }

    @PutMapping(value = ATUALIZAR_DOACAO)
    @Operation(summary = "Atualizar uma doação", description = "Atualiza uma doação existente")
    public ResponseEntity<Void> atualizar(
            @PathVariable(value = "id") UUID id,
            @RequestBody DoacaoDTO doacaoDTO) {
        LOGGER.debug("Requisição para atualizar uma doação");
        doacaoService.atualizar(id, doacaoDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(APAGAR_DOACAO)
    @Operation(summary = "Excluir uma doação", description = "Remove uma doação pelo ID")
    public ResponseEntity<Void> deletar(@PathVariable(value = "id") UUID id) {
        LOGGER.debug("Requisição para deletar uma doação");
        doacaoService.remover(id);
        return ResponseEntity.noContent().build();
    }
}