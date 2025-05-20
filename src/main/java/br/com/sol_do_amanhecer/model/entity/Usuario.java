package br.com.sol_do_amanhecer.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "usuario")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Usuario implements UserDetails, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID uuid;

    @Column(name = "usuario", unique = true, nullable = false, length = 50)
    private String usuario;

    @Column(name = "senha", nullable = false, length = 100)
    private String senha;

    @Column(name = "conta_expirada", nullable = false)
    private Boolean contaExpirada;

    @Column(name = "conta_bloqueada", nullable = false)
    private Boolean contaBloqueada;

    @Column(name = "credenciais_expiradas", nullable = false)
    private Boolean credenciaisExpiradas;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "usuario_permissao",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "permissao_id")
    )
    private List<Permissao> permissoes;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.permissoes;
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.usuario;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.contaExpirada;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.contaBloqueada;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credenciaisExpiradas;
    }

    @Override
    public boolean isEnabled() {
        return this.ativo;
    }
}