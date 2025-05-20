package br.com.sol_do_amanhecer.security;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class LoginDTO implements Serializable {
    private String usuario;
    private String senha;
}
