package br.nexus.dto.request;

import br.nexus.model.TipoDesconto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PromocaoRequest(
        @NotNull(message = "Produto é obrigatório")
        Long produtoId,

        @NotNull(message = "Tipo de desconto é obrigatório")
        TipoDesconto tipoDesconto,

        @NotNull(message = "Valor do desconto é obrigatório")
        @DecimalMin(value = "0.01", message = "Valor do desconto deve ser maior que zero")
        BigDecimal valorDesconto,

        @NotNull(message = "Data inicial é obrigatória")
        LocalDate dataInicio,

        @NotNull(message = "Data final é obrigatória")
        LocalDate dataFim,

        Boolean ativa
) {
}