package br.com.sol_do_amanhecer.controller;

import br.com.sol_do_amanhecer.model.dto.UsuarioDTO;
import br.com.sol_do_amanhecer.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.UUID;

import static br.com.sol_do_amanhecer.shared.constant.PathsConstants.*;

@RestController
@RequestMapping(BASE_URL)
@Tag(name = "Usuário", description = "Endpoints para Gerenciar os Usuários")
public class UsuarioController implements Serializable {
    private final Logger LOGGER = LoggerFactory.getLogger(UsuarioController.class);
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping(value = USUARIO_POR_ID)
    @Operation(summary = "Buscar um usuário",
            description = "Busca um usuário pelo ID",
            tags = {"Usuário"},
            responses = {
                    @ApiResponse(description = "Sucesso", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = UsuarioDTO.class))),
                    @ApiResponse(description = "Sem Conteúdo", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Requisição Inválida", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Não Autorizado", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Não Encontrado", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Erro Interno", responseCode = "500", content = @Content),
            })
    public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable(value = "id") UUID id) {
        LOGGER.debug("Requisição para buscar usuário por ID");
        UsuarioDTO userDTO = this.usuarioService.buscarPorId(id);
        return ResponseEntity.ok().body(userDTO);
    }

    @GetMapping(value = TODOS_USUARIOS)
    @Operation(summary = "Buscar todos os usuários",
            description = "Retorna uma lista de todos os usuários",
            tags = {"Usuário"},
            responses = {
                    @ApiResponse(description = "Sucesso", responseCode = "200",
                            content = {@Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = UsuarioDTO.class))
                            )}),
                    @ApiResponse(description = "Requisição Inválida", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Não Autorizado", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Não Encontrado", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Erro Interno", responseCode = "500", content = @Content),
            })
    public ResponseEntity<Page<UsuarioDTO>> buscarTodos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Boolean ativo) {

        LOGGER.debug("Requisição para buscar usuários com paginação");

        Pageable pageable = PageRequest.of(page, size, Sort.by("criadoEm").ascending());

        Page<UsuarioDTO> usuarios = usuarioService.buscarTodos(ativo, pageable);

        return ResponseEntity.ok().body(usuarios);
    }

    @PostMapping(value = CRIAR_USUARIO)
    @Operation(summary = "Criar um novo usuário",
            description = "Adiciona um novo usuário passando os dados em JSON, XML ou YML",
            tags = {"Usuário"},
            responses = {
                    @ApiResponse(description = "Sucesso", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = UsuarioDTO.class))),
                    @ApiResponse(description = "Requisição Inválida", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Não Autorizado", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Erro Interno", responseCode = "500", content = @Content),
            })
    public ResponseEntity<UsuarioDTO> criar(@RequestBody UsuarioDTO userDTO) {
        LOGGER.debug("Requisição para criar usuário");
        UsuarioDTO createdUsuarioDTO = this.usuarioService.criar(userDTO);
        return ResponseEntity.ok().body(createdUsuarioDTO);
    }

    @PutMapping(value = ATUALIZAR_USUARIO)
    @Operation(summary = "Atualizar um usuário",
            description = "Atualiza um usuário existente passando os dados em JSON, XML ou YML",
            tags = {"Usuário"},
            responses = {
                    @ApiResponse(description = "Atualizado", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = UsuarioDTO.class))),
                    @ApiResponse(description = "Requisição Inválida", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Não Autorizado", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Não Encontrado", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Erro Interno", responseCode = "500", content = @Content),
            })
    public ResponseEntity<Void> atualizar(@PathVariable(value = "id") UUID id, @RequestBody UsuarioDTO userDTO) {
        LOGGER.debug("Requisição para atualizar usuário");
        this.usuarioService.atualizar(id, userDTO);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(value = TROCAR_SENHA_USUARIO)
    @Operation(summary = "Alterar senha do usuário",
            description = "Atualiza apenas a senha do usuário",
            tags = {"Usuário"},
            responses = {
                    @ApiResponse(description = "Senha alterada com sucesso", responseCode = "200", content = @Content),
                    @ApiResponse(description = "Usuário não encontrado", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Erro interno", responseCode = "500", content = @Content),
            })
    public ResponseEntity<Void> trocarSenha(@PathVariable("id") UUID id, @RequestBody String novaSenha) {
        LOGGER.debug("Requisição para trocar senha do usuário com ID: {}", id);
        usuarioService.trocarSenha(id, novaSenha);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping(APAGAR_USUARIO)
    @Operation(summary = "Excluir um usuário",
            description = "Remove um usuário passando o ID como parâmetro",
            tags = {"Usuário"},
            responses = {
                    @ApiResponse(description = "Sem Conteúdo", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Requisição Inválida", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Não Autorizado", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Não Encontrado", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Erro Interno", responseCode = "500", content = @Content),
            })
    public ResponseEntity<Void> deletar(@PathVariable(value = "id") UUID id) {
        LOGGER.debug("Requisição para deletar usuário");
        this.usuarioService.remover(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(RESETAR_SENHA_USUARIO)
    @Operation(
            summary = "Solicitar redefinição de senha pelo username",
            description = "Busca o usuário pelo seu username e envia um e-mail com instruções para redefinição de senha.",
            tags = {"Usuário"},
            responses = {
                    @ApiResponse(description = "E-mail enviado com sucesso", responseCode = "200", content = @Content),
                    @ApiResponse(description = "Usuário não encontrado", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Erro interno", responseCode = "500", content = @Content)
            }
    )
    public ResponseEntity<String> solicitarResetSenhaPorUsername(@PathVariable("username") String username) {
        LOGGER.debug("Iniciando solicitação de redefinição de senha para o usuário: {}", username);

        usuarioService.enviarEmailRedefinicaoSenhaPorUsername(username);

        return ResponseEntity.ok("Um e-mail foi enviado com instruções para redefinir sua senha.");
    }
}
