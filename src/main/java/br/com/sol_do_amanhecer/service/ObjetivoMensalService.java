package br.com.sol_do_amanhecer.service;

import br.com.sol_do_amanhecer.model.dto.ObjetivoMensalDTO;
import br.com.sol_do_amanhecer.model.dto.ObjetivoMensalRequestDTO;
import br.com.sol_do_amanhecer.shared.enums.EMes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ObjetivoMensalService {
    ObjetivoMensalDTO criar(ObjetivoMensalRequestDTO requestDTO);

    void atualizar(UUID id, ObjetivoMensalRequestDTO requestDTO);

    ObjetivoMensalDTO buscarPorId(UUID id);

    Page<ObjetivoMensalDTO> buscarTodos(EMes mes, Integer ano, Pageable pageable);

    void remover(UUID id);
}