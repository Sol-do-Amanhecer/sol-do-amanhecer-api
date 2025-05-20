package br.com.sol_do_amanhecer.service.impl;

import br.com.sol_do_amanhecer.exception.UsuarioException;
import br.com.sol_do_amanhecer.model.dto.UsuarioDTO;
import br.com.sol_do_amanhecer.model.entity.Usuario;
import br.com.sol_do_amanhecer.model.mapper.UsuarioMapper;
import br.com.sol_do_amanhecer.repository.UsuarioRepository;
import br.com.sol_do_amanhecer.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UserDetailsService, UsuarioService {
    private final Logger LOGGER = LoggerFactory.getLogger(UsuarioServiceImpl.class);
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String usuario) throws UsernameNotFoundException {
        LOGGER.info("Procurando um usuário pelo login " + usuario + "!");
        UserDetails userDetails = usuarioRepository.findByUsuario(usuario);
        if (userDetails != null) {
            return userDetails;
        } else {
            throw new UsernameNotFoundException("Usuário" + usuario + " não encontrado");
        }
    }

    @Override
    public UsuarioDTO buscarPorId(UUID id) {
        LOGGER.info("Buscando usuario por id");
        Usuario usuarioEntity = this.usuarioRepository
                .findById(id)
                .orElseThrow(() -> new UsuarioException("User not found with ID: " + id));
        return UsuarioMapper.INSTANCE.entityParaDto(usuarioEntity);
    }

    @Override
    public List<UsuarioDTO> buscarTodos() {
        LOGGER.info("Buscando todos usuarios");
        return this.usuarioRepository
                .findAll()
                .stream()
                .map(UsuarioMapper.INSTANCE::entityParaDto)
                .toList();
    }

    @Override
    public UsuarioDTO criar(UsuarioDTO userDTO) {
        LOGGER.info("Criando um usuario");
        Usuario userEntity = UsuarioMapper.INSTANCE.dtoParaEntity(userDTO);
        userEntity.setSenha(passwordEncoder.encode(userDTO.getSenha()));
        this.usuarioRepository.save(userEntity);
        return UsuarioMapper.INSTANCE.entityParaDto(userEntity);
    }

    @Override
    public void atualizar(UUID id, UsuarioDTO userDTO) {
        LOGGER.info("Atualizando um usuario");
        Usuario userEntity = this.usuarioRepository
                .findById(id)
                .orElseThrow(() -> new UsuarioException("User not found with ID: " + id));
        userEntity.setUsuario(userDTO.getUsuario());
        userEntity.setSenha(passwordEncoder.encode(userDTO.getSenha()));
        usuarioRepository.save(userEntity);
    }

    @Override
    public void remover(UUID id) {
        LOGGER.info("Removendo um usuario");
        Usuario userEntity = this.usuarioRepository
                .findById(id)
                .orElseThrow(() -> new UsuarioException("User not found with ID: " + id));
        this.usuarioRepository.delete(userEntity);
    }
}
