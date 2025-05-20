package br.com.sol_do_amanhecer.controller;

import br.com.sol_do_amanhecer.model.dto.PermissaoDTO;
import br.com.sol_do_amanhecer.service.PermissaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@Tag(name = "Permissão", description = "Endpoints para gerenciamento de permissões")
public class PermissaoController implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(PermissaoController.class);
    private final PermissaoService permissaoService;

    public PermissaoController(PermissaoService permissaoService) {
        this.permissaoService = permissaoService;
    }

    @GetMapping(value = PERMISSAO_POR_ID)
    @Operation(summary = "Busca uma permissão por ID",
            description = "Retorna os dados de uma permissão específica a partir do seu ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucesso",
                            content = @Content(schema = @Schema(implementation = PermissaoDTO.class))),
                    @ApiResponse(responseCode = "204", description = "Sem conteúdo", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Não encontrado", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content)
            })
    public ResponseEntity<PermissaoDTO> buscarPorId(@PathVariable("id") UUID id) {
        LOGGER.debug("Requisição para buscar permissão por ID");
        PermissaoDTO permissaoDTO = permissaoService.buscarPorId(id);
        return ResponseEntity.ok(permissaoDTO);
    }

    @GetMapping(value = TODAS_PERMISSOES)
    @Operation(summary = "Lista todas as permissões",
            description = "Retorna uma lista contendo todas as permissões registradas.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucesso",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = PermissaoDTO.class))
                            )),
                    @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Não encontrado", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content)
            })
    public ResponseEntity<List<PermissaoDTO>> buscarTodos() {
        LOGGER.debug("Requisição para listar todas as permissões");
        List<PermissaoDTO> permissoes = permissaoService.buscarTodos();
        return ResponseEntity.ok(permissoes);
    }

    @PostMapping(value = CRIAR_PERMISSAO)
    @Operation(summary = "Cria uma nova permissão",
            description = "Adiciona uma nova permissão passando um objeto JSON, XML ou YML representando a permissão.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Criado com sucesso",
                            content = @Content(schema = @Schema(implementation = PermissaoDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content)
            })
    public ResponseEntity<PermissaoDTO> criar(@RequestBody PermissaoDTO permissaoDTO) {
        LOGGER.debug("Requisição para criar nova permissão");
        PermissaoDTO criada = permissaoService.criar(permissaoDTO);
        return ResponseEntity.ok(criada);
    }

    @PutMapping(value = ATUALIZAR_PERMISSAO)
    @Operation(summary = "Atualiza uma permissão existente",
            description = "Atualiza os dados de uma permissão a partir de um objeto JSON, XML ou YML.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Atualizado com sucesso",
                            content = @Content(schema = @Schema(implementation = PermissaoDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Não encontrado", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content)
            })
    public ResponseEntity<Void> atualizar(@PathVariable("id") UUID id, @RequestBody PermissaoDTO permissaoDTO) {
        LOGGER.debug("Requisição para atualizar permissão");
        permissaoService.atualizar(id, permissaoDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = APAGAR_PERMISSAO)
    @Operation(summary = "Remove uma permissão",
            description = "Deleta uma permissão com base no ID fornecido.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Removido com sucesso", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Não encontrado", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content)
            })
    public ResponseEntity<Void> remover(@PathVariable("id") UUID id) {
        LOGGER.debug("Requisição para deletar permissão");
        permissaoService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
