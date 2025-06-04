package br.com.sol_do_amanhecer.service;

import br.com.sol_do_amanhecer.model.dto.*;

import java.util.List;
import java.util.UUID;

public interface VoluntarioService {
    VoluntarioResponseDTO buscarPorId(UUID id);

    List<VoluntarioResponseDTO> buscarTodos();

    VoluntarioDTO criar(VoluntarioDTO voluntarioDTO, List<EmailDTO> emailDTOList,
                        List<TelefoneDTO> telefoneDTOList, FormularioVoluntarioDTO formularioDTO);

    void atualizar(UUID id, VoluntarioDTO voluntarioDTO, List<EmailDTO> emailDTOs,
                   List<TelefoneDTO> telefoneDTOs, FormularioVoluntarioDTO formularioDTO);

    void remover(UUID id);
}
