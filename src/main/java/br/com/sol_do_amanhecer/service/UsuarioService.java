package br.com.sol_do_amanhecer.service;

import br.com.sol_do_amanhecer.model.dto.UsuarioDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UsuarioService {
    UsuarioDTO buscarPorId(UUID id);

    Page<UsuarioDTO> buscarTodos(Boolean ativo, Pageable pageable);

    UsuarioDTO criar(UsuarioDTO usuarioDTO);

    void atualizar(UUID id, UsuarioDTO usuarioDTO);

    void remover(UUID id);
}
