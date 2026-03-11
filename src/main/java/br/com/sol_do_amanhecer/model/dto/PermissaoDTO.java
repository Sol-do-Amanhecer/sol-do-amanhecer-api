package br.com.sol_do_amanhecer.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class PermissaoDTO {
    private UUID uuid;
    @NotBlank
    @Max(100)
    private String descricao;

    private LocalDateTime criadoEm;

    private LocalDateTime atualizadoEm;

    public PermissaoDTO(UUID uuid, String descricao) {
        this.uuid = uuid;
        this.descricao = descricao;
    }
}
