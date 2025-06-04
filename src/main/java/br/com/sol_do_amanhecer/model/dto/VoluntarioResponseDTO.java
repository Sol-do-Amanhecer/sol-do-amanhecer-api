package br.com.sol_do_amanhecer.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class VoluntarioResponseDTO {

    private UUID uuid;

    @NotBlank
    private String nomeCompleto;

    @NotNull
    private LocalDate dataNascimento;

    @NotNull
    private EnderecoDTO enderecoDTO;

    @NotNull
    private Boolean ativo;

    private List<EmailDTO> emailDTOList;
    private List<TelefoneDTO> telefoneDTOList;
    private FormularioVoluntarioDTO formularioDTO;
}