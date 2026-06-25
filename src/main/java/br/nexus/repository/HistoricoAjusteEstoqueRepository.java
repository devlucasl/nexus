package br.nexus.repository;

import br.nexus.model.HistoricoAjusteEstoque;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoricoAjusteEstoqueRepository extends JpaRepository<HistoricoAjusteEstoque, Long> {
    List<HistoricoAjusteEstoque> findByProdutoIdOrderByDataHoraDesc(Long produtoId);
}
