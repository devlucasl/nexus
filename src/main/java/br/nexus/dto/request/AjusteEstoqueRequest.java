package br.nexus.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record AjusteEstoqueRequest(
    @NotNull(message = "Produto é obrigatório") Long produtoId,
    @NotNull(message = "Nova quantidade é obrigatória")
    @PositiveOrZero(message = "Nova quantidade não pode ser negativa")
    Integer novaQuantidade,
    @NotBlank(message = "Justificativa é obrigatória")
    @Size(max = 255, message = "Justificativa deve ter no máximo 255 caracteres")
    String justificativa
) {
}
