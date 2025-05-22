package br.com.sol_do_amanhecer.model.dto;

import br.com.sol_do_amanhecer.model.entity.Endereco;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class VoluntarioDTO {

    private UUID uuid;

    @NotBlank
    private String nomeCompleto;

    @NotNull
    private LocalDate dataNascimento;

    @NotNull
    private EnderecoDTO enderecoDTO;

    @NotNull
    private Boolean ativo;
}