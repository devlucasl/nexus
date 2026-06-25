package br.nexus.dto.request;

import br.nexus.model.PerfilUsuario;
import jakarta.validation.constraints.NotNull;

public record AlterarPerfilUsuarioRequest(
        @NotNull(message = "Perfil é obrigatório")
        PerfilUsuario perfil
) {
}