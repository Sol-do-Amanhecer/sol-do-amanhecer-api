package br.com.sol_do_amanhecer.model.dto;

import br.com.sol_do_amanhecer.shared.enums.ETipoAcao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class AcaoDTO {

    private UUID uuid;

    @NotBlank
    @Size(max = 100)
    private String nome;

    @NotBlank
    private String descricao;

    @NotNull
    private LocalDate dataAcao;

    @NotNull
    private ETipoAcao tipo;

    @NotNull
    private List<ImagemAcaoDTO> imagens;

}
