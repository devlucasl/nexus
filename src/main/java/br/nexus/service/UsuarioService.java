package br.nexus.service;

import br.nexus.dto.request.CadastroUsuarioRequest;
import br.nexus.exception.BusinessException;
import br.nexus.exception.ResourceNotFoundException;
import br.nexus.model.PerfilUsuario;
import br.nexus.model.Usuario;
import br.nexus.repository.UsuarioRepository;
import java.time.LocalDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
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

    @Transactional
    public Usuario cadastrar(CadastroUsuarioRequest request) {
        validarCadastro(request);

        String nome = request.nome().trim();
        String login = normalizarLogin(request.login());

        if (usuarioRepository.existsByLogin(login)) {
            throw new BusinessException("Já existe um usuário cadastrado com este login.");
        }

        Usuario usuario = new Usuario(
                nome,
                login,
                passwordEncoder.encode(request.senha()),
                PerfilUsuario.FUNCIONARIO
        );

        usuario.setAtivo(true);

        return usuarioRepository.save(usuario);
    }

    private void validarCadastro(CadastroUsuarioRequest request) {
        if (request.nome() == null || request.nome().trim().length() < 3) {
            throw new BusinessException("Informe um nome com pelo menos 3 caracteres.");
        }

        if (request.login() == null || request.login().trim().length() < 3) {
            throw new BusinessException("Informe um login com pelo menos 3 caracteres.");
        }

        if (request.login().contains(" ")) {
            throw new BusinessException("O login não pode conter espaços.");
        }

        if (request.senha() == null || request.senha().length() < 6) {
            throw new BusinessException("A senha deve ter pelo menos 6 caracteres.");
        }

        if (!request.senha().equals(request.confirmacaoSenha())) {
            throw new BusinessException("A confirmação de senha não confere.");
        }
    }

    private String normalizarLogin(String login) {
        return login.trim().toLowerCase();
    }
}