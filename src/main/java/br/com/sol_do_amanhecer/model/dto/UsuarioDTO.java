package br.com.sol_do_amanhecer.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UsuarioDTO {

    private UUID uuid;
    @NotBlank
    @Size(max = 20, min = 2)
    private String usuario;
    @NotBlank
    @Size(max = 100)
    private String senha;
    @NotBlank
    @Size(max = 1, min = 1)
    private Boolean contaExpirada;
    @NotBlank
    @Size(max = 1, min = 1)
    private Boolean contaBloqueada;
    @NotBlank
    @Size(max = 1, min = 1)
    private Boolean credenciaisExpiradas;
    @NotBlank
    @Size(max = 1, min = 1)
    private Boolean ativo;
    @NotBlank
    private List<PermissaoDTO> permissaoDTOList;
    @NotBlank
    private UUID uuidVoluntario;
}
