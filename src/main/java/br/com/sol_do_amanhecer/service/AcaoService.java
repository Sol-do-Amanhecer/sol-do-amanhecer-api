package br.com.sol_do_amanhecer.service;

import br.com.sol_do_amanhecer.model.dto.AcaoDTO;

import java.util.List;
import java.util.UUID;

public interface AcaoService {
    AcaoDTO buscarPorId(UUID id);
    List<AcaoDTO> buscarTodos();
    AcaoDTO criar(AcaoDTO acaoDTO);
    void atualizar(UUID uuid, AcaoDTO acaoDTO);
    void remover(AcaoDTO acaoDTO);

}
