package br.nexus.dto.response;

import br.nexus.model.Produto;

public record DisponibilidadeResponse(
    Long produtoId,
    String codigo,
    String descricao,
    Integer quantidadeAtual,
    boolean disponivel,
    String status,
    boolean estoqueBaixo
) {
    public static DisponibilidadeResponse from(Produto produto) {
        boolean disponivelAtual = produto.estaDisponivelParaConsulta();
        return new DisponibilidadeResponse(
            produto.getId(),
            produto.getCodigo(),
            produto.getDescricao(),
            produto.getQuantidadeAtual(),
            disponivelAtual,
            disponivelAtual ? "DISPONIVEL" : "INDISPONIVEL",
            produto.estaComEstoqueBaixo()
        );
    }
}
