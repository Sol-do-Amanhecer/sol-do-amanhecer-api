package br.com.sol_do_amanhecer.service.impl;

import br.com.sol_do_amanhecer.exception.UsuarioException;
import br.com.sol_do_amanhecer.model.dto.PermissaoDTO;
import br.com.sol_do_amanhecer.model.dto.UsuarioDTO;
import br.com.sol_do_amanhecer.model.entity.Permissao;
import br.com.sol_do_amanhecer.model.entity.Usuario;
import br.com.sol_do_amanhecer.model.entity.Voluntario;
import br.com.sol_do_amanhecer.model.mapper.UsuarioMapper;
import br.com.sol_do_amanhecer.repository.PermissaoRepository;
import br.com.sol_do_amanhecer.repository.UsuarioRepository;
import br.com.sol_do_amanhecer.repository.VoluntarioRepository;
import br.com.sol_do_amanhecer.service.UsuarioService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper = UsuarioMapper.INSTANCE;

    @Override
    public UserDetails loadUserByUsername(String usuario) throws UsernameNotFoundException {
        LOGGER.info("Procurando um usuário pelo login " + usuario + "!");
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
    public List<UsuarioDTO> buscarTodos() {
        LOGGER.info("Buscando todos usuarios");
        return this.usuarioRepository
                .findAll()
                .stream()
                .map(usuarioMapper::entityParaDto)
                .toList();
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

        userEntity.setVoluntario(voluntario);
        userEntity.setPermissoes(permissoes);

        userEntity.setSenha(passwordEncoder.encode(userDTO.getSenha()));
        this.usuarioRepository.save(userEntity);
        return usuarioMapper.entityParaDto(userEntity);
    }

    @Override
    @Transactional
    public void atualizar(UUID id, UsuarioDTO userDTO) {
        LOGGER.info("Atualizando um usuario");
        Usuario userEntity = this.usuarioRepository
                .findById(id)
                .orElseThrow(() -> new UsuarioException("Usuário não encontrado com ID: " + id));

        userEntity.setUsuario(userDTO.getUsuario());
        userEntity.setSenha(passwordEncoder.encode(userDTO.getSenha()));
        usuarioRepository.save(userEntity);
    }

    @Override
    public void remover(UUID id) {
        LOGGER.info("Removendo um usuario");
        Usuario userEntity = this.usuarioRepository
                .findById(id)
                .orElseThrow(() -> new UsuarioException("Usuário não encontrado com ID: " + id));
        this.usuarioRepository.delete(userEntity);
    }
}