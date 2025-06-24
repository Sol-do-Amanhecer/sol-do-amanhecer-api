package br.com.sol_do_amanhecer.model.dto;

import br.com.sol_do_amanhecer.shared.enums.EMes;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ObjetivoMensalDTO {

    private UUID uuid;

    @NotBlank
    private String titulo;

    @NotBlank
    private String descricao;

    @NotNull
    private EMes mes;

    @NotNull
    private Integer ano;

    @NotNull
    private Double objetivoArrecadacao;

    private Double arrecadado;
    private Double gasto;
    private Double percentualProgresso;

    private LocalDateTime criadoEm;

    private LocalDateTime atualizadoEm;
}