package br.com.sol_do_amanhecer.service.impl;

import br.com.sol_do_amanhecer.exception.UsuarioException;
import br.com.sol_do_amanhecer.model.dto.PermissaoDTO;
import br.com.sol_do_amanhecer.model.dto.UsuarioDTO;
import br.com.sol_do_amanhecer.model.entity.Email;
import br.com.sol_do_amanhecer.model.entity.Permissao;
import br.com.sol_do_amanhecer.model.entity.Usuario;
import br.com.sol_do_amanhecer.model.entity.Voluntario;
import br.com.sol_do_amanhecer.model.mapper.UsuarioMapper;
import br.com.sol_do_amanhecer.repository.EmailRepository;
import br.com.sol_do_amanhecer.repository.PermissaoRepository;
import br.com.sol_do_amanhecer.repository.UsuarioRepository;
import br.com.sol_do_amanhecer.repository.VoluntarioRepository;
import br.com.sol_do_amanhecer.service.UsuarioService;
import br.com.sol_do_amanhecer.util.EmailUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UserDetailsService, UsuarioService {

    private final Logger LOGGER = LoggerFactory.getLogger(UsuarioServiceImpl.class);
    private final UsuarioRepository usuarioRepository;
    private final VoluntarioRepository voluntarioRepository;
    private final PermissaoRepository permissaoRepository;
    private final EmailRepository emailRepository;
    private final EmailUtil emailUtil;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;

    @Override
    public UserDetails loadUserByUsername(String usuario) throws UsernameNotFoundException {
        LOGGER.info("Procurando um usuário pelo login {}!", usuario);
        UserDetails userDetails = usuarioRepository.findByUsuario(usuario);
        if (userDetails != null) {
            return userDetails;
        } else {
            throw new UsernameNotFoundException("Usuário " + usuario + " não encontrado");
        }
    }

    @Override
    public UsuarioDTO buscarPorId(UUID id) {
        LOGGER.info("Buscando usuario por id");
        Usuario usuarioEntity = this.usuarioRepository
                .findById(id)
                .orElseThrow(() -> new UsuarioException("Usuário não encontrado com ID: " + id));
        return usuarioMapper.entityParaDto(usuarioEntity);
    }

    @Override
    public Page<UsuarioDTO> buscarTodos(Boolean ativo, Pageable pageable) {
        LOGGER.info("Buscando usuários, filtro ativo: {}", ativo);

        Page<Usuario> usuarios;

        if (ativo != null) {
            usuarios = usuarioRepository.findByAtivo(ativo, pageable);
        } else {
            usuarios = usuarioRepository.findAll(pageable);
        }

        return usuarios.map(usuarioMapper::entityParaDto);
    }

    @Override
    @Transactional
    public UsuarioDTO criar(UsuarioDTO userDTO) {
        LOGGER.info("Criando um usuario");

        Voluntario voluntario = voluntarioRepository.findById(userDTO.getUuidVoluntario())
                .orElseThrow(() -> new UsuarioException("Voluntário não encontrado com ID: " + userDTO.getUuidVoluntario()));

        List<Permissao> permissoes = new ArrayList<>();

        for (PermissaoDTO permissaoDTO : userDTO.getPermissaoDTOList()) {
            Permissao permissao = permissaoRepository.findById(permissaoDTO.getUuid())
                    .orElseThrow(() -> new UsuarioException("Permissão não encontrada com ID: " + permissaoDTO.getUuid()));
            permissoes.add(permissao);
        }

        Usuario userEntity = usuarioMapper.dtoParaEntity(userDTO);

        userEntity.setContaBloqueada(false);
        userEntity.setContaExpirada(false);
        userEntity.setCredenciaisExpiradas(false);
        userEntity.setVoluntario(voluntario);
        userEntity.setPermissoes(permissoes);

        userEntity.setSenha(passwordEncoder.encode(userDTO.getSenha()));
        this.usuarioRepository.save(userEntity);
        return usuarioMapper.entityParaDto(userEntity);
    }

    @Override
    @Transactional
    public void atualizar(UUID id, UsuarioDTO userDTO) {
        LOGGER.info("Atualizando usuário com ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioException("Usuário não encontrado com ID: " + id));

        usuario.setUsuario(userDTO.getUsuario());
        usuario.setSenha(passwordEncoder.encode(userDTO.getSenha()));
        usuario.setAtivo(userDTO.getAtivo());

        if (Boolean.FALSE.equals(userDTO.getAtivo())) {
            usuario.setContaExpirada(true);
            usuario.setContaBloqueada(true);
            usuario.setCredenciaisExpiradas(true);
        } else {
            usuario.setContaExpirada(false);
            usuario.setContaBloqueada(false);
            usuario.setCredenciaisExpiradas(false);
        }

        if (userDTO.getUuidVoluntario() != null) {
            Voluntario voluntario = voluntarioRepository.findById(userDTO.getUuidVoluntario())
                    .orElseThrow(() -> new UsuarioException("Voluntário não encontrado com ID: " + userDTO.getUuidVoluntario()));
            usuario.setVoluntario(voluntario);
        } else {
            usuario.setVoluntario(null);
        }

        if (userDTO.getPermissaoDTOList() != null) {
            List<UUID> novasIds = userDTO.getPermissaoDTOList().stream()
                    .map(PermissaoDTO::getUuid)
                    .toList();

            List<Permissao> atuais = usuario.getPermissoes() != null
                    ? new ArrayList<>(usuario.getPermissoes())
                    : new ArrayList<>();

            atuais.removeIf(p -> !novasIds.contains(p.getUuid()));

            for (UUID uuidPermissao : novasIds) {
                boolean jaExiste = atuais.stream().anyMatch(p -> p.getUuid().equals(uuidPermissao));
                if (!jaExiste) {
                    Permissao permissao = permissaoRepository.findById(uuidPermissao)
                            .orElseThrow(() -> new UsuarioException("Permissão não encontrada com ID: " + uuidPermissao));
                    atuais.add(permissao);
                }
            }

            usuario.setPermissoes(atuais);
        }

        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void trocarSenha(UUID id, String novaSenha) {
        LOGGER.info("Alterando senha do usuário com ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioException("Usuário não encontrado com ID: " + id));

        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void remover(UUID id) {
        LOGGER.info("Desativando usuário com ID: {}", id);

        Usuario userEntity = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioException("Usuário não encontrado com ID: " + id));

        userEntity.setContaExpirada(true);
        userEntity.setContaBloqueada(true);
        userEntity.setCredenciaisExpiradas(true);
        userEntity.setAtivo(false);

        usuarioRepository.save(userEntity);
    }

    @Override
    @Transactional
    public void enviarEmailRedefinicaoSenhaPorUsername(String username) {
        LOGGER.info("Iniciando processo para redefinição de senha para o username: {}", username);

        Usuario usuario = usuarioRepository.findByUsuario(username);
        if (usuario == null) {
            throw new UsuarioException("Usuário não encontrado com o username: " + username);
        }

        Voluntario voluntario = usuario.getVoluntario();
        if (voluntario == null) {
            throw new UsuarioException("Voluntário associado ao usuário não encontrado.");
        }

        Email email = emailRepository.findFirstByVoluntarioUuid(voluntario.getUuid())
                .orElseThrow(() -> new UsuarioException("Nenhum e-mail encontrado para o voluntário associado ao usuário."));

        String linkRedefinicao = String.format("http://localhost:8080/usuarios/%s/trocar-senha", usuario.getUuid());

        String assunto = "Redefinição de Senha";
        String mensagem = String.format(
                "Olá, %s!\n\n" +
                        "Recebemos uma solicitação para redefinir sua senha. Se você realizou esta solicitação, clique no link abaixo para redefinir sua senha:\n\n" +
                        "%s\n\n" +
                        "Caso você não tenha solicitado a redefinição de senha, pode ignorar este e-mail.\n\n" +
                        "Atenciosamente,\nEquipe Sol do Amanhecer.",
                usuario.getUsuario(), linkRedefinicao
        );

        emailUtil.enviarEmail(email.getEmail(), assunto, mensagem);

        LOGGER.info("E-mail de redefinição enviado com sucesso para o endereço: {}", email.getEmail());
    }
}