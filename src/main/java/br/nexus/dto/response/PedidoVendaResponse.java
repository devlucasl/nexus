package br.nexus.dto.response;

import br.nexus.dto.response.ItemPedidoResponse;
import br.nexus.model.PedidoVenda;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoVendaResponse(
        Long id,
        String numero,
        String usuario,
        String status,
        LocalDateTime dataAbertura,
        LocalDateTime dataFinalizacao,
        BigDecimal valorTotal,
        List<ItemPedidoResponse> itens
) {
    public static PedidoVendaResponse from(PedidoVenda pedido) {
        return new PedidoVendaResponse(
                pedido.getId(),
                pedido.getNumero(),
                pedido.getUsuario().getLogin(),
                pedido.getStatus().name(),
                pedido.getDataAbertura(),
                pedido.getDataFinalizacao(),
                pedido.getValorTotal(),
                pedido.getItens()
                        .stream()
                        .map(ItemPedidoResponse::from)
                        .toList()
        );
    }
}