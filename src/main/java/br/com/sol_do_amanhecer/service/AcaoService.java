package br.com.sol_do_amanhecer.service;

import br.com.sol_do_amanhecer.model.dto.AcaoDTO;
import br.com.sol_do_amanhecer.model.dto.AcaoResponseDTO;
import br.com.sol_do_amanhecer.model.dto.ImagemAcaoDTO;

import java.util.List;
import java.util.UUID;

public interface AcaoService {

    AcaoDTO criar(AcaoDTO acaoDTO, List<ImagemAcaoDTO> imagemDTOs);

    void atualizar(UUID id, AcaoDTO acaoDTO, List<ImagemAcaoDTO> imagemDTOs);

    void remover(UUID id);

    AcaoResponseDTO buscarPorId(UUID id);

    List<AcaoResponseDTO> buscarTodos();
}