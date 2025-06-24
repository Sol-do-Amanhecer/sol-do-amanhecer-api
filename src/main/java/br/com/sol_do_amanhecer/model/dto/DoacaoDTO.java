package br.com.sol_do_amanhecer.model.dto;

import br.com.sol_do_amanhecer.shared.enums.EMeioDoacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class DoacaoDTO {

    private UUID uuid;

    @NotNull
    private LocalDate dataDoacao;

    @NotBlank
    private String nomeDoador;

    @NotNull
    private EMeioDoacao meioDoacao;

    @NotNull
    private Double valor;

    private LocalDateTime criadoEm;

    private LocalDateTime atualizadoEm;

    private String comprovante;
}