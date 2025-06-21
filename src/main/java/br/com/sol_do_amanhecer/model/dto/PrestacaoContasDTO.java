package br.com.sol_do_amanhecer.model.dto;

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
public class PrestacaoContasDTO {

    private UUID uuid;

    @NotNull
    private LocalDate dataTransacao;

    @NotBlank
    private String descricaoGasto;

    @NotBlank
    private String destinoGasto;

    @NotNull
    private Double valorPago;

    @NotBlank
    private String estabelecimento;

    private String notaFiscal;
}