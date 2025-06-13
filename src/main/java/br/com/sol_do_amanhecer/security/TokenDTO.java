package br.com.sol_do_amanhecer.security;

import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class TokenDTO implements Serializable {
    private UUID uuidUsuario;
    private String usuario;
    private Boolean authenticated;
    private Date created;
    private Date expiration;
    private String accessToken;
    private String refreshToken;
}
