package br.com.sol_do_amanhecer.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "prestacao_contas")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class PrestacaoContas implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID uuid;

    @Column(name = "data_transacao", nullable = false)
    private LocalDate dataTransacao;

    @Column(name = "descricao_gasto", nullable = false)
    private String descricaoGasto;

    @Column(name = "destino_gasto", nullable = false)
    private String destinoGasto;

    @Column(nullable = false)
    private Double valorPago;

    @Column(name = "estabelecimento", nullable = false)
    private String estabelecimento;

    @Column(name = "nota_fiscal")
    private String notaFiscal;
}