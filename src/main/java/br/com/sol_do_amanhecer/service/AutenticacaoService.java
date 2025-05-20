package br.com.sol_do_amanhecer.service;


import br.com.sol_do_amanhecer.security.LoginDTO;
import br.com.sol_do_amanhecer.security.TokenDTO;

public interface AutenticacaoService {
    TokenDTO entrar(LoginDTO loginDTO);

    TokenDTO refreshToken(String usuario, String refreshToken);
}
