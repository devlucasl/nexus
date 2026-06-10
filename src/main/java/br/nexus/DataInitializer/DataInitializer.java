package br.nexus.DataInitializer;

import br.nexus.model.entity.Usuario;
import br.nexus.model.enums.PerfilUsuario;
import br.nexus.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (usuarioRepository.findByLogin("admin").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setNome("Administrador");
            admin.setLogin("admin");
            admin.setSenhaHash(passwordEncoder.encode("123456"));
            admin.setPerfil(PerfilUsuario.ADMINISTRADOR);;
            admin.setAtivo(true);
            usuarioRepository.save(admin);
            System.out.println("Usuário admin criado!");
        }
    }
}