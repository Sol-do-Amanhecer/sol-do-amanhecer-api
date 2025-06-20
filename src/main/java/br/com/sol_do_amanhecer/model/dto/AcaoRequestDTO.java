package br.com.sol_do_amanhecer.model.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class AcaoRequestDTO {

    private AcaoDTO acaoDTO;
    private List<ImagemAcaoDTO> imagemDTOList;
}