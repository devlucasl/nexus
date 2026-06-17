package br.nexus.config;

import br.nexus.model.Categoria;
import br.nexus.model.Fornecedor;
import br.nexus.model.PerfilUsuario;
import br.nexus.model.Produto;
import br.nexus.model.Usuario;
import br.nexus.repository.CategoriaRepository;
import br.nexus.repository.FornecedorRepository;
import br.nexus.repository.ProdutoRepository;
import br.nexus.repository.UsuarioRepository;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(
        UsuarioRepository usuarioRepository,
        CategoriaRepository categoriaRepository,
        FornecedorRepository fornecedorRepository,
        ProdutoRepository produtoRepository,
        PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (!usuarioRepository.existsByLogin("admin")) {
                usuarioRepository.save(new Usuario(
                    "Administrador NEXUS",
                    "admin",
                    passwordEncoder.encode("admin123"),
                    PerfilUsuario.ADMINISTRADOR
                ));
            }

            if (!usuarioRepository.existsByLogin("funcionario")) {
                usuarioRepository.save(new Usuario(
                    "Funcionário NEXUS",
                    "funcionario",
                    passwordEncoder.encode("func123"),
                    PerfilUsuario.FUNCIONARIO
                ));
            }

            Categoria categoriaPadrao = categoriaRepository.findByAtivoTrueOrderByNomeAsc()
                .stream()
                .findFirst()
                .orElseGet(() -> categoriaRepository.save(new Categoria("Geral", "Categoria inicial para testes")));

            Fornecedor fornecedorPadrao = fornecedorRepository.findByAtivoTrueOrderByNomeAsc()
                .stream()
                .findFirst()
                .orElseGet(() -> fornecedorRepository.save(new Fornecedor("Fornecedor Padrão", "61999999999", "fornecedor@nexus.local")));

            if (!produtoRepository.existsByCodigo("NEX-001")) {
                Produto produto = new Produto();
                produto.setCodigo("NEX-001");
                produto.setDescricao("Produto demonstrativo NEXUS");
                produto.setPrecoVenda(new BigDecimal("99.90"));
                produto.setQuantidadeAtual(5);
                produto.setEstoqueMinimo(2);
                produto.setDisponivel(true);
                produto.setAtivo(true);
                produto.setCategoria(categoriaPadrao);
                produto.setFornecedor(fornecedorPadrao);
                produtoRepository.save(produto);
            }
        };
    }
}
