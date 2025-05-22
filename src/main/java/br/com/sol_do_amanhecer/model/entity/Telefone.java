package br.com.sol_do_amanhecer.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "telefone")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Telefone implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "voluntario_id", nullable = false)
    private Voluntario voluntario;

    @Column(nullable = false, length = 2)
    private String ddd;

    @Column(nullable = false, length = 9)
    private String telefone;
}