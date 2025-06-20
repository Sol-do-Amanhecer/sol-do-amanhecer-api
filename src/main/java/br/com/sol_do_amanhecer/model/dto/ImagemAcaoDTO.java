package br.com.sol_do_amanhecer.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImagemAcaoDTO {

    private UUID uuid;

    @NotNull(message = "A imagem da ação é obrigatória")
    private byte[] imagem;
}
