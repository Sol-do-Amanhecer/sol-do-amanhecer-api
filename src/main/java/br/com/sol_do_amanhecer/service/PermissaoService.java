package br.com.sol_do_amanhecer.service;

import br.com.sol_do_amanhecer.model.dto.PermissaoDTO;

import java.util.List;
import java.util.UUID;

public interface PermissaoService {
    PermissaoDTO buscarPorId(UUID id);

    List<PermissaoDTO> buscarTodos();

    PermissaoDTO criar(PermissaoDTO permissaoDTO);

    void atualizar(UUID id, PermissaoDTO permissaoDTO);

    void remover(UUID id);
}
