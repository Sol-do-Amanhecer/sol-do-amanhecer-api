package br.com.sol_do_amanhecer.service;

import br.com.sol_do_amanhecer.model.dto.DoacaoDTO;

import java.util.List;
import java.util.UUID;

public interface DoacaoService {
    DoacaoDTO criar(DoacaoDTO doacaoDTO);
    void atualizar(UUID id, DoacaoDTO doacaoDTO);
    void remover(UUID id);
    DoacaoDTO buscarPorId(UUID id);
    List<DoacaoDTO> buscarTodas();
}