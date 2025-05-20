package br.com.sol_do_amanhecer.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "permissao")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Permissao implements GrantedAuthority, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID uuid;

    @Column(nullable = false, length = 180)
    private String descricao;

    @Override
    public String getAuthority() {
        return this.descricao;
    }
}