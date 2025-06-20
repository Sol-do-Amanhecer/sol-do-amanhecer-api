package br.com.sol_do_amanhecer.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "imagem_acao")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ImagemAcao {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID uuid;

    @Lob
    @Column(name = "imagem", nullable = false)
    private byte[] imagem;
}
