package br.com.sol_do_amanhecer.controller;

import br.com.sol_do_amanhecer.model.dto.PrestacaoContasDTO;
import br.com.sol_do_amanhecer.service.PrestacaoContasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import static br.com.sol_do_amanhecer.shared.constant.PathsConstants.*;

@RestController
@RequestMapping(BASE_URL)
@Tag(name = "Prestação de Contas", description = "Endpoints para Gerenciar Prestação de Contas")
public class PrestacaoContasController implements Serializable {

    private final Logger LOGGER = LoggerFactory.getLogger(PrestacaoContasController.class);
    private final PrestacaoContasService prestacaoContasService;

    public PrestacaoContasController(PrestacaoContasService prestacaoContasService) {
        this.prestacaoContasService = prestacaoContasService;
    }

    @PostMapping(value = CRIAR_PRESTACAO)
    @Operation(summary = "Criar nova prestação de contas", description = "Adiciona uma nova prestação de contas")
    public ResponseEntity<PrestacaoContasDTO> criar(@RequestBody PrestacaoContasDTO prestacaoContasDTO) {
        LOGGER.debug("Requisição para criar uma nova prestação de contas");
        PrestacaoContasDTO novaPrestacao = prestacaoContasService.criar(prestacaoContasDTO);
        return ResponseEntity.ok().body(novaPrestacao);
    }

    @GetMapping(value = PRESTACAO_POR_ID)
    @Operation(summary = "Buscar prestação de contas por ID", description = "Busca uma prestação de contas pelo ID")
    public ResponseEntity<PrestacaoContasDTO> buscarPorId(@PathVariable(value = "id") UUID id) {
        LOGGER.debug("Requisição para buscar prestação de contas por ID");
        PrestacaoContasDTO prestacao = prestacaoContasService.buscarPorId(id);
        return ResponseEntity.ok().body(prestacao);
    }

    @GetMapping(value = TODAS_PRESTACOES)
    @Operation(summary = "Buscar todas as prestações de contas",
            description = "Retorna uma lista paginada de prestações de contas com filtro opcional por mês e ano.")
    public ResponseEntity<Page<PrestacaoContasDTO>> buscarTodas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer ano) {

        LOGGER.debug("Requisição para buscar prestações de contas com paginação");

        Pageable pageable = PageRequest.of(page, size);
        Page<PrestacaoContasDTO> prestacoes = prestacaoContasService.buscarTodas(mes, ano, pageable);

        return ResponseEntity.ok().body(prestacoes);
    }

    @PutMapping(value = ATUALIZAR_PRESTACAO)
    @Operation(summary = "Atualizar uma prestação de contas", description = "Atualiza uma prestação de contas existente")
    public ResponseEntity<Void> atualizar(
            @PathVariable(value = "id") UUID id,
            @RequestBody PrestacaoContasDTO prestacaoContasDTO) {
        LOGGER.debug("Requisição para atualizar uma prestação de contas");
        prestacaoContasService.atualizar(id, prestacaoContasDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(APAGAR_PRESTACAO)
    @Operation(summary = "Excluir uma prestação de contas", description = "Remove uma prestação de contas pelo ID")
    public ResponseEntity<Void> deletar(@PathVariable(value = "id") UUID id) {
        LOGGER.debug("Requisição para deletar uma prestação de contas");
        prestacaoContasService.remover(id);
        return ResponseEntity.noContent().build();
    }
}