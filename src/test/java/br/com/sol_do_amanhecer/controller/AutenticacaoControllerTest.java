package br.com.sol_do_amanhecer.controller;

import br.com.sol_do_amanhecer.security.LoginDTO;
import br.com.sol_do_amanhecer.security.TokenDTO;
import br.com.sol_do_amanhecer.service.AutenticacaoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AutenticacaoControllerTest {

    public static final int HORA_EM_MILISSEGUNDO = 3600000;

    @Mock
    private AutenticacaoService autenticacaoService;

    @InjectMocks
    private AutenticacaoController autenticacaoController;

    @Test
    @DisplayName("Deve retornar OK (200) e um TokenDTO quando as credenciais são válidas")
    public void testEntrarSucesso() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsuario("usuarioValido");
        loginDTO.setSenha("senhaCorreta");

        Date agora = new Date();
        Date expiracao = new Date(agora.getTime() + HORA_EM_MILISSEGUNDO);

        TokenDTO tokenDTOEsperado = new TokenDTO(
                UUID.randomUUID(),
                "usuarioValido",
                true,
                agora,
                expiracao,
                "jwt.access.token",
                "jwt.refresh.token"
        );

        when(autenticacaoService.entrar(any(LoginDTO.class))).thenReturn(tokenDTOEsperado);

        ResponseEntity<?> response = autenticacaoController.entrar(loginDTO);

        assertNotNull(response, "A resposta não deve ser nula.");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "O status HTTP deve ser OK.");
        assertEquals(tokenDTOEsperado, response.getBody(), "O corpo da resposta deve ser o TokenDTO esperado.");
        verify(autenticacaoService, times(1)).entrar(any(LoginDTO.class));
    }

    @Test
    @DisplayName("Deve retornar BAD_REQUEST (400) para loginDTO nulo")
    public void testEntrarLoginDTONulo() {
        ResponseEntity<?> response = autenticacaoController.entrar(null);

        assertNotNull(response, "A resposta não deve ser nula.");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Requisição inválida! Verifique os parâmetros.", response.getBody());
        verify(autenticacaoService, never()).entrar(any());
    }

    @Test
    @DisplayName("Deve retornar BAD_REQUEST (400) para usuário nulo")
    public void testEntrarUsuarioNulo() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsuario(null);
        loginDTO.setSenha("senhaValida");

        ResponseEntity<?> response = autenticacaoController.entrar(loginDTO);

        assertNotNull(response, "A resposta não deve ser nula.");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Requisição inválida! Verifique os parâmetros.", response.getBody());
        verify(autenticacaoService, never()).entrar(any());
    }

    @Test
    @DisplayName("Deve retornar BAD_REQUEST (400) para usuário em branco")
    public void testEntrarUsuarioEmBranco() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsuario("");
        loginDTO.setSenha("senhaValida");

        ResponseEntity<?> response = autenticacaoController.entrar(loginDTO);

        assertNotNull(response, "A resposta não deve ser nula.");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Requisição inválida! Verifique os parâmetros.", response.getBody());
        verify(autenticacaoService, never()).entrar(any());
    }

    @Test
    @DisplayName("Deve retornar BAD_REQUEST (400) para usuário com apenas espaços em branco")
    public void testEntrarUsuarioApenasEspacos() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsuario("   ");
        loginDTO.setSenha("senhaValida");

        ResponseEntity<?> response = autenticacaoController.entrar(loginDTO);

        assertNotNull(response, "A resposta não deve ser nula.");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Requisição inválida! Verifique os parâmetros.", response.getBody());
        verify(autenticacaoService, never()).entrar(any());
    }

    @Test
    @DisplayName("Deve retornar BAD_REQUEST (400) para senha nula")
    public void testEntrarSenhaNula() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsuario("usuarioValido");
        loginDTO.setSenha(null);

        ResponseEntity<?> response = autenticacaoController.entrar(loginDTO);

        assertNotNull(response, "A resposta não deve ser nula.");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Requisição inválida! Verifique os parâmetros.", response.getBody());
        verify(autenticacaoService, never()).entrar(any());
    }

    @Test
    @DisplayName("Deve retornar BAD_REQUEST (400) para senha em branco")
    public void testEntrarSenhaEmBranco() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsuario("usuarioValido");
        loginDTO.setSenha("");

        ResponseEntity<?> response = autenticacaoController.entrar(loginDTO);

        assertNotNull(response, "A resposta não deve ser nula.");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Requisição inválida! Verifique os parâmetros.", response.getBody());
        verify(autenticacaoService, never()).entrar(any());
    }

    @Test
    @DisplayName("Deve retornar BAD_REQUEST (400) para senha com apenas espaços em branco")
    public void testEntrarSenhaApenasEspacos() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsuario("usuarioValido");
        loginDTO.setSenha("   ");

        ResponseEntity<?> response = autenticacaoController.entrar(loginDTO);

        assertNotNull(response, "A resposta não deve ser nula.");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Requisição inválida! Verifique os parâmetros.", response.getBody());
        verify(autenticacaoService, never()).entrar(any());
    }

    @Test
    @DisplayName("Deve retornar UNAUTHORIZED (401) quando as credenciais são inválidas")
    public void testEntrarCredenciaisInvalidas() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsuario("usuarioInvalido");
        loginDTO.setSenha("senhaErrada");

        when(autenticacaoService.entrar(any(LoginDTO.class))).thenReturn(null);

        ResponseEntity<?> response = autenticacaoController.entrar(loginDTO);

        assertNotNull(response, "A resposta não deve ser nula.");
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode(), "O status HTTP deve ser UNAUTHORIZED.");
        assertEquals("Credenciais inválidas. Não foi possível autenticar.", response.getBody(), "A mensagem de erro deve ser 'Credenciais inválidas. Não foi possível autenticar.'.");
        verify(autenticacaoService, times(1)).entrar(any(LoginDTO.class));
    }

    @Test
    @DisplayName("Deve retornar OK (200) e um novo TokenDTO ao atualizar um token válido")
    public void testAtualizarTokenSucesso() {
        String username = "usuario";
        String refreshToken = "valid_refresh_token_value";

        Date agora = new Date();
        Date expiracao = new Date(agora.getTime() + HORA_EM_MILISSEGUNDO);
        TokenDTO tokenDTOEsperado = new TokenDTO(
                UUID.randomUUID(),
                "usuario",
                true,
                agora,
                expiracao,
                "jwt.new.access.token",
                "jwt.new.refresh.token"
        );

        when(autenticacaoService.refreshToken(username, refreshToken)).thenReturn(tokenDTOEsperado);

        ResponseEntity<?> response = autenticacaoController.atualizarToken(username, refreshToken);

        assertNotNull(response, "A resposta não deve ser nula.");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "O status HTTP deve ser OK.");
        assertEquals(tokenDTOEsperado, response.getBody(), "O corpo da resposta deve ser o novo TokenDTO.");
        verify(autenticacaoService, times(1)).refreshToken(username, refreshToken);
    }

    @Test
    @DisplayName("Deve retornar BAD_REQUEST (400) quando username é nulo")
    public void testAtualizarTokenUsernameNulo() {
        String refreshToken = "valid_refresh_token_value";

        ResponseEntity<?> response = autenticacaoController.atualizarToken(null, refreshToken);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Requisição inválida! Verifique os parâmetros.", response.getBody());
        verify(autenticacaoService, never()).refreshToken(any(), any());
    }

    @Test
    @DisplayName("Deve retornar BAD_REQUEST (400) quando username é vazio")
    public void testAtualizarTokenUsernameVazio() {
        String refreshToken = "valid_refresh_token_value";

        ResponseEntity<?> response = autenticacaoController.atualizarToken("", refreshToken);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Requisição inválida! Verifique os parâmetros.", response.getBody());
        verify(autenticacaoService, never()).refreshToken(any(), any());
    }

    @Test
    @DisplayName("Deve retornar BAD_REQUEST (400) quando username contém apenas espaços em branco")
    public void testAtualizarTokenUsernameApenasEspacos() {
        String refreshToken = "valid_refresh_token_value";

        ResponseEntity<?> response = autenticacaoController.atualizarToken("   ", refreshToken);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Requisição inválida! Verifique os parâmetros.", response.getBody());
        verify(autenticacaoService, never()).refreshToken(any(), any());
    }

    @Test
    @DisplayName("Deve retornar BAD_REQUEST (400) quando refreshToken é nulo")
    public void testAtualizarTokenRefreshTokenNulo() {
        String username = "usuario";

        ResponseEntity<?> response = autenticacaoController.atualizarToken(username, null);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Requisição inválida! Verifique os parâmetros.", response.getBody());
        verify(autenticacaoService, never()).refreshToken(any(), any());
    }

    @Test
    @DisplayName("Deve retornar BAD_REQUEST (400) quando refreshToken é vazio")
    public void testAtualizarTokenRefreshTokenVazio() {
        String username = "usuario";

        ResponseEntity<?> response = autenticacaoController.atualizarToken(username, "");

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Requisição inválida! Verifique os parâmetros.", response.getBody());
        verify(autenticacaoService, never()).refreshToken(any(), any());
    }

    @Test
    @DisplayName("Deve retornar BAD_REQUEST (400) quando refreshToken contém apenas espaços em branco")
    public void testAtualizarTokenRefreshTokenApenasEspacos() {
        String username = "usuario";

        ResponseEntity<?> response = autenticacaoController.atualizarToken(username, "   ");

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Requisição inválida! Verifique os parâmetros.", response.getBody());
        verify(autenticacaoService, never()).refreshToken(any(), any());
    }

    @Test
    @DisplayName("Deve retornar UNAUTHORIZED (401) quando não é possível atualizar o token")
    public void testAtualizarTokenFalha() {
        String username = "usuario";
        String refreshToken = "invalid_refresh_token_value";

        when(autenticacaoService.refreshToken(username, refreshToken)).thenReturn(null);

        ResponseEntity<?> response = autenticacaoController.atualizarToken(username, refreshToken);

        assertNotNull(response, "A resposta não deve ser nula.");
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode(), "O status HTTP deve ser UNAUTHORIZED.");
        assertEquals("Credenciais inválidas. Não foi possível atualizar o token.", response.getBody(), "A mensagem de erro deve ser conforme esperado.");
        verify(autenticacaoService, times(1)).refreshToken(username, refreshToken);
    }
}