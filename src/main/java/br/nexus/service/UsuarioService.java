package br.nexus.service;

import br.nexus.exception.ResourceNotFoundException;
import br.nexus.model.Usuario;
import br.nexus.repository.UsuarioRepository;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorLogin(String login) {
        return usuarioRepository.findByLogin(login)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
    }

    @Transactional
    public Usuario registrarAcesso(String login) {
        Usuario usuario = buscarPorLogin(login);
        usuario.setUltimoAcesso(LocalDateTime.now());
        return usuarioRepository.save(usuario);
    }
}
