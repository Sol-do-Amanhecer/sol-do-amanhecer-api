package br.com.sol_do_amanhecer.model.entity;

import br.com.sol_do_amanhecer.shared.enums.EMes;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private BigDecimal objetivoArrecadacao;

    @Transient
    private BigDecimal arrecadado;

    @Transient
    private BigDecimal gasto;

    @Transient
    private BigDecimal percentualProgresso;

    @Transient
    private Integer quantidadeDoacao;

    @Transient
    private Integer quantidadePrestacaoConta;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;
}