package br.nexus.dto.response;

import br.nexus.model.Usuario;
import java.time.LocalDateTime;

public record LoginResponse(
    Long id,
    String nome,
    String login,
    String perfil,
    LocalDateTime ultimoAcesso,
    String mensagem
) {
    public static LoginResponse from(Usuario usuario, String mensagem) {
        return new LoginResponse(
            usuario.getId(),
            usuario.getNome(),
            usuario.getLogin(),
            usuario.getPerfil().name(),
            usuario.getUltimoAcesso(),
            mensagem
        );
    }
}
