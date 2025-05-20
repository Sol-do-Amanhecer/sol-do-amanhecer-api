package br.com.sol_do_amanhecer.service;

import br.com.sol_do_amanhecer.model.dto.UsuarioDTO;

import java.util.List;
import java.util.UUID;

public interface UsuarioService {
    UsuarioDTO buscarPorId(UUID id);

    List<UsuarioDTO> buscarTodos();

    UsuarioDTO criar(UsuarioDTO usuarioDTO);

    void atualizar(UUID id, UsuarioDTO usuarioDTO);

    void remover(UUID id);
}
