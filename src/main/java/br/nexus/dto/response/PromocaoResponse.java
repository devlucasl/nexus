package br.nexus.dto.response;

import br.nexus.model.Promocao;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PromocaoResponse(
        Long id,
        Long produtoId,
        String codigoProduto,
        String descricaoProduto,
        String tipoDesconto,
        BigDecimal valorDesconto,
        LocalDate dataInicio,
        LocalDate dataFim,
        boolean ativa,
        String statusAtual,
        BigDecimal precoOriginal,
        BigDecimal precoPromocional
) {
    public static PromocaoResponse from(Promocao promocao) {
        BigDecimal precoOriginal = promocao.getProduto().getPrecoVenda();

        BigDecimal desconto = promocao.estaVigente(LocalDate.now())
                ? promocao.calcularDescontoUnitario(precoOriginal)
                : BigDecimal.ZERO;

        return new PromocaoResponse(
                promocao.getId(),
                promocao.getProduto().getId(),
                promocao.getProduto().getCodigo(),
                promocao.getProduto().getDescricao(),
                promocao.getTipoDesconto().name(),
                promocao.getValorDesconto(),
                promocao.getDataInicio(),
                promocao.getDataFim(),
                promocao.isAtiva(),
                promocao.statusAtual(LocalDate.now()),
                precoOriginal,
                precoOriginal.subtract(desconto)
        );
    }
}