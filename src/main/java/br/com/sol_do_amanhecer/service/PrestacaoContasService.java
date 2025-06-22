package br.com.sol_do_amanhecer.service;

import br.com.sol_do_amanhecer.model.dto.PrestacaoContasDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PrestacaoContasService {
    PrestacaoContasDTO criar(PrestacaoContasDTO prestacaoContasDTO);

    void atualizar(UUID id, PrestacaoContasDTO prestacaoContasDTO);

    void remover(UUID id);

    PrestacaoContasDTO buscarPorId(UUID id);

    Page<PrestacaoContasDTO> buscarTodas(Integer mes, Integer ano, Pageable pageable);
}