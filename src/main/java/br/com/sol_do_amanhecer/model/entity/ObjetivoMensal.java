package br.com.sol_do_amanhecer.model.entity;

import br.com.sol_do_amanhecer.shared.enums.EMes;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "objetivo_mensal")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ObjetivoMensal {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID uuid;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, length = 1000)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EMes mes;

    @Column(nullable = false)
    private Integer ano;

    @Column(nullable = false)
    private Double objetivoArrecadacao;

    @Column(nullable = false)
    private Double objetivoGastos;
    @Transient
    private Double arrecadado;

    @Transient
    private Double gasto;

    @Transient
    private Double percentualProgresso;
}