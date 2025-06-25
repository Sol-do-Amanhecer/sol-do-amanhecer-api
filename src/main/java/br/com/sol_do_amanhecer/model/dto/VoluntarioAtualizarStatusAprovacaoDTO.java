package br.com.sol_do_amanhecer.model.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class VoluntarioAtualizarStatusAprovacaoDTO {
    private Boolean aprovado;
}
