package br.nexus.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "produto")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produto")
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String codigo;

    @Column(nullable = false, length = 255)
    private String descricao;

    @Column(name = "preco_venda", nullable = false, precision = 15, scale = 2)
    private BigDecimal precoVenda;

    @Column(name = "quantidade_atual", nullable = false)
    private Integer quantidadeAtual = 0;

    @Column(name = "estoque_minimo", nullable = false)
    private Integer estoqueMinimo = 0;

    @Column(nullable = false)
    private Boolean disponibilidade = true;

    @Column(nullable = false)
    private Boolean ativo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_fornecedor")
    private Fornecedor fornecedor;

    public boolean possuiEstoqueBaixo() {
        return quantidadeAtual != null
                && estoqueMinimo != null
                && quantidadeAtual <= estoqueMinimo;
    }
}