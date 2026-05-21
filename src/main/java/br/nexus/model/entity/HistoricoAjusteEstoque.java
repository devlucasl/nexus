package br.nexus.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "historico_ajuste_estoque")
public class HistoricoAjusteEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ajuste")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_produto", nullable = false)
    private Produto produto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "quantidade_anterior", nullable = false)
    private Integer quantidadeAnterior;

    @Column(name = "quantidade_nova", nullable = false)
    private Integer quantidadeNova;

    @Column(nullable = false, length = 255)
    private String justificativa;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;
}