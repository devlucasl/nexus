package br.nexus.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProdutoRequest(
    @NotBlank(message = "Código do produto é obrigatório")
    @Size(max = 60, message = "Código deve ter no máximo 60 caracteres")
    String codigo,

    @NotBlank(message = "Descrição do produto é obrigatória")
    @Size(max = 180, message = "Descrição deve ter no máximo 180 caracteres")
    String descricao,

    @NotNull(message = "Preço de venda é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
    BigDecimal precoVenda,

    @NotNull(message = "Quantidade atual é obrigatória")
    @PositiveOrZero(message = "Quantidade atual não pode ser negativa")
    Integer quantidadeAtual,

    @NotNull(message = "Estoque mínimo é obrigatório")
    @PositiveOrZero(message = "Estoque mínimo não pode ser negativo")
    Integer estoqueMinimo,

    @NotNull(message = "Categoria é obrigatória")
    Long categoriaId,

    Long fornecedorId,

    Boolean disponivel
) {
}
