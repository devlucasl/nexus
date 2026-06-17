package br.nexus.dto;

import br.nexus.model.Produto;
import java.math.BigDecimal;

public record ProdutoResponse(
    Long id,
    String codigo,
    String descricao,
    BigDecimal precoVenda,
    Integer quantidadeAtual,
    Integer estoqueMinimo,
    boolean disponivel,
    boolean ativo,
    boolean estoqueBaixo,
    String statusDisponibilidade,
    CategoriaResumo categoria,
    FornecedorResumo fornecedor
) {
    public static ProdutoResponse from(Produto produto) {
        return new ProdutoResponse(
            produto.getId(),
            produto.getCodigo(),
            produto.getDescricao(),
            produto.getPrecoVenda(),
            produto.getQuantidadeAtual(),
            produto.getEstoqueMinimo(),
            produto.estaDisponivelParaConsulta(),
            produto.isAtivo(),
            produto.estaComEstoqueBaixo(),
            produto.estaDisponivelParaConsulta() ? "DISPONIVEL" : "INDISPONIVEL",
            new CategoriaResumo(produto.getCategoria().getId(), produto.getCategoria().getNome()),
            produto.getFornecedor() == null ? null : new FornecedorResumo(produto.getFornecedor().getId(), produto.getFornecedor().getNome())
        );
    }

    public record CategoriaResumo(Long id, String nome) {
    }

    public record FornecedorResumo(Long id, String nome) {
    }
}
