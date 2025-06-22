package br.com.sol_do_amanhecer.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "formulario_voluntario")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class FormularioVoluntario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID uuid;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "voluntario_id", nullable = false)
    private Voluntario voluntario;

    @Column(name = "como_conheceu", nullable = false, columnDefinition = "text")
    private String comoConheceu;

    @Column(name = "motivo_voluntariado", nullable = false, columnDefinition = "text")
    private String motivoVoluntariado;

    @Column(name = "ciente_trabalho_voluntario", nullable = false)
    private Boolean cienteTrabalhoVoluntario;

    @Column(name = "dedicacao_voluntariado", nullable = false, columnDefinition = "text")
    private Boolean dedicacaoVoluntariado;

    @Column(name = "disponibilidade_semana", nullable = false, columnDefinition = "text")
    private String disponibilidadeSemana;

    @Column(name = "compromisso_divulgar", nullable = false)
    private Boolean compromissoDivulgar;

    @Column(name = "compromisso_acao", nullable = false)
    private Boolean compromissoAcao;

    @Column(name = "deseja_camisa", nullable = false)
    private Boolean desejaCamisa;

    @Column(name = "sobre_mim", nullable = false, columnDefinition = "text")
    private String sobreMim;

    @Column(name = "data_resposta", nullable = false)
    private LocalDateTime dataResposta;
}