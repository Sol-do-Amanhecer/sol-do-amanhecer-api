package br.com.sol_do_amanhecer.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UsuarioStatusIdDTO {

    @NotBlank
    private UUID uuid;
    @NotBlank
    @Size(max = 1, min = 1)
    private Boolean ativo;
}
