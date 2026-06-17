package br.nexus.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "historicos_ajuste_estoque")
public class HistoricoAjusteEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public HistoricoAjusteEstoque() {
    }

    public HistoricoAjusteEstoque(Produto produto, Usuario usuario, Integer quantidadeAnterior, Integer quantidadeNova, String justificativa) {
        this.produto = produto;
        this.usuario = usuario;
        this.quantidadeAnterior = quantidadeAnterior;
        this.quantidadeNova = quantidadeNova;
        this.justificativa = justificativa;
        this.dataHora = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Produto getProduto() {
        return produto;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Integer getQuantidadeAnterior() {
        return quantidadeAnterior;
    }

    public Integer getQuantidadeNova() {
        return quantidadeNova;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }
}
