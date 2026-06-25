package br.nexus.dto.response;

import br.nexus.model.Produto;
import java.math.BigDecimal;
import java.math.RoundingMode;

public record ProdutoResponse(
    Long id,
    String codigo,
    String descricao,
    BigDecimal precoVenda,
    BigDecimal precoPromocional,
    boolean promocaoAtiva,
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
        return from(produto, produto.getPrecoVenda(), false);
    }

    public static ProdutoResponse from(Produto produto, BigDecimal precoPromocional, boolean promocaoAtiva) {
        return new ProdutoResponse(
            produto.getId(),
            produto.getCodigo(),
            produto.getDescricao(),
            produto.getPrecoVenda(),
            normalizar(precoPromocional),
            promocaoAtiva,
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

    private static BigDecimal normalizar(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : valor.setScale(2, RoundingMode.HALF_UP);
    }

    public record CategoriaResumo(Long id, String nome) {
    }

    public record FornecedorResumo(Long id, String nome) {
    }
}
