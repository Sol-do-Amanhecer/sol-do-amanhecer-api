package br.com.sol_do_amanhecer.model.dto;

import br.com.sol_do_amanhecer.model.entity.Voluntario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class TelefoneDTO {

    @NotNull
    private UUID uuidVoluntario;

    @NotBlank
    @Size(min = 2, max = 2)
    private String ddd;

    @NotBlank
    @Size(min = 9, max = 9)
    private String telefone;

    private LocalDateTime criadoEm;

    private LocalDateTime atualizadoEm;
}