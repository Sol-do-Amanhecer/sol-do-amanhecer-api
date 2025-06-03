package br.com.sol_do_amanhecer.model.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class VoluntarioRequest {
    private VoluntarioDTO voluntarioDTO;
    private List<EmailDTO> emailDTOList;
    private List<TelefoneDTO> telefoneDTOList;
    private FormularioVoluntarioDTO formularioDTO;
}