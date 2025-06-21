package br.com.sol_do_amanhecer.model.dto;

import br.com.sol_do_amanhecer.shared.enums.EMes;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

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

    @NotNull(message = "O ano do objetivo é obrigatório.")
    private Integer ano;

    @NotNull
    private Double objetivoArrecadacao;

    @NotNull
    private Double objetivoGastos;
}