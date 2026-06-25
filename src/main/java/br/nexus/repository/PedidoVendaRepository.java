package br.nexus.repository;

import br.nexus.model.PedidoVenda;
import br.nexus.model.StatusPedidoVenda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PedidoVendaRepository extends JpaRepository<PedidoVenda, Long> {

    boolean existsByNumero(String numero);

    Optional<PedidoVenda> findByNumero(String numero);

    List<PedidoVenda> findAllByOrderByDataAberturaDesc();

    List<PedidoVenda> findByStatusOrderByDataAberturaDesc(StatusPedidoVenda status);
}