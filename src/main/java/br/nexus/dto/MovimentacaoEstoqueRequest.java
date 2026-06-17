package br.nexus.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record MovimentacaoEstoqueRequest(
    @NotNull(message = "Produto é obrigatório") Long produtoId,
    @NotNull(message = "Quantidade é obrigatória")
    @Positive(message = "Quantidade deve ser maior que zero")
    Integer quantidade,
    @Size(max = 255, message = "Observação deve ter no máximo 255 caracteres")
    String observacao
) {
}
