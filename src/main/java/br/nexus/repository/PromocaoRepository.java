package br.nexus.repository;

import br.nexus.model.Promocao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PromocaoRepository extends JpaRepository<Promocao, Long> {

    List<Promocao> findAllByOrderByDataInicioDesc();

    List<Promocao> findByProdutoIdAndAtivaTrueOrderByDataInicioDesc(Long produtoId);
}