package br.com.sol_do_amanhecer.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class EmailDTO {

    @NotNull
    private UUID uuidVoluntario;

    @NotBlank
    @Email
    @Size(max = 100)
    private String email;
}