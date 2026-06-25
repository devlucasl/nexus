package br.nexus.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CadastroUsuarioRequest(
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 3, max = 120, message = "Nome deve ter entre 3 e 120 caracteres")
        String nome,

        @NotBlank(message = "Login é obrigatório")
        @Size(min = 3, max = 60, message = "Login deve ter entre 3 e 60 caracteres")
        String login,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, max = 60, message = "Senha deve ter entre 6 e 60 caracteres")
        String senha,

        @NotBlank(message = "Confirmação de senha é obrigatória")
        String confirmacaoSenha
) {
}