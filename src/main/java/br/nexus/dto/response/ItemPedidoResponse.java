package br.nexus.dto.response;

import br.nexus.model.ItemPedido;

import java.math.BigDecimal;

public record ItemPedidoResponse(
        Long id,
        Long produtoId,
        String codigoProduto,
        String descricaoProduto,
        Integer quantidade,
        BigDecimal precoUnitario,
        BigDecimal descontoAplicado,
        BigDecimal precoFinalUnitario,
        BigDecimal subtotal
) {
    public static ItemPedidoResponse from(ItemPedido item) {
        return new ItemPedidoResponse(
                item.getId(),
                item.getProduto().getId(),
                item.getProduto().getCodigo(),
                item.getProduto().getDescricao(),
                item.getQuantidade(),
                item.getPrecoUnitario(),
                item.getDescontoAplicado(),
                item.getPrecoUnitario().subtract(item.getDescontoAplicado()),
                item.getSubtotal()
        );
    }
}