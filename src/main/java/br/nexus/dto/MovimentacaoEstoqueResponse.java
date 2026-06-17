package br.nexus.dto;

import br.nexus.model.MovimentacaoEstoque;
import java.time.LocalDateTime;

public record MovimentacaoEstoqueResponse(
    Long id,
    Long produtoId,
    String codigoProduto,
    String descricaoProduto,
    String usuario,
    String tipo,
    Integer quantidade,
    LocalDateTime dataHora,
    String observacao
) {
    public static MovimentacaoEstoqueResponse from(MovimentacaoEstoque movimentacao) {
        return new MovimentacaoEstoqueResponse(
            movimentacao.getId(),
            movimentacao.getProduto().getId(),
            movimentacao.getProduto().getCodigo(),
            movimentacao.getProduto().getDescricao(),
            movimentacao.getUsuario().getLogin(),
            movimentacao.getTipo().name(),
            movimentacao.getQuantidade(),
            movimentacao.getDataHora(),
            movimentacao.getObservacao()
        );
    }
}
