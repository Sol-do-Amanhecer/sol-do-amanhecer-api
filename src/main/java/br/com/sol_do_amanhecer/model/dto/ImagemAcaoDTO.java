package br.com.sol_do_amanhecer.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImagemAcaoDTO {

    @NotNull
    private UUID uuidAcao;

    @NotNull
    private byte[] imagem;

    private LocalDateTime criadoEm;

    private LocalDateTime atualizadoEm;
}
