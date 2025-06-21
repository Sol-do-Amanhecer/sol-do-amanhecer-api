package br.com.sol_do_amanhecer.model.entity;

import br.com.sol_do_amanhecer.shared.enums.EMeioDoacao;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "doacao")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Doacao implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID uuid;

    @Column(name = "data_doacao", nullable = false)
    private LocalDate dataDoacao;

    @Column(name = "nome_doador", nullable = false)
    private String nomeDoador;

    @Enumerated(EnumType.STRING)
    @Column(name = "meio_doacao", nullable = false)
    private EMeioDoacao meioDoacao;

    @Column(nullable = false)
    private Double valor;

    @Column
    private String comprovante;
}