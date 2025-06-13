package br.com.sol_do_amanhecer.security.jwt;


import br.com.sol_do_amanhecer.model.entity.Permissao;
import br.com.sol_do_amanhecer.repository.UsuarioRepository;
import br.com.sol_do_amanhecer.security.TokenDTO;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtTokenProvider {
    @Value("${security.jwt.token.secret-key:secret}")
    private String chaveSecreta;

    @Value("${security.jwt.token.expire-length:3600000}")
    private Long validadeEmMilissegundos;

    private final UsuarioRepository repositorioUsuario;
    private Algorithm algoritmo;

    @PostConstruct
    protected void inicializar() {
        chaveSecreta = Base64.getEncoder().encodeToString(chaveSecreta.getBytes());
        algoritmo = Algorithm.HMAC256(chaveSecreta.getBytes());
    }

    public TokenDTO criarTokenAcesso(UUID uuidUsuario, String usuario, List<Permissao> permissoes) {
        Date agora = new Date();
        Date validade = new Date(agora.getTime() + validadeEmMilissegundos);
        var tokenAcesso = gerarTokenAcesso(usuario, permissoes, agora, validade);
        var tokenAtualizacao = gerarRefreshToken(usuario, permissoes, agora);
        return new TokenDTO(uuidUsuario, usuario, true, agora, validade, tokenAcesso, tokenAtualizacao);
    }

    public TokenDTO criarRefreshToken(String tokenAtualizacao) {
        if (tokenAtualizacao.contains("Bearer ")) {
            tokenAtualizacao = tokenAtualizacao.substring("Bearer ".length());
            tokenAtualizacao = tokenAtualizacao.substring(0, tokenAtualizacao.length() - 1).trim();
        }

        JWTVerifier verificador = JWT.require(algoritmo).build();
        DecodedJWT jwtDecodificado = verificador.verify(tokenAtualizacao);
        String usuario = jwtDecodificado.getSubject();
        UUID uuidUsuario = UUID.fromString(jwtDecodificado.getClaim("uuidUsuario").asString());
        List<String> roles = jwtDecodificado.getClaim("roles").asList(String.class);
        List<Permissao> permissoes = roles.stream().map(nome -> {
            Permissao permissao = new Permissao();
            permissao.setDescricao(nome);
            return permissao;
        }).collect(Collectors.toList());

        return criarTokenAcesso(uuidUsuario, usuario, permissoes);
    }

    private String gerarTokenAcesso(String usuario, List<Permissao> permissoes, Date agora, Date validade) {
        String emissor = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

        return JWT.create()
                .withClaim("roles", permissoes.stream().map(Permissao::getDescricao).collect(Collectors.toList()))
                .withIssuedAt(agora)
                .withExpiresAt(validade)
                .withSubject(usuario)
                .withIssuer(emissor)
                .sign(algoritmo)
                .strip();
    }

    private String gerarRefreshToken(String usuario, List<Permissao> permissoes, Date agora) {
        Date validadeAtualizacao = new Date(agora.getTime() + (validadeEmMilissegundos * 3));
        return JWT.create()
                .withClaim("roles", permissoes.stream().map(Permissao::getDescricao).collect(Collectors.toList()))
                .withIssuedAt(agora)
                .withExpiresAt(validadeAtualizacao)
                .withSubject(usuario)
                .sign(algoritmo)
                .strip();
    }

    public Authentication obterAutenticacao(String token) {
        DecodedJWT jwtDecodificado = decodificarToken(token);
        UserDetails detalhesUsuario = repositorioUsuario.findByUsuario(jwtDecodificado.getSubject());
        return new UsernamePasswordAuthenticationToken(detalhesUsuario, "", detalhesUsuario.getAuthorities());
    }

    private DecodedJWT decodificarToken(String token) {
        JWTVerifier verificador = JWT.require(algoritmo).build();
        return verificador.verify(token);
    }

    public String resolverToken(HttpServletRequest requisicao) {
        String tokenBearer = requisicao.getHeader("Authorization");
        if (tokenBearer != null && tokenBearer.startsWith("Bearer ")) {
            return tokenBearer.substring("Bearer ".length());
        }
        return null;
    }

    public boolean validarToken(String token) {
        try {
            DecodedJWT jwt = decodificarToken(token);
            return !jwt.getExpiresAt().before(new Date());
        } catch (Exception e) {
            throw new RuntimeException("Token JWT expirado ou inválido!");
        }
    }
}
