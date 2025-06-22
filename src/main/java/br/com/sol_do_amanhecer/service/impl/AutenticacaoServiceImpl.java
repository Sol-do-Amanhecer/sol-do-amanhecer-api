package br.com.sol_do_amanhecer.service.impl;

import br.com.sol_do_amanhecer.model.entity.Usuario;
import br.com.sol_do_amanhecer.repository.UsuarioRepository;

import br.com.sol_do_amanhecer.security.LoginDTO;
import br.com.sol_do_amanhecer.security.TokenDTO;
import br.com.sol_do_amanhecer.security.jwt.JwtTokenProvider;
import br.com.sol_do_amanhecer.service.AutenticacaoService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AutenticacaoServiceImpl implements AutenticacaoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AutenticacaoServiceImpl.class);

    private final JwtTokenProvider jwtTokenProvider;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public TokenDTO entrar(LoginDTO loginDTO) {
        try {
            LOGGER.info("Requisição de login recebida");
            String usuario = loginDTO.getUsuario();
            String senha = loginDTO.getSenha();

            Usuario usuarioEntity = this.usuarioRepository.findByUsuario(usuario);

            if (usuarioEntity != null) {
                if (passwordEncoder.matches(senha, usuarioEntity.getSenha())) {
                    return this.jwtTokenProvider.criarTokenAcesso(usuarioEntity.getUuid(), usuario, usuarioEntity.getPermissoes());
                } else {
                    throw new BadCredentialsException("Usuário/senha inválidos!");
                }
            } else {
                throw new UsernameNotFoundException("Usuário: " + usuario + " não encontrado");
            }

        } catch (BadCredentialsException | UsernameNotFoundException exception) {
            LOGGER.error("Erro ao realizar login", exception);
            throw new BadCredentialsException("Usuário/senha inválidos!");
        }
    }

    @Override
    public TokenDTO refreshToken(String usuario, String refreshToken) {
        LOGGER.info("Requisição de refresh token recebida");

        Usuario usuarioEntity = this.usuarioRepository.findByUsuario(usuario);

        if (usuarioEntity != null) {
            return this.jwtTokenProvider.criarRefreshToken(refreshToken);
        } else {
            throw new UsernameNotFoundException("Usuário: " + usuario + " não encontrado");
        }
    }
}
