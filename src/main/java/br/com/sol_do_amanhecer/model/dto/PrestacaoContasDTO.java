package br.com.sol_do_amanhecer.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private BigDecimal valorPago;

    @NotBlank
    private String estabelecimento;

    private String notaFiscal;

    private LocalDateTime criadoEm;

    private LocalDateTime atualizadoEm;
}