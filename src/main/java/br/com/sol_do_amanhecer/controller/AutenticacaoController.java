package br.com.sol_do_amanhecer.controller;


import br.com.sol_do_amanhecer.security.LoginDTO;
import br.com.sol_do_amanhecer.security.TokenDTO;
import br.com.sol_do_amanhecer.service.AutenticacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;

import static br.com.sol_do_amanhecer.shared.constant.PathsConstants.*;


@RestController
@RequestMapping(BASE_URL)
@Tag(name = "Autenticação", description = "Endpoints para Gerenciar a Autenticação")
public class AutenticacaoController implements Serializable {
    private final Logger LOGGER = LoggerFactory.getLogger(AutenticacaoController.class);
    private final AutenticacaoService autenticacaoService;

    public AutenticacaoController(AutenticacaoService autenticacaoService) {
        this.autenticacaoService = autenticacaoService;
    }

    @PostMapping(value = AUTENTICACAO_LOGIN)
    @Operation(summary = "Autenticação do Usuário",
            description = "Autentica um usuário com as credenciais fornecidas",
            tags = {"Autenticação"},
            responses = {
                    @ApiResponse(description = "Sucesso", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = TokenDTO.class))),
                    @ApiResponse(description = "Requisição Inválida", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Não Autorizado", responseCode = "401", content = @Content),
            })
    public ResponseEntity<?> entrar(@RequestBody LoginDTO loginDTO) {
        LOGGER.debug("Requisição de entrada");

        if (verificarSeParametrosSaoInvalidosParaEntrar(loginDTO)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Requisição inválida! Verifique os parâmetros.");
        }

        TokenDTO token = autenticacaoService.entrar(loginDTO);

        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas. Não foi possível autenticar.");
        }

        return ResponseEntity.ok(token);
    }

    @PutMapping(value = AUTH_REFRESH_TOKEN)
    @Operation(summary = "Atualização do Token de Acesso",
            description = "Atualiza o token de acesso utilizando um token de atualização válido",
            tags = {"Autenticação"},
            responses = {
                    @ApiResponse(description = "Sucesso", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = TokenDTO.class))),
                    @ApiResponse(description = "Requisição Inválida", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Não Autorizado", responseCode = "401", content = @Content),
            })
    public ResponseEntity<?> atualizarToken(@PathVariable("usuario") String username, @RequestHeader("Authorization") String refreshToken) {
        LOGGER.debug("Requisição para atualizar token");

        if (verificarSeParametrosSaoInvalidosParaAtualizarToken(username, refreshToken)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Requisição inválida! Verifique os parâmetros.");
        }
        TokenDTO token = autenticacaoService.refreshToken(username, refreshToken);

        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas. Não foi possível atualizar o token.");
        }

        return ResponseEntity.ok(token);
    }

    private boolean verificarSeParametrosSaoInvalidosParaEntrar(LoginDTO loginDTO) {
        return loginDTO == null ||
                loginDTO.getUsuario() == null || loginDTO.getUsuario().isBlank() ||
                loginDTO.getSenha() == null || loginDTO.getSenha().isBlank();
    }

    private boolean verificarSeParametrosSaoInvalidosParaAtualizarToken(String usuario, String refreshToken) {
        return refreshToken == null || refreshToken.isBlank() || usuario == null || usuario.isBlank();
    }
}

