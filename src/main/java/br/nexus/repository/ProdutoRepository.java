package br.nexus.repository;

import br.nexus.model.Produto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    Optional<Produto> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
    List<Produto> findByDescricaoContainingIgnoreCaseOrderByDescricaoAsc(String descricao);
    List<Produto> findAllByOrderByDescricaoAsc();
}
