package br.nexus.dto.response;

import br.nexus.model.Usuario;
import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String nome,
        String login,
        String perfil,
        boolean ativo,
        LocalDateTime ultimoAcesso
) {
    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getLogin(),
                usuario.getPerfil().name(),
                usuario.isAtivo(),
                usuario.getUltimoAcesso()
        );
    }
}