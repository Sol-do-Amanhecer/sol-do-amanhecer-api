package br.com.sol_do_amanhecer.model.dto;

import br.com.sol_do_amanhecer.model.entity.Voluntario;
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
public class FormularioVoluntarioDTO {

    @NotNull
    private UUID uuidVoluntario;

    @NotBlank
    private String comoConheceu;

    @NotBlank
    private String motivoVoluntariado;

    @NotNull
    private Boolean cienteTrabalhoVoluntario;

    @NotBlank
    private String dedicacaoVoluntariado;

    @NotBlank
    private String disponibilidadeSemana;

    @NotNull
    private Boolean compromissoDivulgar;

    @NotNull
    private Boolean compromissoAcao;

    @NotNull
    private Boolean desejaCamisa;

    @NotBlank
    private String sobreMim;

    @NotNull
    private LocalDateTime dataResposta;
}