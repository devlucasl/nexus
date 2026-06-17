package br.nexus.repository;

import br.nexus.model.MovimentacaoEstoque;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {
    boolean existsByProdutoId(Long produtoId);
    List<MovimentacaoEstoque> findByProdutoIdOrderByDataHoraDesc(Long produtoId);
}
