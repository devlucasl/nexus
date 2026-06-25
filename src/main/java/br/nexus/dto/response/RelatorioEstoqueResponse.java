package br.nexus.dto.response;

import br.nexus.model.Produto;

import java.math.BigDecimal;

public record RelatorioEstoqueResponse(
        String codigo,
        String descricao,
        String categoria,
        String fornecedor,
        BigDecimal precoVenda,
        Integer quantidadeAtual,
        Integer estoqueMinimo,
        String disponibilidade,
        boolean estoqueBaixo
) {
    public static RelatorioEstoqueResponse from(Produto produto) {
        return new RelatorioEstoqueResponse(
                produto.getCodigo(),
                produto.getDescricao(),
                produto.getCategoria().getNome(),
                produto.getFornecedor() == null ? "" : produto.getFornecedor().getNome(),
                produto.getPrecoVenda(),
                produto.getQuantidadeAtual(),
                produto.getEstoqueMinimo(),
                produto.estaDisponivelParaConsulta() ? "DISPONIVEL" : "INDISPONIVEL",
                produto.estaComEstoqueBaixo()
        );
    }
}