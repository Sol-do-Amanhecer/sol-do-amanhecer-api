package br.com.sol_do_amanhecer.controller;

import br.com.sol_do_amanhecer.model.dto.ObjetivoMensalDTO;
import br.com.sol_do_amanhecer.model.dto.ObjetivoMensalRequestDTO;
import br.com.sol_do_amanhecer.service.ObjetivoMensalService;
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
@Tag(name = "Objetivos Mensais", description = "Endpoints para Gerenciar Objetivos Mensais")
public class ObjetivoMensalController implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjetivoMensalController.class);
    private final ObjetivoMensalService objetivoMensalService;

    public ObjetivoMensalController(ObjetivoMensalService objetivoMensalService) {
        this.objetivoMensalService = objetivoMensalService;
    }

    @PostMapping(value = CRIAR_OBJETIVO)
    @Operation(summary = "Criar um novo objetivo mensal", description = "Adiciona um novo objetivo mensal")
    public ResponseEntity<ObjetivoMensalDTO> criar(@RequestBody ObjetivoMensalRequestDTO objetivoMensalRequestDTO) {
        LOGGER.debug("Requisição para criar novo objetivo mensal");
        ObjetivoMensalDTO novoObjetivo = objetivoMensalService.criar(objetivoMensalRequestDTO);
        return ResponseEntity.ok().body(novoObjetivo);
    }

    @GetMapping(value = OBJETIVO_POR_ID)
    @Operation(summary = "Buscar objetivo mensal por ID", description = "Busca um objetivo mensal pelo ID")
    public ResponseEntity<ObjetivoMensalDTO> buscarPorId(@PathVariable(value = "id") UUID id) {
        LOGGER.debug("Requisição para buscar objetivo mensal por ID: {}", id);
        ObjetivoMensalDTO objetivoMensal = objetivoMensalService.buscarPorId(id);
        return ResponseEntity.ok().body(objetivoMensal);
    }

    @GetMapping(value = TODOS_OBJETIVOS)
    @Operation(summary = "Buscar todos os objetivos mensais", description = "Retorna uma lista de todos os objetivos mensais")
    public ResponseEntity<List<ObjetivoMensalDTO>> buscarTodos() {
        LOGGER.debug("Requisição para buscar todos os objetivos mensais");
        List<ObjetivoMensalDTO> objetivos = objetivoMensalService.buscarTodos();
        return ResponseEntity.ok().body(objetivos);
    }

    @PutMapping(value = ATUALIZAR_OBJETIVO)
    @Operation(summary = "Atualizar um objetivo mensal", description = "Atualiza um objetivo mensal existente")
    public ResponseEntity<Void> atualizar(
            @PathVariable(value = "id") UUID id,
            @RequestBody ObjetivoMensalRequestDTO objetivoMensalRequestDTO) {
        LOGGER.debug("Requisição para atualizar objetivo mensal ID: {}", id);
        objetivoMensalService.atualizar(id, objetivoMensalRequestDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = APAGAR_OBJETIVO)
    @Operation(summary = "Excluir um objetivo mensal", description = "Remove um objetivo mensal pelo ID")
    public ResponseEntity<Void> deletar(@PathVariable(value = "id") UUID id) {
        LOGGER.debug("Requisição para deletar o objetivo mensal ID: {}", id);
        objetivoMensalService.remover(id);
        return ResponseEntity.noContent().build();
    }
}