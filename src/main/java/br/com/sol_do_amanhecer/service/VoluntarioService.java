package br.com.sol_do_amanhecer.service;

import br.com.sol_do_amanhecer.model.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface VoluntarioService {
    VoluntarioResponseDTO buscarPorId(UUID id);

    Page<VoluntarioResponseDTO> buscarTodos(Boolean ativo, Pageable pageable);

    VoluntarioDTO criar(VoluntarioDTO voluntarioDTO, List<EmailDTO> emailDTOList,
                        List<TelefoneDTO> telefoneDTOList, FormularioVoluntarioDTO formularioDTO);

    void atualizar(UUID id, VoluntarioDTO voluntarioDTO, List<EmailDTO> emailDTOs,
                   List<TelefoneDTO> telefoneDTOs, FormularioVoluntarioDTO formularioDTO);

    void remover(UUID id);

    void atualizarStatusAprovacao(UUID uuid, Boolean aprovado);
}
