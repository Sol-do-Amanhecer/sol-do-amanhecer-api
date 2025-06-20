package br.com.sol_do_amanhecer.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImagemAcaoDTO {

    @NotNull
    private UUID uuidAcao;

    @NotNull(message = "A imagem da ação é obrigatória")
    private byte[] imagem;
}
