package br.com.sol_do_amanhecer.service;

import br.com.sol_do_amanhecer.model.dto.PrestacaoContasDTO;

import java.util.List;
import java.util.UUID;

public interface PrestacaoContasService {
    PrestacaoContasDTO criar(PrestacaoContasDTO prestacaoContasDTO);
    void atualizar(UUID id, PrestacaoContasDTO prestacaoContasDTO);
    void remover(UUID id);
    PrestacaoContasDTO buscarPorId(UUID id);
    List<PrestacaoContasDTO> buscarTodas();
}