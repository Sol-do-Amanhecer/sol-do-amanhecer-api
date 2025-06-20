package br.com.sol_do_amanhecer.model.entity;

import br.com.sol_do_amanhecer.shared.enums.ETipoAcao;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "acao")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Acao implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID uuid;

    @Column(nullable = false)
    private String nome;

    @Column(name = "data_acao", nullable = false)
    private LocalDate dataAcao;

    @Column(nullable = false)
    private String descricao;

    @Column
    @Enumerated(EnumType.STRING)
    private ETipoAcao tipo;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinColumn(name = "id_acao", nullable = false)
    private List<ImagemAcao> imagens;

}
