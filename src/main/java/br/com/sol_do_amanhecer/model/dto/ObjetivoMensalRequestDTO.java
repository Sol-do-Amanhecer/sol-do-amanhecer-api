package br.com.sol_do_amanhecer.model.dto;

import br.com.sol_do_amanhecer.shared.enums.EMes;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ObjetivoMensalRequestDTO {

    @NotBlank
    private String titulo;

    @NotBlank
    private String descricao;

    @NotBlank
    private EMes mes;

    @NotNull
    private Integer ano;

    @NotNull
    private BigDecimal objetivoArrecadacao;

    private LocalDateTime criadoEm;

    private LocalDateTime atualizadoEm;
}