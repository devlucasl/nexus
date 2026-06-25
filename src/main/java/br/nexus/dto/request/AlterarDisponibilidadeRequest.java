package br.nexus.dto.request;

import jakarta.validation.constraints.NotNull;

public record AlterarDisponibilidadeRequest(
    @NotNull(message = "Disponibilidade é obrigatória") Boolean disponivel
) {
}
