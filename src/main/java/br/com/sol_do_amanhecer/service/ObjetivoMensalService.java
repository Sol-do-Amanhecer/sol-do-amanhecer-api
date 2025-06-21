package br.com.sol_do_amanhecer.service;

import br.com.sol_do_amanhecer.model.dto.ObjetivoMensalDTO;
import br.com.sol_do_amanhecer.model.dto.ObjetivoMensalRequestDTO;

import java.util.List;
import java.util.UUID;

public interface ObjetivoMensalService {
    ObjetivoMensalDTO criar(ObjetivoMensalRequestDTO requestDTO);
    void atualizar(UUID id, ObjetivoMensalRequestDTO requestDTO);
    ObjetivoMensalDTO buscarPorId(UUID id);
    List<ObjetivoMensalDTO> buscarTodos();
    void remover(UUID id);
}