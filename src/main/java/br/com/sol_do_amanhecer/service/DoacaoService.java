package br.com.sol_do_amanhecer.service;

import br.com.sol_do_amanhecer.model.dto.DoacaoDTO;
import br.com.sol_do_amanhecer.shared.enums.EMeioDoacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.UUID;

public interface DoacaoService {

    DoacaoDTO criar(DoacaoDTO doacaoDTO);

    void atualizar(UUID id, DoacaoDTO doacaoDTO);

    void remover(UUID id);

    DoacaoDTO buscarPorId(UUID id);

    Page<DoacaoDTO> buscarTodas(Integer ano, Integer mes, EMeioDoacao meioDoacao, Pageable pageable);
}